package org.lorislab.quarkus.log.it.rs;

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
                .when().get("/test/get")
                .then()
                .statusCode(200)
                .body(is("TEST1"));

        String[] data = logLines();
        Assertions.assertEquals(2, data.length);
        Assertions.assertTrue(data[0].endsWith(") GET http://localhost:8081/test/get [false] started."));
        Assertions.assertTrue(data[1].endsWith("s] finished [200-OK,true]."));
        Assertions.assertTrue(data[1].contains(") GET http://localhost:8081/test/get ["));
    }

    @Test
    public void excludeTest() {
        given()
                .when().get("/test/exclude")
                .then()
                .statusCode(200)
                .body(is("TEST1"));

        String[] data = logLines();
        Assertions.assertEquals(1, data.length);
        Assertions.assertEquals("", data[0]);
    }

    @Test
    public void postTest() {
        String param = "param1";
        String body = "body";

        given()
                .when().body(body)
                .post("/test/post/{p}", param)
                .then()
                .statusCode(200)
                .body(is(param + "X" + body));

        String[] data = logLines();
        Assertions.assertEquals(2, data.length);
        Assertions.assertTrue(data[0].endsWith(") POST http://localhost:8081/test/post/" + param + " [true] started."));
        Assertions.assertTrue(data[1].endsWith("s] finished [200-OK,true]."));
        Assertions.assertTrue(data[1].contains(") POST http://localhost:8081/test/post/" + param + " ["));
    }
}
