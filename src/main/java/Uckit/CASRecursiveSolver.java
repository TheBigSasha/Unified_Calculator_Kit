package Uckit;

import com.google.errorprone.annotations.Var;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.Context;

public class CASRecursiveSolver implements VariableComputedObserver{
    public static final ExprEvaluator evaluator = new ExprEvaluator();
    public static final Context uckit = new Context("uckit");

    public static void main(String args[]){
        Variable force = new Variable("F");
        Variable mass = new Variable("M");
        Variable acceleration = new Variable("A");
        Equation forceEqualsMassTimesAcceleration = new Equation("F=M*A",force,mass,acceleration);
        mass.evaluate(44);
        acceleration.evaluate(2);
        startSolve();
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
}