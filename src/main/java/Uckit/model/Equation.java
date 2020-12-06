package Uckit.model;

import com.google.gson.annotations.SerializedName;

import java.util.*;

import static Uckit.model.CASRecursiveSolver.evaluator;

public class Equation implements VariableComputedObserver{
    public static final transient Set<Equation> equations = new HashSet<Equation>();

    @SerializedName("EquationString")
    private final String equationString;
    @SerializedName("Solved")
    private boolean isSolved = false;
    @SerializedName("ReturnVariable")
    private Variable returnType;
    @SerializedName("MemberVariables")
    private Variable[] members;

    public Equation(Equation equation) {
        String s = equation.equationString;
        returnType = equation.returnType;
        Variable[] variables = new Variable[equation.members.length];
        for(int i = 0; i < equation.members.length; i++){
            variables[i] = Variable.get(equation.members[i].getName());
        }
        returnType = Variable.get(equation.returnType.getName());

        s = s.toUpperCase();
        evaluator.eval(s);

        equationString = s;
        members = variables;
        returnType.addDerivedFrom(this);
        if(returnType.hasValue()) isSolved = true;

        for(Variable v : members){
            v.addIncludedIn(this);
        }

        equations.add(this);
        notifyOthers(new VariableComputedEvent(returnType));

    }

    public static void set(Set<Equation> json) {
        equations.clear();
        equations.addAll(json);
    }

    public static Equation get(String name) {
        for(Equation eq : equations){
            if(eq.toString().equals(name)) return eq;
        }

        for(Equation eq : equations){
            if(eq.equationString.equals(name)) return eq;
        }
        return null;
    }

    public static void purge() {
        List<Equation> toDelete = new ArrayList<>(equations);
        toDelete.forEach(Equation::delete);
        equations.clear();
    }


    public boolean isSolved() {
        return isSolved;
    }

    public Equation(String s, Variable returnType, Variable... variables){
        s = s.toUpperCase();
        evaluator.eval(s);
        equationString = s;
        members = variables;
        this.returnType = returnType;
        returnType.addDerivedFrom(this);
        if(returnType.hasValue()) isSolved = true;

        for(Variable v : members){
            v.addIncludedIn(this);
            if(!v.hasValue()) {
                String derEq = evaluator.eval("solve(" + equationString + "," + v.getName() + ")").toScript().replace("->","==").replace("{","").replace("}","");
                Variable[] vars = new Variable[members.length];
                for (int i = 0; i < members.length; i++) {
                    if(members[i].equals(v)){
                        vars[i] = returnType;
                    }else {
                        vars[i] = members[i];
                    }
                }
                Equation anotherOne = new Equation(false, derEq, v, vars);
            }
        }
        equations.add(this);


        notifyOthers(new VariableComputedEvent(returnType));
    }

    private Equation(boolean deriveMore, String s, Variable returnType, Variable... variables){
        {
            s = s.toUpperCase();
            evaluator.eval(s);
            equationString = s;
            members = variables;
            for (Variable v : variables) {
                v.addIncludedIn(this);
            }
            this.returnType = returnType;
            returnType.addDerivedFrom(this);

            if (returnType.hasValue()) isSolved = true;

            equations.add(this);

            notifyOthers(new VariableComputedEvent(returnType));
        }
    }

    public void evaluate() {
        if(!this.isSolved()) {
            for (Variable var : members) {
                if (!var.hasValue()) throw new IllegalArgumentException("Variable " + var + " not known in equation " + this);
            }
            //double result = evaluator.eval("N(" + exprStr +")").evalDouble();
            //double result = evaluator.evalf("solve("+equationString+","+returnType.getName()+")");
            double result;
            try {
                //result = Double.parseDouble(evaluator.eval("Solve(" + equationString + "," + returnType.getName() + ")").toString().replace("{{" + returnType.getName() + "->", "").replace("}}", ""));
                result = evaluator.eval("N(" + equationString +")").evalDouble();
            } catch (Exception e) {
                result = evaluator.evalf(equationString.replace("==", "="));
            }
            List<Step> steps = new ArrayList<>();
            Arrays.asList(members).forEach(e -> steps.addAll((e.getSteps())));
            steps.add(new Step(this, returnType));
            returnType.evaluate(result, steps.toArray(new Step[0]));
            isSolved = true;
        }
    }

    /**
     * @param event
     * @author Sasha
     */
    @Override
    public void notify(VariableComputedEvent event) {
        if(Arrays.asList(members).contains(event.getVar())){
            try{
                evaluate();
            }catch(Exception ignored){

            }
        }
    }

    public String toString(){
        return "Equation [" + equationString+"]";
    }

    public void delete() {
        for(Variable v : members){
            v.removeEquation(this);
        }
        returnType.removeEquation(this);
        equations.remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equation)) return false;
        Equation equation = (Equation) o;
        return equationString.equals(equation.equationString) &&
                Objects.equals(returnType, equation.returnType) &&
                Arrays.equals(members, equation.members);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(equationString, returnType);
        result = 31 * result + Arrays.hashCode(members);
        return result;
    }
}
