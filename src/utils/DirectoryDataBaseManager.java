package utils;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.DirectoryManager;
import model.FileDirectory;
import org.apache.commons.io.FilenameUtils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
/**
 *
 * @author otmane42
 */
public class DirectoryDataBaseManager implements Serializable{

    private Instances documentDataBase;
    private ArrayList<Attribute> attributes;
    public static String DOCUMENT_CHECKPOINT="documentdataBase"; 
    private static DirectoryDataBaseManager instance=new DirectoryDataBaseManager();

    private DirectoryDataBaseManager() {
       Attribute text = new Attribute("text", true);
       Attribute absPath = new Attribute("path", true);
       attributes = new ArrayList<Attribute>();
        Collections.addAll(attributes, text, absPath);
        this.documentDataBase = new Instances("name", attributes,5000);
     
    }
    public static DirectoryDataBaseManager getInstance(){
        return instance;
    }

    public Instances getDocumentDataBase() {
        return documentDataBase;
    }
    
    
    public void initDataSet(DirectoryManager dirManager) {
        this.documentDataBase.clear();
        dirManager.getList().stream()
                .forEach((fileDirectory)->{ 
                    recursiveSearch(fileDirectory.getPath())
                            .parallelStream().forEach((path)-> addFile(path,false));
            });
        WekaUtils.getInstance().indexingDataBase(documentDataBase);
        System.out.println("data base initialized");
    }
    
    public synchronized void addDirectory(FileDirectory dir,boolean update){
        
    }

    public synchronized void addFile(String path, boolean update) {
       BufferedReader bufferedReader =null;
        try {
            DenseInstance denseInstance = new DenseInstance(2);
            denseInstance.setValue(attributes.get(1), path);
            StringBuilder stringBuilder = new StringBuilder();
            String str = "";
            bufferedReader = new BufferedReader(new FileReader(new File(path)));
            while ((str = bufferedReader.readLine()) != null) {
                stringBuilder.append(str);
            }
            denseInstance.setValue(attributes.get(0), stringBuilder.toString());
            this.documentDataBase.add(denseInstance);
            if (update) {
                WekaUtils.getInstance().indexingDataBase(documentDataBase);
                System.out.println("updated");
            }
        } catch (IOException ex) {
            Logger.getLogger(DirectoryDataBaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
           try {
               bufferedReader.close();
           } catch (IOException ex) {
               Logger.getLogger(DirectoryDataBaseManager.class.getName()).log(Level.SEVERE, null, ex);
           }
        }

    }

    public synchronized void deleteFile(String path, boolean update) {
        Instances tmp = new Instances(this.documentDataBase, 0);
        this.documentDataBase.stream()
                .filter((Instance instance)
                        -> {
                   return !instance.stringValue(this.documentDataBase.attribute("path").index()).equals(path);
                                        })
                .forEachOrdered((instance) -> tmp.add(instance));
        this.documentDataBase = tmp;
        if (update) {
            WekaUtils.getInstance().indexingDataBase(this.documentDataBase);
        }

    }

    public synchronized void updateFile(String path) {
        WekaUtils.getInstance().indexingDataBase(documentDataBase);
        System.out.println("updated !!");
    }

    public HashSet<String> recursiveSearch(String path) {
        File[] listFiles = new File(path).listFiles();
        if (listFiles == null) {
            return null;
        }
        HashSet<String> pathSet = new HashSet<String>();
        for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].isDirectory()) {
                pathSet.addAll(recursiveSearch(listFiles[i].getAbsolutePath()));
            } else {
                String ext1 = FilenameUtils.getExtension(listFiles[i].getName()); // returns "txt"
                if (checkExtention(ext1)) {
                    pathSet.add(listFiles[i].getAbsolutePath());
                }
            }

        }
        return pathSet;
    }

    private boolean checkExtention(String ext1) {
        if (ext1.equals("txt")) {
            return true;
        } else if (ext1.equals("pdf")) {
            return true;
        }
        return false;
    }

    public boolean loadCheckPoint(){
        Instances instances=WekaUtils.getInstance().loadDataSet(DOCUMENT_CHECKPOINT);
        if(instances!=null)
        {
            documentDataBase=instances;
            return true;
        }
            return false;
        
    }
    public void saveCheckpoint(){
        WekaUtils.getInstance().saveDataSet(documentDataBase,DOCUMENT_CHECKPOINT);
    }
}
