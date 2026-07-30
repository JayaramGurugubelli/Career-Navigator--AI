package careerpilot_parent.coding.service.impl;

import careerpilot_parent.coding.dto.request.ProblemSolutionRequests.Create;
import careerpilot_parent.coding.dto.request.ProblemSolutionRequests.EditorialUpsert;
import careerpilot_parent.coding.dto.request.ProblemSolutionRequests.Update;
import careerpilot_parent.coding.dto.response.CodingResponses.AdminSolution;
import careerpilot_parent.coding.entity.CodingProblem;
import careerpilot_parent.coding.entity.ProblemSolution;
import careerpilot_parent.coding.mapper.ProblemSolutionMapper;
import careerpilot_parent.coding.repository.CodingProblemRepository;
import careerpilot_parent.coding.repository.ProblemSolutionRepository;
import careerpilot_parent.coding.service.ProblemSolutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProblemSolutionServiceImpl
        implements ProblemSolutionService {

    private final CodingProblemRepository problems;
    private final ProblemSolutionRepository solutions;
    private final ProblemSolutionMapper mapper;

    @Override
    public AdminSolution create(
            Long problemId,
            Create request
    ) {

        CodingProblem problem =
                getRequiredProblem(problemId);

        validateCreate(request);

        ensureUniqueTitle(
                problemId,
                request.language(),
                request.title(),
                null
        );

        if (Boolean.TRUE.equals(request.official())) {
            clearExistingOfficialSolution(problemId);
        }

        ProblemSolution solution =
                ProblemSolution.builder()
                        .problem(problem)
                        .programmingLanguage(request.language())
                        .approach(request.approach())
                        .title(
                                normalizeRequired(
                                        request.title(),
                                        "Solution title is required."
                                )
                        )
                        .explanation(
                                normalizeRequired(
                                        request.explanation(),
                                        "Solution explanation is required."
                                )
                        )
                        .sourceCode(
                                normalizeRequiredCode(
                                        request.sourceCode(),
                                        "Solution source code is required."
                                )
                        )
                        .timeComplexity(
                                normalizeRequired(
                                        request.timeComplexity(),
                                        "Time complexity is required."
                                )
                        )
                        .spaceComplexity(
                                normalizeRequired(
                                        request.spaceComplexity(),
                                        "Space complexity is required."
                                )
                        )
                        .official(
                                Boolean.TRUE.equals(
                                        request.official()
                                )
                        )
                        .active(
                                request.active() == null
                                        || request.active()
                        )
                        .build();

        return mapper.toAdmin(
                solutions.saveAndFlush(solution)
        );
    }

    @Override
    public AdminSolution update(
            Long problemId,
            Long solutionId,
            Update request
    ) {

        getRequiredProblem(problemId);

        ProblemSolution solution =
                getRequiredActiveSolution(
                        problemId,
                        solutionId
                );

        validateUpdate(request);

        ensureUniqueTitle(
                problemId,
                request.language(),
                request.title(),
                solutionId
        );

        if (
                Boolean.TRUE.equals(request.official())
                        && !Boolean.TRUE.equals(
                        solution.getOfficial()
                )
        ) {
            clearExistingOfficialSolution(problemId);
        }

        applyUpdate(solution, request);

        return mapper.toAdmin(
                solutions.saveAndFlush(solution)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AdminSolution get(
            Long problemId,
            Long solutionId,
            Boolean includeInactive
    ) {

        getRequiredProblem(problemId);

        ProblemSolution solution =
                Boolean.TRUE.equals(includeInactive)
                        ? getRequiredSolution(
                        problemId,
                        solutionId
                )
                        : getRequiredActiveSolution(
                        problemId,
                        solutionId
                );

        return mapper.toAdmin(solution);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminSolution> list(
            Long problemId,
            Boolean includeInactive
    ) {

        getRequiredProblem(problemId);

        List<ProblemSolution> result =
                Boolean.TRUE.equals(includeInactive)
                        ? solutions
                        .findAllByProblemIdOrderByOfficialDescCreatedAtAsc(
                                problemId
                        )
                        : solutions
                        .findAllByProblemIdAndActiveTrueOrderByOfficialDescCreatedAtAsc(
                                problemId
                        );

        return result.stream()
                .map(mapper::toAdmin)
                .toList();
    }

    @Override
    public void delete(
            Long problemId,
            Long solutionId
    ) {

        getRequiredProblem(problemId);

        ProblemSolution solution =
                getRequiredActiveSolution(
                        problemId,
                        solutionId
                );

        solution.setActive(false);
        solution.setOfficial(false);

        solutions.saveAndFlush(solution);
    }

    @Override
    public AdminSolution restore(
            Long problemId,
            Long solutionId
    ) {

        getRequiredProblem(problemId);

        ProblemSolution solution =
                getRequiredSolution(
                        problemId,
                        solutionId
                );

        if (Boolean.TRUE.equals(solution.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solution is already active."
            );
        }

        ensureUniqueTitle(
                problemId,
                solution.getProgrammingLanguage(),
                solution.getTitle(),
                solutionId
        );

        solution.setActive(true);

        return mapper.toAdmin(
                solutions.saveAndFlush(solution)
        );
    }

    @Override
    public AdminSolution upsertEditorial(
            Long problemId,
            EditorialUpsert request
    ) {

        CodingProblem problem =
                getRequiredProblem(problemId);

        validateEditorial(request);

        ProblemSolution editorial =
                solutions
                        .findFirstByProblemIdAndOfficialTrueAndActiveTrueOrderByUpdatedAtDesc(
                                problemId
                        )
                        .orElse(null);

        if (editorial == null) {

            editorial =
                    ProblemSolution.builder()
                            .problem(problem)
                            .official(true)
                            .active(true)
                            .build();
        }

        editorial.setProgrammingLanguage(
                request.language()
        );

        editorial.setApproach(
                request.approach()
        );

        editorial.setTitle(
                normalizeRequired(
                        request.title(),
                        "Editorial title is required."
                )
        );

        editorial.setExplanation(
                normalizeRequired(
                        request.explanation(),
                        "Editorial explanation is required."
                )
        );

        editorial.setSourceCode(
                normalizeRequiredCode(
                        request.sourceCode(),
                        "Editorial source code is required."
                )
        );

        editorial.setTimeComplexity(
                normalizeRequired(
                        request.timeComplexity(),
                        "Time complexity is required."
                )
        );

        editorial.setSpaceComplexity(
                normalizeRequired(
                        request.spaceComplexity(),
                        "Space complexity is required."
                )
        );

        editorial.setOfficial(true);
        editorial.setActive(true);

        return mapper.toAdmin(
                solutions.saveAndFlush(editorial)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AdminSolution getEditorial(
            Long problemId
    ) {

        getRequiredProblem(problemId);

        ProblemSolution editorial =
                solutions
                        .findFirstByProblemIdAndOfficialTrueAndActiveTrueOrderByUpdatedAtDesc(
                                problemId
                        )
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Editorial not found."
                                )
                        );

        return mapper.toAdmin(editorial);
    }

    @Override
    public void deleteEditorial(
            Long problemId
    ) {

        getRequiredProblem(problemId);

        ProblemSolution editorial =
                solutions
                        .findFirstByProblemIdAndOfficialTrueAndActiveTrueOrderByUpdatedAtDesc(
                                problemId
                        )
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Editorial not found."
                                )
                        );

        editorial.setOfficial(false);
        editorial.setActive(false);

        solutions.saveAndFlush(editorial);
    }

    private void applyUpdate(
            ProblemSolution solution,
            Update request
    ) {

        solution.setProgrammingLanguage(
                request.language()
        );

        solution.setApproach(
                request.approach()
        );

        solution.setTitle(
                normalizeRequired(
                        request.title(),
                        "Solution title is required."
                )
        );

        solution.setExplanation(
                normalizeRequired(
                        request.explanation(),
                        "Solution explanation is required."
                )
        );

        solution.setSourceCode(
                normalizeRequiredCode(
                        request.sourceCode(),
                        "Solution source code is required."
                )
        );

        solution.setTimeComplexity(
                normalizeRequired(
                        request.timeComplexity(),
                        "Time complexity is required."
                )
        );

        solution.setSpaceComplexity(
                normalizeRequired(
                        request.spaceComplexity(),
                        "Space complexity is required."
                )
        );

        solution.setOfficial(
                Boolean.TRUE.equals(
                        request.official()
                )
        );

        if (request.active() != null) {
            solution.setActive(request.active());
        }
    }

    private void clearExistingOfficialSolution(
            Long problemId
    ) {

        solutions
                .findFirstByProblemIdAndOfficialTrueAndActiveTrueOrderByUpdatedAtDesc(
                        problemId
                )
                .ifPresent(
                        existing -> {
                            existing.setOfficial(false);
                            solutions.save(existing);
                        }
                );
    }

    private void ensureUniqueTitle(
            Long problemId,
            careerpilot_parent.coding.enums.ProgrammingLanguage language,
            String title,
            Long ignoredSolutionId
    ) {

        String normalizedTitle =
                normalizeRequired(
                        title,
                        "Solution title is required."
                );

        boolean exists =
                ignoredSolutionId == null
                        ? solutions
                        .existsByProblemIdAndProgrammingLanguageAndTitleIgnoreCaseAndActiveTrue(
                                problemId,
                                language,
                                normalizedTitle
                        )
                        : solutions
                        .existsByProblemIdAndProgrammingLanguageAndTitleIgnoreCaseAndIdNotAndActiveTrue(
                                problemId,
                                language,
                                normalizedTitle,
                                ignoredSolutionId
                        );

        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An active solution with this title and language already exists."
            );
        }
    }

    private CodingProblem getRequiredProblem(
            Long problemId
    ) {

        if (problemId == null || problemId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A valid coding problem ID is required."
            );
        }

        return problems.findById(problemId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Coding problem not found."
                        )
                );
    }

    private ProblemSolution getRequiredSolution(
            Long problemId,
            Long solutionId
    ) {

        validateSolutionId(solutionId);

        return solutions
                .findByIdAndProblemId(
                        solutionId,
                        problemId
                )
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Solution not found."
                        )
                );
    }

    private ProblemSolution getRequiredActiveSolution(
            Long problemId,
            Long solutionId
    ) {

        validateSolutionId(solutionId);

        return solutions
                .findByIdAndProblemIdAndActiveTrue(
                        solutionId,
                        problemId
                )
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Active solution not found."
                        )
                );
    }

    private void validateSolutionId(
            Long solutionId
    ) {

        if (solutionId == null || solutionId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A valid solution ID is required."
            );
        }
    }

    private void validateCreate(Create request) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solution request is required."
            );
        }

        validateCommon(
                request.language(),
                request.approach(),
                request.title(),
                request.explanation(),
                request.sourceCode(),
                request.timeComplexity(),
                request.spaceComplexity()
        );
    }

    private void validateUpdate(Update request) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solution update request is required."
            );
        }

        validateCommon(
                request.language(),
                request.approach(),
                request.title(),
                request.explanation(),
                request.sourceCode(),
                request.timeComplexity(),
                request.spaceComplexity()
        );
    }

    private void validateEditorial(
            EditorialUpsert request
    ) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Editorial request is required."
            );
        }

        validateCommon(
                request.language(),
                request.approach(),
                request.title(),
                request.explanation(),
                request.sourceCode(),
                request.timeComplexity(),
                request.spaceComplexity()
        );
    }

    private void validateCommon(
            Object language,
            Object approach,
            String title,
            String explanation,
            String sourceCode,
            String timeComplexity,
            String spaceComplexity
    ) {

        if (language == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Programming language is required."
            );
        }

        if (approach == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solution approach is required."
            );
        }

        normalizeRequired(
                title,
                "Solution title is required."
        );

        normalizeRequired(
                explanation,
                "Solution explanation is required."
        );

        normalizeRequiredCode(
                sourceCode,
                "Solution source code is required."
        );

        normalizeRequired(
                timeComplexity,
                "Time complexity is required."
        );

        normalizeRequired(
                spaceComplexity,
                "Space complexity is required."
        );
    }

    private String normalizeRequired(
            String value,
            String message
    ) {

        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    message
            );
        }

        return value.strip();
    }

    private String normalizeRequiredCode(
            String value,
            String message
    ) {

        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    message
            );
        }

        return value;
    }
}