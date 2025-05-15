package api;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class createDecision {
    public static Integer parentDecision;
    @Test
    public void createDecision() throws IOException {
        System.out.println("Запрос на создание decision");
        String jsonFilePath = "src/test/resources/createdecisionKREPOST.json";
        String jsonRequestBody = new String(Files.readAllBytes(Paths.get(jsonFilePath)));

        parentDecision = given()
                .contentType("application/json")
                .body(jsonRequestBody)
                .header("Content-Type", "application/json")
                .header("Cookie", Auth.cookie)
                .when()
                .post(TestConfig.getBaseUrl() + "decision")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("createdecisionjsonSchema.json"))
                .log().all()
                .extract()
                .path("data");
        System.out.println("id decision: " + parentDecision);


    }
    public static Integer getDecision() {return parentDecision;}
}
