/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vue.ListResult;

import com.mycompany.searchengine.Switcher;
import fxml.ResultVisualisation;
import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.Result;
import utils.Fxml;
import vue.ResultView.ResultView;

/**
 * FXML Controller class
 *
 * @author otmane42
 */
public class ListResult implements Initializable {

    @FXML
    private VBox listResult;

    @FXML
    private StackPane root;
    
    private LinkedList<Node> listResults= new LinkedList<>();
    
     
    
   
    /**
     * Initializes the controller class.
     */
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
     //   fillWithDummies();
    }    

    private void fillWithDummies() {
        File[] listFiles = new File("D:\\documents\\tp\\tp rechercheInformation\\tp2\\tp2\\corpus\\acqc\\").listFiles();  
        for (File file : listFiles) {
            ResultView resultView = new ResultView();
            Result result = new Result(file.getAbsolutePath());
            result.setObserver(resultView);
            listResult.getChildren().add(resultView);
        }
    }

    @FXML
    private void openHome(ActionEvent event) {
        Switcher.newInstance().swtichScene(root,Fxml.MAIN);
    }


    public void initList(Set<String> resultQuery,String query) {
        listResult.getChildren().clear();
        for (String string : resultQuery) {
            ResultView resultView = new ResultView();
            Result result = new Result(string);
            result.setObserver(resultView);
            listResult.getChildren().add(resultView);
            listResults.add(resultView);
            resultView.getNameFile().setOnMouseClicked((v)->{
           // Switcher.newInstance().setNamePath("/fxml/result_visualisation");
                ResultVisualisation resultVisualisation = (ResultVisualisation) Switcher.newInstance().swtichScene(root,Fxml.RESULT_VISUALISATION);
                resultVisualisation.init(result.getText(),query,listResults);      
            }); 
    
        }
    }
    public void initViewList(LinkedList<Node> list){
      //  listResult.getChildren().addAll(new LinkedList<>());
        listResult.getChildren().addAll(list);
        
    }
    
}
