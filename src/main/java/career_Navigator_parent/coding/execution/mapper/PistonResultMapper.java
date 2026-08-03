package career_Navigator_parent.coding.execution.mapper;

import career_Navigator_parent.coding.enums.SubmissionStatus;
import career_Navigator_parent.coding.execution.dto.PistonModels.ExecuteResponse;
import career_Navigator_parent.coding.execution.dto.PistonModels.StageResult;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class PistonResultMapper {

    public SubmissionStatus status(
            ExecuteResponse response
    ) {
        if (response == null) {
            return SubmissionStatus.INTERNAL_ERROR;
        }

        StageResult compile =
                response.compile();

        if (isCompilationFailure(compile)) {
            return SubmissionStatus.COMPILATION_ERROR;
        }

        StageResult run =
                response.run();

        if (run == null) {
            return SubmissionStatus.INTERNAL_ERROR;
        }

        /*
         * Some Piston Java packages return javac failures
         * inside the run stage instead of the compile stage.
         */
        if (isCompilationFailure(run)) {
            return SubmissionStatus.COMPILATION_ERROR;
        }

        if (isTimeout(run)) {
            return SubmissionStatus.TIME_LIMIT_EXCEEDED;
        }

        if (isMemoryFailure(run)) {
            return SubmissionStatus.MEMORY_LIMIT_EXCEEDED;
        }

        if (hasExecutionFailure(run)) {
            return SubmissionStatus.RUNTIME_ERROR;
        }

        return SubmissionStatus.ACCEPTED;
    }

    public String stdout(
            ExecuteResponse response
    ) {
        return response == null
                || response.run() == null
                ? null
                : response.run().stdout();
    }

    public String stderr(
            ExecuteResponse response
    ) {
        return response == null
                || response.run() == null
                ? null
                : response.run().stderr();
    }

    public String compilerOutput(
            ExecuteResponse response
    ) {
        if (response == null) {
            return null;
        }

        if (isCompilationFailure(response.compile())) {
            return stageErrorOutput(
                    response.compile()
            );
        }

        if (isCompilationFailure(response.run())) {
            return stageErrorOutput(
                    response.run()
            );
        }

        return null;
    }

    public String message(
            ExecuteResponse response
    ) {
        if (response == null) {
            return "Piston returned no execution response.";
        }

        if (
                response.run() != null
                        && hasText(
                        response.run().message()
                )
        ) {
            return response.run().message();
        }

        if (
                response.compile() != null
                        && hasText(
                        response.compile().message()
                )
        ) {
            return response.compile().message();
        }

        return response.message();
    }

    public Double timeSeconds(
            ExecuteResponse response
    ) {
        if (
                response == null
                        || response.run() == null
        ) {
            return null;
        }

        Long milliseconds =
                response.run()
                        .cpuTimeMilliseconds();

        if (milliseconds == null) {
            milliseconds =
                    response.run()
                            .wallTimeMilliseconds();
        }

        if (
                milliseconds == null
                        || milliseconds < 0
        ) {
            return null;
        }

        return milliseconds / 1_000.0;
    }

    public Long memoryKilobytes(
            ExecuteResponse response
    ) {
        if (
                response == null
                        || response.run() == null
                        || response.run().memory() == null
                        || response.run().memory() < 0
        ) {
            return null;
        }

        return Math.max(
                1L,
                response.run().memory() / 1024L
        );
    }

    private boolean isCompilationFailure(
            StageResult stage
    ) {
        if (stage == null) {
            return false;
        }

        String diagnostic =
                combinedText(stage);

        if (
                diagnostic.contains("compilation failed")
                        || diagnostic.contains("compile error")
                        || diagnostic.contains("compilation error")
                        || diagnostic.contains("javac")
                        || diagnostic.contains("error: ';' expected")
                        || diagnostic.contains("error: class")
                        || diagnostic.contains("error: cannot find symbol")
                        || diagnostic.contains("error: incompatible types")
        ) {
            return true;
        }

        /*
         * Java source diagnostics normally contain a filename,
         * line number and the word "error".
         */
        return diagnostic.contains(".java:")
                && diagnostic.contains("error:");
    }

    private boolean isTimeout(
            StageResult stage
    ) {
        String diagnostic =
                combinedText(stage);

        return diagnostic.contains("time limit exceeded")
                || diagnostic.contains("wall clock")
                || diagnostic.contains("timed out")
                || diagnostic.contains("timeout")
                || diagnostic.contains("cpu limit");
    }

    private boolean isMemoryFailure(
            StageResult stage
    ) {
        String diagnostic =
                combinedText(stage);

        return diagnostic.contains("memory limit exceeded")
                || diagnostic.contains("out of memory")
                || diagnostic.contains("cannot allocate memory")
                || diagnostic.contains("java heap space");
    }

    private boolean hasExecutionFailure(
            StageResult stage
    ) {
        if (stage == null) {
            return true;
        }

        if (
                stage.code() != null
                        && stage.code() != 0
        ) {
            return true;
        }

        return hasText(stage.signal());
    }

    private String stageErrorOutput(
            StageResult stage
    ) {
        if (stage == null) {
            return null;
        }

        return firstNonBlank(
                stage.stderr(),
                stage.output(),
                stage.message()
        );
    }

    private String combinedText(
            StageResult stage
    ) {
        if (stage == null) {
            return "";
        }

        return (
                safe(stage.stderr())
                        + "\n"
                        + safe(stage.output())
                        + "\n"
                        + safe(stage.message())
                        + "\n"
                        + safe(stage.signal())
        ).toLowerCase(Locale.ROOT);
    }

    private String firstNonBlank(
            String... values
    ) {
        if (values == null) {
            return null;
        }

        for (String value : values) {
            if (hasText(value)) {
                return value;
            }
        }

        return null;
    }

    private boolean hasText(
            String value
    ) {
        return value != null
                && !value.isBlank();
    }

    private String safe(
            String value
    ) {
        return value == null
                ? ""
                : value;
    }
}