package vue.main;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jfoenix.controls.JFXTextField;
import com.mycompany.searchengine.GeneralController;
import com.mycompany.searchengine.Switcher;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import utils.Fxml;
import vue.ListResult.ListResult;
/**
 * FXML Controller class
 *
 * @author otmane42
 */
public class MainController implements Initializable {

    @FXML
    private JFXTextField searchField;

    private ObservableSet<String> history = FXCollections.observableSet(new HashSet<String>());

    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private AutoCompletionBinding autoCompletionBinding;
    private GeneralController generalController=GeneralController.getInstance();
    private File file;
    @FXML
    private StackPane root;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // TODO
            
            file = new File("resource/history.txt");
            bufferedReader = new BufferedReader(new FileReader(file));
            bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            
            String line;
            history.addListener((SetChangeListener.Change<? extends String> c) -> {
                if (autoCompletionBinding != null) {
                    autoCompletionBinding.dispose();
                }
                autoCompletionBinding = TextFields.bindAutoCompletion(searchField, history);
                autoCompletionBinding.setVisibleRowCount(1);
                autoCompletionBinding.setPrefWidth(searchField.getPrefWidth());

            });
            while ((line = bufferedReader.readLine()) != null) {
                history.add(line);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bufferedReader.close();
            } catch (Exception ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


   
   
    @FXML
    private void openHistoryView(ActionEvent event) {
    }

    @FXML
    private void openSettingView(ActionEvent event) {
    
        Object createWindow = Switcher.createWindow(Fxml.DIRECTORY_MANAGER,"Directory Manager");
        
    }

    @FXML
    private void openResultView2(ActionEvent event) { 
        updateHistory();
        switchToResultListView();
    }

    @FXML
    private void openResultView(KeyEvent event) {
             if (event.getCode()==KeyCode.ENTER) {
            // TODO add view result            
                 updateHistory();
                 switchToResultListView();
             }
    }
    
    private void switchToResultListView(){
        generalController.executeQuery(searchField.getText());
        ListResult listResult = (ListResult) Switcher.newInstance().swtichScene(root,Fxml.LIST_RESULT);
    }
    private void updateHistory(){
         String str = "";
            if (!history.contains((str = searchField.getText().toLowerCase()))) {
                try {
                       
                    if (!history.isEmpty()) {
                        bufferedWriter.newLine();
                    }
                    bufferedWriter.write(str);
                    bufferedWriter.flush();
                    history.add(str);
                } catch (IOException ex) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
    }

}
