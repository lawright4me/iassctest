package ui;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


public class FirefoxTest {
    public static void main(String[] args) {
        // Укажите путь к GeckoDriver
        System.setProperty("webdriver.gecko.driver", "C:\\Users\\adrozdov\\Downloads\\geckodriver-v0.36.0-win32\\geckodriver.exe");

        // Инициализируем экземпляр WebDriver для Firefox
        WebDriver driver = new FirefoxDriver();

        // Открываем сайт
        driver.get("https://www.example.com");

        // Получаем заголовок страницы
        String title = driver.getTitle();
        System.out.println("Title of the page is: " + title);

        // Закрываем браузер
        driver.quit();
    }
}
