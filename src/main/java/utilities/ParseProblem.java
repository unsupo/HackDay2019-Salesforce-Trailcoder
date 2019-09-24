package utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ParseProblem {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(
            run(
                parse("class Solution {\n" +
                        "    public int problem(int nums, int target) {\n" +
                        "        return nums*target;\n" +
                        "    }\n" +
                        "}")
            )
        );

    }

    private static String run(String parsedCode) throws IOException, InterruptedException {
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
        String line = "";
        while((line = reader.readLine()) != null)
            builder.append(line + "\n");

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

    static String theRest = "public class Problem{" +
            "   public static void main(String[] args){" +
            "        System.out.println(new Solution().problem(%s,%s));" +
            "   }" +
            "}" +
            "%s";

    public static String parse(String code) {
        return String.format(theRest, 5,2,code);
    }
}
