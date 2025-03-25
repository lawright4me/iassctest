package com.example;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.notNullValue;

public class Auth {

    @Test
    public void testPostRequest() {
        
        String baseUrl = "http://iassc2.otn.phoenixit.ru/main/api/v1/login";

        String cookie = given()
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
                .extract().cookie("SC_SESSION"); 

        
        System.out.println("Cookie: " + cookie);
    }
}
