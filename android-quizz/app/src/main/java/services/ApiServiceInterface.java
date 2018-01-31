package services;

import com.google.gson.internal.LinkedTreeMap;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by Florian on 05/01/2018.
 */
public interface ApiServiceInterface {
    @GET("newGame")
    Call<LinkedTreeMap> postNewGame(@QueryMap Map<String, String> params);

    @GET("film/{id}")
    Call<LinkedTreeMap> getFilmByID(@Path("id") String id);
}
