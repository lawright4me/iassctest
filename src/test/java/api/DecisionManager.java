import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DecisionManager {

    private final String apiUrl;
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;
    private final String cookieValue = "SC_SESSION=ZjhlMjk1OWQtY2Q2ZS00MjI0LTk0MzQtZTc5MDk4ZTRhNzk5; Max-Age=7200; Expires=Thu, 27 Mar 2025 15:18:17 GMT; Domain=phoenixit.ru; Path=/; HttpOnly";

    public DecisionManager(String apiUrl, String dbUrl, String dbUser, String dbPassword) {
        this.apiUrl = apiUrl;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    public List<Integer> createDecisions(List<String> jsonFiles) throws Exception {
        List<Integer> decisionIds = new ArrayList<>();

        for (String jsonFilePath : jsonFiles) {
            File jsonFile = new File(jsonFilePath);

            Response response = RestAssured.given()
                    .contentType("application/json")
                    .body(jsonFile)
                    .header("Cookie", cookieValue)
                    .post(apiUrl);

            if (response.getStatusCode() == 200) {
                decisionIds.add(response.jsonPath().getInt("data"));
            } else {
                throw new RuntimeException("Failed : decisions don't create : " + response.getStatusCode());
            }
        }
        return decisionIds;
    }

    // Метод для обновления решений в базе данных
    public void updateDecisions(Map<Integer, String> decisionUpdates) throws Exception {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            String updateSQL = "UPDATE sc.tb_decision SET incident_class_code = ? WHERE decision_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
                for (Map.Entry<Integer, String> entry : decisionUpdates.entrySet()) {
                    preparedStatement.setString(1, entry.getValue());
                    preparedStatement.setInt(2, entry.getKey());
                    preparedStatement.executeUpdate();
                }
            }
        }
    }

    public void logDecisionsFromDatabase() throws Exception {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            String querySQL = "SELECT decision_id, incident_class_code FROM sc.tb_decision";
            try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int decisionId = resultSet.getInt("decision_id");
                    String incidentClassCode = resultSet.getString("incident_class_code");
                    // Логируем информацию о каждом решении
                    System.out.println("Decision ID: " + decisionId + ", Incident Class Code: " + incidentClassCode);
                }
            }
        }
    }


    public static void main(String[] args) {
        try {
            String apiUrl = "http://iassc3.otn.phoenixit.ru/main/api/v1/decision";
            String dbUrl = "jdbc:postgresql://10.125.20.200:5432/scmain3";
            String dbUser = "scadmin3";
            String dbPassword = "Al8sWbbZ14ZR";

            DecisionManager decisionManager = new DecisionManager(apiUrl, dbUrl, dbUser, dbPassword);


            List<String> jsonFiles = List.of(

                    "src/test/resources/createdecisionLAVINA.json",
                    "src/test/resources/createdecisionEDELWEIS.json",
                    "src/test/resources/createdecisionGRANITSA.json",
                    "src/test/resources/createdecisionKREPOST.json",
                    "src/test/resources/createdecisionTAIFUN.json",
                    "src/test/resources/createdecisionVULKAN.json",
                    "src/test/resources/createdecisionZARYA.json",
                    "src/test/resources/createdecisionOBJECT.json",
                    "src/test/resources/createdecisionSIRENA.json"
            );


            List<Integer> createdDecisionIds = decisionManager.createDecisions(jsonFiles);


            String[] incidentClassCodes = {
                    "CHP_FSIN",
                    "TERRORIZM,_EKSTREMIZM",
                    "CHP_GOSGRANITSA",
                    "NAPADENIE_NA_ZDANIE_OVD",
                    "CHS",
                    "MASSOVYE_BESPORYADKI",
                    "ZALOZHNIKI",
                    "ZAKHVAT_OVO",
                    "ROZYSK_ILI_ZADERZHANIE_VOORUZHENNOGO_PRESTUPNIKA"
            };


            Map<Integer, String> decisionUpdates = new HashMap<>();
            for (int i = 0; i < createdDecisionIds.size(); i++) {
                decisionUpdates.put(createdDecisionIds.get(i), incidentClassCodes[i]);
            }


            decisionManager.updateDecisions(decisionUpdates);

            decisionManager.logDecisionsFromDatabase();

            System.out.println("Decisions created and updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
