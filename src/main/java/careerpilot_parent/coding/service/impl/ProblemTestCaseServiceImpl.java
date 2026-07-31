package careerpilot_parent.coding.service.impl;

import careerpilot_parent.coding.dto.request.ProblemTestCaseRequests.BatchCreate;
import careerpilot_parent.coding.dto.request.ProblemTestCaseRequests.BulkCreate;
import careerpilot_parent.coding.dto.request.ProblemTestCaseRequests.BulkDelete;
import careerpilot_parent.coding.dto.request.ProblemTestCaseRequests.Create;
import careerpilot_parent.coding.dto.request.ProblemTestCaseRequests.Import;
import careerpilot_parent.coding.dto.request.ProblemTestCaseRequests.ImportRow;
import careerpilot_parent.coding.dto.request.ProblemTestCaseRequests.Update;
import careerpilot_parent.coding.dto.response.ProblemTestCaseResponses.AdminTestCase;
import careerpilot_parent.coding.dto.response.ProblemTestCaseResponses.BatchResult;
import careerpilot_parent.coding.dto.response.ProblemTestCaseResponses.BulkError;
import careerpilot_parent.coding.dto.response.ProblemTestCaseResponses.BulkResult;
import careerpilot_parent.coding.dto.response.ProblemTestCaseResponses.DeleteResult;
import careerpilot_parent.coding.dto.response.ProblemTestCaseResponses.ImportResult;
import careerpilot_parent.coding.dto.response.ProblemTestCaseResponses.Summary;
import careerpilot_parent.coding.entity.CodingProblem;
import careerpilot_parent.coding.entity.ProblemTestCase;
import careerpilot_parent.coding.enums.TestCaseVisibility;
import careerpilot_parent.coding.mapper.ProblemTestCaseMapper;
import careerpilot_parent.coding.repository.CodingProblemRepository;
import careerpilot_parent.coding.repository.ProblemTestCaseRepository;
import careerpilot_parent.coding.service.ProblemTestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ProblemTestCaseServiceImpl
        implements ProblemTestCaseService {

    private static final int MAX_BATCH_SIZE = 500;
    private static final int DATABASE_BATCH_SIZE = 250;

    private final CodingProblemRepository problems;
    private final ProblemTestCaseRepository testCases;
    private final ProblemTestCaseMapper mapper;

    @Override
    public AdminTestCase create(
            Long problemId,
            Create request
    ) {

        CodingProblem problem =
                getRequiredProblem(problemId);

        validateCreateRequest(request);

        ensureDisplayOrderAvailable(
                problemId,
                request.displayOrder(),
                null
        );

        String input =
                normalizeRequiredText(
                        request.input(),
                        "Test-case input is required."
                );

        String expectedOutput =
                normalizeRequiredText(
                        request.expectedOutput(),
                        "Expected output is required."
                );

        String inputHash =
                sha256(input);

        ensureInputIsNotDuplicated(
                problemId,
                inputHash
        );

        ProblemTestCase testCase =
                buildTestCase(
                        problem,
                        input,
                        expectedOutput,
                        request.visibility(),
                        request.displayOrder(),
                        request.scoreWeight(),
                        request.customTimeLimitSeconds(),
                        request.customMemoryLimitMegabytes(),
                        false,
                        null
                );

        return mapper.toAdminResponse(
                testCases.saveAndFlush(testCase)
        );
    }

    @Override
    public BatchResult createBatch(
            Long problemId,
            BatchCreate request
    ) {

        CodingProblem problem =
                getRequiredProblem(problemId);

        validateBatchRequest(request);

        String batchReference =
                normalizeRequiredText(
                        request.batchReference(),
                        "Batch reference is required."
                );

        List<ProblemTestCase> entities =
                prepareBatchEntities(
                        problem,
                        request.testCases()
                );

        persistInDatabaseBatches(entities);

        int sampleCount =
                countVisibility(
                        entities,
                        TestCaseVisibility.SAMPLE
                );

        int hiddenCount =
                countVisibility(
                        entities,
                        TestCaseVisibility.HIDDEN
                );

        return new BatchResult(
                problemId,
                batchReference,
                request.testCases().size(),
                entities.size(),
                0,
                sampleCount,
                hiddenCount,
                safeInt(
                        testCases.countByProblemIdAndActiveTrue(
                                problemId
                        )
                ),
                Collections.emptyList()
        );
    }

    @Override
    @Deprecated
    public BulkResult createBulk(
            Long problemId,
            BulkCreate request
    ) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Bulk-create request is required."
            );
        }

        BatchResult result =
                createBatch(
                        problemId,
                        request.toBatchCreate()
                );

        return new BulkResult(
                result.problemId(),
                result.batchReference(),
                result.requestedCount(),
                result.createdCount(),
                result.failedCount(),
                result.sampleCount(),
                result.hiddenCount(),
                result.totalProblemTestCaseCount(),
                result.errors()
        );
    }

    @Override
    public ImportResult importTestCases(
            Long problemId,
            Import request
    ) {
        CodingProblem problem = getRequiredProblem(problemId);
        validateImportRequest(request);

        List<ImportRow> importRows = request.rows();
        boolean replaceExisting = Boolean.TRUE.equals(request.replaceExisting());

        if (replaceExisting) {
            testCases.deactivateAllActiveByProblemId(problemId);
            testCases.flush();
        }

        int startingDisplayOrder = resolveStartingDisplayOrder(
                problemId,
                request.startingDisplayOrder(),
                replaceExisting
        );

        List<Create> createRequests = new ArrayList<>(importRows.size());
        int currentDisplayOrder = startingDisplayOrder;

        for (ImportRow row : importRows) {
            TestCaseVisibility visibility = row.visibility() == null
                    ? request.defaultVisibility()
                    : row.visibility();

            Double scoreWeight = row.scoreWeight() == null
                    ? request.defaultScoreWeight()
                    : row.scoreWeight();

            createRequests.add(new Create(
                    row.input(),
                    row.expectedOutput(),
                    visibility,
                    currentDisplayOrder,
                    scoreWeight,
                    row.customTimeLimitSeconds(),
                    row.customMemoryLimitMegabytes()
            ));

            currentDisplayOrder = Math.addExact(currentDisplayOrder, 1);
        }

        List<ProblemTestCase> entities = prepareBatchEntities(problem, createRequests);

        try {
            persistInDatabaseBatches(entities);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "The test-case import conflicted with existing display orders or duplicate inputs.",
                    exception
            );
        }

        int endingDisplayOrder = entities.isEmpty()
                ? startingDisplayOrder
                : Math.addExact(startingDisplayOrder, entities.size() - 1);

        return new ImportResult(
                problemId,
                normalizeRequiredText(request.importReference(), "Import reference is required."),
                importRows.size(),
                entities.size(),
                0,
                startingDisplayOrder,
                endingDisplayOrder,
                safeInt(testCases.countByProblemIdAndActiveTrue(problemId)),
                Collections.emptyList()
        );
    }

    @Override
    public AdminTestCase update(
            Long problemId,
            Long testCaseId,
            Update request
    ) {

        getRequiredProblem(problemId);

        ProblemTestCase testCase =
                getRequiredActiveTestCase(
                        problemId,
                        testCaseId
                );

        validateUpdateRequest(request);

        ensureDisplayOrderAvailable(
                problemId,
                request.displayOrder(),
                testCaseId
        );

        String input =
                normalizeRequiredText(
                        request.input(),
                        "Test-case input is required."
                );

        String expectedOutput =
                normalizeRequiredText(
                        request.expectedOutput(),
                        "Expected output is required."
                );

        String newInputHash =
                sha256(input);

        if (
                !Objects.equals(
                        testCase.getInputHash(),
                        newInputHash
                )
                        && testCases.existsByProblemIdAndInputHashAndActiveTrue(
                        problemId,
                        newInputHash
                )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An active test case with the same input already exists."
            );
        }

        testCase.setInput(input);
        testCase.setExpectedOutput(expectedOutput);
        testCase.setInputHash(newInputHash);
        testCase.setExpectedOutputHash(
                sha256(expectedOutput)
        );
        testCase.setVisibility(
                request.visibility()
        );
        testCase.setDisplayOrder(
                request.displayOrder()
        );
        testCase.setScoreWeight(
                request.scoreWeight()
        );
        testCase.setCustomTimeLimitSeconds(
                request.customTimeLimitSeconds()
        );
        testCase.setCustomMemoryLimitMegabytes(
                request.customMemoryLimitMegabytes()
        );

        if (request.active() != null) {
            testCase.setActive(
                    request.active()
            );
        }

        return mapper.toAdminResponse(
                testCases.saveAndFlush(testCase)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AdminTestCase get(
            Long problemId,
            Long testCaseId,
            Boolean includeInactive
    ) {

        getRequiredProblem(problemId);

        ProblemTestCase testCase =
                Boolean.TRUE.equals(includeInactive)
                        ? getRequiredTestCase(
                        problemId,
                        testCaseId
                )
                        : getRequiredActiveTestCase(
                        problemId,
                        testCaseId
                );

        return mapper.toAdminResponse(testCase);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminTestCase> list(
            Long problemId,
            TestCaseVisibility visibility,
            Boolean includeInactive,
            Pageable pageable
    ) {

        getRequiredProblem(problemId);

        Page<ProblemTestCase> result;

        if (visibility == null) {

            result =
                    Boolean.TRUE.equals(includeInactive)
                            ? testCases.findAllByProblemId(
                            problemId,
                            pageable
                    )
                            : testCases.findAllByProblemIdAndActiveTrue(
                            problemId,
                            pageable
                    );

        } else {

            result =
                    Boolean.TRUE.equals(includeInactive)
                            ? testCases.findAllByProblemIdAndVisibility(
                            problemId,
                            visibility,
                            pageable
                    )
                            : testCases
                            .findAllByProblemIdAndVisibilityAndActiveTrue(
                                    problemId,
                                    visibility,
                                    pageable
                            );
        }

        return result.map(
                mapper::toAdminResponse
        );
    }

    @Override
    public void delete(
            Long problemId,
            Long testCaseId
    ) {

        getRequiredProblem(problemId);

        ProblemTestCase testCase =
                getRequiredActiveTestCase(
                        problemId,
                        testCaseId
                );

        testCase.setActive(false);

        testCases.saveAndFlush(testCase);
    }

    @Override
    public DeleteResult deleteBulk(
            Long problemId,
            BulkDelete request
    ) {

        getRequiredProblem(problemId);

        if (
                request == null
                        || request.testCaseIds() == null
                        || request.testCaseIds().isEmpty()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one test-case ID is required."
            );
        }

        if (
                request.testCaseIds().size()
                        > MAX_BATCH_SIZE
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A maximum of 500 test cases can be deleted per request."
            );
        }

        List<ProblemTestCase> resolved =
                testCases
                        .findAllByIdInAndProblemIdAndActiveTrue(
                                request.testCaseIds(),
                                problemId
                        );

        Set<Long> resolvedIds =
                new HashSet<>();

        for (ProblemTestCase testCase : resolved) {
            resolvedIds.add(
                    testCase.getId()
            );
        }

        Set<Long> unresolvedIds =
                new TreeSet<>(
                        request.testCaseIds()
                );

        unresolvedIds.removeAll(resolvedIds);

        if (!unresolvedIds.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Test cases were not found, are inactive, or belong "
                            + "to another problem: "
                            + unresolvedIds
            );
        }

        resolved.forEach(
                testCase ->
                        testCase.setActive(false)
        );

        testCases.saveAll(resolved);
        testCases.flush();

        return new DeleteResult(
                problemId,
                request.testCaseIds().size(),
                resolved.size()
        );
    }

    @Override
    public AdminTestCase restore(
            Long problemId,
            Long testCaseId
    ) {

        getRequiredProblem(problemId);

        ProblemTestCase testCase =
                getRequiredTestCase(
                        problemId,
                        testCaseId
                );

        if (Boolean.TRUE.equals(testCase.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Test case is already active."
            );
        }

        if (
                testCases.existsByProblemIdAndInputHashAndActiveTrue(
                        problemId,
                        testCase.getInputHash()
                )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An active test case with the same input already exists."
            );
        }

        testCase.setActive(true);

        return mapper.toAdminResponse(
                testCases.saveAndFlush(testCase)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Summary summary(
            Long problemId
    ) {

        getRequiredProblem(problemId);

        Double totalScore =
                testCases.sumActiveScoreWeightByProblemId(
                        problemId
                );

        return new Summary(
                problemId,
                safeInt(
                        testCases.countByProblemId(
                                problemId
                        )
                ),
                safeInt(
                        testCases.countByProblemIdAndActiveTrue(
                                problemId
                        )
                ),
                safeInt(
                        testCases.countByProblemIdAndActiveFalse(
                                problemId
                        )
                ),
                safeInt(
                        testCases
                                .countByProblemIdAndVisibilityAndActiveTrue(
                                        problemId,
                                        TestCaseVisibility.SAMPLE
                                )
                ),
                safeInt(
                        testCases
                                .countByProblemIdAndVisibilityAndActiveTrue(
                                        problemId,
                                        TestCaseVisibility.HIDDEN
                                )
                ),
                totalScore == null
                        ? 0.0
                        : totalScore
        );
    }

    private List<ProblemTestCase> prepareBatchEntities(
            CodingProblem problem,
            List<Create> requests
    ) {

        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one test case is required."
            );
        }

        if (requests.size() > MAX_BATCH_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A maximum of 500 test cases can be created per request."
            );
        }

        Set<Integer> displayOrders =
                new HashSet<>();

        Set<String> inputHashes =
                new HashSet<>();

        List<ProblemTestCase> entities =
                new ArrayList<>(
                        requests.size()
                );

        for (
                int index = 0;
                index < requests.size();
                index++
        ) {

            Create request =
                    requests.get(index);

            validateCreateRequest(request);

            if (
                    !displayOrders.add(
                            request.displayOrder()
                    )
            ) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Duplicate display order in request: "
                                + request.displayOrder()
                );
            }

            ensureDisplayOrderAvailable(
                    problem.getId(),
                    request.displayOrder(),
                    null
            );

            String input =
                    normalizeRequiredText(
                            request.input(),
                            "Test-case input is required."
                    );

            String expectedOutput =
                    normalizeRequiredText(
                            request.expectedOutput(),
                            "Expected output is required."
                    );

            String inputHash =
                    sha256(input);

            if (!inputHashes.add(inputHash)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Duplicate test-case input at request index: "
                                + index
                );
            }

            ensureInputIsNotDuplicated(
                    problem.getId(),
                    inputHash
            );

            entities.add(
                    buildTestCase(
                            problem,
                            input,
                            expectedOutput,
                            request.visibility(),
                            request.displayOrder(),
                            request.scoreWeight(),
                            request.customTimeLimitSeconds(),
                            request.customMemoryLimitMegabytes(),
                            false,
                            null
                    )
            );
        }

        return entities;
    }

    private ProblemTestCase buildTestCase(
            CodingProblem problem,
            String input,
            String expectedOutput,
            TestCaseVisibility visibility,
            Integer displayOrder,
            Double scoreWeight,
            Double customTimeLimitSeconds,
            Integer customMemoryLimitMegabytes,
            Boolean generatedCase,
            Long generatorSeed
    ) {

        return ProblemTestCase.builder()
                .problem(problem)
                .input(input)
                .expectedOutput(expectedOutput)
                .visibility(visibility)
                .displayOrder(displayOrder)
                .scoreWeight(scoreWeight)
                .customTimeLimitSeconds(
                        customTimeLimitSeconds
                )
                .customMemoryLimitMegabytes(
                        customMemoryLimitMegabytes
                )
                .inputHash(
                        sha256(input)
                )
                .expectedOutputHash(
                        sha256(expectedOutput)
                )
                .generatedCase(
                        Boolean.TRUE.equals(
                                generatedCase
                        )
                )
                .generatorSeed(generatorSeed)
                .active(true)
                .build();
    }

    private void persistInDatabaseBatches(
            List<ProblemTestCase> entities
    ) {

        for (
                int start = 0;
                start < entities.size();
                start += DATABASE_BATCH_SIZE
        ) {

            int end =
                    Math.min(
                            start + DATABASE_BATCH_SIZE,
                            entities.size()
                    );

            testCases.saveAll(
                    entities.subList(
                            start,
                            end
                    )
            );

            testCases.flush();
        }
    }

    private int countVisibility(
            List<ProblemTestCase> entities,
            TestCaseVisibility visibility
    ) {

        return safeInt(
                entities.stream()
                        .filter(
                                testCase ->
                                        testCase.getVisibility()
                                                == visibility
                        )
                        .count()
        );
    }

    private void validateBatchRequest(
            BatchCreate request
    ) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Batch-create request is required."
            );
        }

        normalizeRequiredText(
                request.batchReference(),
                "Batch reference is required."
        );

        if (
                request.testCases() == null
                        || request.testCases().isEmpty()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one test case is required."
            );
        }
    }

    private void validateImportRequest(
            Import request
    ) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Test-case import request is required.");
        }

        normalizeRequiredText(request.importReference(), "Import reference is required.");

        if (request.defaultVisibility() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Default visibility is required.");
        }

        if (request.startingDisplayOrder() != null && request.startingDisplayOrder() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Starting display order must be positive.");
        }

        if (request.defaultScoreWeight() == null || request.defaultScoreWeight() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Default score weight must be positive.");
        }

        if (request.rows() == null || request.rows().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one import row is required.");
        }

        if (request.rows().size() > MAX_BATCH_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A maximum of 500 import rows is allowed.");
        }
    }

    private int resolveStartingDisplayOrder(
            Long problemId,
            Integer requestedStartingDisplayOrder,
            boolean replaceExisting
    ) {
        if (requestedStartingDisplayOrder != null) {
            return requestedStartingDisplayOrder;
        }

        if (replaceExisting) {
            return 1;
        }

        Integer maximumDisplayOrder =
                testCases.findMaximumActiveDisplayOrderByProblemId(problemId);

        int currentMaximum = maximumDisplayOrder == null ? 0 : maximumDisplayOrder;

        try {
            return Math.addExact(currentMaximum, 1);
        } catch (ArithmeticException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Unable to allocate the next test-case display order.",
                    exception
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

    private ProblemTestCase getRequiredTestCase(
            Long problemId,
            Long testCaseId
    ) {

        validateTestCaseId(testCaseId);

        return testCases
                .findByIdAndProblemId(
                        testCaseId,
                        problemId
                )
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Test case not found."
                        )
                );
    }

    private ProblemTestCase getRequiredActiveTestCase(
            Long problemId,
            Long testCaseId
    ) {

        validateTestCaseId(testCaseId);

        return testCases
                .findByIdAndProblemIdAndActiveTrue(
                        testCaseId,
                        problemId
                )
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Active test case not found."
                        )
                );
    }

    private void validateTestCaseId(
            Long testCaseId
    ) {

        if (testCaseId == null || testCaseId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A valid test-case ID is required."
            );
        }
    }

    private void ensureDisplayOrderAvailable(
            Long problemId,
            Integer displayOrder,
            Long ignoredTestCaseId
    ) {

        boolean exists =
                ignoredTestCaseId == null
                        ? testCases.existsByProblemIdAndDisplayOrder(
                        problemId,
                        displayOrder
                )
                        : testCases.existsByProblemIdAndDisplayOrderAndIdNot(
                        problemId,
                        displayOrder,
                        ignoredTestCaseId
                );

        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A test case already uses display order: "
                            + displayOrder
            );
        }
    }

    private void ensureInputIsNotDuplicated(
            Long problemId,
            String inputHash
    ) {

        if (
                testCases.existsByProblemIdAndInputHashAndActiveTrue(
                        problemId,
                        inputHash
                )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An active test case with the same input already exists."
            );
        }
    }

    private void validateCreateRequest(
            Create request
    ) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Test-case request is required."
            );
        }

        normalizeRequiredText(
                request.input(),
                "Test-case input is required."
        );

        normalizeRequiredText(
                request.expectedOutput(),
                "Expected output is required."
        );

        if (request.visibility() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Test-case visibility is required."
            );
        }

        if (
                request.displayOrder() == null
                        || request.displayOrder() < 1
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Display order must be at least 1."
            );
        }

        if (
                request.scoreWeight() == null
                        || request.scoreWeight() <= 0
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Score weight must be greater than zero."
            );
        }

        if (
                request.customTimeLimitSeconds() != null
                        && request.customTimeLimitSeconds() <= 0
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Custom time limit must be greater than zero."
            );
        }

        if (
                request.customMemoryLimitMegabytes() != null
                        && request.customMemoryLimitMegabytes() <= 0
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Custom memory limit must be greater than zero."
            );
        }
    }

    private void validateUpdateRequest(
            Update request
    ) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Test-case update request is required."
            );
        }

        validateCreateRequest(
                new Create(
                        request.input(),
                        request.expectedOutput(),
                        request.visibility(),
                        request.displayOrder(),
                        request.scoreWeight(),
                        request.customTimeLimitSeconds(),
                        request.customMemoryLimitMegabytes()
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

        return value.strip();
    }

    private String sha256(
            String value
    ) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] encoded =
                    digest.digest(
                            value.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return HexFormat
                    .of()
                    .formatHex(encoded);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 hashing is not available.",
                    exception
            );
        }
    }

    private int safeInt(
            long value
    ) {

        if (value > Integer.MAX_VALUE) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Value exceeds the supported response range."
            );
        }

        return (int) value;
    }
}