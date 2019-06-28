/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author otmane42
 */
public class CustomFileReader {
    
    public void readFile(String path){
        BufferedReader bufferedReader = null;
        try {
            File file = new File("resource/history.txt");
            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CustomFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException ex) {
                Logger.getLogger(CustomFileReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
           
        
    }
    
    
}
