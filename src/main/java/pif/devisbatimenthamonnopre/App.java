package pif.devisbatimenthamonnopre;
import javafx.application.Application;
import javafx.stage.Stage;
import Controleur.CBatiment;


/**
 * JavaFX App
 */


public class App extends Application {

    @Override
    public void start(Stage primaryStage) {

        // On démarre le contrôleur (qui crée toute l'interface)
        new CBatiment(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}