package Uckit;
import javax.lang.model.element.VariableElement;
import javax.xml.crypto.dsig.SignatureMethod;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RecursiveSolver {
    static HashMap<String, Value> knowns = new HashMap<>();
    static List<Equation> equations = new ArrayList<>();

    public static void main(String args[]){
        Variable mass = new Variable("Mass");
        Variable acceleration = new Variable("Acceleration");
        Variable time = new Variable("Time");
        Variable deltaVelocity = new Variable("D_Velocity");
        Variable force = new Variable("Force");
        Variable area = new Variable("Area");
        Variable length = new Variable("Length");
        Variable width = new Variable("Width");
        Variable pressure = new Variable("Pressure");
        Variable volume = new Variable("Volume");
        Variable NRT = new Variable("NRT");
        Value curVelocity = new Value(deltaVelocity, 1);
        Value curVolume = new Value(volume, 115);
        Value curTime = new Value(time, 0.1);
        Value curMass = new Value(mass, 10);
        Value curWidth = new Value(width, 5);
        Value curLength = new Value(length, 7);
        Variable gasconstnat = new Variable("R");
        Value gasconst = new Value(gasconstnat, 8.31446261815324);
        Variable mols = new Variable("Moles");
        Value moles = new Value(mols,15);
        Variable temperature = new Variable("Temperature");
        SimpleRelation FT = new SimpleRelation(force, Operator.TIMES, temperature, new Variable("Force*Temperature"));
        SimpleRelation AreaOfSquare = new SimpleRelation(length,Operator.TIMES,width,area);
        SimpleRelation FMA = new SimpleRelation(acceleration,Operator.TIMES,mass,force);
        SimpleRelation PFA = new SimpleRelation(force, Operator.OVER, area, pressure);
        SimpleRelation AVT = new SimpleRelation(deltaVelocity,Operator.OVER,time,acceleration);
        List<Operator> operators = new ArrayList<>(Arrays.asList(Operator.TIMES, Operator.OVER, Operator.OVER));
        List<Variable> vars = new ArrayList<>(Arrays.asList(pressure,volume,gasconstnat,mols));
        SimpleMultiRelation PVNRT = new SimpleMultiRelation(vars,operators,temperature);
        System.out.println(knowns.values());
        progress();
        System.out.println(knowns.values());
        System.out.println("\n");
        System.out.println(knowns.get("Force*Temperature"));
    }

    public static void progress(){
        boolean didImprove = false;
        for(Equation e : equations){
            if(e.canEvaluate() && !knowns.containsKey(e.getVariable().getName())){
                knowns.put(e.getVariable().getName(), e.evaluate());
                //progress();
                didImprove = true;
            }
        }
        if(didImprove){
            //System.out.println(knowns.values());
            progress();
        }
    }

    abstract static class Equation<E> implements Evaluable<E> { //TODO: Instantiate via String to Math
        protected Set<Variable> required;
        private Variable returnType;

        public Equation(Set<Variable> required, Variable returnType){
            this.required = required;
            this.returnType = returnType;
            for(Variable v : required){
                v.addIncludedIn(this);
            }
            returnType.addReturnedBy(this);
            equations.add(this);
        }

        protected boolean canEvaluate(Collection<Value<Double>> values) {
            List<Value> test = new ArrayList<>(values);
            for(Variable v : required){
                boolean passed = false;
                for(Value val : test){
                    if(val.getVariable().equals(v)) passed = true;
                }
                if(!passed){
                    //throw new Exception("Variable not present for evaluation : " + v.getName());
                    return false;
                }
            }
            return true;
        }

        protected boolean canEvaluate() {
            for(Variable v : required){
                boolean passed = false;
                for(Value val : knowns.values()){
                    if(val.getVariable().equals(v)) passed = true;
                }
                if(!passed){
                    //throw new Exception("Variable not present for evaluation : " + v.getName());
                    return false;
                }
            }
            return true;
        }


        @Override
        public Variable getVariable() {
            return returnType;
        }
    }
    enum Operator{
        TIMES, OVER
    }

    static class SimpleMultiRelation extends Equation<Double>{
        private final List<Operator> ops;
        public SimpleMultiRelation(List<Variable> parts, List<Operator> ops, Variable output) {
            super(new HashSet<Variable>(parts), output);
            this.ops = ops;
        }

        @Override
        public Value<Double> evaluate() {
            ConcurrentLinkedDeque<Operator> operators = new ConcurrentLinkedDeque<>(ops);
            ConcurrentLinkedDeque<Value<Double>> values = new ConcurrentLinkedDeque<>();
            StringBuilder work = new StringBuilder();
            for(Variable v : required) {
                values.add(knowns.get(v.getName()));
            }
            Double result = 1d;
            int counter = 0;
            while(!values.isEmpty()) {
                Value<Double> val = values.pollFirst();
                work.append(val);
                if (counter <= operators.size() + 1) {
                    try {
                        Operator o = operators.pollFirst();
                        work.append(o);
                        switch (Objects.requireNonNull(o)) {
                            default -> {
                            }
                            case TIMES -> {
                                result *= (Double) val.getValue();
                            }
                            case OVER -> {
                                result /= (Double) val.getValue();
                            }
                        }
                    } catch (ClassCastException | NullPointerException x) {

                    }
                    counter++;
                }
            }
            return new Value<>(getVariable(), result, work.toString());
            }
    }

    static class SimpleRelation extends Equation<Double>{
        private final Operator op;
        public SimpleRelation(Variable a, Operator op, Variable b, Variable output) {
            super(new HashSet<Variable>(Arrays.asList(a, b)), output);
            this.op = op;
        }

        @Override
        public Value<Double> evaluate() {
                Double result = 1d;
                StringBuilder work = new StringBuilder();
                for(Variable v : required){
                    try {
                        switch (op) {
                            case TIMES -> {
                                result *= (Double) knowns.get(v.getName()).getValue();
                                work.append(knowns.get(v.getName()));
                            }
                            case OVER -> {
                                result /= (Double) knowns.get(v.getName()).getValue();
                                work.append(knowns.get(v.getName()));
                            }
                            default -> result = 0.;
                        }
                    }catch(ClassCastException ex){

                    }
                }
                return new Value<>(getVariable(),result, work.toString());
            }
        }

    static class Value<E> implements Evaluable<E> {
        String work = "";
        @Override
        public String toString() {
            return "[" + var.getName() + " " + value.toString() + " : " + work + "]";
        }

        public Variable getVariable() {
            return var;
        }

        @Override
        public Value<E> evaluate() {
            return this;
        }

        private Variable var;

        public E getValue() {
            return value;
        }

        public void setValue(E value) {
            this.value = value;
        }

        private E value;

        public Value(Variable var, E value) {
            this.var = var;
            this.value = value;
            work = "Given";
            knowns.put(var.getName(),this);
        }

        public Value(Variable var, E value, String work) {
            this.var = var;
            this.value = value;
            this.work = work;
            knowns.put(var.getName(),this);
        }

    }

    static class Variable{
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Set<Equation> getIncludedIn() {
            return includedIn;
        }

        public void addIncludedIn(Equation includedIn) {
            this.includedIn.add(includedIn);
        }

        public Set<Equation> getReturnedBy() {
            return returnedBy;
        }

        public void addReturnedBy(Equation returnedBy) {
            this.returnedBy.add(returnedBy);
        }

        public Variable(String name) {
            this.name = name;
            this.includedIn = new HashSet<>();
            this.returnedBy = new HashSet<>();
        }

        private String name;
        private Set<Equation> includedIn;
        private Set<Equation> returnedBy;

        @Override
        public String toString() {
            return "Variable{" +
                    "name='" + name + '\'' +
                    ", includedIn=" + includedIn +
                    ", returnedBy=" + returnedBy +
                    '}';
        }
    }

    interface Evaluable<E>{
        public Variable getVariable();
        public Value<E> evaluate();
    }
}


