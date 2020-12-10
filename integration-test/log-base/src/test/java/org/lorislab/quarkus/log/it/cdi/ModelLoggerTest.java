package org.lorislab.quarkus.log.it.cdi;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lorislab.quarkus.log.it.AbstractTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class ModelLoggerTest extends AbstractTest {

    @Test
    public void modelTest() {
        given()
                .when().get("/model/param1")
                .then()
                .statusCode(200)
                .body(is("param1"));

        String[] data = logLines();
        Assertions.assertEquals(3, data.length);
        Assertions.assertTrue(data[0].endsWith(") model(model:param1) started."));
        Assertions.assertTrue(data[1].endsWith("Model: param1"));
        Assertions.assertTrue(data[2].endsWith("s] succeed."));
        Assertions.assertTrue(data[2].contains(") model(model:param1):model:param1 ["));
    }

    @Test
    public void subModelTest() {
        given()
                .when().get("/model/sub/param1/param2")
                .then()
                .statusCode(200)
                .body(is("param1"));

        String[] data = logLines();
        Assertions.assertEquals(3, data.length);
        Assertions.assertTrue(data[0].endsWith(") subModel(model:param1) started."));
        Assertions.assertTrue(data[1].endsWith("SubModel: param1/param2"));
        Assertions.assertTrue(data[2].endsWith("s] succeed."));
        Assertions.assertTrue(data[2].contains(") subModel(model:param1):model:param1 ["));
    }

    @Test
    public void modelToStringTest() {
        given()
                .when().get("/model/string/param1")
                .then()
                .statusCode(200)
                .body(is("param1"));

        String[] data = logLines();
        Assertions.assertEquals(3, data.length);
        Assertions.assertTrue(data[0].endsWith(") modeToString(Model{param='param1'}) started."));
        Assertions.assertTrue(data[1].endsWith("ModelToString: param1"));
        Assertions.assertTrue(data[2].endsWith("s] succeed."));
        Assertions.assertTrue(data[2].contains(") modeToString(Model{param='param1'}):Model{param='param1'} ["));
    }
}
