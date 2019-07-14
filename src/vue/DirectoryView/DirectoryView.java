/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vue.DirectoryView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import utils.DirectoryViewObserver;
import utils.Fxml;
import vue.ResultView.ResultView;

/**
 * FXML Controller class
 *
 * @author otmane42
 */
public class DirectoryView extends StackPane implements Initializable,DirectoryViewObserver {

    /**
     * Initializes the controller class.
     */
    
    @FXML 
    private Label labelPath;
    
    @FXML 
    private Button deleteBtn;
    
    public DirectoryView() {
        try {
            FXMLLoader loader = new FXMLLoader(Fxml.DIRECTORY_VIEW.get_path());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(DirectoryView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        labelPath.setOnMouseClicked((e)->{
                      try {
                Runtime.getRuntime().exec("explorer.exe /select," +labelPath.getText());
            } catch (IOException ex) {
                Logger.getLogger(ResultView.class.getName()).log(Level.SEVERE, null, ex);
            }
    
        });
       VBox.setMargin(this,new Insets(15,0,0,0));
    } 

    public Button getDeleteBtn() {
        return deleteBtn;
    }
   
    
    
    @Override
    public void updatePath(String path) {
        labelPath.setText(path);
        }

    
}
