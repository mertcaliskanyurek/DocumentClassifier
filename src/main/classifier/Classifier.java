package main.classifier;

import _zem.javax.annotation.Nullable;
import main.model.*;
import zemberek.morphology.TurkishMorphology;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.tokenization.TurkishTokenizer;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Classifier {

    private static final String idleCharRegex = "[!@#$%^&*(),.?\":{}|<>';/\\-0-9]";
    protected static final int THRESHOLD = 2;

    private static final TurkishTokenizer mTokenizer = TurkishTokenizer.DEFAULT;
    private static final TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
    private List<String> mStopWords=null;

	protected List<Document> mTrainDocuments = new ArrayList<>();
    protected List<Document> mTestDocuments = new ArrayList<>();

    protected ClassifierListener mListener=null;

    protected FreqTable mTrainFreqTable;
    protected FreqTable mTestFreqTable;

	public Classifier(@Nullable List<String> stopWords,@Nullable ClassifierListener listener)
    {
        if(stopWords!=null)
            mStopWords = stopWords;

        if(listener!=null)
            mListener = listener;
    }

    /**@param name category name
     * @param documentNameContent key:document name value: document content*/
    public void addCategory(String name, Map<String,String> documentNameContent,boolean train)
    {
        for(Map.Entry<String, String> entry : documentNameContent.entrySet())
        {
            Document tempDoc = new Document(entry.getKey(),name);
            String documentContent = clarifyDocumentContent(entry.getValue());
            List<String> tokens = mTokenizer.tokenizeToStrings(documentContent);

            //remove stop words if stop words file exists
            if(mStopWords!=null)
                tokens.removeAll(mStopWords);

            for(String token:tokens)
            {
                //apply stemming analyze to token.
                //if any stem has been found add stem as feature. Else add token as a feature
                WordAnalysis result = morphology.analyze(token);
                if(!result.getAnalysisResults().isEmpty())
                {
                    String stem = result.getAnalysisResults().get(0).getStem();
                    tempDoc.addFeature(stem);
                }
                else
                    tempDoc.addFeature(token);
            }

            if(train)
                mTrainDocuments.add(applyFutureReduction(tempDoc));
            else
                mTestDocuments.add(applyFutureReduction(tempDoc));
        }
    }

    public abstract void startTrain();

	public abstract void startTest();

	/**@return document content with apllied lower case and removed idle chars*/
    private String clarifyDocumentContent(String documentContent)
    {
        documentContent = documentContent.toLowerCase();
        documentContent = documentContent.replaceAll(idleCharRegex,"");

        return documentContent;
    }

    /**@param document document
     * @return document with removed features row frequency under the {@value THRESHOLD}*/
    private Document applyFutureReduction(Document document)
    {
        Document newDoc = new Document(document.getName(),document.getCategory());
        for(Feature f:document.getFeatures())
            if(f.getRawFreq()<THRESHOLD)
                newDoc.addFeature(f);

        return newDoc;
    }

    /**@param name category name
     * @param train is category train category*/
    public int deleteCategory(String name,boolean train)
    {
        List<Document> documents = new ArrayList<>();
        int deleted=0;

        if(train){
            for(Document d:mTrainDocuments)
            {
                if(d.getCategory().equals(name))
                   deleted++;
                else
                    documents.add(d);
            }
            mTrainDocuments = documents;
        }
        else{
            for(Document d:mTestDocuments)
            {
                if(d.getCategory().equals(name))
                    deleted++;
                else
                    documents.add(d);
            }
            mTestDocuments = documents;
        }

        return deleted;
    }

    /**@param stopWords list of stop words*/
    public void setStopWords(List<String> stopWords) {
        this.mStopWords = stopWords;
    }
}
