package services;

import android.app.Activity;
import android.content.Context;

import com.example.florian.app.MyApp;
import com.example.florian.app.offline.OffLineQuizzActivity;
import com.google.common.collect.ImmutableMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

import entities.Question;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Florian on 13/02/2018.
 */

public class QuestionManager {
    static String FILENAME = "QEQuestions";

    private static int getNbLines() throws IOException {
        File file = new File(MyApp.getContext().getFilesDir(), FILENAME);
        if (!file.exists()) {
            return 0;
        }
        FileInputStream fis = null;

        try {
            fis = MyApp.getContext().openFileInput(FILENAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        int count = 0;
        while (br.readLine() != null) {
            count++;
        }
        return count;
    }

    public static int getLastId() {
        File file = new File(MyApp.getContext().getFilesDir(), FILENAME);
        if (!file.exists()) {
            return 0;
        }
        FileInputStream fis = null;

        try {
            fis = MyApp.getContext().openFileInput(FILENAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line;
        String lastLine = "0===0";
        try {
            while ((line = br.readLine()) != null) {
                lastLine = line;
            }
            return Integer.valueOf(lastLine.split("===")[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Question getRandomQuestion() throws IOException {
        File file = new File(MyApp.getContext().getFilesDir(), FILENAME);
        if (!file.exists()) {
            return null;
        }
        FileInputStream fis = null;

        try {
            fis = MyApp.getContext().openFileInput(FILENAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        Random rd = new Random();
        int nbLine = getNbLines();
        if (nbLine == 0) {
            return null;
        }
        int randomInt = rd.nextInt(nbLine) + 1;
        int count = 1;
        String line = "";
        while (count <= randomInt) {
            line = br.readLine();
            count++;
        }
        String[] array = line.split("===");
        fis.close();
        return new Question(Integer.valueOf(array[0]), array[1], array[2], array[3], array[4], array[5], array[6]);
    }

    private static void writeInFile(Question question) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = MyApp.getContext().openFileOutput(FILENAME, Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write((question.getId() + "===" + question.getQuestion() + "===" + question.getResponseA() + "===" +
                    question.getResponseB() + "===" + question.getResponseC() + "===" +
                    question.getResponseD() + "===" + question.getAnswer() + "\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        fos.close();
    }


    public static void getOfflineQuestions() {
        MyApp.getContext().deleteFile(FILENAME);

        File file = new File(MyApp.getContext().getFilesDir(), FILENAME);
        if (file.exists()) {
            return;
        }

        ApiServiceInterface apiService = ApiService.getService();
        Call<List<Question>> call = apiService
                .getAllQuestions(ImmutableMap.of("lastId", String.valueOf(QuestionManager.getLastId())));
        call.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                List<Question> questions = response.body();
                for (Question question : questions) {
                    try {
                        writeInFile(question);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {

            }
        });
    }
}
