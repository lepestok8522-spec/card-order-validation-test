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
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    void shouldShowErrorForInvalidName() {
        driver.get("http://localhost:9999");

        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Ivan Petrov");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79231234567");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        WebElement nameBlock = driver.findElement(By.cssSelector("[data-test-id='name']"));
        WebElement errorMessage = nameBlock.findElement(By.cssSelector(".input__sub"));

        assertTrue(errorMessage.isDisplayed());
        assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.",
                errorMessage.getText().trim());

        String blockClass = nameBlock.getAttribute("class");
        assertTrue(blockClass.contains("input_invalid"));
    }

    @Test
    void shouldShowErrorForInvalidPhone() {
        driver.get("http://localhost:9999");

        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+7923");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        WebElement phoneBlock = driver.findElement(By.cssSelector("[data-test-id='phone']"));
        WebElement errorMessage = phoneBlock.findElement(By.cssSelector(".input__sub"));

        assertTrue(errorMessage.isDisplayed());
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.",
                errorMessage.getText().trim());

        String blockClass = phoneBlock.getAttribute("class");
        assertTrue(blockClass.contains("input_invalid"));
    }

    @Test
    void shouldShowErrorForEmptyName() {
        driver.get("http://localhost:9999");

        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79231234567");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        WebElement nameBlock = driver.findElement(By.cssSelector("[data-test-id='name']"));
        WebElement errorMessage = nameBlock.findElement(By.cssSelector(".input__sub"));

        assertTrue(errorMessage.isDisplayed());
        assertEquals("Поле обязательно для заполнения", errorMessage.getText().trim());
    }

    @Test
    void shouldShowErrorForUncheckedAgreement() {
        driver.get("http://localhost:9999");

        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79231234567");
        driver.findElement(By.cssSelector("button.button")).click();

        WebElement agreementBlock = driver.findElement(By.cssSelector("[data-test-id='agreement']"));
        WebElement errorMessage = agreementBlock.findElement(By.cssSelector(".checkbox__text"));

        assertTrue(errorMessage.isDisplayed());
        assertEquals("Я соглашаюсь с условиями обработки и использования моих персональных данных и разрешаю сделать запрос в бюро кредитных историй",
                errorMessage.getText().trim());
    }

    @Test
    void shouldHighlightOnlyFirstInvalidField() {
        driver.get("http://localhost:9999");

        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Ivan Petrov");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("123");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        WebElement nameBlock = driver.findElement(By.cssSelector("[data-test-id='name']"));
        WebElement phoneBlock = driver.findElement(By.cssSelector("[data-test-id='phone']"));

        String nameBlockClass = nameBlock.getAttribute("class");
        String phoneBlockClass = phoneBlock.getAttribute("class");

        assertTrue(nameBlockClass.contains("input_invalid"));
        assertFalse(phoneBlockClass.contains("input_invalid"));
    }
}