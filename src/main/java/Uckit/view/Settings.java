package Uckit.view;


import Uckit.application.UCKIT;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
/**
 * @author Sasha
 */
public class Settings implements Initializable, UIChangedObserver {
    @FXML
    public ComboBox<String> GradientChooser;
    public ComboBox<String> ThemeChooser;


    @FXML
    private RadioButton ShowHelpToggle;

    private static boolean showHelp = true;

    private static final List<String> gradients = Arrays.asList("sunset","space", "forest");
    /**
     * @author Sasha
     */
    public static List<String> getPossibleGradients() {
        return gradients;
    }
    /**
     * @author Sasha
     */
    @Override
    public void notify(UIEvent event) {

    }

    /**
     * @author team
     */
    public static boolean showingHelp(){
        return showHelp;
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     * @author Sasha
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ThemeChooser.getItems().addAll(UCKIT.themeNames.keySet());
        ThemeChooser.setValue("Dark");//TODO: remove hard code!
        GradientChooser.getItems().addAll(gradients);
        GradientChooser.setValue(gradients.get(0));
        addListeners();
    }
    /**
     * @author Sasha
     */
    private void addListeners(){
        ThemeChooser.setOnAction(event -> {
            if(ThemeChooser.getValue().equals("Eclipse")){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "In honour of the brave students and staff who have been using Eclipse to code in Java, the Eclipse theme is EXTREMELY accurate to the Eclipse IDE. It completely ruins the user interface, the app may become unusable in this theme. Proceed with caution.", ButtonType.YES, ButtonType.CANCEL);
                alert.setHeaderText("Eclipse Theme");
                DialogPane dp2 = alert.getDialogPane();
                dp2.getStylesheets().addAll(UCKIT.currentCSS, "/styles.css");
                dp2.getStyleClass().addAll("subhead","body");
                ButtonType b = new ButtonType("Install IntelliJ IDEA");

                dp2.getButtonTypes().add(b);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    notifyOthers(new UIEvent(ChangeArea.THEME).withExtraData(ThemeChooser.getValue()));

                }else if(alert.getResult().equals(b)){
                    {
                        try{
                            Desktop.getDesktop().browse(URI.create("https://www.jetbrains.com/idea/"));
                        }catch(Exception ignored) {
                            try {
                                new ProcessBuilder("x-www-browser", "https://www.jetbrains.com/idea/").start();
                            } catch (IOException ignored2) {
                            }
                        }
                    }
                }else{
                    ThemeChooser.setValue("Dark");
                }
            }else {
                notifyOthers(new UIEvent(ChangeArea.THEME).withExtraData(ThemeChooser.getValue()));
            }
            });
        ShowHelpToggle.setOnAction(event -> {
            showHelp = ShowHelpToggle.isSelected();
        });
        GradientChooser.setOnAction(event -> {
            notifyOthers(new UIEvent(ChangeArea.THEME).withExtraData(GradientChooser.getValue()));
        });
    }
}
