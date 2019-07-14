/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.net.URL;

/**
 *
 * @author otmane42
 */
public  enum Fxml {
    
    MAIN{
        @Override
        public URL get_path() {
            return getClass().getResource("/fxml/main.fxml");
        }      
    },
    LIST_RESULT {
        @Override
        public URL get_path() {
            return getClass().getResource("/fxml/list_result.fxml");
        }
    },
    RESULT_VISUALISATION {
             @Override
        public URL get_path() {
            return getClass().getResource("/fxml/result_visualisation.fxml");
        }  
    },
    RESULT_VIEW{
        @Override
        public URL get_path() {
            return getClass().getResource("/fxml/ResultView.fxml");
        }
        },
    DIRECTORY_VIEW{
          @Override
        public URL get_path() {
            return getClass().getResource("/fxml/DirectoryView.fxml");
        }
      
    },DIRECTORY_MANAGER{
        @Override
        public URL get_path() {
            return getClass().getResource("/fxml/DirectoryManager.fxml");
        }
        
    };
    
    /**
     *
     */
    public abstract URL get_path();
}
