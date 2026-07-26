package br.com.erudio.integrationsTests.controllers.withXml;

import br.com.erudio.config.TestConfigs;
import br.com.erudio.integrationsTests.dto.AccountCredentialsDTO;
import br.com.erudio.integrationsTests.dto.PersonDTO;
import br.com.erudio.integrationsTests.dto.TokenDTO;
import br.com.erudio.integrationsTests.dto.wrappers.xml.PersonPagedModel;
import br.com.erudio.integrationsTests.testContainer.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerXmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static XmlMapper objectMapper;

    private static PersonDTO person;
    private static String authToken;

    private static final String TEST_USERNAME = "testuserXML";
    private static final String TEST_FULLNAME = "the plus plus beta tester on XNL";
    private static final String TEST_PASSWORD = "test123";

    @LocalServerPort
    private int port;

    @BeforeAll
    static void setUp() {
        objectMapper = new XmlMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        person = new PersonDTO();
    }

    @Test
    @Order(0)
    void testCreateUserAndLogin() throws JsonProcessingException {
        specification = new RequestSpecBuilder()
                .setPort(port)
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var credentials = new AccountCredentialsDTO(TEST_USERNAME, TEST_FULLNAME, TEST_PASSWORD);

        System.out.println("📝 Criando usuário: " + TEST_USERNAME);

        given(specification)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .body(credentials)
                .when()
                .post("/api/auth/v1/createUser")
                .then()
                .statusCode(200);

        System.out.println("✅ Usuário criado com sucesso!");

        var loginCredentials = new AccountCredentialsDTO(TEST_USERNAME, TEST_FULLNAME, TEST_PASSWORD);

        System.out.println("🔑 Fazendo login com: " + TEST_USERNAME);

        var loginResponse = given(specification)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .body(loginCredentials)
                .when()
                .post("/api/auth/v1/signin")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        TokenDTO token = objectMapper.readValue(loginResponse, TokenDTO.class);

        authToken = token.getAccessToken();

        assertNotNull(authToken, "Token não pode ser nulo");
        assertFalse(authToken.isEmpty(), "Token não pode ser vazio");

        System.out.println("✅ Token obtido com sucesso!");

        specification = new RequestSpecBuilder()
                .setPort(port)
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

        var content = given(specification)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .accept(MediaType.APPLICATION_XML_VALUE)
            .body(person)
            .when()
            .post()
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .extract()
            .body()
            .asString();

        PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
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

        var content = given(specification)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .accept(MediaType.APPLICATION_XML_VALUE)
                .body(person)
            .when()
                .put()
            .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
            .extract()
                .body()
                    .asString();

        PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
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
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                    .pathParam("id", person.getId())
                .when()
                    .get("{id}")
                .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                    .body()
                        .asString();

        PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
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
    @Order(4)
    void disableTest() throws JsonProcessingException {
        assertNotNull(authToken, "É necessário fazer login primeiro (testCreateUserAndLogin deve rodar antes)");

        var content = given(specification)
                .accept(MediaType.APPLICATION_XML_VALUE)
                    .pathParam("id", person.getId())
                .when()
                    .patch("{id}")
                .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                    .body()
                        .asString();

        PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
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
    void findAllTest() throws JsonProcessingException {
        assertNotNull(authToken, "É necessário fazer login primeiro (testCreateUserAndLogin deve rodar antes)");

        var content = given(specification)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                .body()
                .asString();

        System.out.println("Conteúdo: " + content);

        PersonPagedModel wrapper = objectMapper.readValue(content, PersonPagedModel.class);
        List<PersonDTO> people = wrapper.getContent();

        System.out.println("Pessoas: " + people);

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