package com.testinium.step;

import com.google.gson.JsonObject;
import com.testinium.base.BaseTest;
import com.testinium.model.ElementInfo;
import com.thoughtworks.gauge.Step;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseSteps extends BaseTest {

    HashMap<String, String> webHeader = new HashMap<String, String>();
    public static int DEFAULT_MAX_ITERATION_COUNT = 150;
    public static int DEFAULT_MILLISECOND_WAIT_AMOUNT = 100;

    private static String SAVED_ATTRIBUTE;

    private String compareText;

    public BaseSteps() {
        initMap(getFileList());
    }

    WebElement findElement(String key) {
        By infoParam = getElementInfoToBy(findElementInfoByKey(key));
        WebDriverWait webDriverWait = new WebDriverWait(driver, 60);
        WebElement webElement = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(infoParam));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'})", webElement);
        return webElement;
    }

    List<WebElement> findElements(String key) {

        return driver.findElements(getElementInfoToBy(findElementInfoByKey(key)));
    }

    public By getElementInfoToBy(ElementInfo elementInfo) {
        By by = null;
        if (elementInfo.getType().equals("css")) {
            by = By.cssSelector(elementInfo.getValue());
        } else if (elementInfo.getType().equals(("name"))) {
            by = By.name(elementInfo.getValue());
        } else if (elementInfo.getType().equals("id")) {
            by = By.id(elementInfo.getValue());
        } else if (elementInfo.getType().equals("xpath")) {
            by = By.xpath(elementInfo.getValue());
        } else if (elementInfo.getType().equals("linkText")) {
            by = By.linkText(elementInfo.getValue());
        } else if (elementInfo.getType().equals(("partialLinkText"))) {
            by = By.partialLinkText(elementInfo.getValue());

        }
        return by;
    }

    private void clickElement(WebElement element) {
        element.click();
    }

    private void hoverElement(WebElement element) {
        actions.moveToElement(element).build().perform();
    }

    private void hoverElementBy(String key) {
        WebElement webElement = findElement(key);
        actions.moveToElement(webElement).build().perform();
    }

    private boolean isDisplayedBy(By by) {
        return driver.findElement(by).isDisplayed();
    }

    private String getPageSource() {
        return driver.switchTo().alert().getText();
    }

    public static String getSavedAttribute() {
        return SAVED_ATTRIBUTE;
    }

    public String randomString(int stringLength) {

        Random random = new Random();
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUWVXYZabcdefghijklmnopqrstuwvxyz0123456789".toCharArray();
        String stringRandom = "";
        for (int i = 0; i < stringLength; i++) {

            stringRandom = stringRandom + chars[random.nextInt(chars.length)];
        }

        return stringRandom;
    }

    public WebElement findElementWithKey(String key) {
        return findElement(key);
    }

    public String getElementText(String key) {
        return findElement(key).getText();
    }


    public String getElementAttributeValue(String key, String attribute) {
        return findElement(key).getAttribute(attribute);
    }


    public void javaScriptClicker(WebDriver driver, WebElement element) {

        JavascriptExecutor jse = ((JavascriptExecutor) driver);
        jse.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", element);
    }

    public void javascriptclicker(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }

    @Step({"Wait <value> seconds", "<int> saniye bekle"})
    public void waitBySeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("Elementin Yuklenmesi Beklendi");
    }

    @Step({"Wait <key> seconds for otp code", "<key> Elementi Gorunurse OTP Timeout Icin Bekle"})
    public void waitForOtp(String key) {

        List<WebElement> elements = findElements(key);

        if (elements.size() == 0) {
            logger.info("OTP Icın Beklenmeyecek.");
        }
        if (elements.size() >= 1) {
            logger.info("OTP Timeout Icin 60 Saniye Bekleniyor.");
            waitBySeconds(60);
            logger.info("OTP Timeout Icin 60 Saniye Beklendi.");
        }
    }

    @Step({"Wait <value> seconds for page loading", "Sayfanın Yüklenmesi Icin <int> saniye bekle"})
    public void waitForPage(int seconds) {

        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Step({"Wait <value> milliseconds", "<long> milisaniye bekle"})
    public void waitByMilliSeconds(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Step({"Click to element <key> .", "<key> Elementine tıkla"})
    public void findClickElements(String key) {

        By infoParam = getElementInfoToBy(findElementInfoByKey(key));
        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.visibilityOfElementLocated(infoParam));

        List<WebElement> elements = findElements(key);

        if (elements.size() == 0) {

            Assertions.fail(key + " elementine tiklanamiyor. Element Yok Veya Calismiyor " + "\n *" + "\n********************FAIL*********************" + "\n *" + "\n" + key + "' elementine tiklanamadi. Element Yok Veya Calismiyor. " + "\n *" + "\n********************FAIL********************" + "\n *");
        }
        if (elements.size() > 0) {

            clickElement(findElement(key));
            logger.info(key + " elementine tiklandi.");
            waitByMilliSeconds(2000);
        }
    }

    @Step({"Click to element <key> or give <message>.", "<key> key'li Elemente tikla element yoksa <message> yazdir."})
    public void clickWithoutAssert(String key, String message) {

        List<WebElement> elements = findElements(key);

        if (elements.size() == 0) {

            logger.info(message);

        }
        if (elements.size() > 0) {
            logger.info(key + " elementi mevcut. ");

            hoverElement(findElement(key));
            clickElement(findElement(key));
            logger.info(key + "  elementine tiklandi.");
            waitByMilliSeconds(1000);
        }
    }

    @Step({"Press ESC for <key> or give <message>.", "<key> key'li Element Icin ESC Bas element yoksa <message> yazdir."})
    public void pressEscIfEx(String key, String message) {

        List<WebElement> elements = findElements(key);

        if (elements.size() == 0) {

            logger.info(message);

        }
        if (elements.size() > 0) {
            logger.info(key + " elementi mevcut. ");
            findElement(key).sendKeys(Keys.ESCAPE);
            logger.info(key + "  elementi Icin ESC Tusuna Basildi. ");
            waitByMilliSeconds(1000);
        }
    }

    @Step({"Click to element <key> or give <message>.", "<key> key'li Elemente tikla element yoksa <message> yazdir.."})
    public void clickNumWithoutAssert(String key, String message) {

        waitBySeconds(10);
        List<WebElement> elements = findElements(key);

        if (elements.size() == 0) {

            logger.info(message);

        }
        if (elements.size() > 0) {
            logger.info(key + " elementi mevcut. ");
            logger.info("OTP Timeout Icin 60 Saniye Bekleniyor.");
            waitBySeconds(50);
            logger.info("OTP Timeout Icin 60 Saniye Beklendi.");
            hoverElement(findElement(key));
            clickElement(findElement(key));
            logger.info(key + "  elementine tiklandi.");
            waitByMilliSeconds(2000);
        }
    }

    @Step({"Click To Element <key> .", "Elementine Tikla <key>"})
    public void quickClickElements(String key) {

        List<WebElement> elements = findElements(key);

        if (elements.size() == 0) {

            Assertions.fail(key + " elementine tiklanamiyor. Element Yok Veya Calismiyor " + "\n *" + "\n********************FAIL*********************" + "\n *" + "\n" + key + "' elementine tiklanamadi. Element Yok Veya Calismiyor. " + "\n *" + "\n********************FAIL********************" + "\n *");

        }
        if (elements.size() > 0) {
            logger.info(key + " elementi mevcut. ");

            hoverElement(findElement(key));
            clickElement(findElement(key));
            logger.info(key + "  elementine tiklandi.");
            waitByMilliSeconds(2000);
        }
    }

    @Step({"Frame <number> e geç ve <key> elementine tıkla"})
    public void clickElements(int number, String key) {
        driver.switchTo().frame(number);
        clickElement(findElement(key));
        driver.switchTo().defaultContent();
    }


    @Step("<key> elementin üstünde bekle")
    public void hover(String key) {
        hoverElement(findElement(key));
    }

    @Step({"Click to element <key> with focus", "<key> elementine focus ile tıkla"})
    public void clickElementWithFocus(String key) {
        actions.moveToElement(findElement(key));
        actions.click();
        actions.build().perform();
        logger.info(key + " elementine focus ile tıklandı.");
    }


    @Step({"<key> elementini kontrol et", "Check the element <key>"})
    public void checkElement(String key) {


        By infoParam = getElementInfoToBy(findElementInfoByKey(key));
        WebDriverWait wait = new WebDriverWait(driver, 60);
        wait.until(ExpectedConditions.visibilityOfElementLocated(infoParam));
        List<WebElement> elements = findElements(key);

        if (elements.size() == 0) {

            Assertions.fail("* '" + key + "' Elementi Sayfada Mevcut Degil.  * " +
                    "\n *" +
                    "\n********************FAIL*********************" +
                    "\n *" +
                    "\n '" + key + "' elementi Sayfada Mevcut Degil." + key + " Elementini bulunduran Sayfa Acilmadi veya Eksik Acildi" +
                    "\n *" +
                    "\n********************FAIL********************" +
                    "\n *");

        }
        if (elements.size() > 0) {
            logger.info(key + "  Elementi Bulundu. ");
        }
    }

    @Step({"<key> Elementini Kontrol Et", "Check The Element <key>"})
    public void checkTheElement(String key) {

        By infoParam = getElementInfoToBy(findElementInfoByKey(key));
        WebDriverWait wait = new WebDriverWait(driver, 60);
        wait.until(ExpectedConditions.visibilityOfElementLocated(infoParam));

        List<WebElement> elements = findElements(key);

        if (elements.size() == 0) {

            Assertions.fail("* '" + key + "' Elementi Sayfada Mevcut Degil.  * " +
                    "\n *" +
                    "\n********************FAIL*********************" +
                    "\n *" +
                    "\n '" + key + "' elementi Sayfada Mevcut Degil." + key + "Elementini bulunduran Sayfa Acilmadi veya Eksik Acildi" +
                    "\n *" +
                    "\n********************FAIL********************" +
                    "\n *");

        }
        if (elements.size() > 0) {
            logger.info(key + "  Elementi Bulundu. ");
        }
    }

    @Step({"Go to <url> address", "<url> adresine git"})
    public void goToUrl (String url) {
        driver.get(url);
        logger.info(url + " adresine gidiliyor.");
    }

    @Step({"Wait for element to load with id <id>", "Elementin yüklenmesini bekle <id>"})
    public void waitElementLoadWithCss(String id) {
        waitByMilliSeconds(500);
        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            if (driver.findElements(By.cssSelector(id)).size() > 0) {
                logger.info(id + " elementi bulundu.");
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail("Element: '" + id + "' doesn't exist.");
    }

    @Step({"Wait for element to load with xpath <xpath>", "Elementinin yüklenmesini bekle xpath <xpath>"})
    public void waitElementLoadWithXpath(String xpath) {
        waitByMilliSeconds(5000);
        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            if (driver.findElements(By.xpath(xpath)).size() > 0) {
                logger.info(xpath + " elementi bulundu.");
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail("Element: '" + xpath + "' doesn't exist.");
    }

    @Step({"Check if element <key> exists else print message <message>", "Element <key> var mı kontrol et yoksa hata mesajı ver <message>"})
    public void getElementWithKeyIfExistsWithMessage(String key, String message) {
        ElementInfo elementInfo = findElementInfoByKey(key);
        By by = getElementInfoToBy(elementInfo);

        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            if (driver.findElements(by).size() > 0) {
                logger.info(key + " elementi bulundu.");
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail(message +
                "\n *" +
                "\n********************FAIL********************" +
                "\n *" +
                "\n " + message +
                "\n *" +
                "\n********************FAIL********************" +
                "\n *");
    }


    @Step({"Check if element <key> not exists", "Element yok mu kontrol et <key>"})
    public void checkElementNotExists(String key) {
        ElementInfo elementInfo = findElementInfoByKey(key);
        By by = getElementInfoToBy(elementInfo);


        if (driver.findElements(by).size() == 0) {
            logger.info(key + " elementinin olmadığı kontrol edildi.");
            return;
        }


        logger.info(" !!! Sayfada görünmemesi gereken " + key + " elementi mevcut !!! ");
    }

    @Step({"Check if element <key> exists, click element", "<key> Elementi var mı kontrol et varsa tıkla"})
    public void checkElementExistsAndClick(String key) {

        ElementInfo elementInfo = findElementInfoByKey(key);
        By by = getElementInfoToBy(elementInfo);
        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            if (driver.findElements(by).size() > 0) {
                driver.findElement(by).click();
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail("Element '" + key + "' not exist.");
    }

    @Step({"Check if element <key> exists, click element", "<key> Elementi Varsa Tikla Yoksa Devam Et"})
    public void checkElementExistsThenClick(String key) {

        List<WebElement> elements = findElements(key);

        if (elements.size() == 0) {

            logger.info(key + " Elementi Bulunamadi. ");
        }
        if (elements.size() > 0) {
            logger.info(key + "  Elementi Bulundu. ");
            findElement(key).click();

        }
    }

    @Step({"Check if element <key> exists, click element", "Kullanici Secimi Ekranı Gorunurse <key> Elementine Tikla."})
    public void checkElementExistsThenClickB(String key) {
        By infoParam = getElementInfoToBy(findElementInfoByKey(key));
        WebDriverWait wait = new WebDriverWait(driver, 60);
        wait.until(ExpectedConditions.visibilityOfElementLocated(infoParam));
        List<WebElement> elements = findElements(key);

        if (elements.size() == 0) {

            logger.info(key + " Elementi Bulunamadi. ");
        }
        if (elements.size() > 0) {
            logger.info(key + "  Elementi Bulundu. ");
            findElement(key).click();

        }
    }

    @Step({"ıf element <key> exists click", "<key> elementi görünürse tikla"})
    public void ifExistClick(String key) {
        List<WebElement> elements = findElements(key);

        if (elements.size() == 0) {

            logger.info(key + " Elementi Bulunamadi. ");
        }
        if (elements.size() > 0) {
            logger.info(key + "  Elementi Bulundu. ");
            findElement(key).click();

        }
    }

    @Step({"Upload file in project <path> to element <key>", "Proje içindeki <path> dosyayı <key> elemente upload et"})
    public void uploadFile(String path, String key) {
        String pathString = System.getProperty("user.dir") + "/";
        pathString = pathString + path;
        findElement(key).sendKeys(pathString);
        logger.info(path + " dosyası " + key + " elementine yüklendi.");
    }

    @Step({"Write value <text> to element <key>", "<text> textini <key> elemente yaz"})
    public void ssendKeys(String text, String key) {

        By infoParam = getElementInfoToBy(findElementInfoByKey(key));
        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.visibilityOfElementLocated(infoParam));
        List<WebElement> elements = findElements(key);

        if (elements.size() == 0) {

            logger.info(key + " elementine istenilen text yazilamadi. Element Yok veya Calismiyor. ");
            Assertions.fail(" * " + key + " elementine istenilen text yazilamadi. Element Yok veya Calismiyor. " + "\n *" + "\n********************FAIL********************" + "\n *" + "\n " + key + " elementine istenilen text yazilamadi. Element Yok veya Calismiyor. " + "\n *" + "\n********************FAIL********************" + "\n *");
        }
        if (elements.size() > 0) {
            findElement(key).clear();
            findElement(key).sendKeys(text);
            logger.info(text + " texti " + key + "  elementine yazildi.");
        }
    }

    @Step({"Click with javascript to css <css>", "Javascript ile css tıkla <css>"})
    public void javascriptClickerWithCss(String css) {
        assertTrue(isDisplayedBy(By.cssSelector(css)), "Element bulunamadı");
        javaScriptClicker(driver, driver.findElement(By.cssSelector(css)));
        logger.info("Javascript ile " + css + " tıklandı.");
    }

    @Step({"Click with javascript to xpath <xpath>", "Javascript ile xpath tıkla <xpath>"})
    public void javascriptClickerWithXpath(String xpath) {
        assertTrue(isDisplayedBy(By.xpath(xpath)), "Element bulunamadı");
        javaScriptClicker(driver, driver.findElement(By.xpath(xpath)));
        logger.info("Javascript ile " + xpath + " tıklandı.");
    }

    @Step({"Check if current URL contains the value <expectedURL>", "Şuanki URL <url> değerini içeriyor mu kontrol et"})
    public void checkURLContainsRepeat(String expectedURL) {
        waitBySeconds(2);
        String actualURL = driver.getCurrentUrl();

        if (actualURL != null && actualURL.contains(expectedURL)) {
            logger.info("Şu anki URL " + expectedURL + " değerini içeriyor.");
        } else {
            Assertions.fail("Su anki URL Beklenen URL Degerini Icermiyor" + "  Beklenen Deger : " + expectedURL + ", Su Anki:  " + actualURL);
        }

    }

    @Step({"Check if current URL contains the value <expectedURL> if not give error as a <message>", "Şuanki URL <url> değerini içeriyor mu kontrol et icermiyorsa hata olarak <message> yazdir"})
    public void checkURLContainst(String expectedURL, String message) {
        waitBySeconds(2);
        String actualURL = driver.getCurrentUrl();

        if (actualURL != null && actualURL.contains(expectedURL)) {
            logger.info("Şu anki URL " + expectedURL + " değerini içeriyor.");
        } else {
            Assertions.fail(message +
                    "\n *" +
                    "\n********************FAIL********************" +
                    "\n *" +
                    "\n " + message +
                    "\n *" +
                    "\n********************FAIL********************" +
                    "\n *");
        }
    }


    @Step({"Send TAB key to element <key>", "Elemente TAB keyi yolla <key>"})
    public void sendKeyToElementTAB(String key) {
        findElement(key).sendKeys(Keys.TAB);
        logger.info(key + " elementine TAB keyi yollandı.");
    }

    @Step({"<key> li dropdowndan Secim Yap "})
    public void chooseFromDrop(String key) {
        findElement(key).sendKeys(Keys.ARROW_DOWN);
        findElement(key).sendKeys(Keys.ARROW_DOWN);
        findElement(key).sendKeys(Keys.ARROW_DOWN);
        findElement(key).sendKeys(Keys.ENTER);
        logger.info(key + " elementinden secim yapildi.");
    }

    @Step({"Write value <text> to element <key>",
            "<text> textini <key> Alanina Yaz"})
    public void ssendKeys1(String text, String key) {
        By infoParam = getElementInfoToBy(findElementInfoByKey(key));
        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.visibilityOfElementLocated(infoParam));
        if (!key.equals("")) {
            findElement(key).sendKeys(text);
            logger.info(key + " elementine " + text + " texti yazıldı.");
        }
    }

    @Step({"Yetkili Hesabın Şifresini <key> Alanina Yaz"})
    public void sendPass(String key) {
        By infoParam = getElementInfoToBy(findElementInfoByKey(key));
        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.visibilityOfElementLocated(infoParam));
        if (!key.equals("")) {
            findElement(key).sendKeys("K33lwb7979!");
            logger.info(key + " elementine Yetkili Hesap Şifresi texti yazıldı.");
        }
    }


    @Step("<key> elementi ile tanımlı bayi sayisi kontrol edilir")
    public void sirketSayisi(String key) {

        List<WebElement> elements = findElements(key);
        logger.info(elements.size() + "tane tanımlı bayi listelendi");

    }

    @Step({"Send BACKSPACE key to element <key>", "Elemente BACKSPACE keyi yolla <key>"})
    public void sendKeyToElementBACKSPACE(String key) {
        findElement(key).sendKeys(Keys.BACK_SPACE);
        logger.info(key + " elementine BACKSPACE keyi yollandı.");
    }

    @Step({"<key> Elementi Uzerinde Enter'a Bas"})
    public void sendKeyToElementESCAPE(String key) {
        findElement(key).sendKeys(Keys.ENTER);
        logger.info(key + " Elementi Uzerinde Enter'a Basıldı");
    }
     @Step({"Press Enter", "Pop-up Onayı Icin Enter'a Bas"})
    public void sendKeyToElementESCAPE() {

        driver.findElement(By.xpath("header[class='fixed-top']")).sendKeys(Keys.ENTER);
    }


    @Step({"Check if element <key> has attribute <attribute>", "<key> elementi <attribute> niteliğine sahip mi"})
    public void checkElementAttributeExists(String key, String attribute) {
        WebElement element = findElement(key);
        int loopCount = 0;

        if (element.getAttribute(attribute) != null) {
            logger.info(key + " elementi " + attribute + " niteliğine sahip.");
            return;
        } else {
            logger.info(key + " elementi " + attribute + " niteliğine sahip degil");
        }

    }

    @Step({"Check if element <key> not have attribute <attribute>", "<key> elementi <attribute> niteliğine sahip değil mi"})
    public void checkElementAttributeNotExists(String key, String attribute) {
        WebElement element = findElement(key);

        int loopCount = 0;

        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            if (element.getAttribute(attribute) == null) {
                logger.info(key + " elementi " + attribute + " niteliğine sahip olmadığı kontrol edildi.");
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail("Element STILL have the attribute: '" + attribute + "'");
    }

    @Step({"Check if <key> element's attribute <attribute> equals to the value <expectedValue>", "<key> elementinin <attribute> niteliği <value> değerine sahip mi"})
    public void checkElementAttributeEquals(String key, String attribute, String expectedValue) {
        WebElement element = findElement(key);

        String actualValue;
        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            actualValue = element.getAttribute(attribute).trim();
            if (actualValue.equals(expectedValue)) {
                logger.info(key + " elementinin " + attribute + " niteliği " + expectedValue + " değerine sahip.");
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail("Element's attribute value doesn't match expected value");
    }

    @Step({"Check if <key> element's attribute <attribute> contains the value <expectedValue>", "<key> elementinin <attribute> niteliği <value> değerini içeriyor mu"})
    public void checkElementAttributeContains(String key, String attribute, String expectedValue) {
        WebElement element = findElement(key);

        String actualValue;
        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            actualValue = element.getAttribute(attribute).trim();
            if (actualValue.contains(expectedValue)) {
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail("Element's attribute value doesn't contain expected value");
    }

    @Step({"Write <value> to <attributeName> of element <key>", "<value> değerini <attribute> niteliğine <key> elementi için yaz"})
    public void setElementAttribute(String value, String attributeName, String key) {
        String attributeValue = findElement(key).getAttribute(attributeName);
        findElement(key).sendKeys(attributeValue, value);
    }

    @Step({"Write <value> to <attributeName> of element <key> with Js", "<value> değerini <attribute> niteliğine <key> elementi için JS ile yaz"})
    public void setElementAttributeWithJs(String value, String attributeName, String key) {
        WebElement webElement = findElement(key);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].setAttribute('" + attributeName + "', '" + value + "')", webElement);
    }

    @Step({"Clear text of element <key>", "<key> elementinin text alanını temizle"})
    public void clearInputArea(String key) {
        findElement(key).clear();
        logger.info(key + " Elementinin Input Alanı Temizlendi");
    }

    @Step({"Clear text of element <key> with BACKSPACE", "<key> elementinin text alanını BACKSPACE ile temizle"})
    public void clearInputAreaWithBackspace(String key) {
        WebElement element = findElement(key);
        element.clear();
        element.sendKeys("a");
        actions.sendKeys(Keys.BACK_SPACE).build().perform();
    }

    @Step({"Save attribute <attribute> value of element <key>", "<attribute> niteliğini sakla <key> elementi için"})
    public void saveAttributeValueOfElement(String attribute, String key) {
        SAVED_ATTRIBUTE = findElement(key).getAttribute(attribute);
        System.out.println("Saved attribute value is: " + SAVED_ATTRIBUTE);
    }

    @Step({"Write saved attribute value to element <key>", "Kaydedilmiş niteliği <key> elementine yaz"})
    public void writeSavedAttributeToElement(String key) {
        findElement(key).sendKeys(SAVED_ATTRIBUTE);
    }

    @Step({"Check if element <key> contains text <expectedText>", "<key> elementi <text> değerini içeriyor mu kontrol et"})
    public void checkElementContainsText(String key, String expectedText) {

        Boolean containsText = findElement(key).getText().contains(expectedText);
        assertTrue(containsText, "Expected text is not contained");
        logger.info(key + " elementi" + expectedText + "değerini içeriyor.");
    }

    @Step({"Check if element <key> contains text <expectedText>", "<key> elementi <text> değerini içermediğini kontrol et"})
    public void checkElementContainsTextt(String key, String expectedText) {

        Boolean containsText = findElement(key).getText().contains(expectedText);
        assertFalse(containsText, "Expected text is not contained");
        logger.info(key + " elementi" + expectedText + "değerini içeriyor.");
    }

    @Step({"Write random value to element <key>", "<key> elementine random değer yaz"})
    public void writeRandomValueToElement(String key) {
        findElement(key).sendKeys(randomString(15));
    }

    @Step({"Write random value to element <key> starting with <text>", "<key> elementine <text> değeri ile başlayan random değer yaz"})
    public void writeRandomValueToElement(String key, String startingText) {
        String randomText = startingText + randomString(15);
        findElement(key).sendKeys(randomText);
    }

    @Step({"Print element text by <key>", "<key> Elementinin text değerini yazdır"})
    public void printElementText(String key) {
        String messageBox = findElement(key).getText();
        logger.info(messageBox+" Adet urun listelendi");
    }

    @Step({"Write value <string> to element <key> with focus", "<string> değerini <key> elementine focus ile yaz"})
    public void sendKeysWithFocus(String text, String key) {
        actions.moveToElement(findElement(key));
        actions.click();
        actions.sendKeys(text);
        actions.build().perform();
        logger.info(key + " elementine " + text + " değeri focus ile yazıldı.");
    }

    @Step({"Refresh page", "Sayfayı yenile"})
    public void refreshPage() throws InterruptedException {
        driver.navigate().refresh();
        Thread.sleep(5000);

    }


    @Step({"Change page zoom to <value>%", "Sayfanın zoom değerini değiştir <value>%"})
    public void chromeZoomOut(String value) {
        JavascriptExecutor jsExec = (JavascriptExecutor) driver;
        jsExec.executeScript("document.body.style.zoom = '" + value + "%'");
    }

    @Step({"Open new tab", "Yeni sekme aç"})
    public void chromeOpenNewTab() {
        ((JavascriptExecutor) driver).executeScript("window.open()");
    }

    @Step({"Focus on tab number <number>", "<number> numaralı sekmeye odaklan"})//Starting from 1
    public void chromeFocusTabWithNumber(int number) {
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(number - 1));
    }

    @Step("popupa gec")
    public void switchTo() {
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle).close();
        }
    }

    @Step({"Focus on last tab", "Son sekmeye odaklan"})
    public void chromeFocusLastTab() {
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(tabs.size() - 1));
    }

    @Step({"Focus on frame with <key>", "Frame'e odaklan <key>"})
    public void chromeFocusFrameWithNumber(String key) {
        WebElement webElement = findElement(key);
        driver.switchTo().frame(webElement);
    }

    @Step({"Accept Chrome alert popup", "Chrome uyarı popup'ını kabul et"})
    public void acceptChromeAlertPopup() {
        driver.switchTo().alert().accept();
    }


    //----------------------SONRADAN YAZILANLAR-----------------------------------\\


    // Key değeri alınan listeden rasgele element seçme amacıyla yazılmıştır. @Mehmetİnan

    public void randomPick(String key) {
        List<WebElement> elements = findElements(key);
        Random random = new Random();
        int index = random.nextInt(elements.size());
        elements.get(index).click();
    }

    //Javascript driverın başlatılması
    private JavascriptExecutor getJSExecutor() {
        return (JavascriptExecutor) driver;
    }

    //Javascript scriptlerinin çalışması için gerekli fonksiyon
    private Object executeJS(String script, boolean wait) {
        return wait ? getJSExecutor().executeScript(script, "") : getJSExecutor().executeAsyncScript(script, "");
    }

    //Belirli bir locasyona sayfanın kaydırılması
    private void scrollTo(int x, int y) {
        String script = String.format("window.scrollTo(%d, %d);", x, y);
        executeJS(script, true);
    }

    //Belirli bir elementin olduğu locasyona websayfasının kaydırılması
    public WebElement scrollToElementToBeVisible(String key) {
        ElementInfo elementInfo = findElementInfoByKey(key);
        WebElement webElement = driver.findElement(getElementInfoToBy(elementInfo));
        if (webElement != null) {
            scrollTo(webElement.getLocation().getX(), webElement.getLocation().getY() - 100);
        }
        return webElement;
    }


    @Step({"<key> alanına kaydır"})
    public void scrollToElement(String key) {
        scrollToElementToBeVisible(key);
        logger.info(key + " elementinin olduğu alana kaydırıldı");

    }


    @Step({"<key> alanına js ile kaydır"})
    public void scrollToElementWithJs(String key) {
        ElementInfo elementInfo = findElementInfoByKey(key);
        WebElement element = driver.findElement(getElementInfoToBy(elementInfo));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }


    @Step({"<length> uzunlugunda random bir kelime üret ve <saveKey> olarak sakla"})
    public void createRandomString(int length, String saveKey) {
        saveValue(saveKey, randomString(length));

    }

    @Step({"<key> li elementi bul ve değerini <saveKey> saklanan degeri yazdir", "Find element by <key> and compare saved key <saveKey>"})
    public void equalsSendTextByKey(String key, String saveKey) throws InterruptedException {
        WebElement element;
        int waitVar = 0;
        element = findElementWithKey(key);
        while (true) {
            if (element.isDisplayed()) {
                logger.info("WebElement is found at: " + waitVar + " second.");
                element.clear();
                getValue(saveKey);
                element.sendKeys(getValue(saveKey));

                break;
            } else {
                waitVar = waitVar + 1;
                Thread.sleep(1000);
                if (waitVar == 20) {
                    throw new NullPointerException(String.format("by = %s Web element list not found"));
                } else {
                }
            }
        }
    }

    //Zaman bilgisinin alınması
    private Long getTimestamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return (timestamp.getTime());
    }

    @Step({"<key> li elementi bul, temizle ve rasgele  email değerini yaz", "Find element by <key> clear and send keys  random email"})
    public void RandomMail(String key) {
        Long timestamp = getTimestamp();
        WebElement webElement = findElementWithKey(key);
        webElement.clear();
        webElement.sendKeys("testotomasyon" + timestamp + "@tst.com");
        logger.info(key + " Elemente rastgele e mail degeri yazildi");

    }

    @Step({"<key> li elementi bul, temizle ve rasgele tanim değerini yaz", "Find element by <key> clear and send keys  random email"})
    public void randomMail(String key) {
        Long timestamp = getTimestamp();
        WebElement webElement = findElementWithKey(key);
        webElement.clear();
        webElement.sendKeys("testautomation" + timestamp + "@inprogress.now");
        logger.info(key + " Elemente rastgele kullanici tanim degeri yazildi");

    }

    @Step("<key> olarak <text> seçersem")
    public void choosingTextFromList(String key, String text) throws InterruptedException {
        List<WebElement> comboBoxElement = findElements(key);
        for (int i = 0; i < comboBoxElement.size(); i++) {
            String texts = comboBoxElement.get(i).getText();
            String textim = text;
            if (texts.contains(textim)) {
                comboBoxElement.get(i).click();
                break;
            }
        }
        logger.info(key + " comboboxından " + text + " değeri seçildi");


    }

    @Step("<key> olarak comboboxdan bir değer seçilir")
    public void comboBoxRandom(String key) throws InterruptedException {

        List<WebElement> comboBoxElement = findElements(key);
        int randomIndex = new Random().nextInt(comboBoxElement.size());
        Thread.sleep(2000);
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", comboBoxElement.get(randomIndex));
        logger.info(key + " comboboxından herhangi bir değer seçildi");

    }


    @Step("<key> elementine javascript ile tıkla")
    public void clickToElementWithJavaScript(String key) {
        WebElement element = findElement(key);
        javascriptclicker(element);
        logger.info(key + " elementine javascript ile tıklandı");
    }


    // Belirli bir key değerinin olduğu locasyona websayfasının kaydırılması
    public void scrollToElementToBeVisiblest(WebElement webElement) {
        if (webElement != null) {
            scrollTo(webElement.getLocation().getX(), webElement.getLocation().getY() - 100);
        }
    }


    //Çift tıklama fonksiyonu
    public void doubleclick(WebElement elementLocator) {
        Actions actions = new Actions(driver);
        actions.doubleClick(elementLocator).perform();
    }

    @Step("<key> alanını javascript ile temizle")
    public void clearWithJS(String key) {
        WebElement element = findElement(key);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value ='';", element);
    }

    @Step("<key> elementleri arasından <text> kayıtlı değişkene tıkla")
    public void clickParticularElement(String key, String text) {
        List<WebElement> anchors = findElements(key);
        Iterator<WebElement> i = anchors.iterator();
        while (i.hasNext()) {
            WebElement anchor = i.next();
            if (anchor.getText().contains(getValue(text))) {
                scrollToElementToBeVisiblest(anchor);
                doubleclick(anchor);
                break;
            }
        }
    }

    @Step("<key> menu listesinden rasgele seç")
    public void chooseRandomElementFromList(String key) {
        for (int i = 0; i < 3; i++)
            randomPick(key);
    }


    @Step("<key> olarak <index> indexi seçersem")
    public void choosingIndexFromDemandNo(String key, String index) {

        try {
            TimeUnit.SECONDS.sleep(3);

            List<WebElement> anchors = findElements(key);
            WebElement anchor = anchors.get(Integer.parseInt(index));
            anchor.click();
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Step("Siparis durmununu <kartDurumu> elementinden bul")
    public void findOrderStatus(String kartDurumu) throws InterruptedException {
        WebElement webElement = findElement(kartDurumu);
        logger.info(" webelement bulundu");
        compareText = webElement.getText();
        logger.info(compareText + " texti bulundu");
    }

    @Step("<key> elementiyle karsilastir")
    public void compareOrderStatus(String key) throws InterruptedException {
        WebElement cardDetail = findElement(key);
        String supplyDetailStatus = cardDetail.getText();
        logger.info(supplyDetailStatus + " texti bulundu");
        assertTrue(compareText.equals(supplyDetailStatus));
        logger.info(compareText + " textiyle " + supplyDetailStatus + " texti karşılaştırıldı.");
    }

    @Step("<text> textini <key> elemente tek tek yaz")
    public void sendKeyOneByOne(String text, String key) throws InterruptedException {

        WebElement field = findElement(key);
        field.clear();
        if (!key.equals("")) {
            for (char ch : text.toCharArray())
                findElement(key).sendKeys(Character.toString(ch));
            Thread.sleep(10);
            logger.info(key + " elementine " + text + " texti karakterler tek tek girlilerek yazıldı.");
        }
    }

    @Step("<key> elementine <text> değerini js ile yaz")
    public void writeToKeyWithJavaScript(String key, String text) {
        WebElement element = findElement(key);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value=arguments[1]", element, text);
        logger.info(key + " elementine " + text + " değeri js ile yazıldı.");
    }


    //Bugünün Tarihinin seçilmesi
    public String chooseDate() {
        Calendar now = Calendar.getInstance();
        int tarih = now.get(Calendar.DATE) + 2;
        return String.valueOf(tarih);
    }

    @Step("<key> tarihinden 2 gün sonraya al")
    public void chooseTwoDaysFromNow(String key) {
        List<WebElement> elements = findElements(key);
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).getText().equals(chooseDate())) {
                elements.get(i).click();
            }
        }
    }

    @Step("<variable> değişkenini <key> elementine yaz")
    public void sendKeysVariable(String variable, String key) {
        if (!key.equals("")) {
            clearInputArea(key);
            findElement(key).sendKeys(getValue(variable));
            logger.info(key + " elementine " + getValue(variable) + " texti yazıldı.");
        }
    }

    @Step("<key> olarak comboboxtan <text> seçimini yap")
    public void selectDropDown(String key, String text) {
        Select drpCountry = new Select(findElement(key));
        drpCountry.selectByVisibleText(text);
        logger.info(key+" li comboboxtan " + text + " Secimi Yapildi.");
    }

    @Step("<key> elementli alandan rastgele secim yap")
    public void randomPick2(String key) {
        List<WebElement> elements = findElements(key);
        Random random = new Random();
        int index = random.nextInt(elements.size());
        elements.get(index).click();
        logger.info(key + " Elementli Alandan Rastgele Secim Yapildi");
    }

    @Step("Diğer sekmeye geç")
    public void switchToPage() {

        for (String curWindow : driver.getWindowHandles()) {
            driver.switchTo().window(curWindow);
        }
    }

    @Step("Aktif Sekmeyi Kapat")
    public void closeTabs() {

            driver.close();
            logger.info("Aktif Sekme Kapatildi. ");
            waitBySeconds(5);

    }

    @Step("İkinci sekmeye geç")
    public void switchToPage2() {
        String parentWindow = driver.getWindowHandle();
        Set<String> allWindows = driver.getWindowHandles();
        for (String curWindow : allWindows) {
            driver.switchTo().window(curWindow);
        }
    }

    @Step("Aşağıya scroll et")
    public void scrollDownOrUp() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,500)");
    }

    @Step("Aşağıya scroll et2")
    public void scrollDownOrUp2() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,300)");
    }

    @Step("Aşağıya scroll et1000")
    public void scrollDownOrUp3() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,500)");
    }

    @Step("Aşağıya scroll et10001")
    public void scrollDownOrUp4() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,1000)");
    }

    @Step("<key> li elemente Alfabetik veya Ozel Karakter Giriliyormu")
    public void validCharTest(String key) {
        WebElement element = findElementWithKey(key);
        element.sendKeys("qWeRtYuIoU");
        String yazilanDegerAlph = element.getAttribute("value");
        int sizeAlph = yazilanDegerAlph.length();
        if (sizeAlph == 0) {
            System.out.println(key + " Elementine Alfabetik Karakter Girilmiyor");
        }
        element.clear();
        element.sendKeys("%#$!%%");
        String yazilanDegerSpec = element.getAttribute("value");
        int sizeSpec = yazilanDegerSpec.length();
        if (sizeSpec == 0) {
            System.out.println(key + " Elementine Ozel Karakter Girilmiyor");

        }
    }

    @Step("<key>'li dropBox'in secenekleri listeleniyor mu kontrol edilir")
    public void dropBoxKontrol(String key) {
        List<WebElement> elements = findElements(key);
        logger.info(elements.size() + " tane seçenek var");
    }

    @Step({"Select <key> li elementin <index>. indexini sec"})
    public void select(String key, int index) {
        Select select = new Select(findElement(key));
        select.selectByIndex(index);
        logger.info(key + "li comboboxtan " + index + ". secenek secildi");
    }
    @Step({"<key> li elementin <value> degerini seç"})
    public void selectByValue(String key, String value) {
        Select select = new Select(findElement(key));
        select.selectByValue(value);
        logger.info(key + "li comboboxtan " + value + ". secenek secildi");
    }

    @Step({"Check if element <key> not contains text <expectedText>",
            "<key> elementi <text> değerini içermiyor mu kontrol et"})
    public void checkElementNotContainsText(String key, String expectedText) {

        Boolean containsText = findElement(key).getText().contains(expectedText);
        assertFalse(containsText, "Expected text is not contained");
        logger.info(key + " elementi " + expectedText + " değerini içermiyor.");
    }

    @Step("<key>'li elementi kontrol et")
    public void checkElementt(String key) {
        By infoParam = getElementInfoToBy(findElementInfoByKey(key));
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(infoParam));
        List<WebElement> elements = findElements(key);
        if (elements.size() > 0) {
            logger.info("Aranan " + key + " elementi bulundu");
        } else {
            logger.info("Aranan " + key + " elementi bulunamadi");
        }

    }

    @Step("<key> elementi ile izinli kullanıcı sayisi kontrol edilir")
    public void izinliKullaniciSayisi(String key) {
        List<WebElement> elements = findElements(key);
        logger.info(elements.size() + " tane izinli kullanici listelendi");

    }


    @Step({"<key> elementinin yüklenmesini bekle", "wait for element <key>"})
    public void waitForElement(String key) {

        By infoParam = getElementInfoToBy(findElementInfoByKey(key));
        WebDriverWait webDriverWait = new WebDriverWait(driver, 60);
        WebElement webElement = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(infoParam));
        List<WebElement> elements = findElements(key);

        if (elements.size() == 0) {
            logger.info(key + " Elementi Bulunamadi. ");
        }
        if (elements.size() > 0) {

            logger.info(key + "  Elementi icin beklendi. ");
        }
    }

    @Step({"Upload file <path> to element <key>",
            "<path> dosyayı <key> elemente upload et"})
    public void uploadFiletoTestinium(String path, String key) {
        String pathString = System.getProperty("user.dir") + "/";
        logger.info("user dir !!!!!!!" + System.getProperty("user.dir"));
        pathString = pathString + path;
        findElement(key).sendKeys(path);
        logger.info(path + " dosyası " + key + " elementine yüklendi.");
    }











  }

