package springboot;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utilities.ParseProblem;
import utilities.database.Database;

@RestController
public class TestRestController {
    Gson gson = new Gson();
    @RequestMapping("/getalldata")
    public String getAllData() {
        try {
            return gson.toJson(
                    Database.getExistingDatabaseConnection().executeQuery("select * from problems"));
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @RequestMapping("/sendcode")
    public String sendCode(@RequestHeader("problemNum") String problemNum,
                           @RequestHeader("code") String code){
        return ParseProblem.parse(code);
    }
}