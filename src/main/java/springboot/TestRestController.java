package springboot;

import com.google.gson.Gson;
import objects.LeetCodeProblem;
import org.jsoup.Jsoup;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import utilities.FileOptions;
import utilities.ParseProblem;
import utilities.database.Database;

import java.io.IOException;
import java.util.stream.Collectors;

@RestController
public class TestRestController {
    Gson gson = new Gson();
    @RequestMapping("/getalldata")
    public String getAllData() {
        try {
            return gson.toJson(ParseProblem.getAllLeetCodeProblems());
//            return gson.toJson(
//                    Database.getExistingDatabaseConnection().executeQuery("select * from problems"));
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @RequestMapping("/sendcode")
    public String sendCode(@RequestParam("problemNum") String problemNum,
                           @RequestParam("code") String code){
        try {
            return gson.toJson(ParseProblem.runTests(ParseProblem.parse(code,problemNum),problemNum));
        } catch (Exception e) {
            return gson.toJson(e.getMessage());
        }
    }
}