package Uckit.view;

import Uckit.application.UCKIT;
import Uckit.model.CASRecursiveSolver;
import Uckit.model.Variable;
import com.jfoenix.controls.JFXListView;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import org.checkerframework.checker.guieffect.qual.UI;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.*;

public class VariablesController implements Initializable, UIChangedObserver {
    public JFXListView unsolvedVariables;
    public JFXListView solvedVariables;
    public TextField newVarName;
    public BorderPane hostPane;
    public TextField description;
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

        //HARD CODED VARIABLES
        ArrayList<String> hardCodes = new ArrayList<String>(Arrays.asList("NPV"));
      /*  for(String s : hardCodes){
            if(Variable.get(s) == null){
                new Variable(s);
            }
            Variable var = Variable.get(s);
            if(!var.hasValue()){

            }else{

            }
        }*/


        //END HARD CODED


        for(Variable v : CASRecursiveSolver.getKnowns()){
            FlowPane fp = new FlowPane();
            Label var = new Label(v.getName());
            TextField ans = new TextField(v.getValue().toString());
            ans.setEditable(false);

            fp.getChildren().addAll(ans, var);
            solvedVariables.getItems().add(fp);

        }

        for(Variable v : CASRecursiveSolver.getUnsolved()) {
            if (hardCodes.contains(v.getName())) {
                if (v.getName().equals("NPV")) {
                    FlowPane fp = new FlowPane();

                    Label var = new Label(v.getName());
                    if(v.getDescription() != null) var.setTooltip(new Tooltip(v.getDescription()));

                    TextField enterValue = new TextField();
                    tfs.put(v, enterValue);
                    Button compute = new Button("Calculate");
                    Button delete = new Button("Delete");   //TODO: Deleting a variable
                    delete.setOnAction(e -> {
                        try {
                            v.delete();
                            notifyOthers(new UIEvent(ChangeArea.CALCULATION));
                            refreshLists();
                        } catch (Exception ex) {
                            Toast.makeText(null, ex.getMessage(), 1000, 300, 300);

                        }
                    });
                    compute.setOnAction(e -> {                   //TODO: This is not added because it is obsolete
                        try {
                            PopOver po = new PopOver();
                            try {
                                FXMLLoader loader = new FXMLLoader(NPVCalculator.class.getResource("/NPVCalculator.fxml"));
                                Node n = loader.load();
                                NPVCalculator ea = loader.getController();
                                ea.setResultField(enterValue);
                                po.setContentNode(n);
                                po.setCloseButtonEnabled(true);
                                po.setTitle("Calculate NPV");
                                po.setArrowSize(0);

                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            po.show(hostPane.getScene().getWindow());
                            ((Parent) po.getSkin().getNode()).getStylesheets().clear();
                            ((Parent) po.getSkin().getNode()).getStylesheets().addAll(UCKIT.currentCSS, "styles.css");

                        } catch (Exception ex) {
                            Toast.makeText(null, ex.getCause().toString() + " " + ex.getMessage(), 1000, 300, 300);
                        }
                    });

                    fp.getChildren().addAll(enterValue,compute, var);
                    unsolvedVariables.getItems().add(fp);
                }
            } else {
                FlowPane fp = new FlowPane();

                Label var = new Label(v.getName());
                if(v.getDescription() != null) var.setTooltip(new Tooltip(v.getDescription()));

                TextField enterValue = new TextField();
                tfs.put(v, enterValue);
                Button compute = new Button("Set");
                Button delete = new Button("Delete");   //TODO: Deleting a variable
                delete.setOnAction(e -> {
                    try {
                        v.delete();
                        notifyOthers(new UIEvent(ChangeArea.CALCULATION));
                        refreshLists();
                    } catch (Exception ex) {
                        Toast.makeText(null, ex.getMessage(), 1000, 300, 300);

                    }
                });
                compute.setOnAction(e -> {                   //TODO: This is not added because it is obsolete
                    try {
                        double val = Double.parseDouble(enterValue.getText());
                        v.evaluate(val);
                        notifyOthers(new UIEvent(ChangeArea.CALCULATION).withExtraData(v));
                        UCKIT.solver.startSolve();
                    } catch (Exception ex) {
                        Toast.makeText(null, ex.getMessage(), 1000, 300, 300);
                    }
                });

                fp.getChildren().addAll(enterValue, var);
                unsolvedVariables.getItems().add(fp);

            }
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
        CASRecursiveSolver.save();
        CASRecursiveSolver.initialize();
    }

    public void addVariable(ActionEvent actionEvent) {
        try {
            if (!newVarName.getText().isBlank()) {
                try{
                    Double.parseDouble(newVarName.getText());
                    Toast.makeText(null,"Cannot create variable with numeric name",1000,300,300);
                }catch(Exception ex){
                    CASRecursiveSolver.addVariable(newVarName.getText());
                    if(!description.getText().isEmpty()) Variable.get(newVarName.getText()).setDescription(description.getText());

                }
                notifyOthers(new UIEvent(ChangeArea.CALCULATION));
            }
        }catch(Exception e){
            Toast.makeText(null,e.getMessage(),1000,300,300);
        }
    }
}
