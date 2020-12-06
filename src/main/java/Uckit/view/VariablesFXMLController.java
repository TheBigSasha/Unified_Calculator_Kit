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
import org.checkerframework.checker.guieffect.qual.UI;

import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class VariablesFXMLController implements Initializable, UIChangedObserver {
    public JFXListView unsolvedVariables;
    public JFXListView solvedVariables;
    public TextField newVarName;
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
            Label var = new Label(v.getName());
            TextField ans = new TextField(v.getValue().toString());
            ans.setEditable(false);

            fp.getChildren().addAll(ans, var);
            solvedVariables.getItems().add(fp);

        }

        for(Variable v : CASRecursiveSolver.getUnsolved()){
            FlowPane fp = new FlowPane();

            Label var = new Label(v.getName());

            TextField enterValue = new TextField();
            tfs.put(v,enterValue);
            Button compute = new Button("Set");
            Button delete = new Button("Delete");   //TODO: Deleting a variable
            delete.setOnAction(e->{
                try {
                    v.delete();
                    notifyOthers(new UIEvent(ChangeArea.CALCULATION));
                    refreshLists();
                }catch(Exception ex){
                    Toast.makeText(null,ex.getMessage(),1000,300,300);

                }
            });
            compute.setOnAction(e ->{                   //TODO: This is not added because it is obsolete
                try{
                    double val = Double.parseDouble(enterValue.getText());
                    v.evaluate(val);
                    notifyOthers(new UIEvent(ChangeArea.CALCULATION).withExtraData(v));
                    UCKIT.solver.startSolve();
                }catch(Exception ex){
                    Toast.makeText(null,ex.getMessage(),1000,300,300);
                }
            });

            fp.getChildren().addAll(enterValue,var);
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
        //This is a ghetto solution where we just save and re-read the JSON
        UCKIT.solver.save();
        UCKIT.solver.initialize();
    }

    public void addVariable(ActionEvent actionEvent) {
        try {
            if (!newVarName.getText().isBlank()) {
                try{
                    Double.parseDouble(newVarName.getText());
                    Toast.makeText(null,"Cannot create variable with numeric name",1000,300,300);
                }catch(Exception ex){
                    CASRecursiveSolver.addVariable(newVarName.getText());

                }
                notifyOthers(new UIEvent(ChangeArea.CALCULATION));
            }
        }catch(Exception e){
            Toast.makeText(null,e.getMessage(),1000,300,300);
        }
    }
}
