package main.model;

import main.Main;

import java.util.*;
import java.util.stream.Collectors;

public class FreqTable {

    //String: feature <String:documentName,Double:freq value in that document>
    private Map<String,Map<String,Double>> mTable = new Hashtable<>();

    //String: document name , string document category
    private Map<String,String> mDocumentCategoryTable = new Hashtable<>();

    private String mName;

    public FreqTable(String name) {
        this.mName = name;
    }

    public void addValue(String document,String docCategory, String feature, Double freq)
    {
        if(mTable.containsKey(feature))
        {
            mTable.get(feature).put(document,freq);
        }
        else
        {
            Map<String,Double> newColmn = new Hashtable<>();
            newColmn.put(document,freq);
            mTable.put(feature,newColmn);
            mDocumentCategoryTable.put(document,docCategory);
        }
    }

    public String getDocCategory(String document)
    {
       return mDocumentCategoryTable.get(document);
    }


    public String getName() {
        return mName;
    }

    public Set<String> getAllFeatures()
    {
        return mTable.keySet();
    }

    public Set<String> getAllDocuments()
    {
        Set<String> documents = new HashSet<>();

        for(String f:mTable.keySet())
            documents.addAll(mTable.get(f).keySet());

        return documents;
    }

    public List<String> getAllCategories()
    {
        return mDocumentCategoryTable.values().stream().distinct().collect(Collectors.toList());
    }

    public Map<String,Double> getFreqTableWithDocument(Feature feature) {
        return mTable.get(feature);
    }

    public Map<String,Double> getFreqTableWithFuture(String document)
    {
        Map<String,Double> freq = new Hashtable<>();

        for(String feature:mTable.keySet())
            freq.put(feature, mTable.get(feature).getOrDefault(document, 0.0));

        return freq;
    }

}
