package Uckit;

import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.Symbol;

import java.util.*;

import static Uckit.CASRecursiveSolver.evaluator;
import static Uckit.CASRecursiveSolver.uckit;

public class Variable implements VariableComputedObserver {
    //Numeric value of this
    private Double value;
    //Name / symbol of variable
    private String name;
    //Symbol of this variable CAS style
    private Symbol symbol;
    //Description of this variable
    private String description = "";
    //List of steps to get here
    private final List<Step> steps = new ArrayList<>();

    private static final HashMap<String, Variable> variableFromName = new HashMap<>();
    public static final HashMap<Variable, Set<Equation>> derivedFrom = new HashMap<>();
    public static final HashMap<Variable,Set<Equation>> includedIn = new HashMap<>();

    public static Collection<Variable> getAllVariables(){
        return variableFromName.values();
    }

    public static Variable get(String name){
        return variableFromName.get(name);
    }

    public Variable(String name){
        if(variableFromName.containsKey(name)){
            throw new IllegalArgumentException("Variable must have unique name");
        }
        this.name = name;
        this.symbol = new Symbol(name, uckit);
        variableFromName.put(name,this);
        evaluator.eval(symbol);
    }

    public void addDerivedFrom(Equation... equations){
        derivedFrom.computeIfAbsent(this, k -> new HashSet<>());
        for(Equation equation : derivedFrom.get(this)) {
            derivedFrom.get(this).add(equation);
        }
    }

    public void addIncludedIn(Equation... equations){
        includedIn.computeIfAbsent(this, k -> new HashSet<>());
        for(Equation equation : includedIn.get(this)) {
            includedIn.get(this).add(equation);
        }
    }


    public Double getValue() {
        return value;
    }

    private void setValue(Double value) {
        this.value = value;
        getSymbol().assignValue(evaluator.eval(value.toString()));
        evaluator.eval(getExprString());
        notifyOthers(new VariableComputedEvent(this));
    }

    public boolean hasValue(){
        return value != null;
    }

    public String getExprString(){
        if(hasValue()){
            return name + "=" + getValue();
        }else{
            return name;
        }
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public Variable withValue(double value){
        evaluate(value);
        return this;
    }

    public Variable withDescription(String description){
        setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void evaluate(double value, Step... steps){
        setValue(value);
        this.steps.addAll(Arrays.asList(steps));
    }

    public void evaluate(double value){
        steps.add(new Step(this));
        setValue(value);
    }


    /**
     * @param event
     * @author Sasha
     */
    @Override
    public void notify(VariableComputedEvent event) {

    }

    public String toString(){
        return "{ "+ symbol.getSymbolName() + " = " + value + " \n " + "Steps: " + getSteps() + "\n}";
    }
}

class Step{
    private boolean given = false;

    private Equation from;

    private Variable var;

    public Step(Variable var){
        this.var = var;
        given = true;
    }

    public Step(Equation eq, Variable var){
        this.var = var;
        if(eq == null){
            given = true;
        }else{
            from = eq;
        }
    }

    public String toString(){
        String res = var.getSymbol().getSymbolName();
        if(given){
            return res + " - Given";
        }else{
            return res + " - " +from.toString();
        }
    }


    public Equation getFrom() {
        return from;
    }

}