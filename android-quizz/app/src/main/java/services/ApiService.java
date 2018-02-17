package services;

import android.app.Activity;
import android.support.v7.app.AlertDialog;

import com.example.florian.app.R;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Florian on 25/01/2018.
 */

public class ApiService {
    public static ApiServiceInterface getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.41/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ApiServiceInterface.class);
    }

    public static void showErrorMessage(Activity activity) {
        if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.internetIssue);
        builder.setIcon(R.drawable.sablier);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}
