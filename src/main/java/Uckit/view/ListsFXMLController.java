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
import java.util.ResourceBundle;

public class ListsFXMLController implements Initializable, UIChangedObserver {
    public JFXListView unsolvedVariables;
    public JFXListView solvedVariables;

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
        solvedVariables.getItems().clear();
        unsolvedVariables.getItems().clear();
        for(Variable v : CASRecursiveSolver.getKnowns()){
            FlowPane fp = new FlowPane();

            Label var = new Label(v.toString());

            fp.getChildren().add(var);
            solvedVariables.getItems().add(fp);

        }

        for(Variable v : CASRecursiveSolver.getUnsolved()){
            FlowPane fp = new FlowPane();

            Label var = new Label(v.getName());

            TextField enterValue = new TextField();
            Button compute = new Button("Set");
            compute.setOnAction(e ->{
                try{
                    double val = Double.parseDouble(enterValue.getText());
                    v.evaluate(val);
                    notifyOthers(new UIEvent(ChangeArea.CALCULATION).withExtraData(v));
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
        UCKIT.solver.startSolve();
    }
}
