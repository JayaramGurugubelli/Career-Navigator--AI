package careerpilot_parent.coding.execution.client;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(
        Judge0Properties.class
)
public class Judge0Config {

 @Bean
 public RestClient judge0RestClient(
         RestClient.Builder builder,
         Judge0Properties properties
 ) {
  validateProperties(properties);

  RestClient.Builder configuredBuilder =
          builder.baseUrl(
                  properties.baseUrl()
          );

  if (
          properties.apiKey() != null
                  && !properties.apiKey().isBlank()
  ) {
   configuredBuilder.defaultHeader(
           "X-RapidAPI-Key",
           properties.apiKey()
   );
  }

  if (
          properties.apiHost() != null
                  && !properties.apiHost().isBlank()
  ) {
   configuredBuilder.defaultHeader(
           "X-RapidAPI-Host",
           properties.apiHost()
   );
  }

  return configuredBuilder.build();
 }

 private void validateProperties(
         Judge0Properties properties
 ) {
  if (
          properties.baseUrl() == null
                  || properties.baseUrl().isBlank()
  ) {
   throw new IllegalStateException(
           "Judge0 base URL is not configured. "
                   + "Set coding.judge0.base-url."
   );
  }
 }
}