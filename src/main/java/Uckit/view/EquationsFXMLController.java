package Uckit.view;

import Uckit.application.UCKIT;
import Uckit.model.CASRecursiveSolver;
import Uckit.model.Equation;
import Uckit.model.Variable;
import com.jfoenix.controls.JFXListView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import org.checkerframework.checker.guieffect.qual.UI;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Flow;

public class EquationsFXMLController implements Initializable,UIChangedObserver {
    @FXML
    public JFXListView equations;
    public CheckComboBox<String> inputVars;
    public ComboBox<String> returnVar;
    public TextField equationBox;

    /**
     * @param event
     * @author Sasha
     */
    @Override
    public void notify(UIEvent event) {
        refreshList();
        populateComboBoxes();
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
        addListeners();
        refreshList();
        populateComboBoxes();
    }

    private void refreshList() {
        equations.getItems().clear();
        for(Equation eqtn : CASRecursiveSolver.getEquations()){
            FlowPane fp = new FlowPane();
            Label l = new Label(eqtn.toString());
            Button remove = new Button("Delete");
            remove.setOnAction(e->{
                try {
                    CASRecursiveSolver.removeEquation(eqtn.toString());
                    notifyOthers(new UIEvent(ChangeArea.CALCULATION));
                    refreshList();
                }catch(Exception ex){
                    Toast.makeText(null,ex.getMessage(), 1000,300,300);
                }
            });
            fp.getChildren().addAll(remove,l);
            equations.getItems().add(fp);
        }
    }

    private void populateComboBoxes(){
        refreshReturnVars();
        refreshInputVars();
    }

    private void addListeners(){
        returnVar.setOnAction(e ->{
            refreshInputVars();
        });

        inputVars.setOnMouseClicked(e -> {
            refreshReturnVars();
        });

    }

    private void refreshReturnVars() {
        returnVar.getItems().clear();
        for(Variable v : CASRecursiveSolver.getKnowns()){
            returnVar.getItems().add(v.getName());
            returnVar.setValue(v.getName());
        }
        for(Variable v : CASRecursiveSolver.getUnsolved()){
            returnVar.getItems().add(v.getName());
            returnVar.setValue(v.getName());
        }
        this.inputVars.getItems().forEach(item -> {
            if (this.inputVars.getItemBooleanProperty(item).get()) {
                try {
                    returnVar.getItems().remove(item);
                }catch(Exception ex){

                }
            }
        });
    }

    private void refreshInputVars() {
        inputVars.getItems().clear();
        for(Variable v : CASRecursiveSolver.getKnowns()){
            inputVars.getItems().add(v.getName());
        }
        for(Variable v : CASRecursiveSolver.getUnsolved()){
            inputVars.getItems().add(v.getName());
        }
        inputVars.getItems().remove(returnVar.getValue());
    }


    public void solve(ActionEvent actionEvent) {
        UCKIT.solver.startSolve();
    }

    public void addEquation(ActionEvent actionEvent) {
        //TODO: clear variable values when adding a new equation
        try {
            List<String> inputVars = new ArrayList<>();
            this.inputVars.getItems().forEach(item -> {
                if (this.inputVars.getItemBooleanProperty(item).get()) {
                    inputVars.add(item);
                }
            });
            CASRecursiveSolver.addEquation(equationBox.getText(), returnVar.getValue(), inputVars.toArray(new String[0]));
            notifyOthers(new UIEvent(ChangeArea.CALCULATION));
            solve(actionEvent);
        }catch(Exception e){
            Toast.makeText(null,e.getMessage(),1000,300,300);
        }
    }
}
