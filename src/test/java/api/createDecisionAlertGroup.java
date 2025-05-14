package api;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class createDecisionAlertGroup {
    String jsonFilePath = "src/test/resources/request/createDecisionAlertGroup.json";
    String jsonTemplate = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
    String jsonRequestBodyAlertGroup = jsonTemplate.replace("{{decisionId}}", String.valueOf(createDecision.decision));

    String jsonFilePathGroup = "src/test/resources/request/addUsersForAlertGroup.json";
    String jsonRequestBodyAlertGroupAddUsers = new String(Files.readAllBytes(Paths.get(jsonFilePathGroup)));

    String jsonFilePathAlertGroupCheck = "src/test/resources/request/checkAlertGroup.json";
    String jsonRequestBodyAlertGroupCheckTemplate = new String(Files.readAllBytes(Paths.get(jsonFilePathAlertGroupCheck)));
    String jsonRequestBodyAlertGroupCheck = jsonRequestBodyAlertGroupCheckTemplate.replace("{{decisionId}}", String.valueOf(createDecision.decision));


    public static Integer alertGroup;

    public createDecisionAlertGroup() throws IOException {
    }

    @Test
    @Order(1)
    public void createAlertGroup() {
        System.out.println("Запрос на добавление группы оповещения");
        alertGroup = given()
                .log().all()
                .contentType("application/json")
                .body(jsonRequestBodyAlertGroup)
                .header("Content-Type", "application/json")
                .header("Cookie", Auth.cookie)
                .when()
                .post(TestConfig.getBaseUrl() + "decision-alert-group")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .path("data");
        System.out.println("alertGroupId = " + alertGroup);
    }
    public static Integer getAlertGroup() {
        return alertGroup;
    }
    String jsonFilePathAlertGroupUsersAddCheck = "src/test/resources/request/addUsersAlertGroupCheck.json";
    String jsonRequestBodyAlertGroupUsersAddCheckTemplate = new String(Files.readAllBytes(Paths.get(jsonFilePathAlertGroupUsersAddCheck)));
    String jsonRequestBodyAlertGroupUsersAddCheck = jsonRequestBodyAlertGroupUsersAddCheckTemplate.replace("\"alertgroup\"", String.valueOf(createDecisionAlertGroup.alertGroup));

    @Test
    @Order(2)
    public void addUsersToAlertGroup() {
        System.out.println("Запрос на добавление пользователей в группу оповещения");
        given()
                .log().all()
                .body(jsonRequestBodyAlertGroupAddUsers)
                .header("Content-Type", "application/json")
                .header("Cookie", Auth.cookie)
                .when()
                .put(TestConfig.getBaseUrl() + "decision-alert-group/" + createDecisionAlertGroup.alertGroup + "/add-user")
                .then()
                .log().all()
                .statusCode(200);
    }
    @Test
    @Order(3)
    public void checkAlertGroup() {
        System.out.println("Запрос на проверку созданной группы оповещения");
        given()
        .log().all()
                .body(jsonRequestBodyAlertGroupCheck)
                .header("Content-Type", "application/json")
                .header("Cookie", Auth.cookie)
                .when()
                .post(TestConfig.getBaseUrl() + "decision-alert-group/page")
                .then()
                .log().all()
                .statusCode(200)
                .body("data.content[0].id", equalTo(alertGroup));
    }

    @Test
    @Order(4)
    public void checkUsersAlertGroup() {
        System.out.println("Запрос на проверку добавленных пользователей в группу оповещения");
        given()
                .log().all()
                .body(jsonRequestBodyAlertGroupUsersAddCheck)
                .header("Content-Type", "application/json")
                .header("Cookie", Auth.cookie)
                .when()
                .post(TestConfig.getBaseUrl() + "decision-alert-group/alert-group-user/page")
                .then()
                .log().all()
                .statusCode(200)
                .body("data.content.login", hasItems("operalex", "superuser"))
                .body("status", equalTo("OK"));
    }
}
