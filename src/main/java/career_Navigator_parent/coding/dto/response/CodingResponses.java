package career_Navigator_parent.coding.dto.response;

import career_Navigator_parent.coding.enums.ProblemDifficulty;
import career_Navigator_parent.coding.enums.ProblemStatus;
import career_Navigator_parent.coding.enums.ProgrammingLanguage;
import career_Navigator_parent.coding.enums.SolutionApproach;
import career_Navigator_parent.coding.enums.SubmissionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public final class CodingResponses {

 private CodingResponses() {
 }

 public record Tag(
         Long id,
         String name,
         String slug
 ) {
 }

 /**
  * Starter-code response exposed to students.
  *
  * Driver code is intentionally excluded because it may contain
  * internal execution and judging logic.
  */
 public record Starter(
         Long id,
         ProgrammingLanguage language,
         String starterCode,
         String methodSignature
 ) {
 }

 /**
  * Complete starter-code response available only to administrators.
  */
 public record AdminStarter(
         Long id,
         Long problemId,
         ProgrammingLanguage language,
         String starterCode,
         String driverCode,
         String methodSignature,
         Boolean active,
         LocalDateTime createdAt,
         LocalDateTime updatedAt
 ) {
 }

 /**
  * Only sample cases may be exposed to students.
  */
 public record Sample(
         Long id,
         String input,
         String expectedOutput,
         Integer displayOrder
 ) {
 }

 public record Summary(
         Long id,
         String title,
         String slug,
         ProblemDifficulty difficulty,
         Set<Tag> tags,
         double acceptanceRate,
         boolean solved,
         boolean attempted,
         boolean bookmarked,
         double averageRating
 ) {
 }

 public record Detail(
         Long id,
         String title,
         String slug,
         String description,
         String inputFormat,
         String outputFormat,
         String constraints,
         String explanation,
         ProblemDifficulty difficulty,
         Integer timeLimitMilliseconds,
         Integer memoryLimitMegabytes,
         Set<Tag> tags,
         List<Sample> samples,
         List<Starter> starters,
         boolean solved,
         boolean attempted,
         boolean bookmarked,
         boolean solutionUnlocked,
         double acceptanceRate,
         double averageRating
 ) {
 }

 /**
  * Complete admin problem response.
  *
  * Hidden test-case inputs and expected outputs are never embedded here.
  * They should be retrieved only through protected, pageable admin
  * test-case endpoints.
  */
 public record Admin(
         Long id,
         String title,
         String slug,
         String description,
         String inputFormat,
         String outputFormat,
         String constraints,
         String explanation,
         ProblemDifficulty difficulty,
         ProblemStatus status,
         Integer timeLimitMilliseconds,
         Integer memoryLimitMegabytes,
         Integer maximumOutputCharacters,
         Boolean functionBased,
         String functionName,
         String expectedComplexity,
         Boolean premium,
         Boolean active,
         Integer testCaseCount,
         Integer sampleTestCaseCount,
         Integer hiddenTestCaseCount,
         Integer starterCodeCount,
         Long totalSubmissions,
         Long acceptedSubmissions,
         Double acceptanceRate,
         Set<Tag> tags,
         LocalDateTime createdAt,
         LocalDateTime updatedAt
 ) {
 }

 public record Execution(
         SubmissionStatus status,
         String stdout,
         String stderr,
         String compilerOutput,
         Double timeSeconds,
         Long memoryKb,
         String message
 ) {
 }

 public record TestResult(
         int testCaseNumber,
         SubmissionStatus status,
         boolean passed,
         String input,
         String expectedOutput,
         String actualOutput,
         Double timeSeconds,
         Long memoryKb
 ) {
 }

 public record Submission(
         Long submissionId,
         Long problemId,
         ProgrammingLanguage language,
         SubmissionStatus status,
         Integer passedTestCases,
         Integer totalTestCases,
         Double timeSeconds,
         Long memoryKb,
         String compilerOutput,
         String runtimeError,
         boolean completed,
         LocalDateTime submittedAt,
         LocalDateTime judgedAt,
         List<TestResult> testResults
 ) {
 }

 public record Solution(
         Long id,
         ProgrammingLanguage language,
         SolutionApproach approach,
         String title,
         String explanation,
         String sourceCode,
         String timeComplexity,
         String spaceComplexity,
         Boolean official
 ) {
 }

 public record Review(
         Long id,
         Long studentId,
         Integer rating,
         String title,
         String review,
         Boolean containsSpoiler,
         Long helpfulCount,
         LocalDateTime createdAt
 ) {
 }
 public record AdminSolution(
         Long id,
         Long problemId,
         ProgrammingLanguage language,
         SolutionApproach approach,
         String title,
         String explanation,
         String sourceCode,
         String timeComplexity,
         String spaceComplexity,
         Boolean official,
         Boolean active,
         LocalDateTime createdAt,
         LocalDateTime updatedAt
 ) {
 }
}