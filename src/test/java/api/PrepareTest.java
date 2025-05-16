package api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static api.DatabaseHelper.*;
import static api.TestConfig.getBaseUrl;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PrepareTest {

    private final String baseUrl = "http://your-api.com"; // укажите ваш базовый URL
    private final List<String> aliases = Arrays.asList("INCIDENT", "ACCIDENT", "PUBLICATION", "AOE", "RULE", "KEYWORD");


    private static String createRequestBody(List<String> aliases) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"aliases\": [");
        for (int i = 0; i < aliases.size(); i++) {
            sb.append("\"").append(aliases.get(i)).append("\"");
            if (i < aliases.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    class AliasInfo {
        String alias;
        String variableName; // например, indexIncident
        String dateFrom = "2024-01-01"; // например, "2024-01-01"
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        boolean skipDate;

        public AliasInfo(String alias, String variableName, String dateFrom, String currentDate, boolean skipDate) {
            this.alias = alias;
            this.variableName = variableName;
            this.dateFrom = dateFrom;
            this.currentDate = currentDate;
            this.skipDate = skipDate;
        }
    }

    static class Params {
        String param1;
        String param2;
        // добавьте другие параметры по необходимости

        public Params(String param1, String param2) {
            this.param1 = param1;
            this.param2 = param2;
        }
    }


    private String buildRequestBody(Params params, String type) {
        // Формируйте тело запроса в зависимости от типа и параметров
        // Например, можно использовать JSON-шаблон или строку
        return "{ \"param1\": \"" + params.param1 + "\", \"param2\": \"" + params.param2 + "\", \"type\": \"" + type + "\" }";
    }

    @Test
    @Order(1)
    public void loadEntity() throws SQLException {
        try {
            Connection conn1 = DriverManager.getConnection(URL3, DB_USER3, DB_PASSWORD3);
            PreparedStatement pstmt1 = conn1.prepareStatement(
                    "INSERT INTO sc.tb_publication\n" +
                            "(title, status_code, \"text\", source_name, source_region, publish_date, received_time, ml_finish_time)\n" +
                            "VALUES\n" +
                            "('В Ефремове (Тульская область) в результате взрыва газа в одной из квартир многоэтажки','NOVOST','В Ефремове (Тульская область) в результате взрыва газа в одной из квартир многоэтажки, обрушилась часть дома. Об этом пишет ТАСС.На кадрах видеоролика, опубликованного каналом, видно, что один из подъездов многоэтажки сильно разрушен. В настоящее время выясняются обстоятельства ЧП. Представители экстренных служб работают на месте происшествия. По предварительным данным, взрыв произошел на четвертом или пятом этаже. Первый подъезд разрушен полностью. Жертвами взрыва стали пять человек. Под завалами могут находиться еще люди. В ГУ МЧС сообщили, что из-под завалов спасено несколько человек. В том числе, удалось обнаружить и спасти ребенка.','ТАСС','Российская Федерация',CURRENT_DATE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),\n" +
                            "('В Миассе умер лидер организованной преступной группировки','NOVOST','В Миассе умер лидер организованной преступной группировки \"\"Турбазовские\"\" Сергей Чащин,. \"\"Причина смерти пока неясна, ему был 51 год. В исправительном центре у него была довольно мягкая форма содержания, он даже ходил без конвоя по территории и иногда мог уезжать домой\"\", — говорится в материале. В статье указали, что Чащина задержали 21 ноября 2012 года, а в июле 2014-го он был приговорен к 13 годам колонии строгого режима.','ТАСС','Российская Федерация',CURRENT_DATE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),\n" +
                            "('В многоквартирном доме на улице Пермякова в Нижнем Новгороде загорелся мусоропровод','NOVOST','В многоквартирном доме на улице Пермякова в Нижнем Новгороде загорелся мусоропровод. В Нижнем Новгороде произошел пожар в жилом доме на улице Пермякова. Возгорание произошло в мусоропроводе после неосторожного обращения с огнем неизвестных лиц. Спасателям пришлось работать в условиях повышенной опасности для здоровья людей, так как в здании было сильное задымление. К счастью, благодаря своевременной помощи МЧС, никто не пострадал. Спасатели в защитных костюмах и масках эвакуировали семь человек, в том числе двоих детей, чтобы предотвратить отравление продуктами gorenje. Пожар был полностью потушен за короткое время, его площадь составила всего три квадратных метра. В тушении пожара были задействованы 29 сотрудников МЧС и шесть единиц спецтехники. На момент написания этой статьи причины происшествия устанавливаются. Это уже второй случай пожаров в регионе за последнее время - недавно жительница региона погибла в результате пожара на частном животноводческом предприятии.','Интерфакс','Российская Федерация',CURRENT_DATE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),\n" +
                            "('В Орле задержали инспекторов ДПС по обвинению в краже денег у водителя','NOVOST','Пресс-служба УГИБДД по Сахалинской области В Орле задержаны инспекторы дорожной полиции по подозрению в краже свыше 2,6 миллиона рублей у нетрезвого водителя. Об этом сообщила пресс-служба СУСК РФ по Орловской области. По информации следственного управления, возбуждено уголовное дело по факту грабежа в особо крупном размере. Инцидент произошел после того, как сотрудники ДПС остановили автомобиль, водитель которого был в состоянии алкогольного опьянения. Потерпевший заявил, что инспекторы похитили из салона машины свыше 2,6 миллионов рублей. Сотрудники ДПС, в свою очередь, рассказали, что предприняли попытку остановить автомобиль «Мазда-3», который двигался с выключенными фарами. Однако, по их словам, водитель продолжал движение и попытался скрыться, после чего бросил машину и сбежал с места происшествия. Сейчас ведется расследование, направленное на установление всех обстоятельств происшествия. Ранее сообщалось о том, что исполнительный директор ОАО «Радиотехнический институт имени академика А.Л. Минца» Вячеслав Лобузько арестован по подозрению в мошенничестве.','ТАСС','Российская Федерация',CURRENT_DATE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),\n" +
                            "('Вооружённый мужчина ворвался в ОМВД в Москве и попытался взорвать дежурную часть','NOVOST','Вооружённый мужчина ворвался в ОМВД в Москве и попытался взорвать дежурную часть. Неизвестный на «Ниве», вооружённый автоматом, в военном обмундировании, бронежилете и каске ворвался в здание ОМВД России по Бескудниковскому району утром 16 сентября, после чего попытался взорвать дежурную часть. По неподтверждённой информации, мужчина открыл огонь. Известно, что один сотрудник полиции пострадал. Насколько серьёзно, не сообщается.','ТАСС','Российская Федерация',CURRENT_DATE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),\n" +
                            "('Дело о погибшем пивоваре поступило в Брянский районный суд','NOVOST','Дело о погибшем пивоваре поступило в Брянский районный суд. Уголовное дело об оказании услуг, повлекшем по неосторожности смерть человека, поступило в Брянский районный суд – будет вынесен приговор виновным в трагической гибели пивовара на рабочем месте, сообщили в объединённой пресс-службе судов Брянской области. «В апреле 2024 года один из обвиняемых сдал в аренду потерпевшему два металлических контейнера, которые он использовал для частного пивоварения вместе со своим братом. 15 июня 2024 года во время мойки пивоваренного оборудования брат потерпевшего погиб, прислонившись к металлическому контейнеру, корпус которого находился под электрическим напряжением. В результате этого через непродолжительное время наступила смерть мужчины на месте происшествия», – говорится в сообщении. Второй обвиняемый по делу – исполнитель работ по электрификации контейнеров. Следствие считает, что он нарушил правила проведения электромонтажных работ по подключению металлического контейнера к электросети. Оба обвиняются по п. «в» ч.2 ст.238 УК РФ (оказание услуг, не отвечающих требованиям безопасности жизни и здоровья потребителей, если они повлекли по неосторожности смерть человека). Дело поступило в суд 16 октября.','Lenta.ru','Российская Федерация',CURRENT_DATE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),\n" +
                            "('Завязался ожесточенный бой с украинскими диверсантами в Белгородской области','NOVOST','Завязался ожесточенный бой с украинскими диверсантами в Белгородской области. На границе сейчас проходит ожесточенный бой с украинской диверсионно-разведовательной группой (ДРГ) у деревни Дроновка в Белгородской области, сообщает SHOT со ссылкой на источник. С обеих сторон привлечена военная техника, в небе замечены несколько беспилотников. Работают средства противовоздушной обороны.','Lenta.ru','Российская Федерация',CURRENT_DATE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),\n" +
                            "('Массовые беспорядки начались в исправительной колонии во Владикавказе','NOVOST','Массовые беспорядки начались в исправительной колонии во Владикавказе. Об этом «Известиям» сообщили в управлении Следственного комитета по Северной Осетии. Подробностей пока нет, уточнили в ведомстве. Как рассказал ТАСС осведомленный источник, беспорядки устроили около 200 заключенных. На место происшествия прибыли автобусы со спецназом Росгвардии.','ТАСС','Российская Федерация',CURRENT_DATE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),\n" +
                            "('Мурманский водитель въехал в две машины и сбил подростка','NOVOST','Мурманский водитель въехал в две машины и сбил подростка. Водитель за рулем автомобиля Audio столкнулся с тремя попутно ехавшими автомобилистами и сбил подростка на пешеходном переходе в Мурманске 19 октября. По данным Управления Госавтоинспекции УМВД России по Мурманской области, сначала водитель врезался в автомобили Hyundai и Reno, а после чего сбил 13-летнего пешехода. Пострадавшего пешехода с травмами отправили в больницу. Сейчас все подробности происшествия устанавливаются сотрудниками ДПС. Ранее «МК Мурманск» писал, что две легковушки столкнулись в Мурманской области.','Интерфакс','Российская Федерация',CURRENT_DATE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),\n" +
                            "('Неизвестный с ружьем ворвался в компанию микрозаймов и взял в заложницы сотрудницу','NOVOST','Неизвестный с ружьем ворвался в компанию микрозаймов и взял в заложницы сотрудницу. Налетчиком оказался 54-летний Александр Колодешников. Он был клиентом компании и задолжал деньги. Отдавать стало трудно, три года за него платила кредит бывшая жена. Недавно Колодешникову стали поступать угрозы от коллекторов. Сегодня он выпил, взял обрез и пошел в «Деньги в руки». Сейчас его требования — вызвать на место директора конторы, чтобы он извинился за такие методы работы с должниками. Известно, что захватчик несколько раз кодировался от алкогольной зависимости. В ноябре 2017 года он угрожал убийством мастеру цеха на заводе «Севмаш». В мае 2018 года ударил по голове цепью мужчину, после чего также угрожал убить его, — сообщает телеграм-канал. В Северодвинске неизвестный с ружьем в 11 утра ворвался в компанию микрозаймов «Деньги в руки» и захватил в заложники сотрудницу компании, пишут СМИ со ссылкой на очевидцев. Он начал угрожать сотруднице, но девушка успела нажать тревожную кнопку. Известно, что мужчина задолжал компании большую сумму. Он пошел в контору после угроз от коллекторов и требовал вызвать директора компании, чтобы он извинился за такие методы работы. Как сообщает телеграм-канал LIFE SHOT, мужчина, который взял в заложники сотрудницу, пошел на переговоры. Девушку он пока не отпускает. Здание было оцеплено несколько часов, на месте работали полицейские и спецназ Росгвардии.','ТАСС','Российская Федерация',CURRENT_DATE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);\n" +
                            "INSERT INTO sc.tb_oper_indicator_value\n" +
                            "(oper_indicator_value_id, oper_indicator_id, unique_key, department_id, external_id, measure_code, year, value, okato, start_date, end_date)\n" +
                            "VALUES(-1, 1018, 'PROPALI_BEZ_VESTI,_V_TOM_CHISLE_NESOVERSHENNOLETNIE_2025-04-01_0', '000000000000', 'PROPALI_BEZ_VESTI,_V_TOM_CHISLE_NESOVERSHENNOLETNIE', 'EDINITSA', '2025', 3264, '643', '2025-04-01', '2025-04-01');\n" +
                            "INSERT INTO sc.tb_oper_indicator_value\n" +
                            "(oper_indicator_value_id, oper_indicator_id, unique_key, department_id, external_id, measure_code, year, value, okato, start_date, end_date)\n" +
                            "VALUES(-2, 1032, 'REZERV_2025-04-01_0', '000000000000', 'REZERV', 'EDINITSA', '2025', 43, '643', '2025-04-01', '2025-04-01');\n" +
                            "INSERT INTO sc.tb_oper_indicator_value\n" +
                            "(oper_indicator_value_id, oper_indicator_id, unique_key, department_id, external_id, measure_code, year, value, okato, start_date, end_date)\n" +
                            "VALUES(-3, 932, 'ROZYSK_NESOVERSHENNOLETNIKH_2025-04-01_0', '000000000000', 'ROZYSK_NESOVERSHENNOLETNIKH', 'EDINITSA', '2025', 1122, '643', '2025-04-01', '2025-04-01');\n" +
                            "INSERT INTO sc.tb_oper_indicator_value\n" +
                            "(oper_indicator_value_id, oper_indicator_id, unique_key, department_id, external_id, measure_code, year, value, okato, start_date, end_date)\n" +
                            "VALUES(-869, 845, 'GRABEZH_2025-04-01_230002001909', '230002001909', 'GRABEZH', 'EDINITSA', '2025', 714, '45000000000', '2025-04-01', '2025-04-01');\n" +
                            "INSERT INTO sc.tb_oper_indicator_value\n" +
                            "(oper_indicator_value_id, oper_indicator_id, unique_key, department_id, external_id, measure_code, year, value, okato, start_date, end_date)\n" +
                            "VALUES(-870, 872, 'INFORMATSIYA_2025-04-01_230002001909', '230002001909', 'INFORMATSIYA', 'EDINITSA', '2025', 90270, '45000000000', '2025-04-01', '2025-04-01');\n" +
                            "INSERT INTO sc.tb_oper_indicator_value\n" +
                            "(oper_indicator_value_id, oper_indicator_id, unique_key, department_id, external_id, measure_code, year, value, okato, start_date, end_date)\n" +
                            "VALUES(-871, 870, 'IZ''YATIE_OGNESTRELNOGO_ORUZHIYA_2025-04-01_230002001909', '230002001909', 'IZ''YATIE_OGNESTRELNOGO_ORUZHIYA', 'EDINITSA', '2025', 102, '45000000000', '2025-04-01', '2025-04-01');\n" +
                            "INSERT INTO sc.tb_oper_indicator_value\n" +
                            "(oper_indicator_value_id, oper_indicator_id, unique_key, department_id, external_id, measure_code, year, value, okato, start_date, end_date)\n" +
                            "VALUES(-872, 1068, 'KHULIGANSTVO_2025-04-01_230002001909', '230002001909', 'KHULIGANSTVO', 'EDINITSA', '2025', 2, '45000000000', '2025-04-01', '2025-04-01');\n" +
                            "INSERT INTO sc.tb_decision (decision_id, name, description, status, creator_id, create_time, update_time, create_by, update_by, incident_class_code) VALUES\n" +
                            "(-1, 'Заря', 'Заложники', 'NEW', -1, NOW(), NOW(), 'superuser', 'superuser', 'ZALOZHNIKI'),\n" +
                            "(-2, 'Объект', 'Захват ОВО', 'NEW', -1, NOW(), NOW(), 'superuser', 'superuser', 'ZAKHVAT_OVO'),\n" +
                            "(-3, 'Вулкан', 'Массовые беспорядки', 'NEW', -1, NOW(), NOW(), 'superuser', 'superuser', 'MASSOVYE_BESPORYADKI'),\n" +
                            "(-4, 'Крепость', 'Нападение на здание ОВД', 'NEW', -1, NOW(), NOW(), 'superuser', 'superuser', 'NAPADENIE_NA_ZDANIE_OVD'),\n" +
                            "(-5, 'Сирена', 'Розыск преступника', 'NEW', -1, NOW(), NOW(), 'superuser', 'superuser', 'ROZYSK_I_ZADERZHANIE_VOORUZHENNOGO_PRESTUPNIKA'),\n" +
                            "(-6, 'Эдельвейс', 'Терроризм, экстремизм', 'NEW', -1, NOW(), NOW(), 'superuser', 'superuser', 'TERRORIZM/EKSTREMIZM'),\n" +
                            "(-7, 'Граница', 'ЧП госграница', 'NEW', -1, NOW(), NOW(), 'superuser', 'superuser', 'CHP_GOSGRANITSA'),\n" +
                            "(-8, 'Лавина', 'ЧП ФСИН', 'NEW', -1, NOW(), NOW(), 'superuser', 'superuser', 'CHP_FSIN'),\n" +
                            "(-9, 'Тайфун', 'ЧС', 'NEW', -1, NOW(), NOW(), 'superuser', 'superuser', 'CHS');"
            );
        } finally {

        }
    }
    @Test
    @Order(2)
    public void runChains() {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<AliasInfo> aliases = Arrays.asList(
                new AliasInfo("INCIDENT", "indexIncident", "2024-01-01", currentDate, false),
                new AliasInfo("ACCIDENT", "indexAccident", "2024-01-01", currentDate, false),
                new AliasInfo("PUBLICATION", "indexPublication", "2024-01-01", currentDate, false),
                new AliasInfo("AOE", "indexAoe", "2024-01-01", currentDate, false)
                // добавьте остальные по необходимости
        );

        for (AliasInfo info : aliases) {
            // 1. Первый запрос: получить индекс по alias
            Response response1 = RestAssured.given()
                    .log().all()
                    .contentType("application/json")
                    .header("Cookie", Auth.cookie)
                    .body("{\"alias\": \"" + info.alias + "\"}")
                    .when()
                    .post(getBaseUrl() + "es/index")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            // 2. Извлечь значение index из ответа
            String indexValue = response1.asString().trim();
            // Предположим, что ответ содержит поле "index"

            System.out.println("Получен индекс для alias " + info.alias + ": " + indexValue);

            // 3. Второй запрос с полученным индексом
            String reindexUrl = "es/reindex?index=" + indexValue
                    + "&alias=" + info.alias
                    + "&dateFrom=" + info.dateFrom
                    + "&dateTo=" + info.currentDate; // или другой диапазон

            Response response2 = RestAssured.given() // замените на реальный базовый URL
                    .log().all()
                    .when()
                    .header("Cookie", Auth.cookie)
                    .post(getBaseUrl() + reindexUrl)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            System.out.println("Результат reindex для alias " + info.alias + ": " + response2.asString());
        }
    }
    @Test
    @Order(3)
    public void waitForAllTasksToFinish() {
        boolean allFinished = false;

        while (!allFinished) {
            // Формируем параметры запроса
            Response response = RestAssured.given()
                    .queryParams(buildAliasParams(aliases))
                    .header("Cookie", Auth.cookie)
                    .when()
                    .get(getBaseUrl() + "es/reindex/task/last")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            // Предположим, что ответ содержит массив задач
            List<Map<String, Object>> tasks = response.jsonPath().getList("tasks");

            allFinished = true; // предполагаем, что все завершены
            for (Map<String, Object> task : tasks) {
                String alias = (String) task.get("alias");
                String status = (String) task.get("status");
                System.out.println("Alias: " + alias + ", статус: " + status);
                if (!"FINISHED".equalsIgnoreCase(status)) {
                    allFinished = false;
                }
            }

            if (!allFinished) {
                System.out.println("Некоторые задачи еще не завершены. Повтор через 15 секунд...");
                try {
                    TimeUnit.SECONDS.sleep(15);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Поток прерван во время ожидания", e);
                }
            }
        }
        System.out.println("Все задачи завершены!");
    }

    private Map<String, ?> buildAliasParams(List<String> aliases) {
        io.restassured.http.QueryParams params = new io.restassured.http.QueryParams();
        for (String alias : aliases) {
            params.and("alias", alias);
        }
        return (Map<String, ?>) params;
    }
}
