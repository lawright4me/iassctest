import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class createDecision {
    @Test
    public void createDecision() throws IOException {

        String jsonFilePath = "src/test/resources/createdecisionKREPOST.json";
        String jsonRequestBody = new String(Files.readAllBytes(Paths.get(jsonFilePath)));

        given()
                .contentType("application/json")
                .body(jsonRequestBody)
                .header("Content-Type", "application/json")
                .header("Cookie", Auth.cookie)
                .when()
                .post("http://iassc3.otn.phoenixit.ru/main/api/v1/decision")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("createdecisionjsonSchema.json"))
                .log().all();
    }
}
