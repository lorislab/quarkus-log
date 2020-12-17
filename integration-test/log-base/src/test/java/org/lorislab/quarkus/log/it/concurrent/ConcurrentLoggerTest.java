package org.lorislab.quarkus.log.it.concurrent;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lorislab.quarkus.log.it.AbstractTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class ConcurrentLoggerTest extends AbstractTest {

    @Test
    public void baseTest() {
        String param = "param1";
        given()
                .when().get("/concurrent/{param}", param)
                .then()
                .statusCode(200)
                .body(is(param));

        String[] data = logLines();
        Assertions.assertEquals(3, data.length);
        Assertions.assertTrue(data[0].endsWith(") test1(param1) started."));
        Assertions.assertTrue(data[1].endsWith(" Execute param1"));
        Assertions.assertTrue(data[2].endsWith("s] succeed."));
        Assertions.assertTrue(data[2].contains(") test1(param1):null ["));
    }
}
