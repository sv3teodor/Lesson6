import com.codeborne.selenide.*;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Epic;
import io.qameta.allure.selenide.AllureSelenide;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.openqa.selenium.By.linkText;
import static utils.Utils.getSpecialCharacters;

public class Lesson6Selenide {
    @Epic("Tests for lesson 6")

    @BeforeAll
    public static void setUp() {
        System.setProperty("allure.results.directory", "D:\\T1\\out-lesson5\\build");
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true));
        Configuration.browser = "chrome";
    }


    @BeforeEach
    public void prepare() {
        open("https://the-internet.herokuapp.com/");
    }


/*
    1)Перейти на страницу Checkboxes.
    Выделить первый чекбокс, снять выделение со второго чекбокса.
    Вывести в консоль состояние атрибута checked для каждого чекбокса.
    2)Добавить проверки в задание Checkboxes из предыдущей лекции.
    Проверять корректное состояние каждого чекбокса после каждого нажатия на него.
    Запустить тест с помощью @ParametrizedTest, изменяя порядок нажатия на чекбоксы с помощью одного параметра.
    */

    @ParameterizedTest(name ="Checkboxes test. Check boxes status= {arguments}" )
    @ValueSource(strings = {"01", "10", "11", "00"})
    public void checkBoxesText(String checkboxOrder) {
        $(linkText("Checkboxes")).click();
        ElementsCollection checkBoxes = $$(By.xpath("//*[@id=\"checkboxes\"]/input"));

        for (int i = 0; i < checkboxOrder.length(); i++) {
            char currentChar = checkboxOrder.charAt(i);
            boolean shouldBeChecked = currentChar == '1';
            WebElement currentCheckBox = checkBoxes.get(i);
            if (shouldBeChecked != currentCheckBox.isSelected()) {
                currentCheckBox.click();
            }
            assertEquals(shouldBeChecked, currentCheckBox.isSelected(), "Состояние комбобокса не соответствует ожидаемому");
        }

        System.out.println("CheckBox 1=" + checkBoxes.get(0).getAttribute("checked"));
        System.out.println("CheckBox 0=" + checkBoxes.get(1).getAttribute("checked"));
    }

    /*
     1) Перейти на страницу Dropdown.
        Выбрать первую опцию, вывести в консоль текущий текст элемента dropdown,
        выбрать вторую опцию, вывести в консоль текущий текст элемента dropdown.

     2) Добавить проверки в задание Dropdown из предыдущей лекции.
        Проверять корректное состояние каждого dropDown после каждого нажатия на него.
     */
    @ParameterizedTest(name = "Dropdown tests. Test option {arguments}")
    @ValueSource(ints = {1, 2})
    public void dropdownTest(Integer itemNumber) {
        $(linkText("Dropdown")).click();
        $("#dropdown").selectOption(itemNumber);
        $("#dropdown").shouldHave(text(String.format("Option %s", itemNumber)));
        System.out.println($("#dropdown").getText());
    }

    /*
    1)Перейти на страницу Disappearing Elements.
        Добиться отображения 5 элементов,
        максимум за 10 попыток, если нет, провалить тест с ошибкой.

    2)Добавить проверки в задание Disappearing Elements из предыдущей лекции.
        Для каждого обновления страницы проверять наличие 5 элементов. Использовать @RepeatedTest.
    */
    @DisplayName("Disappearing Elements test.")
    @RepeatedTest(10)
    public void disappearingElementsTest() {
        $(linkText("Disappearing Elements")).click();
        $$("ul > li").should(CollectionCondition.size(5), Duration.ofMillis(1000));
    }

    /*
   1) Перейти на страницу Inputs.
   Ввести любое случайное число от 1 до 10 000.
   Вывести в консоль значение элемента Input.
   2)
   Добавить проверки в задание Inputs из предыдущей лекции.
   Проверить, что в поле ввода отображается именно то число, которое было введено.
   Повторить тест 10 раз, используя @TestFactory, с разными значениями, вводимыми в поле ввода.
   Создать проверку негативных кейсов (попытка ввести в поле латинские буквы, спецсимволы, пробел до и после числа).
    */
    @TestFactory
    public Stream<DynamicTest> inputTest() {
        return Stream.of(
                "123",
                "456",
                "789",
                "10000",
                "-1",
                "0",
                "10001"
        ).map(number -> dynamicTest(String.format("Проверка ввода числа %d",number), () -> {
            open("https://the-internet.herokuapp.com/");
            $(linkText("Inputs")).click();
            SelenideElement input = $(By.tagName("input"));
            input.sendKeys(number + Keys.ENTER);
            String inputValue = input.getAttribute("value");
            assertEquals(number, inputValue, "Значение поля ввода не соответствует ожидаемому");
        }));
    }

    @TestFactory
    public List<DynamicTest> inputTestNegative() {
        /*Можно было сюда поместить и позитивные тесты,
            но в задании не указано нужно разносить по разным методам позитивные и негативные тесты или нет.
            Я решил для разнообразия разнести.
         */
        List<DynamicTest> result = new ArrayList<>();
        // Передаем 3 знаяения. Название теста, что будет введено, что ожидаем в поле.
        List<Triple<String, String, String>> collection = new ArrayList<>();
        collection.add(Triple.of("Тест ввода пустой строки", "", ""));
        collection.add(Triple.of("Тест ввода латинских букв", "abcd", ""));
        collection.add(Triple.of("Тест ввода пробел до числа", " 123_", "123"));
        collection.add(Triple.of("Тест ввода пробел после числа", "123 ", "123"));
        //Добавляем тесты для всех спецсимволов
        getSpecialCharacters().stream().forEach(specialChar -> {
            collection.add(Triple.of("Тест ввода спецсимвола с кодом " + (int) specialChar.charValue(),
                    String.valueOf(specialChar.charValue()), ""));
        });

        collection.forEach(test -> {
            result.add(DynamicTest.dynamicTest(test.getLeft(),
                    () -> {
                        open("https://the-internet.herokuapp.com/");
                        $(linkText("Inputs")).click();
                        SelenideElement input = $(By.tagName("input"));
                        input.sendKeys(test.getMiddle() + Keys.ENTER);
                        input.should(Condition.value(test.getRight()));
                    }));
        });
        return result;
    }


    /*1)Перейти на страницу Hovers.
        Навести курсор на каждую картинку.
        Вывести в консоль текст, который появляется при наведении.
      2)Добавить проверки в задание Hovers из предыдущей лекции.
        При каждом наведении курсора, проверить, что отображаемый текст совпадает с ожидаемым.
        Выполнить тест с помощью @ParametrizedTest, в каждом тесте, указывая на какой элемент наводить курсор
        */

    @ParameterizedTest(name = "Hovers test. Test hover number {arguments}")
    @ValueSource(ints = {0, 1, 2})
    public void hoversImageTest(Integer hoverIndex) {
        $(linkText("Hovers")).click();
        ElementsCollection imageElements = $$(".figure");
        Actions actions = new Actions(Selenide.webdriver().object());
        var targetImage = imageElements.get(hoverIndex);
        actions.moveToElement(targetImage).perform();
        String captionText = targetImage.findElement(By.cssSelector(".figcaption")).getText();
        assertEquals(captionText, "name: user" + (hoverIndex + 1) + "\nView profile", "Текст под профилем не соответствует ожидаемому");
        System.out.println("Текст при наведении: " + captionText);
    }

    /*
        1) Перейти на страницу Notification Message.
           Кликать до тех пор, пока не покажется уведомление Action successful.
           После каждого неудачного клика закрывать всплывающее уведомление.
        2) Добавить проверки в задание Notification Message из предыдущей лекции.
           Добавить проверку, что всплывающее уведомление должно быть Successfull.
           Если нет – провалить тест. Использовать @RepeatedTest.
     */
    @DisplayName("Notification Message test.")
    @RepeatedTest(10)
    public void notificationMessageTest() {
        $(linkText("Notification Messages")).click();
        boolean successMessageDisplayed = false;
        Selenide.webdriver().object().manage().window().maximize();//Иначе ссылка на github перекрывает крестик
        $(linkText("Click here")).click();
        $(By.id("flash")).shouldHave(text("successful"), Duration.ofMillis(1000));
    }
    /*
   1) Перейти на страницу Add/Remove Elements.
        Нажать на кнопку Add Element 5 раз.
        С каждым нажатием выводить в консоль текст появившегося элемента.
        Нажать на разные кнопки Delete три раза.
        Выводить в консоль оставшееся количество кнопок Delete и их тексты.
    2) Добавить проверки в задание Add/Remove Elements.
        Проверять, что на каждом шагу остается видимым ожидаемое количество элементов.
        Запустить тест три раза, используя @TestFactory,
        меняя количество созданий и удалений на 2:1, 5:2, 1:3 соответственно.
    */

    @DisplayName("Add/Remove Elements test.")
    @TestFactory
    public List<DynamicTest> addRemoveTest() {

        List<DynamicTest> result = new ArrayList<>();
        // Передаем 3 знаяения. Название теста, что будет введено, что ожидаем в поле.
        List<Pair<Integer, Integer>> collection = new ArrayList<>();
        collection.add(Pair.of(2, 1));
        collection.add(Pair.of(5, 2));
        collection.add(Pair.of(1, 3));

        collection.forEach(test -> {
            result.add(DynamicTest.dynamicTest("Добавляем " + test.getLeft() + " кнопок удаляем " + test.getRight(),
                    () -> {
                        open("https://the-internet.herokuapp.com/");
                        $(linkText("Add/Remove Elements")).click();
                        // Нажимаем на кнопку Add Element
                        WebElement addElementButton = $(By.xpath("//button[text()='Add Element']"));
                        ElementsCollection elements = $$(By.xpath("//*[@id=\"elements\"]/*"));
                        for (int i = 0; i < test.getLeft(); i++) {
                            int oldElementsNumbers = elements.size();
                            addElementButton.click();
                            assertTrue(oldElementsNumbers + 1 == elements.size(), "Неверное количество элементов после добавления");
                            System.out.println("Добавлен элемент: " + elements.get(elements.size() - 1).getText());
                        }

                        // Нажимаем на кнопку Delete
                        for (int i = 0; i < test.getRight(); i++) {
                            int oldElementsNumbers = elements.size();
                            $(By.xpath("//*[@id=\"elements\"]/button[text()='Delete']")).click();
                            assertTrue(oldElementsNumbers - 1 == elements.size(), "Неверное количество элементов после удаления");

                        }
                        // Выводим количество оставшихся кнопок Delete и их тексты
                        ElementsCollection remainingDeleteButtons = $$(By.xpath("//*[@id=\"elements\"]/button[text()='Delete']"));
                        System.out.println("Оставшееся количество кнопок Delete: " + remainingDeleteButtons.size());
                        remainingDeleteButtons.forEach(x -> System.out.println("Текст кнопки Delete: " + x.getText()));
                    }));
        });
        return result;
    }

    /*
    1)Перейти на страницу Status Codes.
    Кликнуть на каждый статус в новом тестовом методе,
    вывести на экран текст после перехода на страницу статуса.
    2)Добавить проверки в задание Status Codes.
    Добавить Проверку, что переход был осуществлен на страницу с корректным статусом.
 */
    @DisplayName("Status Codes test.")
    @Test
    public void statusCodeTest() {
        $(linkText("Status Codes")).click();
        $$(By.xpath("//*[@id=\"content\"]/div/ul/li/a"))
                .texts()
                .forEach(errCode -> {
                    $(By.xpath("//*[@id=\"content\"]/div/ul/li/a[text()=" + errCode + "]")).click();
                    $("div.example p").shouldHave(text("This page returned a " + errCode + " status code"));
                    System.out.println($("div.example p").getText());
                    back();
                });
    }

    @AfterAll
    public static void tearDown() {
        closeWebDriver();
    }


}
