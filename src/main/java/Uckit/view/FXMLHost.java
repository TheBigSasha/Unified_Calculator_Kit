package Uckit.view;
import Uckit.application.UCKIT;

import Uckit.model.VariableComputedEvent;
import Uckit.model.VariableComputedObserver;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

import static Uckit.application.UCKIT.darkThemeCSS;

public class FXMLHost implements Initializable, UIChangedObserver {
    @FXML
    public BorderPane topBar;
    @FXML
    private  BorderPane mainBorderView;


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
        setAppTheme(darkThemeCSS);
        mainBorderView.getStylesheets().add("Styles.css");
        subscribe();
    }

    /**
     * Updates according to observers
     * @author Sasha
     */
    @Override
    public void notify(UIEvent event) {
        if(event.getArea().equals(ChangeArea.THEME)){
            if(Settings.getPossibleGradients().contains(event.getExtraData())){ //Very ghetto, if its a possible gradient, its a gradient, else its a light dark switch
                setTopBarGrad(event.getExtraData());
            }else{
                if(UCKIT.themeNames.containsKey(event.getExtraData())){
                    setAppTheme(UCKIT.themeNames.get(event.getExtraData()));
                }
            }
            return;
        }
        updateGUI();
    }

    /**
     * Sets the theme of the app
     * @author Sasha
     */
    private void setAppTheme(String css) {
        mainBorderView.getStylesheets().removeAll(UCKIT.themeNames.values());
        UCKIT.currentCSS = css;
        mainBorderView.getStylesheets().add(css);

    }

    /**
     * Sets the gradient of the top bar
     * @author Sasha
     */
    private void setTopBarGrad(String grad){
        topBar.getStyleClass().removeAll(Settings.getPossibleGradients());
        topBar.getStyleClass().add(grad);
    }

    private void updateGUI(){

    }

    public void resizeTopBar(Number newValue) {
    }
}
