package api;

import org.junit.jupiter.api.Test;

import static api.TestConfig.getBaseUrl;
import static io.restassured.RestAssured.*;

public class inincidentDecision {
    Integer incident = createIncident.incident;
    @Test
    public void incidentToDecision() {
        System.out.println("Запрос на привязку пур к инциденту");
        given()
                .log().all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", Auth.cookie)

                .when()
                .post(TestConfig.getBaseUrl() + "incident-decision/" + createDecision.decision + "/incident/" + incident )


                .then()
                .statusCode(200)
                .log().ifValidationFails()
                .log().all();
    }
}
