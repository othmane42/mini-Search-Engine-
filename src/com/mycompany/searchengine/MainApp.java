package com.mycompany.searchengine;


import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.Fxml;


public class MainApp extends Application {

    private GeneralController controller;
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Fxml.MAIN.get_path());
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle("Search Engine");
        controller= GeneralController.getInstance();
        controller.init();
        stage.setScene(scene);
        Switcher.newInstance().setCurrentStage(stage);
        stage.show();
        stage.setOnCloseRequest((v)->{
            controller.stop();
            controller.stopWatchers();
       //     controller.saveCheckpoint();
            
        });
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
