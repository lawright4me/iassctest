package api;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static api.DatabaseHelper.*;
import static api.TestConfig.getBaseUrl;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class createIncident {
    public static Integer incident;

    public static Integer idPrecedentMl;
    public static Integer idPrecedentManual;
    public static Integer idKeyFactorMl;
    public static Integer idKeyFactorManual;
    public static Integer idNextIncidentMl;

    String jsonFilePathmlSendPrecedent = "src/test/resources/request/ppr/incident/mlSendPrecedent.json";
    String requestJsonBodymlSendPrecedentForIncidentTemplate = new String(Files.readAllBytes(Paths.get(jsonFilePathmlSendPrecedent)));
    String requestJsonBodymlSendPrecedentForIncident = requestJsonBodymlSendPrecedentForIncidentTemplate.replace("{{incident}}", String.valueOf(incident));

    String jsonFilePathcheckPrecedentForIncident = "src/test/resources/request/ppr/incident/checkPrecedent.json";
    String RequestBodyCheckPrecedentForIncidentTemplate = new String(Files.readAllBytes(Paths.get(jsonFilePathcheckPrecedentForIncident)));
    String RequestBodyCheckPrecedentForIncident = RequestBodyCheckPrecedentForIncidentTemplate.replace("{{incident}}", String.valueOf(incident));

    String jsonFilePathcheckPrecedentForIncidentManual = "src/test/resources/request/ppr/incident/checkPrecedentForIncidentManual.json";
    String RequestBodyCheckPrecedentForIncidentManual = new String(Files.readAllBytes(Paths.get(jsonFilePathcheckPrecedentForIncidentManual)));

    String jsonFilePathAddPrecedentToIncident = "src/test/resources/request/ppr/incident/addPrecedentToIncident.json";
    String RequestBodyAddprecedentToIncidentTemplate = new String(Files.readAllBytes(Paths.get(jsonFilePathAddPrecedentToIncident)));
    String RequestBodyAddPrecedentToIncident = RequestBodyAddprecedentToIncidentTemplate
            .replace("{{precedentMl}}", String.valueOf(idPrecedentMl))
            .replace("{{precedent}}", String.valueOf(idPrecedentManual));

    String jsonFilePathCheckAddPrecedent = "src/test/resources/request/ppr/incident/checkAddPrecedentToIncident.json";
    String RequestBodyCheckAddPrecedent = new String(Files.readAllBytes(Paths.get(jsonFilePathCheckAddPrecedent)));

    String jsonFilePathMlSendKeyFactors = "src/test/resources/request/ppr/incident/mlSendKeyFactors.json";
    String requestJsonBodymlSendKeyFactorsTemplate = new String(Files.readAllBytes(Paths.get(jsonFilePathMlSendKeyFactors)));
    String requestJsonBodymlSendKeyFactors = requestJsonBodymlSendKeyFactorsTemplate.replace("{{incident}}", String.valueOf(incident));

    String jsonFilePathCheckSendKeyFactorsManual = "src/test/resources/request/ppr/incident/checkKeyFactorsForIncidentManual.json";
    String requestJsonBodyCheckManualKeyFactorsTemplate = new String(Files.readAllBytes(Paths.get(jsonFilePathCheckSendKeyFactorsManual)));
    String requestJsonBodyCheckManualKeyFactors = requestJsonBodyCheckManualKeyFactorsTemplate.replace("{{incident}}", String.valueOf(incident));


    String jsonFilePathAddKeyFactors = "src/test/resources/request/ppr/incident/addKeyFactorsToIncident.json";
    String requestBodyAddKeyFactorsTemplate = new String(Files.readAllBytes(Paths.get(jsonFilePathAddKeyFactors)));
    String requestBodyAddKeyFactors = requestBodyAddKeyFactorsTemplate
            .replace("{{indicatorMl}}", String.valueOf(idKeyFactorMl))
            .replace("{{indicatorManual}}", String.valueOf(idKeyFactorManual));



    String jsonFilePathcheckNextIncidentMl = "src/test/resources/request/ppr/incident/checkNextIncidentMl.json";
    String requestBodymlcheckNextIncidentMl = new String(Files.readAllBytes(Paths.get(jsonFilePathcheckNextIncidentMl)));




    public createIncident() throws IOException {
    }

    @Test
    @Order(1)
    public void testCreateIncident() throws IOException {
        System.out.println("Запрос на создание incident");
        String jsonFilePath = "src/test/resources/request/ppr/incident/createincident.json";
        String jsonRequestBody = new String(Files.readAllBytes(Paths.get(jsonFilePath)));

        incident = given()
                .contentType("application/json")
                .body(jsonRequestBody)
                .header("Content-Type", "application/json")
                .header("Cookie", Auth.cookie)
                .when()
                .post(getBaseUrl() + "incident/save")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("response/schema/createincidentjsonSchema.json"))
                .log().all()
                .extract()
                .path("data");
        System.out.println("incident id = " + incident);

    }
    public static Integer getIncident() {return incident;}

    @Test
    @Order(2)
    public void testGetIncident() {
        given()
        .contentType("application/json")
                .header("Cookie", Auth.cookie)
                .when()
                .get(getBaseUrl() + "incident/" + incident)
                .then()
                .statusCode(200)
                .log().all();
    }
    @Test
    @Order(3)
    public void mlSendPrecedent() {
        given()
                .log().all()
        .contentType("application/json")
                .header("Cookie", Auth.cookie)
                .body(requestJsonBodymlSendPrecedentForIncident)
                .when()
                .post(getBaseUrl() + "ml/send")
                .then()
                .statusCode(200)
                .body("status", equalTo("OK"));


    }
    @Test
    @Order(4)
    public void checkPrecedentForIncidentMl() {
        String idTask = null;
        String requestId = null;
        DatabaseHelper dbHelper = new DatabaseHelper();

        try (Connection conn1 = DriverManager.getConnection(URL3, DB_USER3, DB_PASSWORD3);
             PreparedStatement pstmt1 = conn1.prepareStatement(
                     "SELECT request_id FROM ml.tb_ml_request WHERE entity_id = ? AND ml_task_type = 'CLOSEST_INCIDENT_REPORT'")) {

            pstmt1.setInt(1, Integer.parseInt(String.valueOf(incident)));
            try (ResultSet rs1 = pstmt1.executeQuery()) {
                if (rs1.next()) {
                    requestId = rs1.getString("request_id");
                }
            }

            if (requestId != null) {
                // Второй запрос к другой базе данных
                try (Connection conn2 = DriverManager.getConnection(URL1ML, DB_USERML, DB_PASSWORDML);
                     PreparedStatement pstmt2 = conn2.prepareStatement(
                             "SELECT id FROM public.task WHERE req_id = ?")) {

                    pstmt2.setString(1, requestId);
                    try (ResultSet rs2 = pstmt2.executeQuery()) {
                        if (rs2.next()) {
                            idTask = rs2.getString("id");
                        }
                    }
                }
            }

            if (idTask != null) {
                boolean isDone = dbHelper.waitForTaskStatusInDB(
                        Integer.parseInt(idTask), // предполагается что это число
                        "DONE",
                        30,
                        15000
                );

                if (isDone) {
                    Object NotNull = null;
                   idPrecedentMl = given()
                            .log().all()
                            .contentType("application/json")
                            .header("Cookie", Auth.cookie)
                            .body(RequestBodyCheckPrecedentForIncident)
                            .when()
                            .post(getBaseUrl() + "incident/" + incident + "/precedent/auto-search/page")
                            .then()
                            .log().all()
                            .statusCode(200)
                             .body("id", equalTo(NotNull))
                           .extract()
                           .jsonPath()
                           .getInt("data.content[0].id");
                   System.out.println("idPrecedentMl = " + idPrecedentMl);

                } else {
                    // Обработка таймаута или ошибки ожидания
                }
            } else {
                // Не нашли requestId или id задачи
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(5)
    public void checkPrecedentForIncidentManual() {
        idPrecedentManual = given()
                .log().all()
                .contentType("application/json")
                .header("Cookie", Auth.cookie)
                .body(RequestBodyCheckPrecedentForIncidentManual)
                .when()
                .post(getBaseUrl() + "incident/" + incident + "/precedent/manual-search/page")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getInt("data.content[0].id");
            System.out.println("idPrecedentManual = " + idPrecedentManual);
    }

    @Test
    @Order(6)
    public void addPrecedentToIncident() {
        given()
                .log().all()
                .contentType("application/json")
                .header("Cookie", Auth.cookie)
                .body(RequestBodyAddPrecedentToIncident)
                .post(TestConfig.getBaseUrl() + "incident/" + incident + "/precedent/append")
                .then()
                .log().all()
                .statusCode(200)
                .body("status", equalTo("OK"));
    }
    @Test
    @Order(7)
    public void checkAddPrecedentToIncident() {
        given()
                .log().all()
                .contentType("application/json")
                .header("Cookie", Auth.cookie)
                .body(RequestBodyCheckAddPrecedent)
                .when()
                .post(getBaseUrl() + "incident/" + incident + "/precedent/page")
                .then()
                .log().all()
                .statusCode(200)
                .body("data.content[0].id", anyOf(equalTo(idPrecedentMl), equalTo(idPrecedentManual)));
    }

    @Test
    @Order(8)
    public void mlSendKeyFactors()  {
        given()
                .log().all()
                .contentType("application/json")
                .header("Cookie", Auth.cookie)
                .body(requestJsonBodymlSendKeyFactors)
                .post(TestConfig.getBaseUrl() + "ml/send")
                .then()
                .log().all()
                .statusCode(200)
                .body("status", equalTo("OK"));
    }
    @Test
    @Order(9)
    public void checkKeyFactorsForIncidentMl() {
        String idTask = null;
        String requestId = null;
        DatabaseHelper dbHelper = new DatabaseHelper();

        try (Connection conn1 = DriverManager.getConnection(URL3, DB_USER3, DB_PASSWORD3);
             PreparedStatement pstmt1 = conn1.prepareStatement(
                     "SELECT request_id FROM ml.tb_ml_request WHERE entity_id = ? AND ml_task_type = 'INCIDENT_KEY_FACTORS'")) {

            pstmt1.setInt(1, Integer.parseInt(String.valueOf(incident)));
            try (ResultSet rs1 = pstmt1.executeQuery()) {
                if (rs1.next()) {
                    requestId = rs1.getString("request_id");
                }
            }

            if (requestId != null) {
                // Второй запрос к другой базе данных
                try (Connection conn2 = DriverManager.getConnection(URL1ML, DB_USERML, DB_PASSWORDML);
                     PreparedStatement pstmt2 = conn2.prepareStatement(
                             "SELECT id FROM public.task WHERE req_id = ?")) {

                    pstmt2.setString(1, requestId);
                    try (ResultSet rs2 = pstmt2.executeQuery()) {
                        if (rs2.next()) {
                            idTask = rs2.getString("id");
                        }
                    }
                }
            }

            if (idTask != null) {
                boolean isDone = dbHelper.waitForTaskStatusInDB(
                        Integer.parseInt(idTask), // предполагается что это число
                        "DONE",
                        30,
                        15000
                );

                if (isDone) {
                    int maxRetries = 10;
                    int attempt = 0;
                    Integer indicatorId = null;

                    while (attempt < maxRetries) {
                        Object NotNull = null;
                        indicatorId = given()
                                .log().all()
                                .contentType("application/json")
                                .header("Cookie", Auth.cookie)
                                .when()
                                .get(getBaseUrl() + "incident/" + incident + "/indicator/auto-search")
                                .then()
                                .log().all()
                                .statusCode(200)
                                .extract()
                                .jsonPath()
                                .getInt("data[0].indicatorId");

                        System.out.println("Попытка " + (attempt + 1) + ": indicatorId=" + indicatorId);

                        if (indicatorId != null && indicatorId != 0) { // или просто проверить != null
                            // Успех, выходим из цикла
                            break;
                        } else {
                            attempt++;
                            // Можно добавить небольшую задержку между попытками
                            Thread.sleep(1000); // задержка 1 секунда
                        }
                    }

                    if (indicatorId != null && indicatorId != 0) {
                        idKeyFactorMl = indicatorId;
                        System.out.println("Успешно получен indicatorId: " + idKeyFactorMl);
                    } else {
                        System.out.println("Не удалось получить indicatorId после " + maxRetries + " попыток");
                    }
                } else {
                    // Обработка таймаута или ошибки ожидания
                }
            } else {
                // Не нашли requestId или id задачи
            }

        } catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("idKeyFactorMl = " + idKeyFactorMl);
    }

    @Test
    @Order(10)
    public void checkKeyFactorsForIncidentManual() {
        idKeyFactorManual = given()
                .log().all()
                .contentType("application/json")
                .header("Cookie", Auth.cookie)
                .body(requestJsonBodyCheckManualKeyFactors)
                .when()
                .post(getBaseUrl() + "incident/" + incident + "/indicator/manual-search/page")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getInt("data.content[0].indicatorId");
        System.out.println("idKeyFactorManual = " + idKeyFactorManual);
    }
    @Test
    @Order(11)
    public void addKeyFactorsToIncident() {
        given()
        .log().all()
                .contentType("application/json")
                .header("Cookie", Auth.cookie)
                .body(requestBodyAddKeyFactors)
                .post(TestConfig.getBaseUrl() + "incident/" + incident + "/indicator/pivot-table")
                .then()
                .log().all()
                .statusCode(200)
                .body("status", equalTo("OK"));
    }

    @Test
    @Order(12)
    public void CheckAddKeyFactorsToIncidentManual() {
        given()
        .log().all()
                .contentType("application/json")
                .header("Cookie", Auth.cookie)
                .get(TestConfig.getBaseUrl() + "incident/" + incident + "/indicator/pivot-table")
                .then()
                .log().all()
                .statusCode(200)
                .body("data.content[0].id", anyOf(equalTo(idKeyFactorManual), equalTo(idKeyFactorMl)));
        System.out.println("idKeyFactorManual = " + idKeyFactorManual);
        System.out.println("idKeyFactorMl = " + idKeyFactorMl);
    }

    @Test
    @Order(13)
    public void mlSendNextIncident() throws IOException {
        System.out.println(incident);
        String jsonFilePathmlSendNextIncident = "src/test/resources/request/ppr/incident/mlSendNextIncident.json";
        String requestBodymlSendNextIncidentTemplate = new String(Files.readAllBytes(Paths.get(jsonFilePathmlSendNextIncident)));
        String requestBodymlSendNextIncident = requestBodymlSendNextIncidentTemplate.replace("{{incident}}", String.valueOf(incident));
        given()
        .log().all()
                .contentType("application/json")
                .header("Cookie", Auth.cookie)
                .body(requestBodymlSendNextIncident)
                .post(TestConfig.getBaseUrl() + "ml/send")
                .then()
                .log().all()
                .statusCode(200)
                .body("status", equalTo("OK"));
    }

    @Test
    @Order(14)
    public void NextIncidentMl() {
        String idTask = null;
        String requestId = null;
        DatabaseHelper dbHelper = new DatabaseHelper();

        try (Connection conn1 = DriverManager.getConnection(URL3, DB_USER3, DB_PASSWORD3);
             PreparedStatement pstmt1 = conn1.prepareStatement(
                     "SELECT request_id FROM ml.tb_ml_request WHERE entity_id = ? AND ml_task_type = 'NEXT_INCIDENT'")) {

            pstmt1.setInt(1, Integer.parseInt(String.valueOf(incident)));
            try (ResultSet rs1 = pstmt1.executeQuery()) {
                if (rs1.next()) {
                    requestId = rs1.getString("request_id");
                }
            }

            if (requestId != null) {
                // Второй запрос к другой базе данных
                try (Connection conn2 = DriverManager.getConnection(URL1ML, DB_USERML, DB_PASSWORDML);
                     PreparedStatement pstmt2 = conn2.prepareStatement(
                             "SELECT id FROM public.task WHERE req_id = ?")) {

                    pstmt2.setString(1, requestId);
                    try (ResultSet rs2 = pstmt2.executeQuery()) {
                        if (rs2.next()) {
                            idTask = rs2.getString("id");
                        }
                    }
                }
            }

            if (idTask != null) {
                boolean isDone = dbHelper.waitForTaskStatusInDB(
                        Integer.parseInt(idTask), // предполагается что это число
                        "DONE",
                        30,
                        15000
                );

                if (isDone) {
                    Object NotNull = null;
                    given()
                            .log().all()
                            .contentType("application/json")
                            .header("Cookie", Auth.cookie)
                            .when()
                            .post(getBaseUrl() + "incident/" + incident + "/chain")
                            .then()
                            .log().all()
                            .statusCode(200)
                            .body("status", equalTo("OK"));

                } else {
                    // Обработка таймаута или ошибки ожидания
                }
            } else {
                // Не нашли requestId или id задачи
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(15)
    public void checkNextIncidentMl() {
        given()
                .log().all()
                .contentType("application/json")
                .header("Cookie", Auth.cookie)
                .body(requestBodymlcheckNextIncidentMl)
                .when()
                .post(getBaseUrl() + "incident/" + incident + "/chain/auto-search/page")
                .then()
                .log().all()
                .statusCode(200)
                .body("status", equalTo("OK"));


    }

    }
