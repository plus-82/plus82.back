package com.etplus.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

  @Value("${api-path.sign-in}")
  private String SIGN_IN_PATH;

  @Bean
  public OpenAPI openAPI() {
    Info info = new Info()
        .title("82PLUS API")
        .description("Swagger")
        .version("1.0.0");

    return new OpenAPI().info(info);
  }

  @Bean
  public OpenApiCustomizer authEndpointCustomizer() {
    return openAPI -> {
      openAPI.getPaths().addPathItem(SIGN_IN_PATH, loginPathItem());
    };
  }

  // 로그인 API 정의
  private PathItem loginPathItem() {
    Operation loginOperation = new Operation();

    // 요청 본문 스키마 정의
    Schema<?> schema = new ObjectSchema()
        .addProperties("email", new StringSchema())
        .addProperties("password", new StringSchema());
    RequestBody requestBody = new RequestBody()
        .content(new Content()
            .addMediaType("application/x-www-form-urlencoded", new MediaType().schema(schema)));
    loginOperation.requestBody(requestBody);

    // API 응답 정의
    ApiResponses apiResponses = new ApiResponses();
    apiResponses.addApiResponse(String.valueOf(HttpStatus.OK.value()),
        new ApiResponse().description(HttpStatus.OK.getReasonPhrase()));
    apiResponses.addApiResponse(String.valueOf(HttpStatus.FORBIDDEN.value()),
        new ApiResponse().description(HttpStatus.FORBIDDEN.getReasonPhrase()));
    loginOperation.responses(apiResponses);

    // 태그 및 경로 아이템 추가
    loginOperation.addTagsItem("auth-controller");
    return new PathItem().post(loginOperation);
  }

}