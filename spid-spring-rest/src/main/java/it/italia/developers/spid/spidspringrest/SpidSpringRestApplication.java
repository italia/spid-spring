package it.italia.developers.spid.spidspringrest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@Configuration
@ComponentScan(basePackages = { "it.italia.developers.spid.integration", "it.italia.developers.spid.spidspringrest" })
@PropertySources({
		@PropertySource("classpath:application.properties"),
		@PropertySource("classpath:integration.properties"),
		@PropertySource(value = "file:${spid-spring-integration.properties.path}", ignoreResourceNotFound = true) })
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
public class SpidSpringRestApplication {

	public static void main(final String[] args) {
		SpringApplication.run(SpidSpringRestApplication.class, args);
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any()).paths(PathSelectors.any()).build().apiInfo(apiInfo());
	}

	private List<ResponseMessage> errorList() {
		List<ResponseMessage> lista = new ArrayList<>();
		lista.add(new ResponseMessageBuilder().code(500).message("500 message").responseModel(new ModelRef("ErrorRestService")).build());
		lista.add(new ResponseMessageBuilder().code(400).message("Bad Request").responseModel(new ModelRef("ErrorRestService")).build());
		lista.add(new ResponseMessageBuilder().code(401).message("Non Autorizzato").responseModel(new ModelRef("ErrorRestService")).build());
		lista.add(new ResponseMessageBuilder().code(412).message("Errore Validazione").responseModel(new ModelRef("ErrorRestService")).build());
		return lista;
	}

	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo("SPID REST API", "Servizi rest per l'applicazione SPID REST", "0.0.1", null, null, null, null);
		return apiInfo;
	}
}
