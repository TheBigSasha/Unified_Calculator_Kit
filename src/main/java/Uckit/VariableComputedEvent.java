package Uckit;

public class VariableComputedEvent {
    public VariableComputedEvent(Variable var) {
        this.var = var;
    }

    public Variable getVar() {
        return var;
    }

    public void setVar(Variable var) {
        this.var = var;
    }

    private Variable var;
}
