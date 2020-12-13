package Uckit.application;

import Uckit.model.CASRecursiveSolver;
import Uckit.view.FXMLHost;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.scene.layout.HBox;

import java.util.*;

public class UCKIT extends Application {
    //CSS THEMES
    //public static final String darkThemeCSS = "https://raw.githubusercontent.com/joffrey-bion/javafx-themes/master/css/modena_dark.css";
    public static final String darkThemeCSS = "/dark.css";
    public static final String lightThemeCSS = "/light.css";
    public static final String blackThemeCSS = "/black.css";
    public static final String eclipseThemeCSS = "/eclipse.css";
    public static String currentCSS = "";
    public static final HashMap<String, String> themeNames = new HashMap<>();
    static{
        themeNames.put("Dark", darkThemeCSS);
        themeNames.put("Light",lightThemeCSS);
        themeNames.put("Black",blackThemeCSS);
        themeNames.put("Eclipse",eclipseThemeCSS);
    }

    public static Random rand = new Random(System.nanoTime());

    final double versionNumber = 0.3;
    private static final List<String> messages = new ArrayList<>();
    static {
        final String[] hardMessages = new String[]{"If we used SQL this would be better", "Also try Minecraft", "Hey at least the GUI is nicer than Eclipse", "Go Canucks go!", "Visit sashaphoto.ca"};
        messages.addAll(Arrays.asList(hardMessages));
        //TODO: query API for more messages

    }

    public static final CASRecursiveSolver solver = new CASRecursiveSolver();


    public static void main(String args[]){
        launch(args);
    }

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     * @throws Exception if something goes wrong
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXMLHost.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("UCKit " + versionNumber + " - " + getGreeting());
        Scene s = new Scene(root);
        FXMLHost cont = fxmlLoader.getController();
        primaryStage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                cont.resizeTopBar(newValue);
            }
        });
        primaryStage.setScene(s);
        primaryStage.setHeight(700);
        primaryStage.setWidth(1100);
        primaryStage.setResizable(true);
        primaryStage.setOnCloseRequest(e -> exitProgram());
        primaryStage.show();
    }

    private String getGreeting() {
        if(rand == null) rand = new Random(System.nanoTime());
        return messages.get(rand.nextInt(messages.size()));
    }

    private void exitProgram() {
        try {
            solver.save();
        }catch(Exception ignored){}
        System.exit(0);
    }
}
