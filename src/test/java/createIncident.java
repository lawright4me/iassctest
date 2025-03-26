import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class createIncident {
    @Test
    public void testCreateIncident() {
        given()
                .contentType("application/json")
                .body("{\"name\": \"Предотвращение террористической угрозы\", \"typeCode\": \"MITING,_SHESTVIE,_PIKETIROVANIE\", \"startTime\": \"2025-02-13T07:31:03.279Z\", \"endTime\": \"2025-02-13T07:31:03.279Z\", \"addressDto\": {\"guid\": \"f166dd64-ea9b-4ad2-ae35-52882e9906af\", \"name\": \"г. Москва, вн.тер.г. поселение Московский, кв-л 3\", \"oktmo\": \"45000000\", \"regionCode\": \"77\", \"areaCode\": \"4\", \"cityCode\": \"5\", \"placeCode\": \"6\", \"streetCode\": \"7\"}, \"coordinates\": \"54.314194, 48.403131\", \"departmentId\": \"000000000000\", \"characterCode\": \"OBSH'ESTVENNYY\", \"categoryCode\": \"SREDNEY_TYAZHESTI\", \"scaleCode\": \"LOKALNYY\", \"sourceCode\": \"NOVOSTI\", \"primaryFabula\": \"В УФСИН России по Ульяновской области сообщили о привлечении личного состава Управления Росгвардии по Ульяновской области к действиям при пресечении массовых беспорядков в учреждении. Осужденные одного из исправительных учреждений начали уничтожать имущество в отряде, заблокировали двери и установили баррикады. Сотрудникам уголовно-исполнительной системы стабилизировали обстановку и полностью пресекли массовые беспорядки осужденных.\", \"fabula\": \"В УФСИН России по Ульяновской области сообщили о привлечении личного состава Управления Росгвардии по Ульяновской области к действиям при пресечении массовых беспорядков в учреждении. Осужденные одного из исправительных учреждений начали уничтожать имущество в отряде, заблокировали двери и установили баррикады. Сотрудникам уголовно-исполнительной системы стабилизировали обстановку и полностью пресекли массовые беспорядки осужденных.\"}")
                .header("Content-Type", "application/json")
                .header("Cookie", Auth.cookie)
                .when()
                .post("http://iassc3.otn.phoenixit.ru/main/api/v1/incident/save")
                .then()
                .statusCode(200)
                .log().all();

    }
}
