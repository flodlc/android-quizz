package com.quizz.services;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;

import com.quizz.MyApp;
import com.quizz.R;

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
                .baseUrl(MyApp.getContext().getResources().getString(R.string.baseUrl))
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

    public static void showErrorMessage(Activity activity, boolean finish) {
        showErrorMessage(activity);
        if (finish) {
            activity.finish();
        }
    }

    public static boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) MyApp.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnected() && isConnectedFast();
    }

    /**
     * Check if there is fast connectivity
     */
    public static boolean isConnectedFast(){
        ConnectivityManager
                cm = (ConnectivityManager) MyApp.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnected() && isConnectionFast(activeNetwork));
    }

    /**
     * Check if the connection is fast
     */
    public static boolean isConnectionFast(NetworkInfo networkInfo){
        int type = networkInfo.getType();
        int subType = networkInfo.getSubtype();

        if(type==ConnectivityManager.TYPE_WIFI){
            return true;
        }else if(type==ConnectivityManager.TYPE_MOBILE){
            switch(subType){
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
            /*
             * Above API level 7, make sure to set android:targetSdkVersion
             * to appropriate level to use these
             */
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                default:
                    return true;
            }
        }else{
            return false;
        }
    }

    public static boolean checkCode(Activity activity, retrofit2.Response response) {
        if (response.code() == 200) {
            return true;
        } else if (response.code() == 403) {
            RouterService.goConnectPageAndFinish(activity);
            return false;
        } else {
            ApiService.showErrorMessage(activity);
            return false;
        }
    }
}
