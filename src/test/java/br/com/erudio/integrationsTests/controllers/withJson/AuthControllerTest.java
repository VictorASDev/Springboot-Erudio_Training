package br.com.erudio.integrationsTests.controllers.withJson;

import br.com.erudio.config.TestConfigs;
import br.com.erudio.integrationsTests.dto.AccountCredentialsDTO;
import br.com.erudio.integrationsTests.dto.PersonDTO;
import br.com.erudio.integrationsTests.dto.TokenDTO;
import br.com.erudio.integrationsTests.testContainer.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @LocalServerPort
    private int port;
    
    private static TokenDTO token;

    @BeforeAll
    static void setUp() {
       token = new TokenDTO();
    }

    @Test
    @Order(1)
    void signin() {
        AccountCredentialsDTO credentials =
                new AccountCredentialsDTO("admin", "admin123");

        token = given()
                .basePath("api/auth/v1/signin")
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(credentials)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenDTO.class);

        assertNotNull(token.getAccessToken());
        assertNotNull(token.getRefreshToken());
    }

    @Test
    @Order(2)
    void refreshToken() {
        token = given()
                .basePath("/api/auth/v1/refresh")
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("username", token.getUsername())
                    .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + token.getRefreshToken())
                .when()
                    .put("{username}")
                        .then()
                        .statusCode(200)
                            .extract()
                            .body()
                            .as(TokenDTO.class);

        assertNotNull(token.getAccessToken());
        assertNotNull(token.getRefreshToken());
    }
}