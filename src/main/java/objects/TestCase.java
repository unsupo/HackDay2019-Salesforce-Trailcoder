package objects;

public class TestCase {
    Object[] inputs;
    Object output;

    public TestCase(Object[] inputs, Object output) {
        this.inputs = inputs;
        this.output = output;
    }

    public Object[] getInputs() {
        return inputs;
    }

    public void setInputs(Object[] inputs) {
        this.inputs = inputs;
    }

    public Object getOutput() {
        return output;
    }

    public void setOutput(Object output) {
        this.output = output;
    }
}
