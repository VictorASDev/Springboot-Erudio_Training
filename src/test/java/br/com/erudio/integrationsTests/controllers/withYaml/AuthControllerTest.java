package br.com.erudio.integrationsTests.controllers.withYaml;

import br.com.erudio.config.TestConfigs;
import br.com.erudio.integrationsTests.controllers.withYaml.mapper.YAMLMapper;
import br.com.erudio.integrationsTests.dto.AccountCredentialsDTO;
import br.com.erudio.integrationsTests.dto.TokenDTO;
import br.com.erudio.integrationsTests.testContainer.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest extends AbstractIntegrationTest {

    private static TokenDTO token;
    private static YAMLMapper objectMapper;
    private static RequestSpecification specification;

    @BeforeAll
    static void setUp() {
        token = new TokenDTO();
        objectMapper = new YAMLMapper();

        RestAssured.config = RestAssured.config()
                .encoderConfig(
                        encoderConfig()
                                .encodeContentTypeAs(
                                        "application/yaml",
                                        ContentType.TEXT
                                )
                );
    }

    @Test
    @Order(1)
    void signin() throws JsonProcessingException {
        AccountCredentialsDTO credentials =
                new AccountCredentialsDTO("admin", "admin", "admin123");

        token = given()
                .basePath("api/auth/v1/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(credentials, objectMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .as(TokenDTO.class, objectMapper);

        assertNotNull(token.getAccessToken());
        assertNotNull(token.getRefreshToken());
    }

    @Test
    @Order(2)
    void refreshToken() throws JsonProcessingException {
        assertNotNull(token.getRefreshToken(), "Não há tokens de refresh válidos e disponíveis");

        token = given()
                .basePath("/api/auth/v1/refresh")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                    .pathParam("username", token.getUsername())
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + token.getRefreshToken())
                .when()
                    .put("{username}")
                        .then()
                        .statusCode(200)
                            .extract()
                                    .as(TokenDTO.class, objectMapper);

        assertNotNull(token.getAccessToken());
        assertNotNull(token.getRefreshToken());
    }
}