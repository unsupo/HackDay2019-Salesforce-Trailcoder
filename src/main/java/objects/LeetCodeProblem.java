package objects;

public class LeetCodeProblem {
    String title, description, difficulty, html,codeSample;

    public LeetCodeProblem(String title, String description, String difficulty, String descriptionContent, String codeSample) {
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.html = descriptionContent;
        this.codeSample = codeSample;
    }

    public String getHtml() {
        return html;
    }

    public String getCodeSample() {
        return codeSample;
    }

    public void setCodeSample(String codeSample) {
        this.codeSample = codeSample;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return "LeetCodeProblem{" +
                "title='" + title + '\'' +
                ", difficulty='" + difficulty + '\'' +
                '}';
    }
}
