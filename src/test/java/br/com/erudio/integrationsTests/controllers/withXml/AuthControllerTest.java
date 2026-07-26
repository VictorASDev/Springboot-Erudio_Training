package br.com.erudio.integrationsTests.controllers.withXml;

import br.com.erudio.config.TestConfigs;
import br.com.erudio.integrationsTests.dto.AccountCredentialsDTO;
import br.com.erudio.integrationsTests.dto.PersonDTO;
import br.com.erudio.integrationsTests.dto.TokenDTO;
import br.com.erudio.integrationsTests.testContainer.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest extends AbstractIntegrationTest {

    private static TokenDTO token;
    private static XmlMapper objectMapper;

    @LocalServerPort
    private int port;


    @BeforeAll
    static void setUp() {
        token = new TokenDTO();
        objectMapper = new XmlMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Test
    @Order(1)
    void signin() throws JsonProcessingException {
        AccountCredentialsDTO credentials =
                new AccountCredentialsDTO("admin", "admin123");

        var content = given()
                .basePath("api/auth/v1/signin")
                .port(port)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .body(credentials)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .asString();

        token = objectMapper.readValue(content, TokenDTO.class);

        assertNotNull(token.getAccessToken());
        assertNotNull(token.getRefreshToken());
    }

    @Test
    @Order(2)
    void refreshToken() throws JsonProcessingException {
        assertNotNull(token.getRefreshToken(), "Não há tokens de refresh válidos e disponíveis");

        var content = given()
                .basePath("/api/auth/v1/refresh")
                .port(port)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                    .pathParam("username", token.getUsername())
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + token.getRefreshToken())
                .when()
                    .put("{username}")
                        .then()
                        .statusCode(200)
                            .extract()
                                    .asString();

        token = objectMapper.readValue(content, TokenDTO.class);
        assertNotNull(token.getAccessToken());
        assertNotNull(token.getRefreshToken());
    }
}