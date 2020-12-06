package Uckit.model;

import Uckit.view.ChangeArea;
import Uckit.view.UIChangeManager;
import Uckit.view.UIEvent;

import java.util.HashSet;
import java.util.Set;

public class ObserverController {
    private static final Set<VariableComputedObserver> observers = new HashSet<>();

    public static void notifyAll(VariableComputedEvent event) {
        notifyAllBackend(event);
        UIChangeManager.notifyAll(new UIEvent(ChangeArea.CALCULATION));
    }

    public static void notifyAllBackend(VariableComputedEvent event){
        for(VariableComputedObserver obs : observers){
            obs.notify(event);
        }

    }

    public static void subscribe(VariableComputedObserver variableComputedObserver) {
        observers.add(variableComputedObserver);
    }
}
