package com.knoma.web.resource;

import com.knoma.web.WebApp;
import com.knoma.web.config.WebConfig;
import com.knoma.web.pojo.Person;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.restassured.response.ResponseOptions;
import io.restassured.response.Validatable;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class PersonResourceTest {

    private static final String CONFIG_PATH = "config.yml";
    private static final String HOST = "localhost";
    private static final String APP_PORT_KEY = "server.applicationConnectors[0].port";
    private static final String APP_PORT = "8008";

    @ClassRule
    public static final DropwizardAppRule<WebConfig> RULE =
            new DropwizardAppRule<>(WebApp.class, CONFIG_PATH,
                    ConfigOverride.config(APP_PORT_KEY, APP_PORT));


    @Test
    public void createPersonSuccess() throws Exception {
        Person person = new Person(UUID.randomUUID(), "testJ", "testJ", "testj@testj.com");
        ResponseOptions getRes =
                given()
                        .log().all()
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .when()
                        .body(person)
                        .post("http://" + HOST + ":" + RULE.getLocalPort() + "/person/");

        Validatable validatableResponse = (Validatable) getRes;
        validatableResponse.then().log().all().statusCode(201);

        Person result = getRes.getBody().as(Person.class);
        assertThat(result, equalTo(person));
    }

    @Test
    public void getPersonSuccess() throws Exception {
        Person person = new Person(UUID.randomUUID(), "testJ", "testJ", "testj@testj.com");
        ResponseOptions getRes =
                given()
                        .log().all()
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .when()
                        .body(person)
                        .post("http://" + HOST + ":" + RULE.getLocalPort() + "/person/");

        Validatable validatableResponse = (Validatable) getRes;
        validatableResponse.then().log().all().statusCode(201);

        getRes =
                given()
                        .log().all()
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .when()
                        .get("http://" + HOST + ":" + RULE.getLocalPort() + "/person/"+ person.getId());

        validatableResponse = (Validatable) getRes;
        validatableResponse.then().log().all().statusCode(200);

        Person result = getRes.getBody().as(Person.class);
        assertThat(result, equalTo(person));
    }


    @Test
    public void getPersonNotFound() throws Exception {
        ResponseOptions getRes =
                given()
                        .log().all()
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .when()
                        .get("http://" + HOST + ":" + RULE.getLocalPort() + "/person/" + UUID.randomUUID());

        Validatable validatableResponse = (Validatable) getRes;
        validatableResponse.then().log().all().statusCode(404);
    }
}
