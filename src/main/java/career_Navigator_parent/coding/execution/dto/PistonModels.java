package career_Navigator_parent.coding.execution.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class PistonModels {

    private PistonModels() {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record File(
            String name,
            String content
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ExecuteRequest(
            String language,
            String version,
            List<File> files,
            String stdin,
            List<String> args,

            @JsonProperty("compile_timeout")
            Long compileTimeoutMilliseconds,

            @JsonProperty("run_timeout")
            Long runTimeoutMilliseconds,

            @JsonProperty("compile_memory_limit")
            Long compileMemoryLimitBytes,

            @JsonProperty("run_memory_limit")
            Long runMemoryLimitBytes
    ) {
        public ExecuteRequest {
            files = files == null
                    ? List.of()
                    : List.copyOf(files);

            args = args == null
                    ? List.of()
                    : List.copyOf(args);

            stdin = stdin == null
                    ? ""
                    : stdin;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StageResult(
            String stdout,
            String stderr,
            Integer code,
            String signal,
            String output,
            Long memory,
            String message,

            @JsonProperty("cpu_time")
            Long cpuTimeMilliseconds,

            @JsonProperty("wall_time")
            Long wallTimeMilliseconds
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ExecuteResponse(
            String language,
            String version,
            StageResult compile,
            StageResult run,
            String message
    ) {
    }
}