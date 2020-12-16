package org.lorislab.quarkus.log.it.cdi;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lorislab.quarkus.log.it.AbstractTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class BaseLoggerTest extends AbstractTest {

    @Test
    public void baseTest() {
        given()
                .when().get("/test/1")
                .then()
                .statusCode(200)
                .body(is("TEST1"));

        String[] data = logLines();
        Assertions.assertEquals(2, data.length);
        Assertions.assertTrue(data[0].endsWith(") test1() started."));
        Assertions.assertTrue(data[1].endsWith("s] succeed."));
        Assertions.assertTrue(data[1].contains(") test1():TEST1 ["));
    }

    @Test
    public void publicMethodTest() {
        given()
                .when().get("/test/2")
                .then()
                .statusCode(200)
                .body(is("TEST1|TEST2"));

        String[] data = logLines();
        Assertions.assertEquals(2, data.length);
        Assertions.assertTrue(data[0].endsWith(") test1() started."));
        Assertions.assertTrue(data[1].endsWith("s] succeed."));
        Assertions.assertTrue(data[1].contains(") test1():TEST1 ["));
    }

    @Test
    public void externalPackageTest() {
        given()
                .when().get("/test/3")
                .then()
                .statusCode(200)
                .body(is("EXTERNAL"));

        String[] data = logLines();
        Assertions.assertEquals(1, data.length);
    }

    @Test
    public void param1Test() {
        given()
                .when().get("/test/param1/p1")
                .then()
                .statusCode(200)
                .body(is("p1X"));

        String[] data = logLines();
        Assertions.assertEquals(2, data.length);
        Assertions.assertTrue(data[0].endsWith(") param1(p1) started."));
        Assertions.assertTrue(data[1].endsWith("s] succeed."));
        Assertions.assertTrue(data[1].contains(") param1(p1):p1X ["));
    }

    @Test
    public void staticMethodTest() {
        given()
                .when().get("/test/static/param1")
                .then()
                .statusCode(200)
                .body(is("STATIC"));

        String[] data = logLines();
        Assertions.assertEquals(2, data.length);
        Assertions.assertTrue(data[0].endsWith(") staticMethod(param1) started."));
        Assertions.assertTrue(data[1].endsWith("s] succeed."));
        Assertions.assertTrue(data[1].contains(") staticMethod(param1):STATIC ["));
    }

    @Test
    public void staticClassMethodTest() {
        given()
                .when().get("/test/static2/param1")
                .then()
                .statusCode(200)
                .body(is("STATIC"));

        String[] data = logLines();
        Assertions.assertEquals(1, data.length);
        Assertions.assertEquals("", data[0]);
    }
}
