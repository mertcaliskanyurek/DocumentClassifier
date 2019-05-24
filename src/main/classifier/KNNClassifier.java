package main.classifier;

import main.model.*;

import java.util.*;

public final class KNNClassifier extends Classifier {

    private static final int K_VALUE = 2;

    //String: feature name, Integer: the number of document in which the feature occurs
    private Map<String,Integer> mFeatureDocFreqTable = new Hashtable<>();
    public KNNClassifier(List<String> stopWords, ClassifierListener listener) {
        super(stopWords, listener);
    }

    /** calculates TF-IDF value table for features in worker thread*/
    public void startTrain()
    {
        new Thread(() -> {
            mTrainFreqTable = calculateTfIdfFreqTable(mTrainDocuments,"Train Table");

            if(mListener!=null)
                mListener.onTrainFinished(mTrainFreqTable);
        }).start();
    }

    /**calculates TF-IDF in worker thread and calculates cosine sim of every test document and
     * assings a category. Than calculates performance of test for every assigned category*/
    @Override
    public void startTest() {
        new Thread(()->{
            mTestFreqTable = calculateTfIdfFreqTable(mTestDocuments,"Test Table");

            //String: document name , String: assigned category
            Map<String,String> assignedDocuments = new Hashtable<>();

            for(String testDoc:mTestFreqTable.getAllDocuments())
            {
                //Similarity table for testDoc, String: train document name , Double:similarity value
                Map<String,Double> documentSimilarityTable = new Hashtable<>();
                for(String trainDoc:mTrainFreqTable.getAllDocuments())
                {

                    Map<String, Double> trainDocVector =  mTrainFreqTable.getFreqTableWithFuture(trainDoc);
                    Map<String, Double> testDocVector = mTestFreqTable.getFreqTableWithFuture(testDoc);

                    Double cosineSim = calculateCosineSimilarity(testDocVector,trainDocVector);

                    documentSimilarityTable.put(trainDoc,cosineSim);
                }

                String similarDocument = decideDocument(documentSimilarityTable);

                if(similarDocument!=null && mTrainFreqTable.getDocCategory(similarDocument)!=null){
                    assignedDocuments.put(testDoc,mTrainFreqTable.getDocCategory(similarDocument));
                    if(mListener!=null)
                        mListener.onAssignedCategory(testDoc,mTrainFreqTable.getDocCategory(similarDocument));
                    //System.out.println(testDoc+" assigned category : "+mTrainFreqTable.getDocCategory(similarDocument));
                }
                else
                    System.out.println(testDoc+" not assigned any category");

            }

            Map<String, Performance> categoryPerformanceTable = calculatePerformance(mTestFreqTable,assignedDocuments);
            mListener.onTestFinished(categoryPerformanceTable);
        }).start();
    }

    /**@param testTable TF-IDF values of test documents
     * @param assignedDocuments key: document name (test) value:assigned category
     *
     * @return key:category name value:Performance table of category*/
    private Map<String, Performance> calculatePerformance(FreqTable testTable, Map<String,String> assignedDocuments)
    {
        Map<String, Performance> categoryPerformanceTable = new Hashtable<>();
        List<String> categories = testTable.getAllCategories();

        for(String category:categories)
        {
            if(!categoryPerformanceTable.containsKey(category))
                categoryPerformanceTable.put(category,new Performance(category));

            for(String docName:assignedDocuments.keySet())
            {
                String docCategory = testTable.getDocCategory(docName);
                if(docCategory!=null)
                {
                    Performance p = categoryPerformanceTable.get(category);
                    p.addValue(docCategory,assignedDocuments.get(docName));
                    categoryPerformanceTable.put(category,p);
                }
            }
        }

        return categoryPerformanceTable;
    }

