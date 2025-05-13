package api;

import io.restassured.response.ResponseBody;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class createDecisionTask {

    String jsonFilePath = "src/test/resources/createDecisionTask.json";
    String jsonTemplate = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
    String jsonRequestBody = jsonTemplate.replace("{{decisionId}}", String.valueOf(createDecision.decision));

    public createDecisionTask() throws IOException {
    }

    @Test
    public void createTaskTest() {
     Integer  decisionTask =  given()
               .log().all()
                .contentType("application/json")
               .header("Cookie", Auth.cookie)
               .body(jsonRequestBody)
                .when()
                .post(TestConfig.getBaseUrl() + "decision-task")
               .then()
               .statusCode(200)
               .log().all()
               .extract()
               .path("data");
       System.out.println("decision task = " + decisionTask);
    }
}
