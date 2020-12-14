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

    @Test
    public void clientTest() {
        given()
                .when().get("/test/client/get")
                .then()
                .statusCode(200)
                .body(is("CLIENT"));

        String[] data = logLines();
        Assertions.assertEquals(6, data.length);
        Assertions.assertTrue(data[0].endsWith(") GET http://localhost:8081/test/client/get [false] started."));
        Assertions.assertTrue(data[1].endsWith(") GET http://localhost:8081/client/get [false] started."));
        Assertions.assertTrue(data[2].endsWith(") GET http://localhost:8081/client/get [false] started."));
        Assertions.assertTrue(data[3].endsWith("s] finished [200-OK,true]."));
        Assertions.assertTrue(data[3].contains(") GET http://localhost:8081/client/get ["));
        Assertions.assertTrue(data[4].endsWith("s] finished [200-OK,true]."));
        Assertions.assertTrue(data[4].contains(") GET http://localhost:8081/client/get ["));
        Assertions.assertTrue(data[5].endsWith("s] finished [200-OK,true]."));
        Assertions.assertTrue(data[5].contains(") GET http://localhost:8081/test/client/get ["));
    }

    @Test
    public void clientExcludeTest() {
        given()
                .when().get("/test/client/exclude")
                .then()
                .statusCode(200)
                .body(is("CLIENT"));

        String[] data = logLines();
        Assertions.assertEquals(4, data.length);
        Assertions.assertTrue(data[0].endsWith(") GET http://localhost:8081/test/client/exclude [false] started."));
        Assertions.assertTrue(data[1].endsWith(") GET http://localhost:8081/client/exclude [false] started."));
        Assertions.assertTrue(data[2].endsWith("s] finished [200-OK,true]."));
        Assertions.assertTrue(data[2].contains(") GET http://localhost:8081/client/exclude ["));
        Assertions.assertTrue(data[3].endsWith("s] finished [200-OK,true]."));
        Assertions.assertTrue(data[3].contains(") GET http://localhost:8081/test/client/exclude ["));
    }
}
