package br.com.erudio.integrationsTests.controllers.withYaml;

import br.com.erudio.config.TestConfigs;
import br.com.erudio.integrationsTests.controllers.withYaml.mapper.YAMLMapper;
import br.com.erudio.integrationsTests.dto.AccountCredentialsDTO;
import br.com.erudio.integrationsTests.dto.PersonDTO;
import br.com.erudio.integrationsTests.dto.TokenDTO;
import br.com.erudio.integrationsTests.dto.wrappers.yaml.PersonPagedModel;
import br.com.erudio.integrationsTests.testContainer.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerYamlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static YAMLMapper objectMapper;

    private static PersonDTO person;
    private static String authToken;

    private static final String TEST_USERNAME = "testuserYaml";
    private static final String TEST_FULLNAME = "the plus plus beta tester on Yaml";
    private static final String TEST_PASSWORD = "test123";

    @LocalServerPort
    private int port;

    @BeforeAll
    static void setUp() {
        objectMapper = new YAMLMapper();
        person = new PersonDTO();
    }

    @Test
    @Order(0)
    void testCreateUserAndLogin() throws JsonProcessingException {
        specification = new RequestSpecBuilder()
                .setPort(port)
                .setConfig(
                        RestAssured.config()
                                .encoderConfig(
                                        encoderConfig()
                                                .encodeContentTypeAs(
                                                        "application/yaml",
                                                        ContentType.TEXT
                                                )
                                )
                )
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var credentials = new AccountCredentialsDTO(TEST_USERNAME, TEST_FULLNAME, TEST_PASSWORD);

        System.out.println("📝 Criando usuário: " + TEST_USERNAME);

        given(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(credentials, objectMapper)
                .when()
                .post("/api/auth/v1/createUser")
                .then()
                .statusCode(200);

        System.out.println("✅ Usuário criado com sucesso!");

        var loginCredentials = new AccountCredentialsDTO(TEST_USERNAME, TEST_FULLNAME, TEST_PASSWORD);

        System.out.println("🔑 Fazendo login com: " + TEST_USERNAME);

        var loginResponse = given(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(credentials, objectMapper)
                .when()
                .post("/api/auth/v1/signin")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenDTO.class, objectMapper);

        authToken = (String) loginResponse.getAccessToken();

        assertNotNull(authToken, "Token não pode ser nulo");
        assertFalse(authToken.isEmpty(), "Token não pode ser vazio");

        System.out.println("✅ Token obtido com sucesso!");

        specification = new RequestSpecBuilder()
                .setPort(port)
                .setConfig(
                        RestAssured.config()
                                .encoderConfig(
                                        encoderConfig()
                                                .encodeContentTypeAs(
                                                        "application/yaml",
                                                        ContentType.TEXT
                                                )
                                )
                )
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
                .addHeader("Authorization", "Bearer " + authToken)
                .setBasePath("/api/persons/v1")
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    void createTest() throws JsonProcessingException {
        assertNotNull(authToken, "É necessário fazer login primeiro (testCreateUserAndLogin deve rodar antes)");

        mockPerson();

        var createdPerson = given(specification)
            .contentType(MediaType.APPLICATION_YAML_VALUE)
            .accept(MediaType.APPLICATION_YAML_VALUE)
            .body(person, objectMapper)
            .when()
            .post()
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_YAML_VALUE)
            .extract()
            .body()
            .as(PersonDTO.class, objectMapper);

        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);

        assertEquals("Linus", createdPerson.getFirstName());
        assertEquals("Torvalds", createdPerson.getLastName());
        assertEquals("Helsinki - Finland", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertTrue(createdPerson.getEnabled());

    }

    @Test
    @Order(2)
    void updateTest() throws JsonProcessingException {
        assertNotNull(authToken, "É necessário fazer login primeiro (testCreateUserAndLogin deve rodar antes)");

        person.setLastName("Benedict Torvalds");

        var createdPerson = given(specification)
            .contentType(MediaType.APPLICATION_YAML_VALUE)
            .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(person, objectMapper)
            .when()
                .put()
            .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
            .extract()
                .body()
                    .as(PersonDTO.class, objectMapper);

        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);

        assertEquals("Linus", createdPerson.getFirstName());
        assertEquals("Benedict Torvalds", createdPerson.getLastName());
        assertEquals("Helsinki - Finland", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertTrue(createdPerson.getEnabled());

    }

    @Test
    @Order(3)
    void findByIdTest() throws JsonProcessingException {
        assertNotNull(authToken, "É necessário fazer login primeiro (testCreateUserAndLogin deve rodar antes)");

        var content = given(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                    .pathParam("id", person.getId())
                .when()
                    .get("{id}")
                .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                    .body()
                        .as(PersonDTO.class, objectMapper);

        assertNotNull(content.getId());
        assertTrue(content.getId() > 0);

        assertEquals("Linus", content.getFirstName());
        assertEquals("Benedict Torvalds", content.getLastName());
        assertEquals("Helsinki - Finland", content.getAddress());
        assertEquals("Male", content.getGender());
        assertTrue(content.getEnabled());
    }

    @Test
    @Order(4)
    void disableTest() throws JsonProcessingException {
        assertNotNull(authToken, "É necessário fazer login primeiro (testCreateUserAndLogin deve rodar antes)");

        var createdPerson = given(specification)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                    .pathParam("id", person.getId())
                .when()
                    .patch("{id}")
                .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                    .body()
                        .as(PersonDTO.class, objectMapper);

        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);

        assertEquals("Linus", createdPerson.getFirstName());
        assertEquals("Benedict Torvalds", createdPerson.getLastName());
        assertEquals("Helsinki - Finland", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
        assertFalse(createdPerson.getEnabled());
    }

    @Test
    @Order(5)
    void deleteTest() throws JsonProcessingException {
        assertNotNull(authToken, "É necessário fazer login primeiro (testCreateUserAndLogin deve rodar antes)");

        given(specification)
                .pathParam("id", person.getId())
            .when()
                .delete("{id}")
            .then()
                .statusCode(204);
    }


    @Test
    @Order(6)
    void findAllTest() {

        assertNotNull(authToken,
                "É necessário fazer login primeiro (testCreateUserAndLogin deve rodar antes)");

        PersonPagedModel wrapper = given(specification)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PersonPagedModel.class, objectMapper);

        List<PersonDTO> people = wrapper.getContent();

        assertNotNull(people);
        assertFalse(people.isEmpty());

        PersonDTO personOne = people.get(0);

        assertNotNull(personOne.getId());
        assertTrue(personOne.getId() > 0);

        assertEquals("Ad", personOne.getFirstName());
        assertEquals("Henkmann", personOne.getLastName());
        assertEquals("7th Floor", personOne.getAddress());
        assertEquals("Male", personOne.getGender());
        assertTrue(personOne.getEnabled());

        PersonDTO personFour = people.get(4);

        assertNotNull(personFour.getId());
        assertTrue(personFour.getId() > 0);

        assertEquals("Adora", personFour.getFirstName());
        assertEquals("Trevascus", personFour.getLastName());
        assertEquals("Room 1342", personFour.getAddress());
        assertEquals("Female", personFour.getGender());
        assertTrue(personFour.getEnabled());
    }

    private void mockPerson() {
        person.setFirstName("Linus");
        person.setLastName("Torvalds");
        person.setAddress("Helsinki - Finland");
        person.setGender("Male");
        person.setEnabled(true);
    }
}