package Uckit;

import Uckit.Equation;
import Uckit.Variable;

public class Step{
    private boolean given = false;

    private String fromName;

    private String varName;

    public Step(Variable var){
        this.varName = var.getName();
        given = true;
    }

    public Step(Equation eq, Variable var){
        this.varName = var.getName();
        if(eq == null){
            given = true;
        }else{
            fromName = eq.toString();
        }
    }

    public String toString(){
        String res = "<" + varName;
        if(given){
            return res + " - Given>";
        }else{
            return res + " - " +fromName+">";
        }
    }

    public String getFromName() {
        return fromName;
    }

    public boolean isGiven() {
        return given;
    }

    public String getVarName() {
        return varName;
    }
}