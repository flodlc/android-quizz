package com.quizz.services;

import android.content.Context;

import com.quizz.MyApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.quizz.entities.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Florian on 13/02/2018.
 */

public class UserManager {

    private static String FILENAME = "QEUser";

    public static String getData(String key) {
        File file = new File(MyApp.getContext().getFilesDir(), FILENAME);
        if (!file.exists()) {
            return "";
        }
        FileInputStream fis = null;
        try {
            fis = MyApp.getContext().openFileInput(FILENAME);
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

    private static FileOutputStream getFOS(String fileName) {
        FileOutputStream fos = null;
        try {
            fos = MyApp.getContext().openFileOutput(fileName, Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fos;
    }

    private static BufferedReader getBF(String fileName) {
        FileInputStream fis = null;
        try {
            fis = MyApp.getContext().openFileInput(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        return br;
    }

    public static void setData(String key, String data) throws IOException {
        MyApp.getContext().deleteFile("tempFile");
        FileOutputStream fos = getFOS(FILENAME);
        FileOutputStream tempFos = getFOS("tempFile");


        File file = new File(MyApp.getContext().getFilesDir(), FILENAME);
        if (!file.exists()) {
            fos.write((key + "===" + data + "\n").getBytes());
            fos.close();
            tempFos.close();
            return;
        }

        BufferedReader br = getBF(FILENAME);
        BufferedReader tempBr = getBF("tempFile");

        String line;
        while ((line = br.readLine()) != null) {
            String[] array = line.split("===");
            if (!array[0].equals(key)) {
                tempFos.write((line + "\n").getBytes());
            }
        }

        MyApp.getContext().deleteFile(FILENAME);

        fos = getFOS(FILENAME);
        fos.write((key + "===" + data + "\n").getBytes());

        while ((line = tempBr.readLine()) != null) {
            fos.write((line + "\n").getBytes());
        }

        fos.close();
        tempFos.close();
        tempBr.close();
        br.close();
        MyApp.getContext().deleteFile("tempFile");
    }

    public static void saveUser(User user) {
        if (user != null) {
            try {
                UserManager.setData("username", user.getUsername());
                UserManager.setData("userId", String.valueOf(user.getId()));
                UserManager.setData("record", String.valueOf(user.getRecord() != null ? user.getRecord() : 0));

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static User getUSer() {
        String username;
        String userId;
        String record;
        if ((username = getData("username")) != "" && (userId = getData("userId")) != ""
                && (record = getData("record")) != "") {
            User user = new User();
            user.setUsername(username);
            user.setId(Integer.valueOf(userId));
            user.setRecord(Integer.valueOf(record));
            return user;
        } else {
            return null;
        }
    }

    public static void savePHPSESSID(Response response) {
        try {
            if (response.headers().get("Set-Cookie") != null) {
                String token = response.headers().get("Set-Cookie").substring(10,
                        response.headers().get("Set-Cookie").indexOf(";"));
                UserManager.setData("PHPSESSID", token);
            } else if (response.raw().priorResponse() != null
                    && response.raw().priorResponse().headers().toMultimap().get("Set-Cookie").get(0) != null) {

                String value = response.raw().priorResponse().headers().toMultimap().get("Set-Cookie").get(0);
                String token = value.substring(10,
                        value.indexOf(";"));
                UserManager.setData("PHPSESSID", token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveRecord(User user) {
        try {
            setData("record", String.valueOf(user.getRecord()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void checkDistantRecord(User user) {
        String record = UserManager.getData("record");
        if (!user.getRecord().equals(Integer.valueOf(record.equals("") ? "0" : record))) {
            user.setRecord(Integer.valueOf(record.equals("") ? "0" : record));

            ApiServiceInterface apiService = ApiService.getService();
            Call<User> call = apiService.editUser(user);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        }
    }
}
