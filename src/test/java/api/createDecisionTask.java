package api;

import io.restassured.response.ResponseBody;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class createDecisionTask {

    public static Integer  decisionTask;

    String jsonFilePath = "src/test/resources/request/ppr/decision/createDecisionTask.json";
    String jsonTemplate = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
    String jsonRequestBodyDecisionTask = jsonTemplate.replace("{{decisionId}}", String.valueOf(createDecision.decision));

    String jsonFilePathGroupForce = "src/test/resources/request/ppr/decision/createDecisionTaskGroupForce.json"; // тело для добавления группы задействоаемых
    String jsonRequestBodyGroupForce = new String(Files.readAllBytes(Paths.get(jsonFilePathGroupForce)));       // сил и средств

    String JsonFilePathGroupForceGv = "src/test/resources/request/ppr/decision/createDecisionTaskGroupForceGv.json";    // тело для добавления группы задействоаемых
    String JsonRequestBodyGroupForceGv = new String(Files.readAllBytes(Paths.get(JsonFilePathGroupForceGv)));            // сил и средств гос. власти


    public createDecisionTask() throws IOException {
    }

    @Test
    @Order(1)
    public void createTaskTest() {
     decisionTask =  given()
               .log().all()
                .contentType("application/json")
               .header("Cookie", Auth.cookie)
               .body(jsonRequestBodyDecisionTask)
                .when()
                .post(TestConfig.getBaseUrl() + "decision-task")
               .then()
               .statusCode(200)
               .log().all()
               .extract()
               .path("data");
       System.out.println("decision task = " + decisionTask);
    }
    @Test
    @Order(2)
    public void addGroupForceDecisionTask() throws IOException {
        given()
                .log().all()
                .contentType("application/json")
                .header("Cookie", Auth.cookie)
                .body(jsonRequestBodyGroupForce)
                .when()
                .post(TestConfig.getBaseUrl() + "decision-task/" + decisionTask + "/group-force")
                .then()
                .statusCode(200)
                .log().all()
                .body("status", equalTo("OK"));

    }
    @Test
    @Order(3)
    public void addGroupForceDecisionTaskGoverment() throws IOException {
        given()
        .log().all()
                .contentType("application/json")
                .header("Cookie", Auth.cookie)
                .body(JsonRequestBodyGroupForceGv)
                .when()
                .post(TestConfig.getBaseUrl() + "decision-task/" + decisionTask + "/group-force-gv")
                .then()
                .statusCode(200)
                .log().all()
                .body("status", equalTo("OK"));

    }
    @Test
    @Order(4)
    public void checkDecisionTask() throws IOException {
        given()
        .log().all()
                .contentType("application/json")
                .header("Cookie", Auth.cookie)
                .get(TestConfig.getBaseUrl() + "decision-task/" + decisionTask)
                .then()
                .statusCode(200)
                .log().all()
                .body("status", equalTo("OK"))
                .body("data.id", equalTo(decisionTask));
    }
}
