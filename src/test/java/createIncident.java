import io.restassured.response.Response;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class createIncident {
    private Integer incidentId; // Объявляем переменную на уровне класса
    private String url;
    public void testCreateIncident() throws IOException {
        String jsonFilePath = "src/test/resources/createincident.json";
        String jsonRequestBody = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        String url = TestConfig.getBaseUrl();
        Response response = given()
                .contentType("application/json")
                .body(jsonRequestBody)
                .header("Content-Type", "application/json")
                .header("Cookie", Auth.cookie)
                .when()
                .post(url + "incident/save");

        response.then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("createincidentjsonSchema.json"))
                .log().all();

        incidentId = response.jsonPath().getInt("data");
        System.out.println("Incident Id: " + incidentId); // Логируем значение incidentId
    }

    public Integer getStatusForIncident() {
        String url = TestConfig.getBaseUrl();

        if (incidentId == null) {
            System.out.println("incidentId is null before making the request.");
            return null; // Возвращаем null, если incidentId не инициализирован
        }

        // Переменная для хранения statusId
        final Integer[] statusId = {null}; // Используем массив для объявления переменной `final`

        // Используем Awaitility для выполнения ретраев
        Awaitility.await()
                .atMost(180, TimeUnit.SECONDS)    // Максимальное время ожидания
                .pollDelay(5, TimeUnit.SECONDS)   // Интервал между попытками
                .until(() -> {
                    Response response = given()
                            .contentType("application/json")
                            .header("Cookie", Auth.cookie)
                            .log().all()
                            .when()
                            .get(url + "incident/" + incidentId);

                    response.then().log().all();

                    if (response.jsonPath().getMap("data.incidentClass") != null) {
                        // Сохраняем statusId в массив
                        statusId[0] = response.jsonPath().getInt("data.incidentClass.id");
                        return true; // Указываем, что условие выполнено
                    } else {
                        System.out.println("Поле data.incidentClass отсутствует в ответе.");
                        return false; // Условие не выполнено
                    }
                });

        return statusId[0]; // Возвращаем statusId
    }
    @Test
    public void testWorkflow() throws IOException {
        testCreateIncident(); // Сначала создаем инцидент
        Integer statusId = getStatusForIncident(); // Затем получаем статус
        System.out.println("Status Id: " + statusId);
    }
}