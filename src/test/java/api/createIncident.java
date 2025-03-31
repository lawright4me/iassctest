package api;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class createIncident {

    public static String incident;

    @Test
    public void testCreateIncident() throws IOException {

        String jsonFilePath = "src/test/resources/createincident.json";
        String jsonRequestBody = new String(Files.readAllBytes(Paths.get(jsonFilePath)));


        incident = given()
                .contentType("application/json")
                .body(jsonRequestBody)
                .header("Content-Type", "application/json")
                .header("Cookie", Auth.cookie)
                .when()
                .post("http://iassc3.otn.phoenixit.ru/main/api/v1/incident/save")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("createincidentjsonSchema.json"))
                .log().all().toString();

    }
}
