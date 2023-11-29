package com.zmu.cloud.commons.config;


import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @author YH
 */
@Configuration
@EnableSwagger2WebMvc
@Slf4j
@Profile({"default", "dev", "test"})
@RequiredArgsConstructor
public class Swagger2Config {

  final ZmuCloudProperties zmuCloudProperties;

  @Bean
  public Docket buildDocket(@Value("${spring.application.name}") String appName) {
    log.info("init Swagger2Config - {}", appName);
    Docket docket = new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(new ApiInfoBuilder().title(appName + " API文档   ").version("1.0").build())
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(PathSelectors.ant("/api/**"))
            .build()
            .groupName("云慧养 APP")
            .pathMapping("/")
            .globalOperationParameters(getParameters());
    if (!appName.contains("api")) {
      docket.enable(false);
    }
    return docket;
  }

  @Bean
  public Docket sphDocket(@Value("${spring.application.name}") String appName) {
    log.info("init Swagger2Config - {}", appName);
    Docket docket = new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(new ApiInfoBuilder().title(appName + " API文档   ").version("1.0").contact("xcZwXBAgew0zSBaFNiuDO9lMpHjDXQLnMUEYNgHg4PgfH9JOCOMi").build())
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(PathSelectors.ant("/sph/api/**"))
            .build()
            .groupName("智慧猪家 APP")
            .pathMapping("/")
            .globalOperationParameters(getSphParameters());
    if (!appName.contains("api")) {
      docket.enable(false);
    }
    return docket;
  }

  @Bean
  public Docket adminDocket(@Value("${spring.application.name}") String appName) {
    log.info("init Swagger2Config - {}", appName);
    Docket docket = new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(new ApiInfoBuilder().title(appName + " API文档   ").version("1.0").build())
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(PathSelectors.ant("/admin/**"))
            .build()
            .groupName("云慧养 Admin")
            .pathMapping("/")
            .globalOperationParameters(getParameters());
    if (!appName.contains("admin")) {
      docket.enable(false);
    }
    return docket;
  }

  @Bean
  public Docket sphAdminDocket(@Value("${spring.application.name}") String appName) {
    log.info("init Swagger2Config - {}", appName);
    Docket docket = new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(new ApiInfoBuilder().title(appName + " API文档   ").version("1.0").build())
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(PathSelectors.ant("/sph/admin/**"))
            .build()
            .groupName("智慧猪家 Admin")
            .pathMapping("/")
            .globalOperationParameters(getSphParameters());
    if (!appName.contains("admin")) {
      docket.enable(false);
    }
    return docket;
  }

  private List<Parameter> getParameters() {
    ParameterBuilder token = new ParameterBuilder();
    List<Parameter> pars = new ArrayList<>();
    token.name(zmuCloudProperties.getConfig().getTokenHeaderName())
        .modelRef(new ModelRef("String")).parameterType("header").required(true);
    pars.add(token.build());
    token = new ParameterBuilder();
    token.name(zmuCloudProperties.getConfig().getPigFarmIdHeaderName())
        .modelRef(new ModelRef("Long")).parameterType("header").required(true);
    pars.add(token.build());
    return pars;
  }

  private List<Parameter> getSphParameters() {
    ParameterBuilder token = new ParameterBuilder();
    List<Parameter> pars = new ArrayList<>();
    token.name(zmuCloudProperties.getConfig().getSphTokenHeaderName())
            .modelRef(new ModelRef("String")).parameterType("header").required(true);
    pars.add(token.build());
    return pars;
  }
}