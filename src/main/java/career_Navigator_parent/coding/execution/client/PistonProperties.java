package career_Navigator_parent.coding.execution.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "coding.piston")
public record PistonProperties(

        @NotBlank(message = "Piston base URL is required.")
        String baseUrl,

        String executePath,

        @Positive(message = "Connect timeout must be positive.")
        Long connectTimeoutMilliseconds,

        @Positive(message = "Read timeout must be positive.")
        Long readTimeoutMilliseconds,

        @Positive(message = "Maximum output size must be positive.")
        Integer maximumOutputCharacters

) {

    public String resolvedExecutePath() {
        if (executePath == null || executePath.isBlank()) {
            return "/api/v2/execute";
        }

        return executePath.startsWith("/")
                ? executePath
                : "/" + executePath;
    }

    public Duration resolvedConnectTimeout() {
        return Duration.ofMillis(
                connectTimeoutMilliseconds == null
                        ? 5_000L
                        : connectTimeoutMilliseconds
        );
    }

    public Duration resolvedReadTimeout() {
        return Duration.ofMillis(
                readTimeoutMilliseconds == null
                        ? 30_000L
                        : readTimeoutMilliseconds
        );
    }

    public int resolvedMaximumOutputCharacters() {
        return maximumOutputCharacters == null
                ? 100_000
                : maximumOutputCharacters;
    }

    public String normalizedBaseUrl() {
        String normalized = baseUrl.trim();

        while (normalized.endsWith("/")) {
            normalized = normalized.substring(
                    0,
                    normalized.length() - 1
            );
        }

        return normalized;
    }
}