package Uckit.model;

import com.google.errorprone.annotations.Var;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.Context;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CASRecursiveSolver implements VariableComputedObserver{
    public static final ExprEvaluator evaluator = new ExprEvaluator();
    public static final Context uckit = new Context("uckit");
    private static final String EQUATION_PATH = "equations.json";
    private static final Type VARIABLE_TYPE = new TypeToken<List<Variable>>() {}.getType();
    private static final String VARIABLE_PATH = "variables.json";
    private static final Type EQUATION_TYPE = new TypeToken<Set<Equation>>() {}.getType();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String args[]){
        evaluator.clearVariables();
        Variable force = new Variable("F");
        Variable mass = new Variable("M");
        Variable acceleration = new Variable("A");
        new Equation("F==M*A",force,mass,acceleration);
        mass.evaluate(44);
        acceleration.evaluate(2);
        Variable area = new Variable("Area");
        area.evaluate(10);
        Variable pressure = new Variable("Pressure");
        new Equation("Pressure == F * Area", pressure, force,area);
        startSolve();
        System.out.println(equationsToString());
        System.out.println(knownsToString());
    }

    private static String knownsToString() {
        StringBuilder sb = new StringBuilder();
        for(Variable var : Variable.getAllVariables()){
            if(var.hasValue()){
                sb.append(var.toString()).append("\n");
            }
        }
        return sb.toString();
    }

    private static String equationsToString(){
        StringBuilder sb = new StringBuilder();
        for(Equation var : Equation.equations){
            sb.append(var.toString()).append("\n");
        }
        return sb.toString();
    }

    public static void startSolve(){
        boolean didProgress = false;
        for(Equation eq : Equation.equations){
            try{
                if(!eq.isSolved()) {
                    eq.evaluate();
                    didProgress = true;
                }
            }catch(Exception ignored){
            }
        }
        if(didProgress) {
            startSolve();
        }
    }

    public static void setEquationsFromJSON(){
        try{
            Equation.equations.addAll(readEquationsFromFile());
            readEquationsFromFile().forEach(Equation::new);
        } catch (FileNotFoundException | NullPointerException fileNotFoundException) {
            try {
                gson.toJson(Equation.equations, new FileWriter(EQUATION_PATH));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch(JsonSyntaxException ex){
            ex.printStackTrace();
        }
    }

    public static void setVariablesFromJSON(){
        try{
            List<Variable> vars = readVariablesFromFile();
            vars.forEach(var -> {
                Variable v = new Variable(var);
            });

        } catch (FileNotFoundException | NullPointerException fileNotFoundException) {
            try {
                FileWriter varWriter = new FileWriter(VARIABLE_PATH);
                gson.toJson(Variable.getAllVariables(), varWriter);
                varWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch(JsonSyntaxException ex){
            ex.printStackTrace();
        }
    }

    public static void save(){
        try {
            gson.toJson(Equation.equations, new FileWriter(EQUATION_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileWriter eqtWriter = new FileWriter(EQUATION_PATH);
            gson.toJson(Variable.getAllVariables(), eqtWriter);
            eqtWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static Set<Equation> readEquationsFromFile() throws FileNotFoundException, JsonSyntaxException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(EQUATION_PATH));

        return gson.fromJson(bufferedReader, EQUATION_TYPE);

    }

    public static List<Variable> readVariablesFromFile() throws FileNotFoundException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(VARIABLE_PATH));


        return gson.fromJson(bufferedReader, VARIABLE_TYPE);
    }

    /**
     * @param event
     * @author Sasha
     */
    @Override
    public void notify(VariableComputedEvent event) {
        for(Equation equation : Variable.includedIn.get(event.getVar())){
            try{
                equation.evaluate();
            }catch(Exception ignored){

            }
        }
    }

    public static Set<Variable> getUnsolved(){
        Set<Variable> out = new HashSet<>();
        for(Variable v : Variable.getAllVariables()){
            if(!v.hasValue()){
                out.add(v);
            }
        }
        return out;
    }

    public static Set<Variable> getKnowns(){
        Set<Variable> out = new HashSet<>();
        for(Variable v : Variable.getAllVariables()){
            if(v.hasValue()){
                out.add(v);
            }
        }
        return out;
    }

    public static Set<Equation> getEquations(){
        return Equation.equations;
    }

    public static void addEquation(String equation, String outputVariableName, String... inputVariableNames){
        Variable out = Variable.get(outputVariableName);
        if(out == null) out = new Variable(outputVariableName);

        Variable[] inputVars = new Variable[inputVariableNames.length];
        for (int i = 0, inputVariableNamesLength = inputVariableNames.length; i < inputVariableNamesLength; i++) {
            if(Variable.has(inputVariableNames[i])) {
                inputVars[i] = Variable.get(inputVariableNames[i]);
            }else{
                Variable v = new Variable(inputVariableNames[i]);
                inputVars[i] = v;
            }
        }
        new Equation(equation,out,inputVars);
    }

    public static void setVariable(String name, Double value){
        if(Variable.has(name)){
            Variable v = Variable.get(name);
            v.evaluate(value);
        }else{
            Variable v = new Variable(name).withValue(value);
        }
    }

    public static void removeVariable(String name){
        Variable.get(name).delete();
    }

    public static void removeEquation(String name){
        if (Equation.get(name) != null) {
            for(Set<Equation> eqs : Variable.includedIn.values()){
                eqs.remove(Equation.get(name));
            }
            for(Set<Equation> eqs : Variable.derivedFrom.values()){
                eqs.remove(Equation.get(name));
            }
            Equation.get(name).delete();
        }
    }

}