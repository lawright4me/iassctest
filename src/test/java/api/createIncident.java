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
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class createIncident {

    Integer idPrecedentMl;
    Integer idPrecedentManual;

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


    public static Integer incident;

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
    public void mlSend() {
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

    }
