package edu.onecommitman.framework;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.List;


public class RGSTest {
    static WebDriver driver;
    static WebDriverWait wait;


    @BeforeEach
    public void beforEach(){
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10), Duration.ofSeconds(2));
        driver.get("https://rgs.ru/");
    }

    @ParameterizedTest
    @MethodSource("edu.onecommitman.framework.data.DataClass#nameValues")
    public void test(String usersName){
        //3. Компаниям
        WebElement baseMenu = driver.findElement(By.xpath("//a[contains(@href, '/for-companies')]"));
        wait.until(ExpectedConditions.elementToBeClickable(baseMenu));
        baseMenu.click();

        //3. Компаниям -> Здоровье
        WebElement subMenu = driver.findElement(By.xpath("//span[text()='Здоровье' and contains(@class, 'padding')]")); //обнаружили кнопку Здоровье
        wait.until(ExpectedConditions.elementToBeClickable(subMenu)); //Ожидаем пока кнопка будет доступна
        subMenu.click();

        //3. Компаниям -> Здоровье -> Добровольное медицинское страхование
        WebElement subMenuTransit = driver.findElement(By.xpath("//a[text()='Добровольное медицинское страхование' and contains(@href, 'strakhovanie')]"));
        subMenuTransit.click();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //4. Проверить наличие заголовка "Добровольное медицинское страхование"
        WebElement titleInsurancePage = driver.findElement(By.xpath("//h1[text()='Добровольное медицинское страхование' and contains(@class, 'title word-breaking')]"));

        assertTrue(titleInsurancePage.isDisplayed() && titleInsurancePage.getText().contains("Добровольное медицинское страхование"), "Страница не загрузилась");
        //h1[text()='Добровольное медицинское страхование' and contains(@class, 'title word-breaking')]

        //5. Нажать кноку "Отправить заявку"
        WebElement sendAnApplicationButton = driver.findElement(By.xpath("//button[contains(@type, 'button')]"));
        sendAnApplicationButton.click();

        //6. Проверить, что открылась нужная страница
        WebElement titleCallBack = driver.findElement(By.xpath("//h2[contains(@class, 'section-basic__title title--h2 word-breaking title--h2') and contains(./text(), 'Оперативно перезвоним')]"));
        assertTrue(titleCallBack.isDisplayed(), "Страница не загрузилась");

        fillInputField(driver.findElement(By.xpath("//input[contains(@name, 'userName')]")), usersName);
        fillInputFieldPhone(driver.findElement(By.xpath("//input[contains(@name, 'userTel')]")), "9991234567");
        fillInputField(driver.findElement(By.xpath("//input[contains(@name, 'userEmail')]")), "qwertyqwerty");
        fillInputField(driver.findElement(By.xpath("//input[@placeholder='Введите']")), "Москва");

        List<WebElement> adressList = driver.findElements(By.xpath("//input[@placeholder='Введите']/../..//span[contains(@class, item)]"));
        for(WebElement element:adressList){
            if(element.getText().contains("Москва")){
                element.click();
                break;
            }
        }

        WebElement checkbox = driver.findElement(By.xpath("//div//input[contains(@class, 'checkbox')]"));
        Actions action = new Actions(driver);
        action.click(checkbox).build().perform();

        WebElement submitButton = driver.findElement(By.xpath("//div//button[contains(@type, 'submit')]"));
        submitButton.click();

        //10. У поля ввода адреса электронной почты присутствует сообщение об ошибке
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(@class, 'input__error text--small')]")));
        WebElement errorMailMessage = driver.findElement(By.xpath("//span[contains(@class, 'input__error text--small')]"));
        //errorMailMessage.isDisplayed();
        assertEquals("Введите корректный адрес электронной почты", errorMailMessage.getText());

        //Пауза перед закрытием браузера
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        driver.quit();//Закрытие браузера, завершение сессии
    }
    @AfterEach
    public void afterEach(){
        driver.quit();
    }

    private void scrollToElementJs(WebElement element) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    private void fillInputField(WebElement element, String value) {
        scrollToElementJs(element);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
        //element.clear();
        element.sendKeys(value);
        boolean checkFlag = wait.until(ExpectedConditions.attributeContains(element, "value", value));
        assertTrue(checkFlag, "Поле было заполнено некорректно");
    }
    private void fillInputFieldPhone(WebElement element, String value) {
        scrollToElementJs(element);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
        element.clear();
        element.sendKeys(value);

        String number = value.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "+7 ($1) $2-$3");
        boolean checkFlag = wait.until(ExpectedConditions.attributeContains(element, "value", number));
        assertTrue(checkFlag, "Поле было заполнено некорректно");
    }


}
