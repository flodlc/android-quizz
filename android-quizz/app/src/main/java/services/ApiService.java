package services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Florian on 25/01/2018.
 */

public class ApiService {
    public static ApiServiceInterface getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://172.20.10.4/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ApiServiceInterface.class);
    }
}
