package Uckit.model;

import java.util.HashSet;
import java.util.Set;

public interface VariableComputedObserver {
    /**
     * @author Sasha
     */
    void notify(VariableComputedEvent event);
    //TODO: with no destructors, we may have a memory leak upon scene transitions, where controllers remain alive after their transition happens because they are referenced from the list of observables. Switch to interrupt based?
    default void notifyOthers(VariableComputedEvent event){
        ObserverController.notifyAll(event);
    }

    /**
     * @author Sasha
     */
    default void subscribe(){
        ObserverController.subscribe(this);
    }
}

class ObserverController{
    private static final Set<VariableComputedObserver> observers = new HashSet<>();

    public static void notifyAll(VariableComputedEvent event) {
        for(VariableComputedObserver observer : observers){
            observer.notify(event);
        }
    }

    public static void subscribe(VariableComputedObserver variableComputedObserver) {
        observers.add(variableComputedObserver);
    }
}
