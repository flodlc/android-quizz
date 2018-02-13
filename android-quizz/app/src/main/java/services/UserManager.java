package services;

import android.app.Activity;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Florian on 13/02/2018.
 */

public class UserManager {

    private static String FILENAME = "QEUser";

    public static String getData(Activity activity, String key) {
        File file = new File(activity.getFilesDir(), FILENAME);
        if (!file.exists()) {
            return "";
        }
        FileInputStream fis = null;
        try {
            fis = activity.openFileInput(FILENAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line;
        try {
            while ((line = br.readLine()) != null) {
                String[] array = line.split("===");
                if (array[0].equals(key)) {
                    br.close();
                    return array[1];
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static FileOutputStream getFOS(Activity activity, String fileName) {
        FileOutputStream fos = null;
        try {
            fos = activity.openFileOutput(fileName, Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fos;
    }

    private static BufferedReader getBF(Activity activity, String fileName) {
        FileInputStream fis = null;
        try {
            fis = activity.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        return br;
    }

    public static void setData(Activity activity, String key, String data) throws IOException {
        activity.deleteFile("tempFile");
        FileOutputStream fos = getFOS(activity, FILENAME);
        FileOutputStream tempFos = getFOS(activity, "tempFile");


        File file = new File(activity.getFilesDir(), FILENAME);
        if (!file.exists()) {
            fos.write((key + "===" + data + "\n").getBytes());
            fos.close();
            tempFos.close();
            return;
        }

        BufferedReader br = getBF(activity, FILENAME);
        BufferedReader tempBr = getBF(activity, "tempFile");

        String line;
        while ((line = br.readLine()) != null) {
            String[] array = line.split("===");
            if (!array[0].equals(key)) {
                tempFos.write((line + "\n").getBytes());
            }
        }

        activity.deleteFile(FILENAME);

        fos = getFOS(activity, FILENAME);
        fos.write((key + "===" + data + "\n").getBytes());

        while ((line = tempBr.readLine()) != null) {
            fos.write((line + "\n").getBytes());
        }

        fos.close();
        tempFos.close();
        tempBr.close();
        br.close();
    }
}
