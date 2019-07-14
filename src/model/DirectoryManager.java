/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import utils.DirectoryWatcher;
import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author otmane42
 */
public class DirectoryManager implements Serializable {
    
    public static String SETTING_FILE_NAME="setting.ser"; 
    private HashSet<FileDirectory> list=new HashSet<>();
    public transient static File FILE= new File(SETTING_FILE_NAME);
    
    public DirectoryManager() {
    }
    
    public void addDirectory(FileDirectory file){
      list.add(file);
    }

  
    public void setList(HashSet<FileDirectory> list) {
        this.list.addAll(list);
    }
   
   
    public HashSet<FileDirectory> getList() {
        return list;
    }
    @Override
    public String toString(){
        return list.stream().map(FileDirectory::toString).collect(Collectors.joining(" "));
    }

    @Override
    public boolean equals(Object obj) {
             if(obj==this)return true;
        if(!(obj instanceof DirectoryManager))return false;
        DirectoryManager tmp=(DirectoryManager) obj;
        long count = list.stream().filter((file1)->{
            return tmp.getList().stream().anyMatch((file)->{
                return file.equals(file1);
            });
        }).count();
        System.out.println("in equal count : "+count);
        return count==list.size() && list.size()==tmp.getList().size();
    }
  
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.list);
        return hash;
    }
    
    
    
    public void clone_like(DirectoryManager clone){
        if (clone!=null){
            this.list.clear();
            this.list.addAll(clone.getList());
            Iterator<FileDirectory> iterator = this.list.iterator();
            while(iterator.hasNext()){
                FileDirectory next = iterator.next();
                if(!next.exists())
                    iterator.remove();
            }
        }
    }
    public boolean checkChanges(){
        return this.list.stream()
                .filter((f)-> f.exists())
                .anyMatch((v)->{
                  boolean bool=v.getLastModified()!=new File(v.getPath()).lastModified();
            if(bool)  v.update();
                    return bool;
                        });
    }
    
    public void startWatch(){
       this.list.stream().forEach((file)->{file.startWatch();});
    }

    public void stopWatch(){
        this.list.stream().forEach((file)->{file.stopWatch();});
    }
    public void join(){
         for (FileDirectory fileDirectory : this.list) {
             try {
                 DirectoryWatcher directoryWatcher = fileDirectory.getDirectoryWatcher();
                 directoryWatcher.getThread().join();
             } catch (InterruptedException ex) {
                 Logger.getLogger(DirectoryManager.class.getName()).log(Level.SEVERE, null, ex);
             }
         }
        
    }

    
  
}
