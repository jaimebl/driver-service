package com.thefloow.driver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.concurrent.CompletableFuture;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(createApiInfo())
                .useDefaultResponseMessages(false)
                .select()
                .paths(PathSelectors.regex("/drivers.*"))
                .build()
                .tags(
                        new Tag("drivers", "")
                )
                .genericModelSubstitutes(CompletableFuture.class);
    }

    private ApiInfo createApiInfo() {
        return new ApiInfoBuilder()
                .title("Driver Service")
                .description("A service to manage details of drivers")
                .version("1.0")
                .build();
    }
}