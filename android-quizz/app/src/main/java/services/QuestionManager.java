package services;

import android.app.Activity;
import android.content.Context;

import com.example.florian.app.MyApp;
import com.example.florian.app.offline.OffLineQuizzActivity;

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
    Activity activity;

    public QuestionManager(Activity activity) {
        this.activity = activity;
    }

    private int getNbLines() throws IOException {
        FileInputStream fis = null;

        try {
            fis = activity.openFileInput(FILENAME);
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

    public Question getRandomQuestion() throws IOException {
        File file = new File(MyApp.getContext().getFilesDir(), FILENAME);
        if (!file.exists()) {
            return null;
        }
        FileInputStream fis = null;

        try {
            fis = activity.openFileInput(FILENAME);
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
        return new Question(array[0], array[1], array[2], array[3], array[4], array[5]);
    }

    public void writeInFile(Question question) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = activity.openFileOutput(FILENAME, Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write((question.getQuestion() + "===" + question.getResponseA() + "===" +
                    question.getResponseB() + "===" + question.getResponseC() + "===" +
                    question.getResponseD() + "===" + question.getAnswer() + "\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        fos.close();
    }

    public static void getOfflineQuestions(Activity activity, final OffLineQuizzActivity offLineQuizzActivity) {
        //For test
        activity.deleteFile(FILENAME);

        File file = new File(activity.getFilesDir(), FILENAME);
        if (file.exists()) {
            return;
        }

        final QuestionManager qm = new QuestionManager(activity);

        ApiServiceInterface apiService = ApiService.getService();
        Call<List<Question>> call = apiService.getAllQuestions();
        call.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                List<Question> questions = response.body();
                for (Question question : questions) {
                    try {
                        qm.writeInFile(question);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (!offLineQuizzActivity.isDestroyed()) {
                    offLineQuizzActivity.displayQuestion();
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {

            }
        });
    }
}
