package objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import utilities.FileOptions;

import java.io.File;
import java.io.IOException;

import static java.lang.reflect.Modifier.TRANSIENT;

public class LeetCodeProblemTestCases {
    public static void main(String[] args) throws IOException {
        LeetCodeProblemTestCases testCases = new LeetCodeProblemTestCases();
        testCases.setProblemNum("76");
        testCases.setTitle("N-Queens II");
        testCases.setMethodName("totalNQueens");
        testCases.setParams(new Object[]{Integer.class});
        testCases.setReturnType(Integer.class);
        testCases.setTestCases(new TestCase[]{
                new TestCase(new Object[]{4}, 2)
        });
//        String dir = "leetcode_problems_tests";
//        new File(dir).mkdir();
//        FileOptions.writeToFileOverWrite(dir+"/"+testCases.getTitle(),gson.toJson(testCases));
    }

    String className="Solution", methodName, problemNum, title;
    Object[] params;
    Object returnType;
    TestCase[] testCases;

    public String getProblemNum() {
        return problemNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setProblemNum(String problemNum) {
        this.problemNum = problemNum;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Object getReturnType() {
        return returnType;
    }

    public void setReturnType(Object returnType) {
        this.returnType = returnType;
    }

    public TestCase[] getTestCases() {
        return testCases;
    }

    public void setTestCases(TestCase[] testCases) {
        this.testCases = testCases;
    }
}
