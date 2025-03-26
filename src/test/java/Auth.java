
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.notNullValue;

public class Auth {

    public static String cookie;

    @Test
    public void testPostRequest() {
        String baseUrl = "http://iassc3.otn.phoenixit.ru/main/api/v1/login";

        cookie = given()
                .header("Content-Type", "application/json")
                .header("Accept", "*/*")
                .queryParam("username", "superuser")
                .queryParam("password", "superuser")
                .when()
                .post(baseUrl)
                .then()
                .statusCode(200)
                .header("Set-Cookie", notNullValue())
                .log().all()
                .extract().header("Set-Cookie");

        System.out.println("Cookie: " + cookie);
    }

    public static String getCookie() {
        return cookie;
    }
}

