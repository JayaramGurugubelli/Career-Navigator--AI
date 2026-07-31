package career_Navigator_parent.coding.execution.mapper;

import career_Navigator_parent.coding.enums.SubmissionStatus;
import career_Navigator_parent.coding.execution.dto.Judge0Models.Result;
import org.springframework.stereotype.Component;

@Component
public class Judge0ResultMapper {

 public SubmissionStatus status(
         Result result
 ) {
  if (
          result == null
                  || result.status() == null
                  || result.status().id() == null
  ) {
   return SubmissionStatus.INTERNAL_ERROR;
  }

  return switch (result.status().id()) {

   case 3 ->
           SubmissionStatus.ACCEPTED;

   case 4 ->
           SubmissionStatus.WRONG_ANSWER;

   case 5 ->
           SubmissionStatus.TIME_LIMIT_EXCEEDED;

   case 6 ->
           SubmissionStatus.COMPILATION_ERROR;

   case 7, 8, 9, 10, 11, 12 ->
           SubmissionStatus.RUNTIME_ERROR;

   case 13, 14 ->
           SubmissionStatus.INTERNAL_ERROR;

   default ->
           SubmissionStatus.INTERNAL_ERROR;
  };
 }

 public Double time(
         Result result
 ) {
  if (
          result == null
                  || result.time() == null
                  || result.time().isBlank()
  ) {
   return null;
  }

  try {
   return Double.valueOf(
           result.time()
   );
  } catch (NumberFormatException exception) {
   return null;
  }
 }

 public Long memory(
         Result result
 ) {
  if (
          result == null
                  || result.memory() == null
  ) {
   return null;
  }

  return result
          .memory()
          .longValue();
 }
}