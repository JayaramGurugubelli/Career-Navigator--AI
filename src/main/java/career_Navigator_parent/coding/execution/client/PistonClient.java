package career_Navigator_parent.coding.execution.client;

import career_Navigator_parent.coding.execution.dto.PistonModels.ExecuteRequest;
import career_Navigator_parent.coding.execution.dto.PistonModels.ExecuteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class PistonClient {

    private final RestClient pistonRestClient;
    private final PistonProperties properties;

    public ExecuteResponse execute(
            ExecuteRequest request
    ) {
        validateRequest(request);

        try {
            ExecuteResponse response =
                    pistonRestClient
                            .post()
                            .uri(properties.resolvedExecutePath())
                            .body(request)
                            .retrieve()
                            .body(ExecuteResponse.class);

            if (response == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Piston returned an empty execution response."
                );
            }

            return response;

        } catch (HttpClientErrorException exception) {
            throw providerException(
                    exception.getStatusCode().value(),
                    exception.getResponseBodyAsString(),
                    exception
            );

        } catch (HttpServerErrorException exception) {
            throw providerException(
                    exception.getStatusCode().value(),
                    exception.getResponseBodyAsString(),
                    exception
            );

        } catch (ResourceAccessException exception) {
            throw new ResponseStatusException(
                    HttpStatus.GATEWAY_TIMEOUT,
                    "Piston did not respond within the configured timeout.",
                    exception
            );

        } catch (ResponseStatusException exception) {
            throw exception;

        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Unable to communicate with the Piston execution engine.",
                    exception
            );
        }
    }

    private void validateRequest(
            ExecuteRequest request
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Piston execution request cannot be null."
            );
        }

        if (
                request.language() == null
                        || request.language().isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Piston language is required."
            );
        }

        if (
                request.version() == null
                        || request.version().isBlank()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Piston runtime version is required."
            );
        }

        if (
                request.files() == null
                        || request.files().isEmpty()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one source file is required."
            );
        }
    }

    private ResponseStatusException providerException(
            int status,
            String responseBody,
            Exception cause
    ) {
        String message =
                responseBody == null || responseBody.isBlank()
                        ? "No response body was returned."
                        : responseBody;

        return new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "Piston returned HTTP "
                        + status
                        + ": "
                        + message,
                cause
        );
    }
}