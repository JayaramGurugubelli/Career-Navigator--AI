package careerpilot_parent.coding.service.impl;

import careerpilot_parent.coding.dto.request.ProblemRequests;
import careerpilot_parent.coding.dto.request.ProblemRequests.Create;
import careerpilot_parent.coding.dto.request.ProblemRequests.Starter;
import careerpilot_parent.coding.dto.request.ProblemRequests.Status;
import careerpilot_parent.coding.dto.request.ProblemRequests.TestCase;
import careerpilot_parent.coding.dto.request.ProblemRequests.Update;
import careerpilot_parent.coding.dto.response.CodingResponses;
import careerpilot_parent.coding.dto.response.CodingResponses.Admin;
import careerpilot_parent.coding.entity.CodingProblem;
import careerpilot_parent.coding.entity.ProblemStarterCode;
import careerpilot_parent.coding.entity.ProblemTag;
import careerpilot_parent.coding.entity.ProblemTestCase;
import careerpilot_parent.coding.enums.ProblemStatus;
import careerpilot_parent.coding.enums.ProgrammingLanguage;
import careerpilot_parent.coding.mapper.CodingProblemMapper;
import careerpilot_parent.coding.repository.CodingProblemRepository;
import careerpilot_parent.coding.repository.ProblemTagRepository;
import careerpilot_parent.coding.service.ProblemManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProblemManagementServiceImpl
        implements ProblemManagementService {

 private final CodingProblemRepository problems;
 private final ProblemTagRepository tags;
 private final CodingProblemMapper mapper;

 @Override
 public Admin create(Create request) {

  validateTestCases(request.testCases());
  validateStarterCodes(request.starterCodes());

  CodingProblem problem = CodingProblem.builder()
          .title(normalizeRequiredText(
                  request.title(),
                  "Problem title is required."
          ))
          .slug(generateUniqueSlug(request.title()))
          .description(
                  normalizeRequiredText(
                          request.description(),
                          "Problem description is required."
                  )
          )
          .inputFormat(
                  normalizeNullableText(
                          request.inputFormat()
                  )
          )
          .outputFormat(
                  normalizeNullableText(
                          request.outputFormat()
                  )
          )
          .constraints(
                  normalizeNullableText(
                          request.constraints()
                  )
          )
          .explanation(
                  normalizeNullableText(
                          request.explanation()
                  )
          )
          .difficulty(request.difficulty())
          .timeLimitMilliseconds(
                  request.timeLimitMilliseconds()
          )
          .memoryLimitMegabytes(
                  request.memoryLimitMegabytes()
          )
          .functionBased(
                  Boolean.TRUE.equals(
                          request.functionBased()
                  )
          )
          .functionName(
                  normalizeNullableText(
                          request.functionName()
                  )
          )
          .expectedComplexity(
                  normalizeNullableText(
                          request.expectedComplexity()
                  )
          )
          .premium(
                  Boolean.TRUE.equals(
                          request.premium()
                  )
          )
          .status(ProblemStatus.DRAFT)
          .active(true)
          .totalSubmissions(0L)
          .acceptedSubmissions(0L)
          .build();

  Set<ProblemTag> resolvedTags =
          resolveTags(request.tagIds());

  problem.getTags().addAll(resolvedTags);

  addChildren(
          problem,
          request.testCases(),
          request.starterCodes()
  );

  CodingProblem savedProblem =
          problems.save(problem);

  return mapper.admin(savedProblem);
 }

 @Override
 public Admin update(
         Long problemId,
         Update request
 ) {

  validateTestCases(request.testCases());
  validateStarterCodes(request.starterCodes());

  CodingProblem problem =
          getRequiredProblem(problemId);

  problem.setTitle(
          normalizeRequiredText(
                  request.title(),
                  "Problem title is required."
          )
  );

  problem.setDescription(
          normalizeRequiredText(
                  request.description(),
                  "Problem description is required."
          )
  );

  problem.setInputFormat(
          normalizeNullableText(
                  request.inputFormat()
          )
  );

  problem.setOutputFormat(
          normalizeNullableText(
                  request.outputFormat()
          )
  );

  problem.setConstraints(
          normalizeNullableText(
                  request.constraints()
          )
  );

  problem.setExplanation(
          normalizeNullableText(
                  request.explanation()
          )
  );

  problem.setDifficulty(
          request.difficulty()
  );

  problem.setTimeLimitMilliseconds(
          request.timeLimitMilliseconds()
  );

  problem.setMemoryLimitMegabytes(
          request.memoryLimitMegabytes()
  );

  problem.setFunctionBased(
          Boolean.TRUE.equals(
                  request.functionBased()
          )
  );

  problem.setFunctionName(
          normalizeNullableText(
                  request.functionName()
          )
  );

  problem.setExpectedComplexity(
          normalizeNullableText(
                  request.expectedComplexity()
          )
  );

  problem.setPremium(
          Boolean.TRUE.equals(
                  request.premium()
          )
  );

  if (request.active() != null) {
   problem.setActive(request.active());
  }

  Set<ProblemTag> resolvedTags =
          resolveTags(request.tagIds());

  problem.getTags().clear();
  problem.getTags().addAll(resolvedTags);

  updateTestCases(
          problem,
          request.testCases()
  );

  updateStarterCodes(
          problem,
          request.starterCodes()
  );

  CodingProblem savedProblem =
          problems.save(problem);

  return mapper.admin(savedProblem);
 }

 @Override
 public Admin status(
         Long problemId,
         Status request
 ) {

  CodingProblem problem =
          getRequiredProblem(problemId);

  validateStatusTransition(
          problem,
          request.status()
  );

  problem.setStatus(request.status());

  if (
          request.status()
                  == ProblemStatus.ARCHIVED
  ) {
   problem.setActive(false);
  }

  if (
          request.status()
                  == ProblemStatus.PUBLISHED
  ) {
   problem.setActive(true);
  }

  return mapper.admin(
          problems.save(problem)
  );
 }

 @Override
 @Transactional(readOnly = true)
 public Admin get(Long problemId) {

  return mapper.admin(
          getRequiredProblem(problemId)
  );
 }

 @Override
 @Transactional(readOnly = true)
 public Page<Admin> list(Pageable pageable) {

  return problems.findAll(pageable)
          .map(mapper::admin);
 }

 @Override
 public void delete(Long problemId) {

  CodingProblem problem =
          getRequiredProblem(problemId);

  problem.setActive(false);
  problem.setStatus(
          ProblemStatus.ARCHIVED
  );

  problems.save(problem);
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
                  () ->
                          new ResponseStatusException(
                                  HttpStatus.NOT_FOUND,
                                  "Coding problem not found."
                          )
          );
 }

 private Set<ProblemTag> resolveTags(
         Set<Long> requestedTagIds
 ) {

  if (
          requestedTagIds == null
                  || requestedTagIds.isEmpty()
  ) {
   return new HashSet<>();
  }

  if (requestedTagIds.contains(null)) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Tag IDs cannot contain null values."
   );
  }

  Set<Long> normalizedIds =
          requestedTagIds.stream()
                  .filter(Objects::nonNull)
                  .collect(Collectors.toSet());

  Set<Long> invalidNumberIds =
          normalizedIds.stream()
                  .filter(id -> id <= 0)
                  .collect(Collectors.toSet());

  if (!invalidNumberIds.isEmpty()) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Invalid tag IDs: "
                   + invalidNumberIds
   );
  }

  List<ProblemTag> resolvedTags =
          tags.findAllByIdIn(normalizedIds);

  Map<Long, ProblemTag> resolvedById =
          resolvedTags.stream()
                  .collect(
                          Collectors.toMap(
                                  ProblemTag::getId,
                                  Function.identity()
                          )
                  );

  Set<Long> missingIds =
          new TreeSet<>(normalizedIds);

  missingIds.removeAll(
          resolvedById.keySet()
  );

  if (!missingIds.isEmpty()) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Coding problem tag IDs do not exist: "
                   + missingIds
                   + ". Create the tags first using "
                   + "POST /api/admin/coding/tags."
   );
  }

  Set<Long> inactiveIds =
          resolvedTags.stream()
                  .filter(
                          tag ->
                                  !Boolean.TRUE.equals(
                                          tag.getActive()
                                  )
                  )
                  .map(ProblemTag::getId)
                  .collect(
                          Collectors.toCollection(
                                  TreeSet::new
                          )
                  );

  if (!inactiveIds.isEmpty()) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Inactive coding problem tag IDs cannot be used: "
                   + inactiveIds
   );
  }

  return new HashSet<>(resolvedTags);
 }

 private void updateTestCases(
         CodingProblem problem,
         List<TestCase> requests
 ) {

  Map<Integer, ProblemTestCase>
          existingByDisplayOrder =
          problem.getTestCases()
                  .stream()
                  .collect(
                          Collectors.toMap(
                                  ProblemTestCase::getDisplayOrder,
                                  Function.identity()
                          )
                  );

  Set<Integer> requestedOrders =
          requests.stream()
                  .map(TestCase::displayOrder)
                  .collect(Collectors.toSet());

  problem.getTestCases().removeIf(
          existing ->
                  !requestedOrders.contains(
                          existing.getDisplayOrder()
                  )
  );

  for (TestCase request : requests) {

   ProblemTestCase testCase =
           existingByDisplayOrder.get(
                   request.displayOrder()
           );

   if (testCase == null) {
    testCase =
            ProblemTestCase.builder()
                    .problem(problem)
                    .active(true)
                    .build();

    problem.getTestCases().add(
            testCase
    );
   }

   applyTestCase(
           testCase,
           problem,
           request
   );
  }
 }

 private void updateStarterCodes(
         CodingProblem problem,
         List<Starter> requests
 ) {

  Map<ProgrammingLanguage, ProblemStarterCode>
          existingByLanguage =
          problem.getStarterCodes()
                  .stream()
                  .collect(
                          Collectors.toMap(
                                  ProblemStarterCode
                                          ::getProgrammingLanguage,
                                  Function.identity()
                          )
                  );

  Set<ProgrammingLanguage>
          requestedLanguages =
          requests.stream()
                  .map(Starter::language)
                  .collect(Collectors.toSet());

  problem.getStarterCodes()
          .removeIf(
                  existing ->
                          !requestedLanguages.contains(
                                  existing
                                          .getProgrammingLanguage()
                          )
          );

  for (Starter request : requests) {

   ProblemStarterCode starterCode =
           existingByLanguage.get(
                   request.language()
           );

   if (starterCode == null) {
    starterCode =
            ProblemStarterCode.builder()
                    .problem(problem)
                    .programmingLanguage(
                            request.language()
                    )
                    .active(true)
                    .build();

    problem.getStarterCodes()
            .add(starterCode);
   }

   applyStarterCode(
           starterCode,
           problem,
           request
   );
  }
 }

 private void addChildren(
         CodingProblem problem,
         List<TestCase> testCases,
         List<Starter> starterCodes
 ) {

  for (TestCase request : testCases) {

   ProblemTestCase testCase =
           ProblemTestCase.builder()
                   .problem(problem)
                   .active(true)
                   .build();

   applyTestCase(
           testCase,
           problem,
           request
   );

   problem.getTestCases().add(
           testCase
   );
  }

  for (Starter request : starterCodes) {

   ProblemStarterCode starterCode =
           ProblemStarterCode.builder()
                   .problem(problem)
                   .active(true)
                   .build();

   applyStarterCode(
           starterCode,
           problem,
           request
   );

   problem.getStarterCodes()
           .add(starterCode);
  }
 }

 private void applyTestCase(
         ProblemTestCase testCase,
         CodingProblem problem,
         TestCase request
 ) {

  testCase.setProblem(problem);

  testCase.setInput(
          normalizeRequiredText(
                  request.input(),
                  "Test case input is required."
          )
  );

  testCase.setExpectedOutput(
          normalizeRequiredText(
                  request.expectedOutput(),
                  "Expected output is required."
          )
  );

  testCase.setVisibility(
          request.visibility()
  );

  testCase.setDisplayOrder(
          request.displayOrder()
  );

  testCase.setScoreWeight(Double.valueOf(request.scoreWeight()));

  testCase.setCustomTimeLimitSeconds(
          request.customTimeLimitSeconds()
  );

  testCase.setCustomMemoryLimitMegabytes(
          request.customMemoryLimitMegabytes()
  );

  testCase.setActive(true);
 }

 private void applyStarterCode(
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
          request.driverCode()
  );

  starterCode.setMethodSignature(
          normalizeNullableText(
                  request.methodSignature()
          )
  );

  starterCode.setActive(true);
 }

 private void validateTestCases(
         List<TestCase> testCases
 ) {

  if (
          testCases == null
                  || testCases.isEmpty()
  ) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "At least one test case is required."
   );
  }

  Set<Integer> displayOrders =
          new HashSet<>();

  int totalScore = 0;

  for (TestCase testCase : testCases) {

   if (testCase.displayOrder() == null) {
    throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Test-case display order is required."
    );
   }

   if (
           !displayOrders.add(
                   testCase.displayOrder()
           )
   ) {
    throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Duplicate test-case display order: "
                    + testCase.displayOrder()
    );
   }

   if (
           testCase.scoreWeight() == null
                   || testCase.scoreWeight() < 1
   ) {
    throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Every test-case score weight must be at least 1."
    );
   }

   totalScore += testCase.scoreWeight();
  }

  if (totalScore <= 0) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Total test-case score must be greater than zero."
   );
  }
 }

 private void validateStarterCodes(
         List<Starter> starterCodes
 ) {

  if (
          starterCodes == null
                  || starterCodes.isEmpty()
  ) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "At least one starter code is required."
   );
  }

  Set<ProgrammingLanguage> languages =
          new HashSet<>();

  for (Starter starter : starterCodes) {

   if (starter.language() == null) {
    throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Programming language is required."
    );
   }

   if (!languages.add(starter.language())) {
    throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Duplicate starter code for language: "
                    + starter.language()
    );
   }
  }
 }

 private void validateStatusTransition(
         CodingProblem problem,
         ProblemStatus requestedStatus
 ) {

  if (requestedStatus == null) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Problem status is required."
   );
  }

  if (
          requestedStatus
                  == ProblemStatus.PUBLISHED
  ) {

   if (
           !Boolean.TRUE.equals(
                   problem.getActive()
           )
   ) {
    throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Inactive problem cannot be published."
    );
   }

   boolean hasActiveTestCase =
           problem.getTestCases() != null
                   && problem.getTestCases()
                   .stream()
                   .anyMatch(
                           testCase ->
                                   Boolean.TRUE.equals(
                                           testCase.getActive()
                                   )
                   );

   if (!hasActiveTestCase) {
    throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "At least one active test case is required before publishing."
    );
   }

   boolean hasActiveStarterCode =
           problem.getStarterCodes() != null
                   && problem.getStarterCodes()
                   .stream()
                   .anyMatch(
                           starterCode ->
                                   Boolean.TRUE.equals(
                                           starterCode
                                                   .getActive()
                                   )
                   );

   if (!hasActiveStarterCode) {
    throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "At least one active starter code is required before publishing."
    );
   }
  }
 }

 private String generateUniqueSlug(
         String title
 ) {

  String baseSlug =
          normalizeRequiredText(
                  title,
                  "Problem title is required."
          )
                  .toLowerCase(Locale.ROOT)
                  .replaceAll(
                          "[^a-z0-9]+",
                          "-"
                  )
                  .replaceAll(
                          "(^-|-$)",
                          ""
                  );

  if (baseSlug.isBlank()) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Problem title cannot generate a valid slug."
   );
  }

  String candidate = baseSlug;
  int suffix = 2;

  while (problems.existsBySlug(candidate)) {
   candidate =
           baseSlug + "-" + suffix++;
  }

  return candidate;
 }

 private String normalizeRequiredText(
         String value,
         String errorMessage
 ) {

  if (value == null || value.isBlank()) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           errorMessage
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
    @Override
    public Admin updateActivation(
            Long problemId,
            ProblemRequests.Activation request
    ) {
        CodingProblem problem =
                getRequiredProblem(problemId);

        boolean requestedActive =
                Boolean.TRUE.equals(
                        request.active()
                );

        if (
                requestedActive
                        && problem.getStatus()
                        == ProblemStatus.ARCHIVED
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Archived coding problems cannot be activated. "
                            + "Change the problem status to DRAFT first."
            );
        }

        problem.setActive(requestedActive);

        CodingProblem savedProblem =
                problems.save(problem);

        return mapper.admin(savedProblem);
    }
}