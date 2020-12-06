package Uckit.model;

import com.google.gson.annotations.SerializedName;
import org.matheclipse.core.expression.Symbol;

import java.util.*;

import static Uckit.model.CASRecursiveSolver.evaluator;
import static Uckit.model.CASRecursiveSolver.uckit;

public class Variable implements VariableComputedObserver {
    //Numeric value of this
    @SerializedName("Value")
    private Double value;
    //Name / symbol of variable
    @SerializedName("Name")
    private String name;
    //Symbol of this variable CAS style
    @SerializedName("Symbol")
    private Symbol symbol;
    //Description of this variable
    @SerializedName("Description")
    private String description = "";
    //List of steps to get here
    @SerializedName("Steps")
    private final Set<Step> steps = new HashSet<>();

    private static transient final HashMap<String, Variable> variableFromName = new HashMap<>();
    public static transient final HashMap<Variable, Set<Equation>> derivedFrom = new HashMap<>();
    public static transient final HashMap<Variable,Set<Equation>> includedIn = new HashMap<>();

    public Variable(Variable var) {
        name = var.name;
        symbol = new Symbol(var.symbol.getSymbolName(), uckit);
        value = var.value;
        description = var.description;
        steps.addAll(var.steps);
        variableFromName.put(name,this);
        evaluator.eval(symbol);
    }

    public static Collection<Variable> getAllVariables(){
        return variableFromName.values();
    }

    public static Variable get(String name){
        return variableFromName.get(name);
    }

    public Variable(String name){
        name = name.toUpperCase();
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
        return Arrays.asList(steps.toArray(new Step[0]));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variable)) return false;
        Variable variable = (Variable) o;
        return Objects.equals(getValue(), variable.getValue()) &&
                Objects.equals(name, variable.name) &&
                Objects.equals(getSymbol(), variable.getSymbol()) &&
                Objects.equals(getDescription(), variable.getDescription()) &&
                Objects.equals(getSteps(), variable.getSteps());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue(), name, getSymbol(), getDescription(), getSteps());
    }

    public String getName() {
        return name;
    }
}