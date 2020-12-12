package org.lorislab.quarkus.log.vertx.web;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lorislab.quarkus.log.it.AbstractTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class VertxWebLoggerTest extends AbstractTest {

    @Test
    public void get1Test() {
        String param = "p1";
        given()
                .when().get("/test/get1/{p}", param)
                .then()
                .statusCode(200)
                .body(is("GET" + param));

        String[] data = logLines();
        Assertions.assertEquals(2, data.length);
        Assertions.assertTrue(data[0].endsWith(") GET /test/get1/p1 [false] started."));
        Assertions.assertTrue(data[1].endsWith("s] finished [200-OK,true]."));
        Assertions.assertTrue(data[1].contains(") GET /test/get1/p1 ["));
    }

    @Test
    public void excludeTest() {
        String param = "p1";
        given()
                .when().get("/test/exclude/{p}", param)
                .then()
                .statusCode(200)
                .body(is("GET" + param));

        String[] data = logLines();
        Assertions.assertEquals(1, data.length);
        Assertions.assertEquals(data[0], "");
    }

    @Test
    public void postTest() {
        String param = "p1";
        String body = "body123";
        given()
                .when()
                .body(body)
                .post("/test/post1/{p}", param)
                .then()
                .statusCode(200)
                .body(is("POST" + param + body));

        String[] data = logLines();
        Assertions.assertEquals(2, data.length);
        Assertions.assertTrue(data[0].endsWith(") POST /test/post1/p1 [true] started."));
        Assertions.assertTrue(data[1].endsWith("s] finished [200-OK,true]."));
        Assertions.assertTrue(data[1].contains(") POST /test/post1/p1 ["));
    }

    @Test
    public void postEmptyContentTest() {
        String param = "p1";
        given()
                .when()
                .post("/test/post1/{p}", param)
                .then()
                .statusCode(200)
                .body(is("POST" + param));

        String[] data = logLines();
        Assertions.assertEquals(2, data.length);
        Assertions.assertTrue(data[0].endsWith(") POST /test/post1/p1 [false] started."));
        Assertions.assertTrue(data[1].endsWith("s] finished [200-OK,true]."));
        Assertions.assertTrue(data[1].contains(") POST /test/post1/p1 ["));
    }
}
