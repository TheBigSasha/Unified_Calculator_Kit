package Uckit.model;

import Uckit.view.ChangeArea;
import Uckit.view.UIChangeManager;
import Uckit.view.UIChangedObserver;
import Uckit.view.UIEvent;

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

