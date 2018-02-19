package services;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.example.florian.app.MainActivity;
import com.example.florian.app.MyApp;
import com.example.florian.app.R;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Florian on 25/01/2018.
 */

public class ApiService {
    public static ApiServiceInterface getService() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.followRedirects(true);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                String token = UserManager.getData("PHPSESSID");

                Request request = original.newBuilder()
                        .header("Cookie", "PHPSESSID=" + token)
                        .method(original.method(), original.body())
                        .build();

                Response response = chain.proceed(request);
                return response;
            }
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.41/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
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

    public static boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) MyApp.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }
}
