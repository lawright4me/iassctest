package api;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class incidentDecision {
    String incident = createIncident.incident;
    @Test
    public void incidentToDecision() {
        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", Auth.cookie)

                .when()
                .post("http://iassc3.otn.phoenixit.ru/main/api/v1/incident-decision/decision/112" + incident )


                .then()
                .statusCode(200)
                .log().ifValidationFails()
                .log().all();
    }
}
