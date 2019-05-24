package main.classifier;

import main.model.Performance;
import main.model.FreqTable;

import java.util.Map;

public interface ClassifierListener {

    void onTrainFinished(FreqTable table);
    void onTestFinished(Map<String, Performance> categoryPerformanceTable);

    void onAssignedCategory(String docName,String assignedCategory);
}
