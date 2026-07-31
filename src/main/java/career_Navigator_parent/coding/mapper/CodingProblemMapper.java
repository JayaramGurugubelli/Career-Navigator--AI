package career_Navigator_parent.coding.mapper;

import career_Navigator_parent.coding.dto.response.CodingResponses.Admin;
import career_Navigator_parent.coding.dto.response.CodingResponses.AdminStarter;
import career_Navigator_parent.coding.dto.response.CodingResponses.Detail;
import career_Navigator_parent.coding.dto.response.CodingResponses.Sample;
import career_Navigator_parent.coding.dto.response.CodingResponses.Starter;
import career_Navigator_parent.coding.dto.response.CodingResponses.Summary;
import career_Navigator_parent.coding.dto.response.CodingResponses.Tag;
import career_Navigator_parent.coding.entity.CodingProblem;
import career_Navigator_parent.coding.entity.ProblemStarterCode;
import career_Navigator_parent.coding.entity.ProblemTag;
import career_Navigator_parent.coding.entity.ProblemTestCase;
import career_Navigator_parent.coding.enums.TestCaseVisibility;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CodingProblemMapper {

 public Admin admin(CodingProblem problem) {

  if (problem == null) {
   return null;
  }

  List<ProblemTestCase> testCases =
          safeTestCases(problem);

  List<ProblemStarterCode> starterCodes =
          safeStarterCodes(problem);

  int activeTestCaseCount =
          Math.toIntExact(
                  testCases.stream()
                          .filter(this::isActiveTestCase)
                          .count()
          );

  int sampleTestCaseCount =
          Math.toIntExact(
                  testCases.stream()
                          .filter(this::isActiveTestCase)
                          .filter(testCase ->
                                  testCase.getVisibility()
                                          == TestCaseVisibility.SAMPLE
                          )
                          .count()
          );

  int hiddenTestCaseCount =
          Math.toIntExact(
                  testCases.stream()
                          .filter(this::isActiveTestCase)
                          .filter(testCase ->
                                  testCase.getVisibility()
                                          == TestCaseVisibility.HIDDEN
                          )
                          .count()
          );

  int activeStarterCodeCount =
          Math.toIntExact(
                  starterCodes.stream()
                          .filter(this::isActiveStarterCode)
                          .count()
          );

  return new Admin(
          problem.getId(),
          problem.getTitle(),
          problem.getSlug(),
          problem.getDescription(),
          problem.getInputFormat(),
          problem.getOutputFormat(),
          problem.getConstraints(),
          problem.getExplanation(),
          problem.getDifficulty(),
          problem.getStatus(),
          problem.getTimeLimitMilliseconds(),
          problem.getMemoryLimitMegabytes(),
          problem.getMaximumOutputCharacters(),
          problem.getFunctionBased(),
          problem.getFunctionName(),
          problem.getExpectedComplexity(),
          problem.getPremium(),
          problem.getActive(),
          activeTestCaseCount,
          sampleTestCaseCount,
          hiddenTestCaseCount,
          activeStarterCodeCount,
          safeLong(problem.getTotalSubmissions()),
          safeLong(problem.getAcceptedSubmissions()),
          acceptanceRate(problem),
          mapTags(problem),
          problem.getCreatedAt(),
          problem.getUpdatedAt()
  );
 }

 public Summary summary(
         CodingProblem problem,
         boolean solved,
         boolean attempted,
         boolean bookmarked,
         double rating
 ) {
  if (problem == null) {
   return null;
  }

  return new Summary(
          problem.getId(),
          problem.getTitle(),
          problem.getSlug(),
          problem.getDifficulty(),
          mapTags(problem),
          acceptanceRate(problem),
          solved,
          attempted,
          bookmarked,
          rating
  );
 }

 public Detail detail(
         CodingProblem problem,
         boolean solved,
         boolean attempted,
         boolean bookmarked,
         double rating
 ) {
  if (problem == null) {
   return null;
  }

  List<Sample> samples =
          safeTestCases(problem)
                  .stream()
                  .filter(this::isActiveTestCase)
                  .filter(testCase ->
                          testCase.getVisibility()
                                  == TestCaseVisibility.SAMPLE
                  )
                  .sorted(
                          Comparator.comparing(
                                  ProblemTestCase::getDisplayOrder,
                                  Comparator.nullsLast(
                                          Comparator.naturalOrder()
                                  )
                          )
                  )
                  .map(testCase ->
                          new Sample(
                                  testCase.getId(),
                                  testCase.getInput(),
                                  testCase.getExpectedOutput(),
                                  testCase.getDisplayOrder()
                          )
                  )
                  .toList();

  List<Starter> starters =
          safeStarterCodes(problem)
                  .stream()
                  .filter(this::isActiveStarterCode)
                  .sorted(
                          Comparator.comparing(
                                  starterCode ->
                                          starterCode
                                                  .getProgrammingLanguage()
                                                  .name()
                          )
                  )
                  .map(starterCode ->
                          new Starter(
                                  starterCode.getId(),
                                  starterCode.getProgrammingLanguage(),
                                  starterCode.getStarterCode(),
                                  starterCode.getMethodSignature()
                          )
                  )
                  .toList();

  return new Detail(
          problem.getId(),
          problem.getTitle(),
          problem.getSlug(),
          problem.getDescription(),
          problem.getInputFormat(),
          problem.getOutputFormat(),
          problem.getConstraints(),
          problem.getExplanation(),
          problem.getDifficulty(),
          problem.getTimeLimitMilliseconds(),
          problem.getMemoryLimitMegabytes(),
          mapTags(problem),
          samples,
          starters,
          solved,
          attempted,
          bookmarked,
          solved,
          acceptanceRate(problem),
          rating
  );
 }

 public AdminStarter adminStarter(
         ProblemStarterCode starterCode
 ) {
  if (starterCode == null) {
   return null;
  }

  Long problemId =
          starterCode.getProblem() == null
                  ? null
                  : starterCode.getProblem().getId();

  return new AdminStarter(
          starterCode.getId(),
          problemId,
          starterCode.getProgrammingLanguage(),
          starterCode.getStarterCode(),
          starterCode.getDriverCode(),
          starterCode.getMethodSignature(),
          starterCode.getActive(),
          starterCode.getCreatedAt(),
          starterCode.getUpdatedAt()
  );
 }

 private Set<Tag> mapTags(
         CodingProblem problem
 ) {
  if (
          problem == null
                  || problem.getTags() == null
                  || problem.getTags().isEmpty()
  ) {
   return Collections.emptySet();
  }

  return problem.getTags()
          .stream()
          .filter(tag ->
                  Boolean.TRUE.equals(tag.getActive())
          )
          .sorted(
                  Comparator.comparing(
                          ProblemTag::getName,
                          Comparator.nullsLast(
                                  String.CASE_INSENSITIVE_ORDER
                          )
                  )
          )
          .map(this::mapTag)
          .collect(
                  Collectors.toCollection(
                          LinkedHashSet::new
                  )
          );
 }

 private Tag mapTag(
         ProblemTag tag
 ) {
  return new Tag(
          tag.getId(),
          tag.getName(),
          tag.getSlug()
  );
 }

 private List<ProblemTestCase> safeTestCases(
         CodingProblem problem
 ) {
  if (problem.getTestCases() == null) {
   return Collections.emptyList();
  }

  return problem.getTestCases();
 }

 private List<ProblemStarterCode> safeStarterCodes(
         CodingProblem problem
 ) {
  if (problem.getStarterCodes() == null) {
   return Collections.emptyList();
  }

  return problem.getStarterCodes();
 }

 private boolean isActiveTestCase(
         ProblemTestCase testCase
 ) {
  return testCase != null
          && Boolean.TRUE.equals(
          testCase.getActive()
  );
 }

 private boolean isActiveStarterCode(
         ProblemStarterCode starterCode
 ) {
  return starterCode != null
          && Boolean.TRUE.equals(
          starterCode.getActive()
  );
 }

 private double acceptanceRate(
         CodingProblem problem
 ) {
  long total =
          safeLong(
                  problem.getTotalSubmissions()
          );

  long accepted =
          safeLong(
                  problem.getAcceptedSubmissions()
          );

  if (total <= 0L) {
   return 0.0;
  }

  double rate =
          accepted * 100.0 / total;

  return Math.round(rate * 100.0) / 100.0;
 }

 private long safeLong(
         Long value
 ) {
  return value == null
          ? 0L
          : value;
 }
}