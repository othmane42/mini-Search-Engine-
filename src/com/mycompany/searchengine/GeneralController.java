/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.searchengine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.DirectoryManager;
import model.FileDirectory;
import model.Result;
import utils.DirectoryDataBaseManager;
import utils.WekaUtils;

/**
 *
 * @author otmane42
 */
public class GeneralController {
    private static GeneralController instance=new GeneralController();
    private static final String TEST_PATH="D:\\documents\\tp\\tp rechercheInformation\\tp2\\tp2\\corpus";
    private DirectoryDataBaseManager directoryDataBaseManager;
    private String currentQuery;
    private Result currentResult;
    private ArrayList<Result> listResult;
    private DirectoryManager directoryManager;
    private GeneralController() {
       currentQuery="";
       listResult = new ArrayList<Result>();
      directoryDataBaseManager =DirectoryDataBaseManager.getInstance();
    }
    public static GeneralController getInstance(){
        return instance;
    }

    
    public DirectoryDataBaseManager getDirectoryDataBaseManager() {
        return directoryDataBaseManager;
    }
    public void init(){
     //     File file = new File(TEST_PATH);
          directoryManager= new DirectoryManager();
        DirectoryManager loadCheckpoint = loadCheckpoint();
        directoryManager.clone_like(loadCheckpoint);
        //  directoryManager.addDirectory(new FileDirectory(file,directoryDataBaseManager));
        if(directoryManager.checkChanges()){
            System.out.println("doc changed");
            directoryDataBaseManager.initDataSet(directoryManager);
      
        }
        else{
              boolean loaded = directoryDataBaseManager.loadCheckPoint();
              if(!loaded){
                  System.out.println("not loaded ");
                  directoryDataBaseManager.initDataSet(directoryManager);
              }
              else{
                  boolean loadCheckPoint = WekaUtils.getInstance().loadCheckPoint();
                  if(!loadCheckPoint){
                      System.out.println("weka not loaded");
                      directoryDataBaseManager.initDataSet(directoryManager);
                      
                  }
              }
        }
       directoryManager.startWatch();
    }

    public ArrayList<Result> getListResult() {
        return listResult;
    }

    public String getCurrentQuery() {
        return currentQuery;
    }

    public Result getCurrentResult() {
        return currentResult;
    }

    public DirectoryManager getDirectoryManager() {
        return directoryManager;
    }
    
    
    

    public void executeQuery(String query) {
        currentQuery=query;
        listResult.clear();
        WekaUtils.getInstance().getResultQuery(currentQuery).stream().forEach((s)->{listResult.add(new Result(s));});
    }

    public void selectCurrentResult(Result result) {
        this.currentResult= result;       
    }

   
    public void updateDirectoryManager(HashSet<FileDirectory> tmpListFileDirectory) {
        DirectoryManager directory = new DirectoryManager();
        directory.setList(tmpListFileDirectory);
        if(!directoryManager.equals(directory)){
                System.out.println("not equal ! ");
                directoryManager.clone_like(directory);
                System.out.println("directory manager , size : "+directoryManager.getList().size());
                directoryDataBaseManager.initDataSet(directoryManager);
         startWatchers();
        }       
    }

    void stopWatchers() {
        directoryManager.stopWatch();
    }
    void startWatchers(){
        directoryManager.startWatch();
    }

    void saveCheckpointManager() 
    {
       ObjectOutputStream output = null;
        try {
            DirectoryManager.FILE.createNewFile();
            output = new ObjectOutputStream(new FileOutputStream(DirectoryManager.FILE));
            output.writeObject(directoryManager);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileDirectory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileDirectory.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
                Logger.getLogger(FileDirectory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
 
    }
    public DirectoryManager loadCheckpoint(){
          ObjectInputStream input=null;
        DirectoryManager newObject=null;
       if(DirectoryManager.FILE.exists())
            try {
            input = new ObjectInputStream(new FileInputStream(DirectoryManager.FILE));
            newObject = (DirectoryManager) input.readObject();
            
         } catch (FileNotFoundException ex) {
            Logger.getLogger(DirectoryManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DirectoryManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DirectoryManager.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try{
             input.close();   
            } catch (IOException ex) {
               Logger.getLogger(DirectoryManager.class.getName()).log(Level.SEVERE, null, ex);
           }
        }
       
      return newObject;
  
    }    

    void stop() {
        stopWatchers();
        saveCheckpointManager();
        directoryDataBaseManager.saveCheckpoint();
        boolean saveCheckpoint = WekaUtils.getInstance().saveCheckpoint();
        if(saveCheckpoint)
            System.out.println("tfIdf saved successfully ! ");
    }
    
}
