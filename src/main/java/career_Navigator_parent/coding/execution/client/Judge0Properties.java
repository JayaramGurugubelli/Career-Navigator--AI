package career_Navigator_parent.coding.execution.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(
        prefix = "coding.judge0"
)
public record Judge0Properties(

        @NotBlank(
                message = "Judge0 base URL is required"
        )
        String baseUrl,

        String apiKey,

        String apiHost,

        @Positive(
                message = "Polling attempts must be greater than zero"
        )
        Integer pollingAttempts,

        @Positive(
                message = "Polling delay must be greater than zero"
        )
        Long pollingDelayMilliseconds

) {
    public int resolvedPollingAttempts() {
        return pollingAttempts == null
                ? 15
                : pollingAttempts;
    }

    public long resolvedPollingDelayMilliseconds() {
        return pollingDelayMilliseconds == null
                ? 350L
                : pollingDelayMilliseconds;
    }
}