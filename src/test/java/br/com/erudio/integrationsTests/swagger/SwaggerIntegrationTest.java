package br.com.erudio.integrationsTests.swagger;

import br.com.erudio.config.TestConfigs;
import br.com.erudio.integrationsTests.testContainer.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SwaggerIntegrationTest extends AbstractIntegrationTest {

	@LocalServerPort
	private int port;

	@Test
	void shouldDisplaySwaggerUIPage() {
		var content = given()
			.basePath("/swagger-ui/index.html")
				.port(port)
			.when()
				.get()
			.then()
				.statusCode(200)
			.extract()
				.body()
					.asString();
		assertTrue(content.contains("Swagger UI"));
	}

}