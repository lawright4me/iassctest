import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

public class createIncident {
    @Test
    public void testCreateIncident() throws IOException {

        String jsonFilePath = "src/test/resources/createincident.json";
        String jsonRequestBody = new String(Files.readAllBytes(Paths.get(jsonFilePath)));

        given()
                .contentType("application/json")
                .body(jsonRequestBody)
                .header("Content-Type", "application/json")
                .header("Cookie", Auth.cookie)
                .when()
                .post("http://iassc3.otn.phoenixit.ru/main/api/v1/incident/save")
                .then()
                .statusCode(200)
                .log().all();

    }
}
