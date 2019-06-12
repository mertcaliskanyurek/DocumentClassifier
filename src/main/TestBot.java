package main;

import main.controller.MainController;

import java.io.File;

public class TestBot {

    private static final  String trainDirEkonomi = "src/main/res/docs/odev-veriler/train/ekonomi/";
    private static final  String trainDirMagazin = "src/main/res/docs/odev-veriler/train/magazin/";
    private static final  String trainDirSaglik = "src/main/res/docs/odev-veriler/train/saglik/";
    private static final  String trainDirSpor = "src/main/res/docs/odev-veriler/train/spor/";

    private static final String[] trainDirs = {trainDirEkonomi,trainDirMagazin,trainDirSaglik,trainDirSpor};

    private static final  String testDirEkonomi = "src/main/res/docs/odev-veriler/test/ekonomi/";
    private static final  String testDirMagazin = "src/main/res/docs/odev-veriler/test/magazin/";
    private static final  String testDirSaglik = "src/main/res/docs/odev-veriler/test/saglik/";
    private static final  String testDirSpor = "src/main/res/docs/odev-veriler/test/spor/";

    private static final String[] testDirs = {testDirEkonomi,testDirMagazin,testDirSaglik,testDirSpor};

    public static final String defaultStopWordsFilePath = "src/main/res/docs/stop-words-turkish-github.txt";
    public static final String defaultOutputFilePath = "src/main/res/docs/";

    public static void run(MainController controller)
    {

        controller.stopWordsBrowsePressed(new File(defaultStopWordsFilePath));
        controller.outputDirBrowsePressed(defaultOutputFilePath);

        for(String dir:trainDirs)
        {
            File dirFile = new File(dir);
            controller.addCategoryPressed(dirFile.getName(),dirFile.listFiles(),true);
        }

        for(String dir:testDirs)
        {
            File dirFile = new File(dir);
            controller.addCategoryPressed(dirFile.getName(),dirFile.listFiles(),false);
        }

        controller.onTrainPressed();
    }
}
