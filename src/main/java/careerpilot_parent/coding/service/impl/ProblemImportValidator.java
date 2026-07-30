package careerpilot_parent.coding.service.impl;

import careerpilot_parent.coding.dto.request.ProblemImportRequests;
import careerpilot_parent.coding.dto.response.ProblemImportResponses;
import careerpilot_parent.coding.enums.ProblemImportIssueSeverity;
import careerpilot_parent.coding.repository.CodingProblemRepository;
import careerpilot_parent.coding.repository.ProblemTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ProblemImportValidator {

    private static final Pattern NON_ALPHANUMERIC =
            Pattern.compile("[^a-z0-9]+");

    private static final Pattern EDGE_HYPHENS =
            Pattern.compile("(^-+|-+$)");

    private final CodingProblemRepository codingProblemRepository;
    private final ProblemTagRepository problemTagRepository;

    public ProblemImportResponses.ValidationResult validate(
            ProblemImportRequests.ImportProblems request
    ) {

        List<ProblemImportResponses.ProblemValidation> validations =
                new ArrayList<>();

        List<ProblemImportResponses.ValidationIssue> globalIssues =
                new ArrayList<>();

        Set<String> requestSlugs = new HashSet<>();

        int validCount = 0;
        int invalidCount = 0;
        int duplicateCount = 0;

        for (int index = 0; index < request.getProblems().size(); index++) {

            ProblemImportRequests.ImportProblem problem =
                    request.getProblems().get(index);

            String slug = resolveSlug(problem);

            List<ProblemImportResponses.ValidationIssue> issues =
                    validateProblem(index, problem, slug);

            boolean duplicateInsideRequest =
                    !requestSlugs.add(slug);

            if (duplicateInsideRequest) {

                duplicateCount++;

                issues.add(issue(
                        index,
                        problem.getTitle(),
                        "slug",
                        "DUPLICATE_SLUG_IN_REQUEST",
                        "Another problem in this import has the same slug: " + slug,
                        ProblemImportIssueSeverity.ERROR
                ));
            }

            boolean valid = issues.stream()
                    .noneMatch(issue ->
                            issue.getSeverity()
                                    == ProblemImportIssueSeverity.ERROR
                    );

            if (valid) {
                validCount++;
            } else {
                invalidCount++;
            }

            validations.add(
                    ProblemImportResponses.ProblemValidation.builder()
                            .problemIndex(index)
                            .title(problem.getTitle())
                            .generatedSlug(slug)
                            .valid(valid)
                            .issues(issues)
                            .build()
            );
        }

        if (Boolean.FALSE.equals(request.getContinueOnError())
                && invalidCount > 0) {

            globalIssues.add(
                    ProblemImportResponses.ValidationIssue.builder()
                            .field("continueOnError")
                            .code("IMPORT_WILL_BE_REJECTED")
                            .message(
                                    "The import contains invalid problems and "
                                            + "continueOnError is false."
                            )
                            .severity(ProblemImportIssueSeverity.ERROR)
                            .build()
            );
        }

        return ProblemImportResponses.ValidationResult.builder()
                .importReference(request.getImportReference())
                .valid(invalidCount == 0)
                .totalProblems(request.getProblems().size())
                .validProblems(validCount)
                .invalidProblems(invalidCount)
                .duplicateProblems(duplicateCount)
                .problems(validations)
                .issues(globalIssues)
                .validatedAt(LocalDateTime.now())
                .build();
    }

    public List<ProblemImportResponses.ValidationIssue> validateProblem(
            int index,
            ProblemImportRequests.ImportProblem problem,
            String slug
    ) {

        List<ProblemImportResponses.ValidationIssue> issues =
                new ArrayList<>();

        if (slug == null || slug.isBlank()) {

            issues.add(issue(
                    index,
                    problem.getTitle(),
                    "slug",
                    "INVALID_SLUG",
                    "A valid slug could not be generated.",
                    ProblemImportIssueSeverity.ERROR
            ));

            return issues;
        }

        if (codingProblemRepository.existsBySlug(slug)) {

            issues.add(issue(
                    index,
                    problem.getTitle(),
                    "slug",
                    "SLUG_ALREADY_EXISTS",
                    "A coding problem already exists with slug: " + slug,
                    ProblemImportIssueSeverity.ERROR
            ));
        }

        if (problem.getTagIds() != null
                && !problem.getTagIds().isEmpty()) {

            Set<Long> requestedTagIds =
                    new HashSet<>(problem.getTagIds());

            long availableTags =
                    problemTagRepository.findAllById(requestedTagIds)
                            .size();

            if (availableTags != requestedTagIds.size()) {

                issues.add(issue(
                        index,
                        problem.getTitle(),
                        "tagIds",
                        "TAG_NOT_FOUND",
                        "One or more tag IDs do not exist.",
                        ProblemImportIssueSeverity.ERROR
                ));
            }
        }

        if (Boolean.TRUE.equals(problem.getFunctionBased())
                && (problem.getFunctionName() == null
                || problem.getFunctionName().isBlank())) {

            issues.add(issue(
                    index,
                    problem.getTitle(),
                    "functionName",
                    "FUNCTION_NAME_REQUIRED",
                    "Function name is required for a function-based problem.",
                    ProblemImportIssueSeverity.ERROR
            ));
        }

        if (problem.getStarterCodes() != null) {

            Set<Object> languages = new HashSet<>();

            problem.getStarterCodes().forEach(starterCode -> {

                if (!languages.add(starterCode.getLanguage())) {

                    issues.add(issue(
                            index,
                            problem.getTitle(),
                            "starterCodes",
                            "DUPLICATE_STARTER_LANGUAGE",
                            "Duplicate starter code language: "
                                    + starterCode.getLanguage(),
                            ProblemImportIssueSeverity.ERROR
                    ));
                }
            });
        }

        if (problem.getTestCases() != null) {

            Set<Integer> displayOrders = new HashSet<>();

            int totalWeight = 0;

            for (ProblemImportRequests.ImportTestCase testCase
                    : problem.getTestCases()) {

                if (!displayOrders.add(testCase.getDisplayOrder())) {

                    issues.add(issue(
                            index,
                            problem.getTitle(),
                            "testCases.displayOrder",
                            "DUPLICATE_DISPLAY_ORDER",
                            "Duplicate test-case display order: "
                                    + testCase.getDisplayOrder(),
                            ProblemImportIssueSeverity.ERROR
                    ));
                }

                if (testCase.getScoreWeight() != null) {
                    totalWeight += testCase.getScoreWeight();
                }
            }

            if (totalWeight != 100) {

                issues.add(issue(
                        index,
                        problem.getTitle(),
                        "testCases.scoreWeight",
                        "INVALID_SCORE_WEIGHT",
                        "Total test-case score weight must be exactly 100. "
                                + "Current total: " + totalWeight,
                        ProblemImportIssueSeverity.ERROR
                ));
            }
        }

        return issues;
    }

    public String resolveSlug(
            ProblemImportRequests.ImportProblem problem
    ) {

        if (problem.getSlug() != null
                && !problem.getSlug().isBlank()) {

            return normalizeSlug(problem.getSlug());
        }

        return normalizeSlug(problem.getTitle());
    }

    public String normalizeSlug(String value) {

        if (value == null || value.isBlank()) {
            return "";
        }

        String normalized = Normalizer
                .normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();

        normalized = NON_ALPHANUMERIC
                .matcher(normalized)
                .replaceAll("-");

        return EDGE_HYPHENS
                .matcher(normalized)
                .replaceAll("");
    }

    private ProblemImportResponses.ValidationIssue issue(
            Integer index,
            String title,
            String field,
            String code,
            String message,
            ProblemImportIssueSeverity severity
    ) {

        return ProblemImportResponses.ValidationIssue.builder()
                .problemIndex(index)
                .problemTitle(title)
                .field(field)
                .code(code)
                .message(message)
                .severity(severity)
                .build();
    }
}