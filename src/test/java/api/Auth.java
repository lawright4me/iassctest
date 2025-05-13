package api;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.notNullValue;

public class Auth {

    public static String cookie;

    @Test
    public void authPost() {
        System.out.println("Запрос на аутенфикацию в системе ИАС СЦ");

        cookie = given()
                .header("Content-Type", "application/json")
                .header("Accept", "*/*")
                .queryParam("username", TestConfig.getBaseUser())
                .queryParam("password", TestConfig.getBasePassword())
                .when()
                .post(TestConfig.getBaseUrl() + "login")
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

