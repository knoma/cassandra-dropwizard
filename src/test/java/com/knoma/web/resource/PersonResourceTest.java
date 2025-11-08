package com.knoma.web.resource;

import com.knoma.web.WebApp;
import com.knoma.web.config.WebConfig;
import com.knoma.web.pojo.Person;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.CassandraContainer;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(DropwizardExtensionsSupport.class)
public class PersonResourceTest {

    static CassandraContainer<?> cassandra = new CassandraContainer<>("cassandra:latest")
            .withStartupTimeout(Duration.ofMinutes(2))
            .withInitScript("cql/db.cql");

    static {
        cassandra.start();  // Keyspace created here automatically
    }

   public static final DropwizardAppExtension<WebConfig> RULE =
            new DropwizardAppExtension<>(
                    WebApp.class,
                    "config.yml", // Assuming this configuration file path is correct
                    ConfigOverride.config("server.applicationConnectors[0].port", "0"),
                    ConfigOverride.config("cassandra.contactPoints[0].host", cassandra.getContactPoint().getHostName()),
                    ConfigOverride.config("cassandra.contactPoints[0].port", String.valueOf(cassandra.getContactPoint().getPort())) // Use port 0 for dynamic assignment
            );

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = RULE.getLocalPort();
    }

    private Person createTestPerson(UUID id) {
        // Person POJO must have UUID, name, lastName, email fields/constructor
        Person person = new Person(id, "TestName", "TestLast", "test." + id.toString().substring(0, 8) + "@example.com");

        // Use RestAssured.baseURI and RestAssured.port set in @BeforeAll
        Response postResponse = given()
                .contentType(ContentType.JSON)
                .body(person)
                .when()
                // Assuming PersonResource uses @Path("/person") and the POST is directly on that path
                .put("/person");

        postResponse.then()
                .log().ifValidationFails()
                .statusCode(201); // Asserts the expected creation status code

        Person result = postResponse.getBody().as(Person.class);
        assertThat(result.getId(), notNullValue());

        return result;
    }

    @BeforeAll
    public static void setupRestAssured() {
        // Configure RestAssured to use the dynamic port assigned by Dropwizard
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = RULE.getLocalPort();
    }

    // --- Tests ---

    @Test
    public void createPersonSuccess() throws Exception {
        UUID id = UUID.randomUUID();

        Person createdPerson = createTestPerson(id);

        assertThat(createdPerson.getFirstName(), equalTo("TestName"));
        assertThat(createdPerson.getId(), equalTo(id));
    }

    @Test
    public void getPersonSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        Person createdPerson = createTestPerson(id);

        Response getResponse = given()
                .accept(ContentType.JSON)
                .when()
                .get("/person/{id}", createdPerson.getId()); // Use path parameter

        getResponse.then()
                .log().ifValidationFails()
                .body("id", equalTo(createdPerson.getId().toString()))
                .body("firstName", equalTo(createdPerson.getFirstName()))
                .body("lastName", equalTo(createdPerson.getLastName()))
                .body("email", equalTo(createdPerson.getEmail()))
                .statusCode(200);
    }

    @Test
    public void getPersonNotFound() throws Exception {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/person/{id}", UUID.randomUUID())
                .then()
                .log().ifValidationFails()
                .body("size()", equalTo(0))
                .statusCode(200);
    }

    @Test
    public void getAllPersonSuccess() throws Exception {
        createTestPerson(UUID.randomUUID());

        Response getResponse = given()
                .accept(ContentType.JSON)
                .when()
                .get("/person/all");

        getResponse.then()
                .log().ifValidationFails()
                .statusCode(200);

        Person[] result = getResponse.getBody().as(Person[].class);
        assertThat(result.length, greaterThan(0));
    }

    @Test
    public void getPersonCountSuccess() throws Exception {
        createTestPerson(UUID.randomUUID());

        Response getResponse = given()
                .accept(ContentType.JSON)
                .when()
                .get("/person/count");

        getResponse.then()
                .log().ifValidationFails()
                .statusCode(200);

        Map<String, Integer> map = getResponse.getBody().as(new io.restassured.common.mapper.TypeRef<Map<String, Integer>>() {});

        assertThat(map.get("count"), greaterThan(0));
    }
}