package ru.netology;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.*;

public class CardValidationTest {
    private WebDriver driver;

    @BeforeAll
    static void setupAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage", "--no-sandbox", "--headless");
        driver = new ChromeDriver(options);
        // Открытие страницы вынесено в предусловие
        driver.get("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    void shouldShowErrorForInvalidName() {
        // Невалидное имя (латиница)
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Ivan Petrov");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79231234567");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        // Поиск сообщения об ошибке по комбинированному селектору
        WebElement errorMessage = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub"));

        assertTrue(errorMessage.isDisplayed());
        assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.",
                errorMessage.getText().trim());
    }

    @Test
    void shouldShowErrorForInvalidPhone() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+7923");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        // Поиск сообщения об ошибке по комбинированному селектору
        WebElement errorMessage = driver.findElement(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub"));

        assertTrue(errorMessage.isDisplayed());
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.",
                errorMessage.getText().trim());
    }

    @Test
    void shouldShowErrorForEmptyName() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79231234567");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        // Поиск сообщения об ошибке по комбинированному селектору
        WebElement errorMessage = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub"));

        assertTrue(errorMessage.isDisplayed());
        assertEquals("Поле обязательно для заполнения", errorMessage.getText().trim());
    }

    @Test
    void shouldShowErrorForEmptyPhone() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        // Поиск сообщения об ошибке по комбинированному селектору
        WebElement errorMessage = driver.findElement(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub"));

        assertTrue(errorMessage.isDisplayed());
        assertEquals("Поле обязательно для заполнения", errorMessage.getText().trim());
    }

    @Test
    void shouldShowErrorForUncheckedAgreement() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79231234567");
        // чекбокс НЕ нажимаем
        driver.findElement(By.cssSelector("button.button")).click();

        // Для чекбокса комбинированный селектор с классом input_invalid
        WebElement errorMessage = driver.findElement(By.cssSelector("[data-test-id='agreement'].input_invalid .checkbox__text"));

        assertTrue(errorMessage.isDisplayed());
        assertEquals("Я соглашаюсь с условиями обработки и использования моих персональных данных и разрешаю сделать запрос в бюро кредитных историй",
                errorMessage.getText().trim());
    }

    @Test
    void shouldHighlightOnlyFirstInvalidField() {
        // Оба поля невалидны
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Ivan Petrov");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("123");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        // Проверяем, что сообщение об ошибке есть только у первого поля (имя)
        WebElement nameErrorMessage = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub"));
        assertTrue(nameErrorMessage.isDisplayed());

        // Проверяем, что у второго поля (телефон) нет сообщения об ошибке
        // Ищем элемент с селектором для телефона, ожидаем, что его не существует
        boolean phoneHasError;
        try {
            driver.findElement(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub"));
            phoneHasError = true;
        } catch (Exception e) {
            phoneHasError = false;
        }

        assertFalse(phoneHasError, "У телефона не должно быть сообщения об ошибке, так как ошибка только в первом поле");
    }
}