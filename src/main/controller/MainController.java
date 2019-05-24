package main.controller;

import main.classifier.Classifier;
import main.classifier.ClassifierListener;
import main.classifier.KNNClassifier;
import main.gui.MainFrame;
import main.gui.MainFrameListener;
import main.model.*;
import main.text_file_helper.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MainController implements MainFrameListener, ClassifierListener {

    private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("main.res.Output");
    private static final ResourceBundle ERRORS = ResourceBundle.getBundle("main.res.Error");

    private Classifier mClassifier;
    private MainFrame mWindow;

    private String mOutputDir = null;
    public MainController()
    {
        mWindow = new MainFrame(this);
        mWindow.setVisible(true);

        //Homeword bot will handle read files.
        mWindow.setEnabledAllComponents(false);
        mClassifier = new KNNClassifier(null,this);
    }

    private boolean setStopWordsFile(File stopWords)
    {
        try {
            String stopWordsFileText = FileHelper.readFromTextFile(stopWords);
            String [] stopWordStrings = stopWordsFileText.split("\n");
            mClassifier.setStopWords(Arrays.asList(stopWordStrings));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
           return false;
        }
    }

    @Override
    public void addCategoryPressed(String name, File[] documents,boolean train) {

        Map<String,String> documentTexts = new HashMap<>();
        for(File f: documents)
        {
            try {
                String documentName = f.getName();
                String documentText = FileHelper.readFromTextFile(f);

                mWindow.addLog(documentName+" readed");
                if(!documentTexts.containsKey(documentName))
                    documentTexts.put(documentName,documentText);
                else
                    mWindow.addLog(documentName+ERRORS.getString("documentAlreadyLoaded"));

            } catch (IOException e) {
                e.printStackTrace();
                mWindow.addLog(f.getName()+ERRORS.getString("documentCantRead"));
            }
        }

        mClassifier.addCategory(name,documentTexts,train);
        String trainText = train?RESOURCES.getString("train"):RESOURCES.getString("test");
        mWindow.addOutput(trainText+" "+RESOURCES.getString("categoryAdded")+name+"  "
                +RESOURCES.getString("documentSize")+documentTexts.size());
    }

    private void showTrainResult(FreqTable table) {
        mWindow.addOutput(RESOURCES.getString("result"));
        mWindow.addOutput(RESOURCES.getString("features")+table.getAllFeatures().size());
        mWindow.addOutput(RESOURCES.getString("docSize")+table.getAllDocuments().size());

        mWindow.addOutput(RESOURCES.getString("youCanTestNow"));
    }

    private void writeTableToFile(FreqTable table)
    {
        if(mOutputDir == null)
        {
            mWindow.addLog(ERRORS.getString("outputFileMissing"));
            return;
        }

        List<String> csvStringLines = getTableAsCsvLines(table);
        File outputFile = new File(mOutputDir,table.getName()+".txt");

        try {
            FileHelper.writeToTextFile(outputFile,csvStringLines);
        } catch (IOException e) {
            e.printStackTrace();
            mWindow.addLog(ERRORS.getString("erroWhileWritingOutput"));
        }
    }

    private List<String> getTableAsCsvLines(FreqTable table)
    {
        List<String> lines = new ArrayList<>();
        StringBuilder tableCsvString = new StringBuilder();
        //DecimalFormat df = new DecimalFormat("#.##");

        Set<String> features = table.getAllFeatures();

        for(String feature:features)
            tableCsvString.append(feature+',');

        lines.add(tableCsvString.toString());
        //clear string builder when added new line
        tableCsvString.setLength(0);

        for(String document:table.getAllDocuments())
        {
            tableCsvString.append(document+',');
            Map<String,Double> featureFreqTable = table.getFreqTableWithFuture(document);
            for(String feature:features)
            {
                Double freq = featureFreqTable.get(feature);
                tableCsvString.append(String.valueOf(freq)+',');
            }

            tableCsvString.append(table.getDocCategory(document)+',');
            lines.add(tableCsvString.toString());
            tableCsvString.setLength(0);
        }

        return lines;
    }

    private void showTestResults(Map<String, Performance> categoryPerformanceTable) {
        StringBuilder sb = new StringBuilder();
        List<String> textFileLines = new ArrayList<>();
        for(String category:categoryPerformanceTable.keySet())
        {
            Performance performance = categoryPerformanceTable.get(category);
            sb.append(RESOURCES.getString("category")+category);
            sb.append(" | Precision : "+performance.calculatePrecision());
            sb.append(" | Recall : "+performance.calculateRecall());
            sb.append(" | F-Score : "+performance.calculateFScore());
            sb.append("\n");
            textFileLines.add(sb.toString());
            sb.setLength(0);
        }

        for(String s:textFileLines)
            mWindow.addOutput(s);

        if(mOutputDir!=null) {
            try {
                FileHelper.writeToTextFile(new File(mOutputDir,"Performance.txt"),textFileLines);
            } catch (IOException e) {
                e.printStackTrace();
                mWindow.addLog("Error while writing performance file : "+e.getMessage());
            }
        }
    }

    @Override
    public void deleteCategoryPressed(String name,boolean train) {
        int deleted = mClassifier.deleteCategory(name,train);
        if(deleted>0)
            mWindow.addLog(name+" Category delete success : "+deleted+" categories affected");
        else
            mWindow.addLog(ERRORS.getString("errorWhilecategoryDelete")+name);
    }

    @Override
    public void stopWordsBrowsePressed(File stopWordsFile) {
        if(!setStopWordsFile(stopWordsFile))
            mWindow.addLog(ERRORS.getString("cantReadStopWordsFile"));
        else
            mWindow.addLog("Stop words file loaded");
    }

    @Override
    public void outputDirBrowsePressed(String dirPath) {
        mOutputDir =dirPath;
    }

    @Override
    public void onTrainPressed() {
        mWindow.setEnabledAllComponents(false);
        mClassifier.startTrain();
        mWindow.addOutput(RESOURCES.getString("trainStarted"));
    }

    @Override
    public void onTestPressed() {
        mWindow.setEnabledAllComponents(false);
        mClassifier.startTest();
        mWindow.addOutput(RESOURCES.getString("testStarted"));
    }

    @Override
    public void onTrainFinished(FreqTable table) {
        mWindow.addOutput(RESOURCES.getString("trainFinished"));
        mWindow.setEnabledAllComponents(true);
        showTrainResult(table);
        writeTableToFile(table);

    }

    @Override
    public void onTestFinished(Map<String, Performance> categoryPerformanceTable) {
        mWindow.setEnabledAllComponents(true);
        mWindow.addOutput(RESOURCES.getString("testFinished"));
        showTestResults(categoryPerformanceTable);
    }

    @Override
    public void onAssignedCategory(String docName, String assignedCategory) {
        mWindow.addOutput(docName+" - "+assignedCategory+" kategorisine atandı");
    }

}
