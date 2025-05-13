package api;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.notNullValue;

public class Auth {

    public static String cookie;

    @Test
    public void authPost() {
        String baseUrl = TestConfig.getBaseUrl() + "login";
        String user = TestConfig.getBaseUser();
        String password = TestConfig.getBasePassword();

        cookie = given()
                .header("Content-Type", "application/json")
                .header("Accept", "*/*")
                .queryParam("username", user)
                .queryParam("password", password)
                .when()
                .post(baseUrl)
                .then()
                .header("Set-Cookie", notNullValue())
                .log().all()
                .extract().header("Set-Cookie");

        System.out.println("Cookie: " + cookie);
    }

    public static String getCookie() {
        return cookie;
    }
}

