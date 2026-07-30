package careerpilot_parent.coding.execution.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Judge0Models {

 private Judge0Models() {
 }

 @JsonInclude(
         JsonInclude.Include.NON_NULL
 )
 public record Request(

         @JsonProperty("source_code")
         String sourceCode,

         @JsonProperty("language_id")
         Integer languageId,

         @JsonProperty("stdin")
         String standardInput,

         @JsonProperty("expected_output")
         String expectedOutput,

         @JsonProperty("cpu_time_limit")
         Double cpuTimeLimit,

         @JsonProperty("memory_limit")
         Integer memoryLimit
 ) {
 }
 @JsonIgnoreProperties(
         ignoreUnknown = true
 )
 public record Token(
         String token
 ) {
 }

 @JsonIgnoreProperties(
         ignoreUnknown = true
 )
 public record Status(
         Integer id,
         String description
 ) {
 }

    public record Result(
            String token,
            String stdout,
            String stderr,

            @JsonProperty("compile_output")
            String compileOutput,

            String message,
            Status status,
            String time,
            Integer memory
    ) {
    }
}