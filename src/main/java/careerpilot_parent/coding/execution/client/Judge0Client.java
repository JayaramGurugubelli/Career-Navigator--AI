package careerpilot_parent.coding.execution.client;

import careerpilot_parent.coding.execution.dto.Judge0Models.Request;
import careerpilot_parent.coding.execution.dto.Judge0Models.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class Judge0Client {

 private static final int MAX_POLL_ATTEMPTS = 20;
 private static final long POLL_INTERVAL_MILLISECONDS = 500L;

 private final RestTemplate restTemplate;

 @Value("${judge0.base-url}")
 private String baseUrl;

 @Value("${judge0.api-key:}")
 private String apiKey;

 @Value("${judge0.api-host:}")
 private String apiHost;

 public Result execute(
         Request request
 ) {
  validateConfiguration();

  String submissionToken =
          createSubmission(request);

  return waitForResult(submissionToken);
 }

 private String createSubmission(
         Request request
 ) {
  String url =
          normalizeBaseUrl()
                  + "/submissions"
                  + "?base64_encoded=false"
                  + "&wait=false";

  HttpEntity<Request> entity =
          new HttpEntity<>(
                  request,
                  createHeaders()
          );

  try {
   ResponseEntity<Map> response =
           restTemplate.exchange(
                   url,
                   HttpMethod.POST,
                   entity,
                   Map.class
           );

   Map<?, ?> responseBody =
           response.getBody();

   if (
           responseBody == null
                   || responseBody.get("token") == null
   ) {
    throw new ResponseStatusException(
            HttpStatus.BAD_GATEWAY,
            "Judge0 did not return a submission token."
    );
   }

   return responseBody
           .get("token")
           .toString();

  } catch (HttpClientErrorException exception) {
   throw providerException(
           exception.getStatusCode().value(),
           exception.getResponseBodyAsString()
   );

  } catch (HttpServerErrorException exception) {
   throw providerException(
           exception.getStatusCode().value(),
           exception.getResponseBodyAsString()
   );

  } catch (ResourceAccessException exception) {
   throw new ResponseStatusException(
           HttpStatus.BAD_GATEWAY,
           "Unable to connect to Judge0 at "
                   + normalizeBaseUrl()
                   + ". Verify that Judge0 is running and reachable.",
           exception
   );
  }
 }

 private Result waitForResult(
         String token
 ) {
  String url =
          normalizeBaseUrl()
                  + "/submissions/"
                  + token
                  + "?base64_encoded=false"
                  + "&fields=stdout,stderr,compile_output,message,status,time,memory,token";

  HttpEntity<Void> entity =
          new HttpEntity<>(
                  createHeaders()
          );

  for (
          int attempt = 1;
          attempt <= MAX_POLL_ATTEMPTS;
          attempt++
  ) {
   Result result =
           fetchResult(
                   url,
                   entity
           );

   if (isFinished(result)) {
    return result;
   }

   sleepBeforeNextPoll();
  }

  throw new ResponseStatusException(
          HttpStatus.GATEWAY_TIMEOUT,
          "Judge0 execution did not complete within the configured polling time."
  );
 }

 private Result fetchResult(
         String url,
         HttpEntity<Void> entity
 ) {
  try {
   ResponseEntity<Result> response =
           restTemplate.exchange(
                   url,
                   HttpMethod.GET,
                   entity,
                   Result.class
           );

   Result result =
           response.getBody();

   if (result == null) {
    throw new ResponseStatusException(
            HttpStatus.BAD_GATEWAY,
            "Judge0 returned an empty execution result."
    );
   }

   return result;

  } catch (HttpClientErrorException exception) {
   throw providerException(
           exception.getStatusCode().value(),
           exception.getResponseBodyAsString()
   );

  } catch (HttpServerErrorException exception) {
   throw providerException(
           exception.getStatusCode().value(),
           exception.getResponseBodyAsString()
   );

  } catch (ResourceAccessException exception) {
   throw new ResponseStatusException(
           HttpStatus.BAD_GATEWAY,
           "Judge0 became unreachable while retrieving the execution result.",
           exception
   );
  }
 }

 private boolean isFinished(
         Result result
 ) {
  if (
          result.status() == null
                  || result.status().id() == null
  ) {
   return false;
  }

  Integer statusId =
          result.status().id();

  /*
   * Judge0:
   * 1 = In Queue
   * 2 = Processing
   * 3 and above = completed
   */
  return statusId >= 3;
 }

 private HttpHeaders createHeaders() {
  HttpHeaders headers =
          new HttpHeaders();

  headers.setContentType(
          MediaType.APPLICATION_JSON
  );

  headers.setAccept(
          java.util.List.of(
                  MediaType.APPLICATION_JSON
          )
  );

  if (
          apiKey != null
                  && !apiKey.isBlank()
  ) {
   headers.set(
           "X-RapidAPI-Key",
           apiKey.trim()
   );
  }

  if (
          apiHost != null
                  && !apiHost.isBlank()
  ) {
   headers.set(
           "X-RapidAPI-Host",
           apiHost.trim()
   );
  }

  return headers;
 }

 private void validateConfiguration() {
  if (
          baseUrl == null
                  || baseUrl.isBlank()
  ) {
   throw new ResponseStatusException(
           HttpStatus.INTERNAL_SERVER_ERROR,
           "Judge0 base URL is not configured."
   );
  }
 }

 private String normalizeBaseUrl() {
  String normalized =
          baseUrl.trim();

  while (normalized.endsWith("/")) {
   normalized =
           normalized.substring(
                   0,
                   normalized.length() - 1
           );
  }

  return normalized;
 }

 private ResponseStatusException providerException(
         int providerStatus,
         String responseBody
 ) {
  String message =
          responseBody == null
                  || responseBody.isBlank()
                  ? "No provider response body."
                  : responseBody;

  return new ResponseStatusException(
          HttpStatus.BAD_GATEWAY,
          "Judge0 returned HTTP "
                  + providerStatus
                  + ": "
                  + message
  );
 }

 private void sleepBeforeNextPoll() {
  try {
   Thread.sleep(
           POLL_INTERVAL_MILLISECONDS
   );

  } catch (InterruptedException exception) {
   Thread.currentThread()
           .interrupt();

   throw new ResponseStatusException(
           HttpStatus.SERVICE_UNAVAILABLE,
           "Judge0 execution polling was interrupted.",
           exception
   );
  }
 }
}