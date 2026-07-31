package career_Navigator_parent.coding.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import career_Navigator_parent.coding.dto.request.ProblemImportRequests;
import career_Navigator_parent.coding.dto.response.ProblemImportResponses;
import career_Navigator_parent.coding.entity.*;
import career_Navigator_parent.coding.enums.ProblemImportStatus;
import career_Navigator_parent.coding.event.ProblemImportQueuedEvent;
import career_Navigator_parent.coding.repository.*;
import career_Navigator_parent.coding.service.ProblemImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemImportServiceImpl implements ProblemImportService {

    private final ProblemImportJobRepository importJobRepository;
    private final ProblemImportFailureRepository failureRepository;
    private final ProblemImportedItemRepository importedItemRepository;

    private final ProblemImportValidator importValidator;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public ProblemImportResponses.ValidationResult validateImport(
            ProblemImportRequests.ImportProblems request
    ) {

        return importValidator.validate(request);
    }

    @Override
    @Transactional
    public ProblemImportResponses.ImportResult importProblems(
            ProblemImportRequests.ImportProblems request
    ) {

        String importReference = request.getImportReference().trim();

        return importJobRepository
                .findByImportReference(importReference)
                .map(this::toExistingImportResult)
                .orElseGet(() -> createImportJob(request));
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemImportResponses.ImportStatus getImportStatus(
            Long importId
    ) {

        ProblemImportJob job = importJobRepository.findById(importId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Problem import job not found."
                ));

        List<ProblemImportResponses.ImportFailure> failures =
                failureRepository
                        .findByImportJobIdOrderByProblemIndexAsc(importId)
                        .stream()
                        .map(this::toFailureResponse)
                        .toList();

        List<ProblemImportResponses.ImportedProblem> importedProblems =
                importedItemRepository
                        .findByImportJobIdOrderByProblemIndexAsc(importId)
                        .stream()
                        .map(this::toImportedProblemResponse)
                        .toList();

        double progress = job.getTotalProblems() == 0
                ? 0.0
                : (job.getProcessedProblems() * 100.0)
                / job.getTotalProblems();

        return ProblemImportResponses.ImportStatus.builder()
                .importId(job.getId())
                .importReference(job.getImportReference())
                .status(job.getStatus())
                .totalProblems(job.getTotalProblems())
                .processedProblems(job.getProcessedProblems())
                .successfulProblems(job.getSuccessfulProblems())
                .failedProblems(job.getFailedProblems())
                .skippedProblems(job.getSkippedProblems())
                .progressPercentage(
                        Math.round(progress * 100.0) / 100.0
                )
                .continueOnError(job.getContinueOnError())
                .currentProblem(job.getCurrentProblem())
                .failureMessage(job.getFailureMessage())
                .failures(failures)
                .importedProblems(importedProblems)
                .createdAt(job.getCreatedAt())
                .startedAt(job.getStartedAt())
                .completedAt(job.getCompletedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    private ProblemImportResponses.ImportResult createImportJob(
            ProblemImportRequests.ImportProblems request
    ) {

        ProblemImportResponses.ValidationResult validation =
                importValidator.validate(request);

        if (Boolean.FALSE.equals(request.getContinueOnError())
                && !validation.getValid()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Import validation failed. Validate the payload before importing."
            );
        }

        String requestPayload;

        try {
            requestPayload = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException exception) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to serialize the import request.",
                    exception
            );
        }

        ProblemImportJob job = ProblemImportJob.builder()
                .importReference(request.getImportReference().trim())
                .status(ProblemImportStatus.QUEUED)
                .totalProblems(request.getProblems().size())
                .processedProblems(0)
                .successfulProblems(0)
                .failedProblems(0)
                .skippedProblems(0)
                .continueOnError(
                        Boolean.TRUE.equals(request.getContinueOnError())
                )
                .requestPayload(requestPayload)
                .build();

        ProblemImportJob savedJob =
                importJobRepository.save(job);

        eventPublisher.publishEvent(
                new ProblemImportQueuedEvent(savedJob.getId())
        );

        return ProblemImportResponses.ImportResult.builder()
                .importId(savedJob.getId())
                .importReference(savedJob.getImportReference())
                .status(savedJob.getStatus())
                .totalProblems(savedJob.getTotalProblems())
                .acceptedProblems(validation.getValidProblems())
                .continueOnError(savedJob.getContinueOnError())
                .message("Problem import has been queued successfully.")
                .statusUrl(
                        "/api/admin/coding/imports/" + savedJob.getId()
                )
                .createdAt(savedJob.getCreatedAt())
                .build();
    }

    private ProblemImportResponses.ImportResult toExistingImportResult(
            ProblemImportJob job
    ) {

        return ProblemImportResponses.ImportResult.builder()
                .importId(job.getId())
                .importReference(job.getImportReference())
                .status(job.getStatus())
                .totalProblems(job.getTotalProblems())
                .acceptedProblems(job.getTotalProblems())
                .continueOnError(job.getContinueOnError())
                .message(
                        "An import already exists for this importReference."
                )
                .statusUrl(
                        "/api/admin/coding/imports/" + job.getId()
                )
                .createdAt(job.getCreatedAt())
                .build();
    }

    private ProblemImportResponses.ImportFailure toFailureResponse(
            ProblemImportFailure failure
    ) {

        return ProblemImportResponses.ImportFailure.builder()
                .problemIndex(failure.getProblemIndex())
                .problemTitle(failure.getProblemTitle())
                .generatedSlug(failure.getGeneratedSlug())
                .errorCode(failure.getErrorCode())
                .message(failure.getMessage())
                .failedAt(failure.getFailedAt())
                .build();
    }

    private ProblemImportResponses.ImportedProblem
    toImportedProblemResponse(ProblemImportedItem item) {

        return ProblemImportResponses.ImportedProblem.builder()
                .problemIndex(item.getProblemIndex())
                .problemId(item.getProblemId())
                .title(item.getTitle())
                .slug(item.getSlug())
                .importedAt(item.getImportedAt())
                .build();
    }
}