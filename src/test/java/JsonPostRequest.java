import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JsonPostRequest {

    private static final String TARGET_URL = "http://your-api-endpoint.com/api"; // Замените на ваш URL

    public static void main(String[] args) {
        // Массив файлов JSON
        String[] jsonFiles = {"data1.json", "data2.json", "data3.json", "data4.json"};

        for (String jsonFile : jsonFiles) {
            try {
                String jsonData = readJsonFromFile(jsonFile);
                sendPostRequest(jsonData);
            } catch (IOException e) {
                System.err.println("Error reading file " + jsonFile + ": " + e.getMessage());
            }
        }
    }


    private static String readJsonFromFile(String filename) throws IOException {
        StringBuilder jsonData = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonData.append(line);
            }
        }

        return jsonData.toString();
    }

    private static void sendPostRequest(String jsonData) {
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(jsonData)
                .when()
                .post(TARGET_URL)
                .then()
                .extract().response();

        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());
    }
}
