package model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import utils.DirectoryDataBaseManager;
import utils.DirectoryWatcher;
import java.io.File;
import java.io.Serializable;
import java.util.Objects;
import utils.DirectoryViewObserver;

/**
 *
 * @author otmane42
 */
public class FileDirectory  implements Serializable {

    private String path;
    private long lastModified;
    private long size;
    private transient DirectoryWatcher directoryWatcher;
    private transient DirectoryViewObserver observer;
    
    public FileDirectory(File file,DirectoryDataBaseManager manager) {
        
        this.path = file.getAbsolutePath();
        this.lastModified= file.lastModified();
        this.size=file.getTotalSpace();
        this.directoryWatcher=new DirectoryWatcher(this);   
    }

    public FileDirectory() {
  
    }
    

    public DirectoryWatcher getDirectoryWatcher() {
        return directoryWatcher;
    }

    public void setObserver(DirectoryViewObserver observer) {
        this.observer = observer;
        notifyObserver();
    }
    public void startWatch(){
      if(directoryWatcher==null)
      {
          this.directoryWatcher=new DirectoryWatcher(this);
          this.directoryWatcher.startWatch();
      }
     else if(directoryWatcher.getThread()==null || (directoryWatcher.getThread()!=null && !directoryWatcher.getThread().isAlive()) )
           directoryWatcher.startWatch();
    }
 
    public void stopWatch(){
        directoryWatcher.stopWatch();
    }
        
    public String getPath() {
        return path;
    }
    public boolean exists(){
       return new File(this.path).exists();
    }

    public long getLastModified() {
        return lastModified;
    }

    public long getSize() {
        return size;
    }

    public void setPath(String path) {
        this.path = path;
        notifyObserver();
    }

    @Override
    public String toString() {
        return path;
        //To change body of generated methods, choose Tools | Templates.
    }

    public void update(){
        File file = new File(this.path);
        this.path = file.getAbsolutePath();
        this.lastModified= file.lastModified();
        this.size=file.getTotalSpace();
    }
    @Override
    public boolean equals(Object obj) {
        if(obj==this)return true;
        if(!(obj instanceof FileDirectory))return false;
        FileDirectory tmp=(FileDirectory)obj;
       return this.path.equals(tmp.getPath());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.path);
        hash = 71 * hash + (int) (this.lastModified ^ (this.lastModified >>> 32));
        hash = 71 * hash + (int) (this.size ^ (this.size >>> 32));
        return hash;
    }
    

    private void notifyObserver() {
        this.observer.updatePath(this.path);
    }
    
        
    
    
}
