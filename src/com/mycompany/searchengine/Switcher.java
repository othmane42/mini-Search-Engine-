package com.mycompany.searchengine;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.Fxml;

/**
 *
 * @author othma
 */
public class Switcher {
  private StackPane rootpane;
 // private String namePath;
  private Stage currentStage;
  private  static Switcher switcher=new Switcher(); 
  
  public static Switcher newInstance()
    {
        return switcher;
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
        this.rootpane=(StackPane)currentStage.getScene().getRoot();
    }
    

    
    public void setRootpane(StackPane rootpane) {
        this.rootpane = rootpane;
        this.currentStage=(Stage)rootpane.getScene().getWindow();
    
    }
  //   public  Switcher setNamePath(String namePath) {
  //      this.namePath = namePath;
  //      return switcher;
  //  }
   
    public Object swtichScene(Node currentscene,Fxml url){
        try {
            FXMLLoader loader=new FXMLLoader(url.get_path());
            Node nextscene=loader.load();
            rootpane.getChildren().clear();
           // rootpane.getChildren().remove(currentscene);
            rootpane.getChildren().add(nextscene);
            rootpane.setPrefWidth(currentStage.getWidth());
            rootpane.setPrefHeight(currentStage.getHeight());
            double width = currentStage.getWidth();
            currentStage.show();
            return loader.getController();
          
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
      public static Object createWindow(Fxml path,String title) {
      try {
          Parent root=null;
          FXMLLoader loader=new FXMLLoader(path.get_path());
          root=loader.load();
          Stage stage = new Stage();
          stage.setTitle(title);
          stage.setMinWidth(400);
          Scene scene = new Scene(root);
          stage.setScene(scene);
          stage.initModality(Modality.APPLICATION_MODAL);
          //  stage.getIcons().add(new Image(getClass().getResource("/res/Icons/logo.png").toExternalForm()));
          stage.show();
          
          return loader.getController();
          //   return null;
      } catch (IOException ex) {
          Logger.getLogger(Switcher.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
    }
   
    
 
}
