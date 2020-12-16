package org.lorislab.quarkus.log.it.mutiny;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lorislab.quarkus.log.it.AbstractTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class MutinyLoggerTest extends AbstractTest {

    @Test
    public void uniTest() {
        String param = "param1";
        given()
                .when().get("/mutiny/uni/{param}", param)
                .then()
                .statusCode(200)
                .body(is(param));

        String[] data = logLines();
        Assertions.assertEquals(3, data.length);
        Assertions.assertTrue(data[0].endsWith(") uni(param1) started."));
        Assertions.assertTrue(data[1].endsWith(" Execute param1"));
        Assertions.assertTrue(data[2].endsWith("s] succeed."));
        Assertions.assertTrue(data[2].contains(") uni(param1):param1 ["));
    }

    @Test
    public void multiTest() {

        String param = "param1";
        String result = param + "-5";
        given()
                .when().get("/mutiny/multi/{param}", param)
                .then()
                .statusCode(200)
                .body(is(result));

        String[] data = logLines();
        Assertions.assertEquals(7, data.length);
        Assertions.assertTrue(data[0].endsWith(") multi(param1) started."));
        Assertions.assertTrue(data[1].endsWith(" Execute param1-1"));
        Assertions.assertTrue(data[2].endsWith(" Execute param1-2"));
        Assertions.assertTrue(data[3].endsWith(" Execute param1-3"));
        Assertions.assertTrue(data[4].endsWith(" Execute param1-4"));
        Assertions.assertTrue(data[5].endsWith(" Execute param1-5"));
        Assertions.assertTrue(data[6].endsWith("s] succeed."));
        Assertions.assertTrue(data[6].contains(") multi(param1):null ["));
    }
}
