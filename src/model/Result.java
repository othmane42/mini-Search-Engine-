/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Observer;

/**
 *
 * @author otmane42
 */
public class Result {

    public static int MAX_SIZE_HEADER = 200;
    public String nameFile;
    public String absolutePath;
    private String header;
    private Observer resultView;

   
    public Result(String absolutePath) {
        String data="";
        try {
            this.absolutePath = absolutePath;
            data = new String(Files.readAllBytes(Paths.get(this.absolutePath)));
            this.header=data.substring(0, MAX_SIZE_HEADER);
            this.nameFile=Paths.get(absolutePath).getFileName().toString();
            
        } catch (IOException ex) {
            Logger.getLogger(Result.class.getName()).log(Level.SEVERE, null, ex);
        }catch(StringIndexOutOfBoundsException ex){
            this.header=data;
        }

    }

    public String getHeader() {
        return header;
    }
    public void setObserver(Observer observer){
        this.resultView=observer;
        this.resultView.notifier(header, absolutePath, nameFile);
    }

    public Observer getResultView() {
        return resultView;
    }

    public String getText() {
        try {
  //          String string = new String(Files.readAllBytes(Paths.get(this.absolutePath)));
            BufferedReader buf=new BufferedReader(new FileReader(new File(this.absolutePath)));
            StringBuilder builder= new StringBuilder();
            String str="";
            int i=0;
            while((str=buf.readLine())!=null ){
                builder.append(str+"\n");
                System.out.println("num ligne"+(i++));
            }
            return builder.toString();
        } catch (IOException ex) {
            Logger.getLogger(Result.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    

}
