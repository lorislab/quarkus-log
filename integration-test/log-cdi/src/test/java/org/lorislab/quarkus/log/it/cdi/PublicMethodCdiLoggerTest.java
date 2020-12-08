package org.lorislab.quarkus.log.it.cdi;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lorislab.quarkus.log.it.cdi.profile.DefaultTestProfile;
import org.lorislab.quarkus.log.it.cdi.profile.PublicMethodsTestProfile;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestProfile(PublicMethodsTestProfile.class)
public class PublicMethodCdiLoggerTest extends AbstractTest {

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
        Assertions.assertEquals(4, data.length);
        Assertions.assertTrue(data[0].endsWith(") test1() started."));
        Assertions.assertTrue(data[1].endsWith("s] succeed."));
        Assertions.assertTrue(data[1].contains(") test1():TEST1 ["));
        Assertions.assertTrue(data[2].endsWith(") test2() started."));
        Assertions.assertTrue(data[3].endsWith("s] succeed."));
        Assertions.assertTrue(data[3].contains(") test2():TEST2 ["));
    }

    @Test
    public void externalPackageTest() {
        given()
                .when().get("/test/3")
                .then()
                .statusCode(200)
                .body(is("EXTERNAL"));

        String[] data = logLines();
        Assertions.assertEquals(2, data.length);
        Assertions.assertTrue(data[0].endsWith(") external() started."));
        Assertions.assertTrue(data[1].endsWith("s] succeed."));
        Assertions.assertTrue(data[1].contains(") external():EXTERNAL ["));
    }
}
