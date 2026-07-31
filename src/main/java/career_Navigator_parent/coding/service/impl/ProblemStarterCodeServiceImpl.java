package career_Navigator_parent.coding.service.impl;

import career_Navigator_parent.coding.dto.request.ProblemRequests.Starter;
import career_Navigator_parent.coding.dto.response.CodingResponses.AdminStarter;
import career_Navigator_parent.coding.entity.CodingProblem;
import career_Navigator_parent.coding.entity.ProblemStarterCode;
import career_Navigator_parent.coding.enums.ProgrammingLanguage;
import career_Navigator_parent.coding.mapper.CodingProblemMapper;
import career_Navigator_parent.coding.repository.CodingProblemRepository;
import career_Navigator_parent.coding.repository.ProblemStarterCodeRepository;
import career_Navigator_parent.coding.service.ProblemStarterCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProblemStarterCodeServiceImpl
        implements ProblemStarterCodeService {

    private final CodingProblemRepository problems;
    private final ProblemStarterCodeRepository starterCodes;
    private final CodingProblemMapper mapper;

    @Override
    public AdminStarter create(
            Long problemId,
            Starter request
    ) {

        CodingProblem problem =
                getRequiredProblem(problemId);

        validateRequest(request);

        ProblemStarterCode existing =
                starterCodes
                        .findByProblemIdAndProgrammingLanguage(
                                problemId,
                                request.language()
                        )
                        .orElse(null);

        /*
         * Restore an existing soft-deleted language instead of inserting
         * another row and violating the unique problem/language constraint.
         */
        if (existing != null) {

            if (Boolean.TRUE.equals(existing.getActive())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Active starter code already exists for language: "
                                + request.language()
                );
            }

            applyRequest(
                    existing,
                    problem,
                    request
            );

            existing.setActive(true);

            return mapper.adminStarter(
                    starterCodes.save(existing)
            );
        }

        ProblemStarterCode starterCode =
                ProblemStarterCode.builder()
                        .problem(problem)
                        .programmingLanguage(
                                request.language()
                        )
                        .starterCode(
                                normalizeRequiredText(
                                        request.starterCode(),
                                        "Starter code is required."
                                )
                        )
                        .driverCode(
                                normalizeNullableCode(
                                        request.driverCode()
                                )
                        )
                        .methodSignature(
                                normalizeNullableText(
                                        request.methodSignature()
                                )
                        )
                        .active(true)
                        .build();

        try {
            ProblemStarterCode saved =
                    starterCodes.saveAndFlush(
                            starterCode
                    );

            return mapper.adminStarter(saved);

        } catch (DataIntegrityViolationException exception) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Starter code already exists for language: "
                            + request.language()
            );
        }
    }

    @Override
    public AdminStarter update(
            Long problemId,
            ProgrammingLanguage language,
            Starter request
    ) {

        getRequiredProblem(problemId);
        validateLanguage(language);
        validateRequest(request);

        if (request.language() != language) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Request language must match path language."
            );
        }

        ProblemStarterCode starterCode =
                getRequiredStarterCode(
                        problemId,
                        language
                );

        applyRequest(
                starterCode,
                starterCode.getProblem(),
                request
        );

        starterCode.setActive(true);

        return mapper.adminStarter(
                starterCodes.save(starterCode)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AdminStarter get(
            Long problemId,
            ProgrammingLanguage language
    ) {

        getRequiredProblem(problemId);
        validateLanguage(language);

        return mapper.adminStarter(
                getRequiredStarterCode(
                        problemId,
                        language
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminStarter> list(
            Long problemId,
            Boolean includeInactive
    ) {

        getRequiredProblem(problemId);

        List<ProblemStarterCode> result;

        if (Boolean.TRUE.equals(includeInactive)) {

            result =
                    starterCodes
                            .findAllByProblemIdOrderByProgrammingLanguageAsc(
                                    problemId
                            );

        } else {

            result =
                    starterCodes
                            .findAllByProblemIdAndActiveTrueOrderByProgrammingLanguageAsc(
                                    problemId
                            );
        }

        return result.stream()
                .map(mapper::adminStarter)
                .toList();
    }

    @Override
    public void delete(
            Long problemId,
            ProgrammingLanguage language
    ) {

        getRequiredProblem(problemId);
        validateLanguage(language);

        ProblemStarterCode starterCode =
                getRequiredStarterCode(
                        problemId,
                        language
                );

        /*
         * Soft delete is safer because submissions and audit records may
         * still reference the language configuration historically.
         */
        starterCode.setActive(false);

        starterCodes.save(starterCode);
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

    private ProblemStarterCode getRequiredStarterCode(
            Long problemId,
            ProgrammingLanguage language
    ) {

        return starterCodes
                .findByProblemIdAndProgrammingLanguage(
                        problemId,
                        language
                )
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Starter code not found for language: "
                                        + language
                        )
                );
    }

    private void validateRequest(Starter request) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Starter-code request is required."
            );
        }

        validateLanguage(request.language());

        normalizeRequiredText(
                request.starterCode(),
                "Starter code is required."
        );
    }

    private void validateLanguage(
            ProgrammingLanguage language
    ) {

        if (language == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Programming language is required."
            );
        }
    }

    private void applyRequest(
            ProblemStarterCode starterCode,
            CodingProblem problem,
            Starter request
    ) {

        starterCode.setProblem(problem);

        starterCode.setProgrammingLanguage(
                request.language()
        );

        starterCode.setStarterCode(
                normalizeRequiredText(
                        request.starterCode(),
                        "Starter code is required."
                )
        );

        starterCode.setDriverCode(
                normalizeNullableCode(
                        request.driverCode()
                )
        );

        starterCode.setMethodSignature(
                normalizeNullableText(
                        request.methodSignature()
                )
        );
    }

    private String normalizeRequiredText(
            String value,
            String message
    ) {

        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    message
            );
        }

        return value.trim();
    }

    private String normalizeNullableText(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }

    /*
     * Code indentation and line breaks are meaningful, so do not trim
     * every line or collapse spaces.
     */
    private String normalizeNullableCode(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value;
    }
}