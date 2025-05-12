import static io.restassured.RestAssured.given;

public class createDecisionTask {
    public static void main(String[] args) {
        given()
                .contentType("application/json")
                .when()
                .post(TestConfig.getBaseUrl() + "decision-task")
    }
}
