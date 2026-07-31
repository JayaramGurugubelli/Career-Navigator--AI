package career_Navigator_parent.coding.dto.request;

import career_Navigator_parent.coding.enums.ProgrammingLanguage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public final class ExecutionRequests {

 private static final int MAX_SOURCE_CODE_LENGTH = 100_000;
 private static final int MAX_CUSTOM_INPUT_LENGTH = 50_000;

 private ExecutionRequests() {
 }

 /**
  * Used by:
  *
  * POST /api/student/coding/executions/run
  *
  * The problem ID is supplied in the request body.
  */
 public record Run(

         @NotNull(message = "Problem ID is required.")
         @Positive(message = "Problem ID must be greater than zero.")
         Long problemId,

         @NotNull(message = "Programming language is required.")
         ProgrammingLanguage language,

         @NotBlank(message = "Source code is required.")
         @Size(
                 max = MAX_SOURCE_CODE_LENGTH,
                 message = "Source code cannot exceed 100000 characters."
         )
         String sourceCode,

         @Size(
                 max = MAX_CUSTOM_INPUT_LENGTH,
                 message = "Custom input cannot exceed 50000 characters."
         )
         String customInput
 ) {
 }

 /**
  * Used by:
  *
  * POST /api/student/coding/problems/{problemId}/run
  *
  * The problem ID comes from the URL path and therefore is not duplicated
  * in the request body.
  */
 public record ProblemRun(

         @NotNull(message = "Programming language is required.")
         ProgrammingLanguage language,

         @NotBlank(message = "Source code is required.")
         @Size(
                 max = MAX_SOURCE_CODE_LENGTH,
                 message = "Source code cannot exceed 100000 characters."
         )
         String sourceCode,

         @Size(
                 max = MAX_CUSTOM_INPUT_LENGTH,
                 message = "Custom input cannot exceed 50000 characters."
         )
         String customInput
 ) {
 }

 /**
  * Used by:
  *
  * POST /api/student/coding/submissions
  */
 public record Submit(

         @NotNull(message = "Problem ID is required.")
         @Positive(message = "Problem ID must be greater than zero.")
         Long problemId,

         @NotNull(message = "Programming language is required.")
         ProgrammingLanguage language,

         @NotBlank(message = "Source code is required.")
         @Size(
                 max = MAX_SOURCE_CODE_LENGTH,
                 message = "Source code cannot exceed 100000 characters."
         )
         String sourceCode
 ) {
 }

 public record Review(

         @NotNull(message = "Rating is required.")
         @Min(
                 value = 1,
                 message = "Rating must be at least 1."
         )
         @Max(
                 value = 5,
                 message = "Rating cannot exceed 5."
         )
         Integer rating,

         @Size(
                 max = 120,
                 message = "Review title cannot exceed 120 characters."
         )
         String title,

         @NotBlank(message = "Review is required.")
         @Size(
                 max = 3000,
                 message = "Review cannot exceed 3000 characters."
         )
         String review,

         Boolean containsSpoiler
 ) {
 }
}