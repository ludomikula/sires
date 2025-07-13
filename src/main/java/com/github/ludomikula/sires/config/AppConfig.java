package com.github.ludomikula.sires.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Configuration
public class AppConfig {

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
            .info(
                new Info()
                    .title("Sires API")
                    .description("Simple Reporting Server API")
                    .version("0.0.1")
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                )
            );
    }


}
