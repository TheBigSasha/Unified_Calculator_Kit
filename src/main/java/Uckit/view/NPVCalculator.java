package Uckit.view;

import com.jfoenix.controls.JFXListView;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.Flow;

public class NPVCalculator implements Initializable,  UIChangedObserver {
    public TextField TF_INITIAL_INVESTMENT;
    public TextField TF_DISCOUNT_RATE;
    public JFXListView LV_CASH_FLOWS;
    public TextField TF_CASH_FLOW;
    public Button BTN_ADD_CF;
    public TextField TF_RESULT;

    private final ArrayList<Double> cashFlows = new ArrayList<>();
    public BorderPane hostPane;
    private TextField resultField;

    /**
     * @param event
     * @author Sasha
     */
    @Override
    public void notify(UIEvent event) {

    }

    private void evaluate(){
        try{
            TF_RESULT.setText(evaluate(TF_INITIAL_INVESTMENT.getText(),TF_DISCOUNT_RATE.getText(),cashFlows).toString());
            if(resultField != null){
                resultField.setText(evaluate(TF_INITIAL_INVESTMENT.getText(),TF_DISCOUNT_RATE.getText(),cashFlows).toString());
            }
        }catch(Exception ex){
            Toast.makeText(null,ex.getMessage(),1000,300,300);
        }
    }

    private static Double evaluate(String initialInvestment, String discountRate, Collection<Double> cashflows) {
        double r = Double.parseDouble(discountRate);
        double initInvest = Double.parseDouble(initialInvestment);
        ArrayList<Double> cfs = new ArrayList<>();
        cfs.add(-1.0 * initInvest);
        cfs.addAll(cashflows);
        double result = 0.;
        for(int i = 0; i < cfs.size(); i++){
            double Ct = cfs.get(i);
            double onePlusRtoTheT = Math.pow((1.0 + r),(double) i);
            result += (Ct / onePlusRtoTheT);
        }
        return result;
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
        BTN_ADD_CF.setOnAction(e -> {
            try {
                double cashFlow = Double.parseDouble(TF_CASH_FLOW.getText());
                Label l = new Label(String.valueOf(cashFlow));
                FlowPane fp = new FlowPane(l);
                Button b = new Button("X");
                fp.getChildren().add(b);
                cashFlows.add(cashFlow);

                b.setOnAction(ev ->{
                    LV_CASH_FLOWS.getItems().remove(fp);
                    cashFlows.remove(cashFlow);
                    evaluate();
                });
                LV_CASH_FLOWS.getItems().add(fp);
                evaluate();

            }catch(Exception ex){
                Toast.makeText(null,ex.getMessage(),1000,300,300);
            }
        });
    }

    public void setResultField(TextField enterValue) {

    }
}
