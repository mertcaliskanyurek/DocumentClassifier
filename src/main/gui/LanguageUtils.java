package main.gui;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class LanguageUtils {

    private static final String MAIN_FRAME_PATH = "main/res/MainFrame.properties";
    private static final String OUTPUT_PATH = "main/res/Output.properties";
    private static final String ERROR_PATH = "main/res/Error.properties";


    public static final int PROPS_MAIN_FRAME = 0;
    public static final int PROPS_MAIN_OUTPUT = 1;
    public static final int PROPS_MAIN_ERROR = 2;

    public static Properties getProperties(Class _class,int properties)
    {
        InputStream utf8is;

        switch (properties)
        {
            case PROPS_MAIN_FRAME:
                utf8is = _class.getClassLoader().getResourceAsStream(MAIN_FRAME_PATH);
                break;
            case PROPS_MAIN_OUTPUT:
                utf8is = _class.getClassLoader().getResourceAsStream(OUTPUT_PATH);
                break;
            case PROPS_MAIN_ERROR:
                utf8is =  _class.getClassLoader().getResourceAsStream(ERROR_PATH);
                break;

                default:
                    return null;
        }

        if(utf8is == null)
            return null;

        Properties mainFrameProps;
        Reader reader = null;
        try {
            reader = new InputStreamReader(utf8is, StandardCharsets.UTF_8);
            mainFrameProps = new Properties();
            mainFrameProps.load(reader);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return mainFrameProps;
    }
}
