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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
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
    private static WekaUtils wekaUtils = new WekaUtils();
    private WordsFromFile wordsFromFile;
    public Instances documentDataBase;
  
    private Stemmer stemmer;

    private HashMap<String, Double> termIdf = new HashMap<>();
  
    private WordTokenizer wordTokenizer;
    
    private Instances tfIdfDataBase;

    private WekaUtils() {
        this.wordsFromFile = new WordsFromFile();
        this.wordTokenizer = new WordTokenizer();
    }

    public static WekaUtils getInstance() {
        return wekaUtils;
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

    public HashMap<String, Double> getTermIdf() {
        return termIdf;
    }

    private void indexingDataBase(Instances instances) {
        try {
            StringToWordVector stringToWordVector = new StringToWordVector();
            this.wordsFromFile.setStopwords(new File(STOP_WORDS));
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(DELIMITERS)));
            String str = "";
            while ((str = bufferedReader.readLine()) != null) {
                stringBuilder.append(str);
            }
            this.wordTokenizer.setDelimiters(stringBuilder.toString());
            this.stemmer = new LovinsStemmer();
            stringToWordVector.setStemmer(this.stemmer);
            stringToWordVector.setTokenizer(this.wordTokenizer);
            stringToWordVector.setStopwordsHandler(this.wordsFromFile);
            stringToWordVector.setIDFTransform(true);
            stringToWordVector.setInputFormat(instances);
            //  stringToWordVector.setTFTransform(true);
            //   stringToWordVector.setOutputWordCounts(true);
            Instances useFilter = Filter.useFilter(instances, stringToWordVector);
            initIdfTermMap(useFilter);
            stringToWordVector.setTFTransform(true);
            useFilter = Filter.useFilter(instances, stringToWordVector);
         //   return useFilter;
         this.tfIdfDataBase=useFilter;
        } catch (Exception ex) {
            Logger.getLogger(WekaUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
       // return null;
       // return null;
    }

    private HashMap<String, Double> indexingQuery(String query) {
        HashMap<String, Double> tfIdf = new HashMap<>();
        // StringTokenizer stringTokenizer = new StringTokenizer(query, "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n\r \t");
        //    LovinsStemmer lovinsStemmer = new LovinsStemmer();
        this.wordTokenizer.tokenize(query);
        String str = "";
        int total_count = 0;
        while (this.wordTokenizer.hasMoreElements()) {
            // str=stringTokenizer.nextToken();
            str = this.wordTokenizer.nextElement();
            String stemmed = stemmer.stem(str);
            if (!wordsFromFile.isStopword(str) && termIdf.containsKey(stemmed)) {
                tfIdf.merge(stemmed, 1.0, (t, u) -> {
                    return t + u; //To change body of generated lambdas, choose Tools | Templates.
                });
                //   tfIdf.merge(stemmed,1/stringTokenizer.countTokens());
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
        for (String term : tfidfQuery.keySet()) {

            double documentTfidf = instance.value(tfIdfDataBase.attribute(term).index());
            dotProduct += documentTfidf * tfidfQuery.get(term);
            queryEuclidian_length += Math.pow(tfidfQuery.get(term), 2);
            documentEuclidian_length += Math.pow(documentTfidf, 2);

        }
    //    System.out.println(" dot product : "+dotProduct+" querylength :"+queryEuclidian_length+" documentLength :"+documentEuclidian_length);
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

  
    public void initDataSet(String root) {
       if(documentDataBase!=null) this.documentDataBase.clear();
        HashSet<String> recursiveSearch = recursiveSearch(root);
        Attribute text = new Attribute("text", true);
        Attribute absPath = new Attribute("path", true);
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        Collections.addAll(attributes, text, absPath);
        Instances instances = new Instances("name", attributes, 100);
        for (String path : recursiveSearch) {
            try {
                DenseInstance denseInstance = new DenseInstance(2);
                denseInstance.setValue(absPath, path);
                StringBuilder stringBuilder = new StringBuilder();
                String str = "";
                BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
                while ((str = bufferedReader.readLine()) != null) {
                    stringBuilder.append(str);
                }
                denseInstance.setValue(text, stringBuilder.toString());
                instances.add(denseInstance);
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
     
       this.documentDataBase=instances;
        indexingDataBase(instances);
      //  return instances;
    }

    public  HashSet<String> recursiveSearch(String path) {
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

    private  boolean checkExtention(String ext1) {
        if (ext1.equals("txt")) {
            return true;
        }
        else if (ext1.equals("pdf")){
            return true;
        }
        return false;
    }

    public Set<String> getResultQuery(String query) {
       
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
    
      return results.keySet();
    }
    
    
}
