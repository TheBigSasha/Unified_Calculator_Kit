package Uckit.view;

import Uckit.model.ObserverController;
import Uckit.model.VariableComputedEvent;

import java.util.HashSet;
import java.util.Set;

public class UIChangeManager {
    private static final Set<UIChangedObserver> observers = new HashSet<>();

    public static void subscribe(UIChangedObserver uiChangedObserver) {
        observers.add(uiChangedObserver);
    }

    public static void notifyAll(UIEvent event) {
        for (UIChangedObserver obs : observers) {
            obs.notify(event);
        }
        if(event.getArea().equals(ChangeArea.CALCULATION)){
            ObserverController.notifyAllBackend(new VariableComputedEvent(event.getVar()));
        }
    }
}
