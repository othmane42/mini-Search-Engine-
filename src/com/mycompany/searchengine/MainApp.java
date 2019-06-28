package com.mycompany.searchengine;


import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import utils.Fxml;
import utils.WekaUtils;


public class MainApp extends Application {

    static final String TEST_PATH="D:\\documents\\tp\\tp rechercheInformation\\tp2\\tp2\\corpus";
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Fxml.MAIN.get_path());
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle("Search Engine");
        WekaUtils.getInstance().initDataSet(TEST_PATH);
        stage.setScene(scene);
        Switcher.newInstance().setCurrentStage(stage);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