    /**Decides the similar document of documents similarity table with {@value K_VALUE}
     *
     * @param documentSimilarityTable key:document name of train document value: similarity value
     *
     * @return decided document name*/
    private String decideDocument(Map<String,Double> documentSimilarityTable)
    {
        if(documentSimilarityTable.size()==0)
            return null;

        Stack<String> maxValuesStack = new Stack<>();
        Double max = 0.0;
        for(Map.Entry<String,Double> entry:documentSimilarityTable.entrySet())
        {
            if(entry.getValue()>max)
            {
                maxValuesStack.push(entry.getKey());
                max = entry.getValue();
            }

        }

        if(K_VALUE<2)
        {
            return maxValuesStack.pop();
        }
        else {
            List<String> alternatives = new ArrayList<>();
            for (int i = 0;i<maxValuesStack.size() && i < K_VALUE; i++)
                alternatives.add(maxValuesStack.pop());

            if(alternatives.size()!=0) {
                int rand = new Random().nextInt(alternatives.size());
                return alternatives.get(rand);
            }
            else
                return null;
        }
    }

    /**Calculates cosine similary of feature - TFIDF vectors
     *
     * @param testVector key: feature name value: TF-IDF value
     * @param trainVector key: feature name value: TF-IDF value
     *
     * @return Similarity value. If denominator will be 0 returnvalue will be also 0*/
    private Double calculateCosineSimilarity(Map<String, Double> testVector,Map<String, Double> trainVector)
    {
        double sumProduct = 0;
        double sumVector1Sq = 0;
        double sumVector2Sq = 0;

        for (String feature:trainVector.keySet()) {
            if(testVector.containsKey(feature) && trainVector.containsKey(feature))
                sumProduct+=(testVector.get(feature)*trainVector.get(feature));
        }

        for(Double freq:testVector.values())
            sumVector1Sq+= Math.pow(freq,2);

        for(Double freq:trainVector.values())
            sumVector2Sq+=Math.pow(freq,2);

        if (sumVector1Sq == 0 || sumVector2Sq == 0) {
            return 0.0;
        }

        return sumProduct / (Math.sqrt(sumVector1Sq * sumVector2Sq));
    }

    /**Calculates TF-IDF table values of given documents. While calculating document frequency
     * method will look train documents.
     *
     * @param documents list of documents
     * @param tableName name of table
     *
     * @return document-feature-TFIDF freq table*/
    private FreqTable calculateTfIdfFreqTable(List<Document> documents,String tableName)
    {
        FreqTable table = new FreqTable(tableName);
        //calculate tf-idf value for every categorys feature
        for(Document document:documents)
        {
            for(Feature feature:document.getFeatures())
            {
                Double tfIdfValue = calculateTfIdf(mTrainDocuments,feature);
                table.addValue(document.getName(),document.getCategory(),feature.getName(),tfIdfValue);
            }
        }

        return table;
    }

    /**Calculates tfIdf of feature. For document frequency method will look trainDocuments
     *
     * @param trainDocs train documents for document frequency
     * @param feature feature for feature to calculate
     *
     * @return TF-IDF value of feature. If document frequency will be 0 return value will be 0*/
    private Double calculateTfIdf(List<Document> trainDocs,Feature feature)
    {
        double docFreq = calculateDocFreq(trainDocs,feature);

        if(docFreq==0.0)
            return 0.0;

        double freq = feature.getRawFreq();
        double N = trainDocs.size();
        //System.out.println(feature.getName()+"freq : "+freq+"docFreq : "+docFreq+"N : "+trainDocs.size()+"tfidf : "+(freq * Math.log10(N/docFreq)));
        return freq * Math.log10(N/docFreq);
    }

    /**Calculates document frequency of a feature with given documents. In our case
     *  this documents will be train documents
     *
     * @param documents list of documents
     * @param feature feature to calculate document frequency with given documents*/
    private int calculateDocFreq(List<Document> documents,Feature feature)
    {
        if(mFeatureDocFreqTable.containsKey(feature.getName()))
            return mFeatureDocFreqTable.get(feature.getName());
        else {
            int count = 0;
            for (Document document : documents) {
                if (document.getFeatures().contains(feature))
                    count++;
            }

            mFeatureDocFreqTable.put(feature.getName(),count);
            return count;
        }
    }
}
