package scraping;

import com.google.gson.Gson;
import objects.LeetCodeProblem;
import utilities.FileOptions;
import utilities.database.Database;

import java.io.IOException;

public class AddProblemsToDatabase {
    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();

        FileOptions.getAllFiles("leetcode_problems").stream().map(a-> {
            try {
                return gson.fromJson(FileOptions.readFileIntoString(a), LeetCodeProblem.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).forEach(a->{
            try {
                Database.getExistingDatabaseConnection().executeQuery(String.format("insert into " +
                        "problems(problem_name,difficulty,difficulty_num,html,description,code_sample)" +
                        " values('%s','%s','%s','%s','%s');",
                        a.getTitle(),a.getDifficulty(),convertDifficulty(a.getDifficulty()),a.getHtml(),
                        a.getDescription(),a.getCodeSample()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        System.exit(0);
    }

    private static String convertDifficulty(String difficulty) {
        switch (difficulty.toLowerCase()){
            case "easy": return 1+"";
            case "medium": return 2+"";
            case "hard": return 3+"";
            default:
                return 100+"";
        }
    }
}
