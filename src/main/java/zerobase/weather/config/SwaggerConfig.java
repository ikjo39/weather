package zerobase.weather.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	// 인터넷에 swaggerConfig 검색

	@Bean
	public Docket api() {

		// admin용 api도 따로 있음
		// 이건 개발자만 알고 있어야함
		return new Docket(DocumentationType.SWAGGER_2)
			.select()
			// error controller 는 basePackage 에 포함된 것이 아닌 Spring 단에 있음
			.apis(RequestHandlerSelectors.basePackage("zerobase.weather"))
			// any()는 모든 API 들을 나오게 하겠다.
			.paths(PathSelectors.any())
			.build().apiInfo(apiInfo());

	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title("날씨 일기 프로젝트 :)")
			.description("날씨 일기를 CRUD 할 수 있는 벡앤드 API 입니다.")
			.version("2.0")
			.build();
	}
}
