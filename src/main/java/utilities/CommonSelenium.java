package utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CommonSelenium {

    private static String   jsProp = "document.querySelector('%s').value='%s'",
                            jsButt = "document.querySelector('%s').click()",
                            jsSel = "document.querySelector('%s')",
                            jsPropid = "document.getElementById('%s').value='%s'",
                            jsButtid = "document.getElementById('%s').click()";

    public static WebDriver getChromeDriver(String...args){
        WebDriverManager.chromedriver().setup();
        String userProfile = System.getProperty("user.dir")+"/src/main/resources/chrome_profile";
        new File(userProfile).mkdir();
//        String userProfile = "/Users/jarndt/Library/Application Support/Google/Chrome/Profile 2";
        ChromeOptions options = new ChromeOptions();
        for(String arg : args)
            options.addArguments(arg);
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("profile.password_manager_enabled", true);     // This step will enable "Offer to save password" checkbox in chrome://settings.
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("user-data-dir="+userProfile);
        options.addArguments("--start-maximized");
        return new ChromeDriver(options);
    }


    public static Object executeJS(String js, WebDriver driver){
        return ((JavascriptExecutor) driver).executeScript(js);
    }

    public static void setValue(String selector, String value, WebDriver driver) {
        executeJS(String.format(jsProp,selector,value.replace("'","\\'").replace("\n","\\n")),driver);
    }

    public static void click(String selector, WebDriver driver) {
        executeJS(String.format(jsButt, selector),driver);
    }

    public static void waitForIFrame(WebDriver driver, ExpectedCondition<WebElement>...conditions) {
        WebDriverWait ww = new WebDriverWait(driver, 60 * 5 /*wait 5 minutes for 2FA */);
        if(conditions == null || conditions.length == 0 || conditions[0] == null)
            ww.until((ExpectedCondition<Boolean>) d ->
                    Jsoup.parse(driver.getPageSource()).select("iframe").size() > 0
                    && !Jsoup.parse(driver.getPageSource()).select("iframe").get(0).attr("id").isEmpty()
            );
        else {
            ww.until(ExpectedConditions.or(
                    (ExpectedCondition<Boolean>) d ->
                            Jsoup.parse(driver.getPageSource()).select("iframe").size() > 0
                                    && !Jsoup.parse(driver.getPageSource()).select("iframe").get(0).attr("id").isEmpty(),
                    conditions[0]
            ));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(Jsoup.parse(driver.getPageSource()).select("iframe").size()==0)
            return;
        String iframeId = Jsoup.parse(driver.getPageSource()).select("iframe").get(0).attr("id");
        if(!iframeId.isEmpty()) {
            ww.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.id(iframeId), 0));
            driver.switchTo().frame(driver.findElement(By.id(iframeId)));
        }
    }

    public static void setValueById(String id, String username, WebDriver driver) {
        executeJS(String.format(jsPropid,id,username.replace("'","\\'").replace("\n","\\n")),driver);
    }

    public static void clickById(String id, WebDriver driver) {
        executeJS(String.format(jsButtid, id),driver);
    }

    public static void waitForId(WebDriver driver,String id) {
        WebDriverWait ww = new WebDriverWait(driver, 60 * 5 /*wait 5 minutes for 2FA */);
        ww.until((ExpectedCondition<Boolean>) d ->
                Jsoup.parse(driver.getPageSource()).getElementById(id).id().equals(id)
        );
    }
}
