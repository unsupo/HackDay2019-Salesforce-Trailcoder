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
            return gson.toJson(FileOptions.getAllFiles("leetcode_problems").stream().map(a-> {
                try {
                    LeetCodeProblem problem = gson.fromJson(FileOptions.readFileIntoString(a), LeetCodeProblem.class);
                    String html = Jsoup.parse(problem.getHtml()).select("div").stream().filter(b ->
                            b.classNames().stream().filter(c -> c.contains("content") && c.contains("question-content"))
                                    .collect(Collectors.toList()).size() > 0
                    ).collect(Collectors.toList()).get(0).html();
                    problem.setHtml(html);
                    return problem;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList()));
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
            return ParseProblem.run(ParseProblem.parse(code));
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}