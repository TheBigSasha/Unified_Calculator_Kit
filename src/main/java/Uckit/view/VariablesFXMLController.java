package Uckit.view;

import Uckit.application.UCKIT;
import Uckit.model.CASRecursiveSolver;
import Uckit.model.Variable;
import com.jfoenix.controls.JFXListView;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class VariablesFXMLController implements Initializable, UIChangedObserver {
    public JFXListView unsolvedVariables;
    public JFXListView solvedVariables;
    private HashMap<Variable, TextField> tfs = new HashMap<>();

    /**
     * @param event
     * @author Sasha
     */
    @Override
    public void notify(UIEvent event) {
        if(event.getArea().equals(ChangeArea.CALCULATION)){
            refreshLists();
        }
    }

    private void refreshLists() {
        tfs.clear();
        solvedVariables.getItems().clear();
        unsolvedVariables.getItems().clear();
        for(Variable v : CASRecursiveSolver.getKnowns()){
            FlowPane fp = new FlowPane();

            Label var = new Label(v.toStringNoWork());

            fp.getChildren().add(var);
            solvedVariables.getItems().add(fp);

        }

        for(Variable v : CASRecursiveSolver.getUnsolved()){
            FlowPane fp = new FlowPane();

            Label var = new Label(v.getName());

            TextField enterValue = new TextField();
            tfs.put(v,enterValue);
            Button compute = new Button("Set");
            compute.setOnAction(e ->{
                try{
                    double val = Double.parseDouble(enterValue.getText());
                    v.evaluate(val);
                    notifyOthers(new UIEvent(ChangeArea.CALCULATION).withExtraData(v));
                    UCKIT.solver.startSolve();
                }catch(Exception ex){
                    Toast.makeText(null,ex.getMessage(),1000,300,300);
                }
            });

            fp.getChildren().add(var);
            fp.getChildren().add(enterValue);
            fp.getChildren().add(compute);
            unsolvedVariables.getItems().add(fp);

        }
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        subscribe();
        refreshLists();
    }

    public void solve(ActionEvent ex){
        HashMap<Variable, Double> toSet = new HashMap<>();
        for(Variable v : tfs.keySet()){
            if(!tfs.get(v).getText().isBlank()){
                try{
                    double val = Double.parseDouble(tfs.get(v).getText());
                    toSet.put(v,val);
                }catch(Exception d){
                    Toast.makeText(null,d.getMessage(),1000,300,300);
                }
            }
        }
         ((Runnable) () -> {
             for (Variable v : toSet.keySet()) {
                 v.evaluate(toSet.get(v));
             }
         }).run();

        UCKIT.solver.startSolve();
    }

    public void reset(ActionEvent actionEvent) {
        //TODO: Reset the values of every variable to null
    }
}
