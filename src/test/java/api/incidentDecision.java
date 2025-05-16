package api;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class incidentDecision {
    public static Integer decision;
    @Test
    public void incidentToDecision() {
        System.out.println("Запрос на привязку пур к инциденту");
        decision = given()
                .log().all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", Auth.cookie)
                .when()
                .post(TestConfig.getBaseUrl() + "incident-decision/" + createDecision.parentDecision + "/incident/" )
                .then()
                .statusCode(200)
                .log().ifValidationFails()
                .log().all()
                .body("status", equalTo("OK"))
                .body("data", equalTo(createDecision.parentDecision + 1))
                .extract()
                .path("data");
        System.out.println("decisionID = " + decision);
    }
}
