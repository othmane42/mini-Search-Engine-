/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vue.DirectoryManager;

import com.mycompany.searchengine.GeneralController;
import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.FileDirectory;
import vue.DirectoryView.DirectoryView;

/**
 * FXML Controller class
 *
 * @author otmane42
 */
public class DirectoryManagerController implements Initializable {

    /**
     * Initializes the controller class.
     * 
     * 
     */
    
   
    private HashSet<FileDirectory> tmpListFileDirectory=new HashSet<>();
    private GeneralController  generalController= GeneralController.getInstance();
    private Stage stage;
   
    
    @FXML
    private BorderPane root;
    @FXML
    private VBox listDirectories;
    @FXML
    private VBox addVBox;
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        generalController.getDirectoryManager().getList().forEach((file)->{
            DirectoryView directoryView = prepareView(file);
             listDirectories.getChildren().add(listDirectories.getChildren().indexOf(addVBox),directoryView);
             tmpListFileDirectory.add(file);
        });
    }

    private DirectoryView prepareView(FileDirectory file){
                  DirectoryView directoryView = new DirectoryView();
                  file.setObserver(directoryView);
                  directoryView.getDeleteBtn().setOnAction((e)->{
                  //   Parent parent = directoryView.getParent();
                    listDirectories.getChildren().remove(directoryView);
                     tmpListFileDirectory.remove(file);
            });
        return directoryView;
    }

    @FXML
    private void apply(ActionEvent event) {
        generalController.updateDirectoryManager(tmpListFileDirectory);
        closeWindow();
    }

    private void closeWindow(){
         stage = (Stage) root.getScene().getWindow();
        stage.close();
    }
 
    @FXML
    private void cancel(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void ajouterDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File direcFile = directoryChooser.showDialog(null);
        if(direcFile!=null){
            FileDirectory fileDirectory = new FileDirectory(direcFile,GeneralController.getInstance().getDirectoryDataBaseManager());
            DirectoryView directoryView = prepareView(fileDirectory);
            listDirectories.getChildren().add(listDirectories.getChildren().indexOf(addVBox),directoryView);
            tmpListFileDirectory.add(fileDirectory);
        }
    }
    
}
