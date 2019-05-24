package main.text_file_helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class FileHelper {

    public static boolean writeToTextFile(File f, List<String> lines) throws IOException {

        if(!f.exists())
            if(!f.createNewFile())
                return false;

        FileWriter fw = new FileWriter(f.toString());
        BufferedWriter bw = new BufferedWriter(fw);

        for (String s:lines) {
            bw.write(s);
            bw.newLine();
        }

        bw.flush();
        fw.close();
        bw.close();
        return true;
    }

	public static String readFromTextFile(File f) throws IOException {
        FileReader fileReader = new FileReader(f);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(f),"ISO-8859-9"));

        StringBuilder sb = new StringBuilder();
        String line = bufferedReader.readLine();

        while (line!=null)
        {
            sb.append(line+"\n");
            line = bufferedReader.readLine();
        }

        fileReader.close();
        bufferedReader.close();

        return sb.toString();

    }

}
