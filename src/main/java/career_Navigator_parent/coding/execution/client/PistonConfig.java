package career_Navigator_parent.coding.execution.client;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(PistonProperties.class)
public class PistonConfig {

    @Bean
    public RestClient pistonRestClient(
            RestClient.Builder builder,
            PistonProperties properties
    ) {
        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(
                properties.resolvedConnectTimeout()
        );

        requestFactory.setReadTimeout(
                properties.resolvedReadTimeout()
        );

        return builder
                .baseUrl(properties.normalizedBaseUrl())
                .requestFactory(requestFactory)
                .defaultHeader(
                        "Content-Type",
                        "application/json"
                )
                .defaultHeader(
                        "Accept",
                        "application/json"
                )
                .build();
    }
}