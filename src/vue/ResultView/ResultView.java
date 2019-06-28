/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vue.ResultView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import utils.Observer;

/**
 * FXML Controller class
 *
 * @author otmane42
 */
public class ResultView extends StackPane implements Initializable,Observer {

    @FXML
    private StackPane result;
    
    @FXML
    private Label nameFile;
    
    @FXML
    private Label absolutePath;
    
    @FXML
    private Label header;

    /**
     * Initializes the controller class.
     */
    
    public ResultView(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ResultView.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ResultView.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    } 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        absolutePath.setOnMouseClicked((e)->{
                      try {
                Runtime.getRuntime().exec("explorer.exe /select," +absolutePath.getText());
            } catch (IOException ex) {
                Logger.getLogger(ResultView.class.getName()).log(Level.SEVERE, null, ex);
            }
    
        });
        
       
       
        
      
    } 

    public Label getNameFile() {
        return nameFile;
    }
    
    
   
    @Override
    public void notifier(String header,String absPath,String nameFile) {
        this.absolutePath.setText(absPath);
        this.header.setText(header);
        this.nameFile.setText(nameFile);    
    }

    
}
