import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Lesson5Tests extends BaseTest {

    /*Перейти на страницу Checkboxes.
            Выделить первый чекбокс, снять выделение со второго чекбокса.
            Вывести в консоль состояние атрибута checked для каждого чекбокса.
             */
    @DisplayName("Checkboxes test.")
    @Test
    public void checkBoxesText() {
        driver.findElement(By.linkText("Checkboxes")).click();
        List<WebElement> checkBoxes = driver.findElements(By.xpath("//*[@id=\"checkboxes\"]/input"));
        checkBoxes.get(0).click();
        checkBoxes.get(1).click();
        Assertions.assertTrue(checkBoxes.get(0).isSelected());
        Assertions.assertFalse(checkBoxes.get(1).isSelected());
        System.out.println("CheckBox 1=" + checkBoxes.get(0).getAttribute("checked"));
        System.out.println("CheckBox 0=" + checkBoxes.get(1).getAttribute("checked"));
    }

    /*
     Перейти на страницу Dropdown.
     Выбрать первую опцию, вывести в консоль текущий текст элемента dropdown,
     выбрать вторую опцию, вывести в консоль текущий текст элемента dropdown.
     */
    @DisplayName("Dropdown test.")
    @Test
    public void dropdownTest() {
        driver.findElement(By.linkText("Dropdown")).click();
        Select dropdown = new Select(driver.findElement(By.id("dropdown")));
        dropdown.selectByIndex(1);
        System.out.println(dropdown.getFirstSelectedOption().getText());
        dropdown.selectByIndex(2);
        System.out.println(dropdown.getFirstSelectedOption().getText());
    }

    /*
    Перейти на страницу Disappearing Elements.
    Добиться отображения 5 элементов,
    максимум за 10 попыток, если нет, провалить тест с ошибкой.
     */
    @DisplayName("Disappearing Elements test.")
    @Test
    public void disappearingElementsTest() {
        driver.findElement(By.linkText("Disappearing Elements")).click();
        int attempts = 0;
        int maxAttempts = 10;
        while (attempts < maxAttempts) {
            List<WebElement> elements = driver.findElements(By.cssSelector("ul > li"));
            if (elements.size() == 5) {
                System.out.println("Отображено 5 элементов.");
                return;
            } else {
                attempts++;
                System.out.println("Попытка " + attempts + ": Найдено элементов - " + elements.size());
            }
            driver.navigate().refresh();
        }
        Assertions.fail("Не удалось отобразить 5 элементов за " + maxAttempts + " попыток.");
    }

    /*
    Перейти на страницу Inputs.
    Ввести любое случайное число от 1 до 10 000.
    Вывести в консоль значение элемента Input.
     */
    @DisplayName("Inputs test.")
    @Test
    public void inputValueTest() {
        driver.findElement(By.linkText("Inputs")).click();
        int randomNumber = new Random().nextInt(10000) + 1;
        WebElement inputElement = driver.findElement(By.tagName("input"));
        inputElement.sendKeys(String.valueOf(randomNumber));
        System.out.println("Введенное число: " + inputElement.getAttribute("value"));
    }

    /*
        Перейти на страницу Hovers.
        Навести курсор на каждую картинку.
        Вывести в консоль текст, который появляется при наведении.
     */
    @DisplayName("Hovers test.")
    @Test
    public void hoversImageTest() {
        driver.findElement(By.linkText("Hovers")).click();
        List<WebElement> imageElements = driver.findElements(By.cssSelector(".figure"));
        Actions actions = new Actions(driver);
        for (WebElement imageElement : imageElements) {
            actions.moveToElement(imageElement).perform();
            WebElement userCaption = imageElement.findElement(By.cssSelector(".figcaption"));
            String captionText = userCaption.getText();
            System.out.println("Текст при наведении: " + captionText);
        }
    }

    /*
        Перейти на страницу Notification Message.
        Кликать до тех пор, пока не покажется уведомление Action successful.
        После каждого неудачного клика закрывать всплывающее уведомление.
     */
    @DisplayName("Notification Message test.")
    @Test
    public void notificationMessageTest() {
        driver.findElement(By.linkText("Notification Messages")).click();
        boolean successMessageDisplayed = false;

        int attempts = 100; //Что бы не попасть в бесконечный цикл
        driver.manage().window().maximize(); //Иначе ссылка на github перекрывает крестик
        while (!successMessageDisplayed && attempts > 0) {
            attempts--;
            WebElement clickHereButton = driver.findElement(By.linkText("Click here"));
            clickHereButton.click();
            WebElement messageElement = driver.findElement(By.id("flash"));
            String messageText = messageElement.getText();
            if (messageText.contains("Action successful")) {
                successMessageDisplayed = true;
                System.out.println("Уведомление 'Action successful' появилось!");
            } else {
                WebElement closeButton = driver.findElement(By.xpath("//*[@id=\"flash\"]/a"));
                closeButton.click();
                clickHereButton = driver.findElement(By.linkText("Click here"));
                clickHereButton.click();
            }
        }
    }

    /*
    Перейти на страницу Add/Remove Elements.
    Нажать на кнопку Add Element 5 раз.
    С каждым нажатием выводить в консоль текст появившегося элемента.
    Нажать на разные кнопки Delete три раза.
    Выводить в консоль оставшееся количество кнопок Delete и их тексты.
     */
    @DisplayName("Add/Remove Elements test.")
    @Test
    public void addRemoveTest() {
        driver.findElement(By.linkText("Add/Remove Elements")).click();
        // Нажимаем на кнопку Add Element 5 раз
        WebElement addElementButton = driver.findElement(By.xpath("//button[text()='Add Element']"));
        for (int i = 0; i < 5; i++) {
            addElementButton.click();
            List<WebElement> elements = driver.findElements(By.xpath("//*[@id=\"elements\"]/*"));
            System.out.println("Добавлен элемент: " + elements.get(elements.size() - 1).getText());
        }

        // Нажимаем на кнопку Delete три раза
        for (int i = 0; i < 3; i++) {
            List<WebElement> deleteButtons = driver.findElements(By.xpath("//*[@id=\"elements\"]/button[text()='Delete']"));
            if (!deleteButtons.isEmpty()) {
                WebElement deleteButton = deleteButtons.get(0);
                deleteButton.click();
            }
        }

        // Выводим количество оставшихся кнопок Delete и их тексты
        List<WebElement> remainingDeleteButtons = driver.findElements(By.xpath("//*[@id=\"elements\"]/button[text()='Delete']"));
        System.out.println("Оставшееся количество кнопок Delete: " + remainingDeleteButtons.size());
        remainingDeleteButtons.forEach(x -> System.out.println("Текст кнопки Delete: " + x.getText()));

    }

    /*
    Перейти на страницу Status Codes.
    Кликнуть на каждый статус в новом тестовом методе,
    вывести на экран текст после перехода на страницу статуса.
     */
    @DisplayName("Status Codes test.")
    @Test
    public void statusCodeTest() {
        driver.findElement(By.linkText("Status Codes")).click();
        driver.findElements(By.xpath("//*[@id=\"content\"]/div/ul/li/a"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList())
                .forEach(code -> {
                    driver.findElement((By.xpath("//*[@id=\"content\"]/div/ul/li/a[text()=" + code + "]"))).click();
                    System.out.println(driver.findElement(By.xpath("//*[@id=\"content\"]/div/p[1]")).getText());
                    driver.navigate().back();
                });
    }
}
