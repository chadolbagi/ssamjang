package chadolbagi.ssamjang.config;

import java.util.Collections;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicates;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Profile({ "dev" })
public class SwaggerConfig {
    // http://springfox.github.io/springfox/docs/current/ 를 참고해서 작성해주세요.
    @Bean
    public Docket api(TypeResolver resolver) {
        ApiInfo apiInfo = new ApiInfo(
            /* title */"Chadolbagi SSAMJANG API",
            /* description */"911 Operator using wit.ai https://wit.ai/",
            /* version */getClass().getPackage().getImplementationVersion(),
            /* termsOfService */null,
            new Contact("Chadolbagi", "http://chadolbagi.ai", null),
            /* license */null,
            /* licenseUrl */null,
            Collections.emptyList());

        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(Predicates.not(PathSelectors.regex("/error.*")))
            .build()
            .apiInfo(apiInfo)
            .useDefaultResponseMessages(false);
            // .additionalModels(resolver.resolve(InitialChatParam.class));
    }
}
