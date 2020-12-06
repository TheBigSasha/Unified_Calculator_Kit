package Uckit;

import java.util.*;

import static Uckit.CASRecursiveSolver.evaluator;

public class Equation implements VariableComputedObserver{
    public static final Set<Equation> equations = new HashSet<Equation>();

    private final String equationString;
    private boolean isSolved = false;
    private Variable returnType;
    private Variable[] members;


    public boolean isSolved() {
        return isSolved;
    }

    public Equation(String s, Variable returnType, Variable... variables){
        evaluator.eval(s);
        equations.add(this);
        equationString = s;
        members = variables;
        for(Variable v : variables){
            v.addIncludedIn(this);
        }
        this.returnType = returnType;
        returnType.addDerivedFrom(this);

        if(returnType.hasValue()) isSolved = true;

        notifyOthers(new VariableComputedEvent(returnType));
    }

    public void evaluate() {
        String exprStr = equationString;
        for(Variable var : members){
            exprStr = exprStr.replace(var.getSymbol().getSymbolName(),var.getValue().toString());
        }
        //double result = evaluator.eval("N(" + exprStr +")").evalDouble();
        double result = evaluator.evalf(equationString);
        List<Step> steps = new ArrayList<>();
        Arrays.asList(members).forEach(e->steps.addAll((e.getSteps())));
        steps.add(new Step(this, returnType));
        returnType.evaluate(result, steps.toArray(new Step[0]));
        isSolved = true;
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
        return "Equation " + equationString;
    }
}
