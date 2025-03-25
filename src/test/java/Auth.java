package com.example;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.notNullValue;

public class Auth {

    @Test
    public void testPostRequest() {
        // URL API, который вы хотите протестировать
        String baseUrl = "http://iassc2.otn.phoenixit.ru/main/api/v1/login";

        String cookie = given()
                .header("Content-Type", "application/json")
                .header("Accept", "*/*")
                .queryParam("username", "superuser")
                .queryParam("password", "superuser")
                .when()
                .post(baseUrl) // Используем базовый URL
                .then()
                .statusCode(200) // Проверка кода состояния
                .header("Set-Cookie", notNullValue()) // Проверка содержания
                .log().all() // Логируем все детали ответа
                .extract().cookie("SC_SESSION"); // Извлекаем значение заголовка Set-Cookie

        // Логируем значение переменной cookie
        System.out.println("Cookie: " + cookie);
    }
}
