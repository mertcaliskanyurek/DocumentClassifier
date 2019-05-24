package main.gui;

import java.io.File;

public interface MainFrameListener {
	
	void addCategoryPressed(String name,File[] documents,boolean train);
	
	void deleteCategoryPressed(String name,boolean train);
	
	void stopWordsBrowsePressed(File stopWordsFile);
	
	void outputDirBrowsePressed(String dirPath);
	
	void onTrainPressed();
	void onTestPressed();
	
}
