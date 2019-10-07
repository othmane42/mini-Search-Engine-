package utils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.mycompany.searchengine.GeneralController;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.stemmers.LovinsStemmer;
import weka.core.stemmers.Stemmer;
import weka.core.stopwords.WordsFromFile;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author otmane42
 */
public class WekaUtils {

    private static String STOP_WORDS = "resource/stopWords.txt";
    private static String DELIMITERS = "resource/delimiters.txt";
    private static String TFIDF_DATABASE_NAME_FILE="tfIdfDataBase";
    private static String IDF_TERM_NAME_FILE="idf-term.ser";
    private static WekaUtils wekaUtils = new WekaUtils();
    private WordsFromFile wordsFromFile;
  
    private Stemmer stemmer;

    private HashMap<String, Double> termIdf = new HashMap<>();
  
    private WordTokenizer wordTokenizer;
    
    private Instances tfIdfDataBase;

    private WekaUtils() {
        this.wordsFromFile = new WordsFromFile();
        this.wordTokenizer = new WordTokenizer();
        this.stemmer = new LovinsStemmer();
          init();
    }
    private void init(){
        BufferedReader bufferedReader = null;
        try {
            this.wordsFromFile.setStopwords(new File(STOP_WORDS));
            StringBuilder stringBuilder = new StringBuilder();
            bufferedReader = new BufferedReader(new FileReader(new File(DELIMITERS)));
            String str = "";
            while ((str = bufferedReader.readLine()) != null) {
                stringBuilder.append(str);
            }
            this.wordTokenizer.setDelimiters(stringBuilder.toString());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WekaUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WekaUtils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException ex) {
                Logger.getLogger(WekaUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static WekaUtils getInstance() {
        return wekaUtils;
    }

    public boolean saveCheckpoint(){
       if(tfIdfDataBase!=null && !tfIdfDataBase.isEmpty()){
               saveIDFTERM();
        return saveDataSet(tfIdfDataBase,TFIDF_DATABASE_NAME_FILE);
    
          }
       return false;
    }
    public boolean saveDataSet( Instances dataSet, String nameFile) {
        try {
            ArffSaver arffSaver = new ArffSaver();
            arffSaver.setInstances(dataSet);
            arffSaver.setFile(new File(nameFile.endsWith(".arff") ? nameFile : nameFile + ".arff"));
            arffSaver.writeBatch();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public Instances loadDataSet(String nameDataset){
            ArffLoader arffLoader = new ArffLoader();
            Instances instances=null;
        try {
            File file = new File(nameDataset.endsWith(".arff") ? nameDataset : nameDataset + ".arff");
            if(file.exists()){
                      arffLoader.setSource(file);
                      instances=arffLoader.getDataSet();
            }
        } catch (IOException ex) {
            Logger.getLogger(WekaUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
                 return instances;
    }

    public HashMap<String, Double> getTermIdf() {
        return termIdf;
    }

    public Instances getTfIdfDataBase() {
        return tfIdfDataBase;
    }

    
    public void indexingDataBase(Instances instances) {
        try {
            StringToWordVector stringToWordVector = new StringToWordVector();
            stringToWordVector.setStemmer(this.stemmer);
            stringToWordVector.setTokenizer(this.wordTokenizer);
            stringToWordVector.setStopwordsHandler(this.wordsFromFile);
            stringToWordVector.setIDFTransform(true);
            stringToWordVector.setInputFormat(instances);
            Instances useFilter = Filter.useFilter(instances, stringToWordVector);
            initIdfTermMap(useFilter);
            stringToWordVector.setTFTransform(true);
            useFilter = Filter.useFilter(instances, stringToWordVector);
         //   return useFilter;
         this.tfIdfDataBase=useFilter;
        } catch (Exception ex) {
            Logger.getLogger(WekaUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private HashMap<String, Double> indexingQuery(String query) {
        HashMap<String, Double> tfIdf = new HashMap<>();
        this.wordTokenizer.tokenize(query);
        String str = "";
        int total_count = 0;
        while (this.wordTokenizer.hasMoreElements()) {
            str = this.wordTokenizer.nextElement();
            String stemmed = stemmer.stem(str);
            if (!wordsFromFile.isStopword(str) && termIdf.containsKey(stemmed)) {
                tfIdf.merge(stemmed, 1.0, (t, u) -> {
                    return t + u; //To change body of generated lambdas, choose Tools | Templates.
                });
                total_count++;
            }

        }
        
        for (String string : tfIdf.keySet()) {
            Double tf = tfIdf.get(string);
            tfIdf.put(string, (tf / total_count) * termIdf.get(string));
        }
        return tfIdf;

    }
    
    private double RSV(HashMap<String, Double> tfidfQuery, Instance instance) {

        double queryEuclidian_length = 0;
        double documentEuclidian_length = 0;
        double dotProduct = 0;
    //    for (String term : tfidfQuery.keySet()) {
        for(int i=0,a=tfIdfDataBase.numAttributes();i<a;i++){
            
            Attribute attribute = tfIdfDataBase.attribute(i);
            double documentTfidf = instance.value(attribute.index());
            Double tfidfquery=( (tfidfquery=tfidfQuery.get(attribute.name()))==null)? 0:tfidfquery;
            dotProduct += documentTfidf *tfidfquery;
           
            queryEuclidian_length += Math.pow(tfidfquery, 2);
            documentEuclidian_length += Math.pow(documentTfidf, 2);

        }
        if(queryEuclidian_length==0 || documentEuclidian_length==0 || dotProduct==0)return 0;
        return dotProduct / (Math.sqrt(documentEuclidian_length)*Math.sqrt(queryEuclidian_length));

    }

    private void initIdfTermMap(Instances useFilter) {
        Enumeration<Attribute> enumerateAttributes = useFilter.enumerateAttributes();
        termIdf.clear();
        while (enumerateAttributes.hasMoreElements()) {
            Attribute nextElement = enumerateAttributes.nextElement();
            double max = useFilter.attributeStats(nextElement.index()).numericStats.max;
            termIdf.put(nextElement.name(), max);
        }

    }

    public Set<String> getResultQuery(String query) {
       
       Instances documentDataBase=DirectoryDataBaseManager.getInstance().getDocumentDataBase();
       Map<String,Double> map=new TreeMap<>();
       HashMap<String, Double> indexingQuery = indexingQuery(query);
       for (Instance instance : tfIdfDataBase) {
         double score= RSV(indexingQuery,instance);
         int index = tfIdfDataBase.indexOf(instance);
        if(score!=0) map.put(documentDataBase.instance(index).stringValue(1), score); 
        }
       Comparator<String> comparator = (Comparator<String>) (String o1, String o2) -> {
           int compare=0; 
           return ((compare=map.get(o2).compareTo(map.get(o1)))==0)? 1:compare;
        };
      TreeMap<String,Double> results = new TreeMap<String, Double>(comparator);
      results.putAll(map);
      results.forEach((s,v)->{
          System.out.println("s : "+s+"  v :"+v);
      });
      
      return results.keySet();
    }

    public boolean loadCheckPoint() {
        Instances loadDataSet = loadDataSet(TFIDF_DATABASE_NAME_FILE);
        HashMap<String, Double> loadIDFTERM = loadIDFTERM();
        if(loadDataSet==null || loadIDFTERM==null){
            return false;
        }
        else {
            tfIdfDataBase=loadDataSet;
            termIdf=loadIDFTERM;
            return true;
        }
    }
    
    private void saveIDFTERM(){
                ObjectOutputStream output = null;
      try {
            new File(IDF_TERM_NAME_FILE).createNewFile();
            output = new ObjectOutputStream(new FileOutputStream(IDF_TERM_NAME_FILE));
            output.writeObject(termIdf);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WekaUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WekaUtils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
                Logger.getLogger(WekaUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
     
    }
    
    private HashMap<String,Double> loadIDFTERM(){
                    ObjectInputStream input=null;
        HashMap<String,Double> newObject=null;
        File file=new File(IDF_TERM_NAME_FILE);
        if(file.canRead() && file.length()>0) 
            try {
            input = new ObjectInputStream(new FileInputStream(new File(IDF_TERM_NAME_FILE)));
            newObject =(HashMap<String, Double>) input.readObject();
         } catch (FileNotFoundException ex) {
            Logger.getLogger(WekaUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WekaUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(WekaUtils.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try{
             input.close();   
            } catch (IOException ex) {
               Logger.getLogger(WekaUtils.class.getName()).log(Level.SEVERE, null, ex);
           }
        }
              
      return newObject;
     
    
    
    }
    
}
