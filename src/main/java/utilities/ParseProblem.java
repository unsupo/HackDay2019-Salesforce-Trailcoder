package utilities;

import com.google.gson.Gson;
import objects.LeetCodeProblem;
import objects.LeetCodeProblemTestCases;
import objects.TestCase;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ParseProblem {

    public static void main(String[] args) throws Exception {
        System.out.println(
            runTests(
                parse("class Solution { \n" +
                        "     public int totalNQueens(int n) {\n" +
                        "        if (n <= 0)\n" +
                        "            return n == 0 ? 0 : 1;\n" +
                        "        boolean [] rows = new boolean[n], diags = new boolean[2 * n], antiDiags = new boolean[2 * n];\n" +
                        "        return solve(0, n, rows, antiDiags, diags);\n" +
                        "    }\n" +
                        "    \n" +
                        "    private int solve(int col, int n, boolean[] rows, boolean[] antiDiags, boolean[] diags) {\n" +
                        "        if (col == n)\n" +
                        "            return 1;\n" +
                        "        int sum = 0;\n" +
                        "        for (int row = 0; row < n; row++) {\n" +
                        "            if (rows[row] || antiDiags[row - col + n] || diags[row + col])\n" +
                        "                continue;\n" +
                        "            rows[row] = antiDiags[row - col + n] = diags[row + col] = true;\n" +
                        "            sum += solve(col + 1, n, rows, antiDiags, diags);\n" +
                        "            rows[row] = antiDiags[row - col + n] = diags[row + col] = false;\n" +
                        "        }\n" +
                        "        return sum;\n" +
                        "    } \n" +
                        " }", 76+"")
                    , 76+"")
        );

    }

    public List<LeetCodeProblem> leetCodeProblems = new ArrayList<>();
    public List<LeetCodeProblemTestCases> leetCodeProblemsTests = new ArrayList<>();

    Gson gson = new Gson();
    private ParseProblem(){
        try {
            leetCodeProblems = FileOptions.getAllFiles("leetcode_problems").stream().map(a-> {
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
            }).collect(Collectors.toList());
            leetCodeProblemsTests = FileOptions.getAllFiles("leetcode_problems_tests").stream().map(a-> {
                try {
                    LeetCodeProblemTestCases problem = gson.fromJson(FileOptions.readFileIntoString(a), LeetCodeProblemTestCases.class);
                    return problem;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        init();
    }
    private static ParseProblem instance;
    public static List<LeetCodeProblem> getAllLeetCodeProblems(){
        if(instance==null)
            instance = new ParseProblem();
        return instance.leetCodeProblems;
    }

    public static String run(String parsedCode) throws IOException, InterruptedException {
        String f ="Problem",ff=f+".java";
        FileOptions.writeToFileOverWrite(ff,parsedCode);
        StringBuilder builder = new StringBuilder();
        String errors = runCommand("javac "+ff);
        if(!errors.trim().equals(""))
            builder.append(errors);
        builder.append(runCommand("java "+f));
        return builder.toString();
    }

    private static String runCommand(String command) throws IOException, InterruptedException {
        Process proc = Runtime.getRuntime().exec(command);

        // Read the output

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(proc.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        int i = 0;
        while((line = reader.readLine()) != null)
            builder.append(line + (i++==0?"":"\n"));

        BufferedReader ereader =
                new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        line = "";
        while((line = ereader.readLine()) != null)
            builder.append(line + "\n");
        BufferedReader oreader =
                new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        line = "";
        while((line = oreader.readLine()) != null)
            builder.append(line + "\n");

        proc.waitFor();
        return builder.toString();
    }

    static String theRest = "public class Problem{\n" +
            "   public static void main(String[] args){\n" +
            "        System.out.print(new Solution().%s(%s));\n" +
            "   }\n" +
            "}\n" +
            "%s";

    public static String parse(String code, String problemNum) {
        try {
            getAllLeetCodeProblems();
            String parsed = URLDecoder.decode(code,"UTF-8").replace(" ","");
            LeetCodeProblemTestCases testCase = testCasesHashMap.get(problemNum);
            parsed=String.format(theRest, testCase.getMethodName(),"%s", parsed);
//            String testing = parsed.replace("\n","");
//            LeetCodeProblemTestCases testCase = testCasesHashMap.get(problemNum);
//            String regexFirstTest = String.format("class\\s+Solution\\s+\\{\\s+public\\s+%s\\s+\\%s\\(%s\\s+\\w+\\)\\s+\\{",
//                    testCase.getMethodName(),testCase.getReturnType(),
//                    Arrays.asList(testCase.getParams()).stream().map(a->a.toString()).collect(Collectors.joining("\\s+,\\s+")));
//            if(!code.matches(Pattern.quote(regexFirstTest)))
//                throw new Exception("Invalid class, method, input, or output type(s)");
//            String theCode = String.format(theRest, 5,2, URLDecoder.decode(code,"UTF-8").replace(" ",""));
//            LeetCodeProblem leetCodeProblem = getAllLeetCodeProblems().get(Integer.parseInt(problemNum));

//            leetCodeProblem.getCodeSample();
//            Object o = Class.forName("Solution").newInstance();

            return parsed;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String runTests(String parsed, String problemNum) throws Exception {
        LeetCodeProblemTestCases testCase = testCasesHashMap.get(problemNum);
        for(TestCase testCase1: testCase.getTestCases()) {
            String inputs = Arrays.asList(testCase1.getInputs()).stream()
                    .map(a -> stringify(a)).collect(Collectors.joining(","));
            String yourAnswer = run(String.format(parsed, inputs));
            if(!yourAnswer.equals(stringify(testCase1.getOutput())))
                throw new Exception(String.format("Failed Test Case: %s, expected %s", inputs, testCase1.getOutput().toString())+
                        "\nYour Answer: "+yourAnswer);
        }
        return "Correct";
    }

    private static String stringify(Object a) {
        if(a instanceof String)
            return String.format("\"%s\"", a.toString());
        if(a instanceof Integer | a instanceof Boolean)
            return a.toString();
        if(a instanceof Integer[][]){
            StringBuilder builder = new StringBuilder("new int[][]{");
            Integer[][] aa = (Integer[][])a;
            for(Integer[] i : aa){
                builder.append("{");
                for (int j = 0; j < i.length; j++) {
                    builder.append(i[j]+(i.length-1==j?"":","));
                }
                builder.append("},");
            }
            builder.append("}");
            return builder.toString();
        }
        return "Not Supported";
    }

    private static HashMap<String,LeetCodeProblemTestCases> testCasesHashMap = new HashMap<>();
    private static void init(){
        LeetCodeProblemTestCases testCases = new LeetCodeProblemTestCases();
        testCases.setProblemNum("76");
        testCases.setTitle("N-Queens II");
        testCases.setMethodName("totalNQueens");
        testCases.setParams(new Object[]{Integer.class});
        testCases.setReturnType(Integer.class);
        testCases.setTestCases(new TestCase[]{
                new TestCase(new Object[]{4}, 2),
                new TestCase(new Object[]{1}, 1),
                new TestCase(new Object[]{2}, 0),
                new TestCase(new Object[]{3}, 0),
                new TestCase(new Object[]{5}, 10)
        });
        testCasesHashMap.put(testCases.getProblemNum(),testCases);

        testCases = new LeetCodeProblemTestCases();
        testCases.setProblemNum("34");
        testCases.setTitle("Regular Expression Matching");
        testCases.setMethodName("isMatch");
        testCases.setParams(new Object[]{String.class,String.class});
        testCases.setReturnType(Boolean.class);
        testCases.setTestCases(new TestCase[]{
                new TestCase(new Object[]{"aa","a"}, false),
                new TestCase(new Object[]{"aa","a*"}, true),
                new TestCase(new Object[]{"ab",".*"}, true),
                new TestCase(new Object[]{"aab","c*a*b"}, true),
                new TestCase(new Object[]{"mississippi","mis*is*p*."}, false)
        });
        testCasesHashMap.put(testCases.getProblemNum(),testCases);

        testCases = new LeetCodeProblemTestCases();
        testCases.setProblemNum("7");
        testCases.setTitle("Unique Paths II");
        testCases.setMethodName("uniquePathsWithObstacles");
        testCases.setParams(new Object[]{Integer[][].class});
        testCases.setReturnType(Integer.class);
        testCases.setTestCases(new TestCase[]{
                new TestCase(new Object[]{new Integer[][]{
                        {0,0,0},
                        {0,1,0},
                        {0,0,0}
                    }}, 2),
                new TestCase(new Object[]{new Integer[][]{
                        {0}
                }}, 1)
        });
        testCasesHashMap.put(testCases.getProblemNum(),testCases);
    }
}
