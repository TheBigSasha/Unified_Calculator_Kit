package Uckit.view;

import java.util.HashSet;
import java.util.Set;

public interface UIChangedObserver {
    /**
     * @author Sasha
     */
    void notify(UIEvent event);
    //TODO: with no destructors, we may have a memory leak upon scene transitions, where controllers remain alive after their transition happens because they are referenced from the list of observables. Switch to interrupt based?
    default void notifyOthers(UIEvent event){
        UIChangeManager.notifyAll(event);
    }

    /**
     * @author Sasha
     */
    default void subscribe(){
        UIChangeManager.subscribe(this);
    }

}

