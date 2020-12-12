package org.lorislab.quarkus.log.it.cdi;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lorislab.quarkus.log.it.AbstractTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class ExcludeLoggerTest extends AbstractTest {

    @Test
    public void excludeClassTest() {
        String param = "P1";
        given()
                .when().get("/exclude/1/{p}", param)
                .then()
                .statusCode(200)
                .body(is("EXCLUDE:P1"));

        String[] data = logLines();
        Assertions.assertEquals(1, data.length);
        Assertions.assertEquals("", data[0]);
    }

    @Test
    public void excludeClassMethodTest() {
        String param = "P1";
        given()
                .when().get("/exclude/2/{p}", param)
                .then()
                .statusCode(200)
                .body(is("METHOD:P1EXCLUDE:P1"));

        String[] data = logLines();
        Assertions.assertEquals(2, data.length);
        Assertions.assertTrue(data[0].endsWith(") method(P1) started."));
        Assertions.assertTrue(data[1].endsWith("s] succeed."));
        Assertions.assertTrue(data[1].contains(") method(P1):METHOD:P1 ["));
    }
}
