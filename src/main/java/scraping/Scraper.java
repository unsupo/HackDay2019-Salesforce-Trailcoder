package scraping;

import com.google.gson.Gson;
import objects.LeetCodeProblem;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.CommonSelenium;
import utilities.FileOptions;
import utilities.PasswordEncryptDecrypt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Scraper {
    public static void main(String[] args) throws Exception {
        String dir = "leetcode_problems";
        new File(dir).mkdir();
        Gson gson = new Gson();
        Scraper s = new Scraper("theleetcoder","password");
        List<LeetCodeProblem> leetCodeProblems = new ArrayList<>();
        List<String> problems = FileOptions.readFileIntoListString("problems.txt");
        List<String> premiumProblems = FileOptions.readFileIntoListString("premium-problems.txt");
        problems = problems.stream().filter(a->!premiumProblems.contains(a)).collect(Collectors.toList());
        for(String problemLink : problems) {
            String[] split = problemLink.split("/");
            String name = split[split.length-1];
            List<File> test = FileOptions.getAllFiles(dir).stream().filter(a -> a.getName().contains(name)).collect(Collectors.toList());
            if(test.size()>0)
                continue;
            LeetCodeProblem leetCodeProblem = s.parse(problemLink);
            leetCodeProblems.add(leetCodeProblem);
            FileOptions.writeToFileOverWrite(dir+"/"+name+".json",
                    gson.toJson(leetCodeProblem));
        }
        s.driver.quit();
    }

    private static final String BASE_URL="https://leetcode.com";
    private static final String LOGIN_URL=BASE_URL+"/problemset/all/"; //"https://leetcode.com/accounts/login/";

    private WebDriver driver;
    private boolean isLoggedIn = false;

    public Scraper(String username, String encpassword){
        setup(username,encpassword);
    }

    public void setup(String username, String encpassword){
        try {
//            new File(DIR).mkdirs();
            driver = CommonSelenium.getChromeDriver();//"--headless"
            login(username,
                    PasswordEncryptDecrypt.decrypt(encpassword));
        }catch (Exception e){
            e.printStackTrace();
            driver.quit();
        }
    }

    private void login(String username, String password){
        if(isLoggedIn)
            return;
        driver.get(LOGIN_URL);
        if(isNeedLogin())
            return;
        new WebDriverWait(driver,60*5)
                .until(webDriver -> Jsoup.parse(driver.getPageSource()).select("input").size() == 2);
        Elements inputs = Jsoup.parse(driver.getPageSource()).select("input");
        driver.findElement(By.cssSelector(inputs.get(0).cssSelector())).click();
        driver.findElement(By.cssSelector(inputs.get(0).cssSelector())).sendKeys(username);
        driver.findElement(By.cssSelector(inputs.get(1).cssSelector())).click();
        driver.findElement(By.cssSelector(inputs.get(1).cssSelector())).sendKeys(password);
        driver.findElement(By.cssSelector(Jsoup.parse(driver.getPageSource()).select("button").get(1).cssSelector())).click();
//        CommonSelenium.setValue(inputs.get(0).cssSelector(),username,driver);
//        CommonSelenium.click(inputs.get(1).cssSelector(),driver);
//        CommonSelenium.setValue(inputs.get(1).cssSelector(),password,driver);
//        CommonSelenium.click(Jsoup.parse(driver.getPageSource()).select("button").stream().filter(a->a.text().equals("Sign In")).collect(Collectors.toList()).get(0).cssSelector(),driver);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new WebDriverWait(driver,60*5)
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector("tbody.reactable-data")));
//                .until(webDriver -> Jsoup.parse(driver.getPageSource()).select("tbody.reactable-data").size() == 2);
        isLoggedIn = true;
//        driver.switchTo().
    }

    private boolean isNeedLogin() {
        try {
            new WebDriverWait(driver, 30)
                    .until(ExpectedConditions.elementToBeClickable(By.cssSelector("tbody.reactable-data")));
            isLoggedIn=true;
            return true;
        }catch (Exception e){
            /*DO NOTHING*/
            return false;
        }
    }

    private LeetCodeProblem parse(String problemLink) {
        driver.navigate().to(BASE_URL+problemLink);
        try {
            new WebDriverWait(driver, 5)
                    .until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.banner")));
            return null;
        }catch (Exception e){
            /*DO NOTHING this is a good thing*/
        }
        try {
            new WebDriverWait(driver, 60 * 5)
                    .until(webDriver -> Jsoup.parse(driver.getPageSource()).select("a > div > span > div > span").size() >= 3);
            String descriptionContent = Jsoup.parse(driver.getPageSource()).select("div").stream().filter(a -> a.hasAttr("data-key") && a.attr("data-key").equals("description-content")).collect(Collectors.toList()).get(0).html();
            String title = Jsoup.parse(driver.getPageSource()).select("div").stream()
                    .filter(a -> a.hasAttr("data-cy") && a.attr("data-cy").equals("question-title"))
                    .collect(Collectors.toList()).get(0).text().replaceAll("[0-9]+\\. ", "");
            String difficulty = Jsoup.parse(driver.getPageSource()).select("div").stream().filter(a -> a.hasAttr("diff")).collect(Collectors.toList()).get(0).text();
            String descriptiion = Jsoup.parse(driver.getPageSource()).select("div > p").parents().get(0).text();
            String codeSample = Jsoup.parse(driver.getPageSource()).select("div.CodeMirror-code >div").text().replaceAll("[0-9]+","\n");
            return new LeetCodeProblem(title,descriptiion,difficulty,descriptionContent,codeSample);
        }catch (Exception e){
            driver.navigate().refresh();
            return parse(problemLink);
        }
    }
}

/**
// * get aws cloud formations
for(Element e : Jsoup.parse(driver.getPageSource()).select(".awsui-cards-card-container-inner")) {
  Thread.sleep(2000);
  driver.findElement(By.cssSelector(e.cssSelector())).click();
  String name = Jsoup.parse(driver.getPageSource()).select(".awsui-breadcrumb > a").last().text();
  String contents = Jsoup.parse(driver.getPageSource()).select("code").text();
  FileOptions.writeToFileOverWrite(name+".json",contents);
}
 **/