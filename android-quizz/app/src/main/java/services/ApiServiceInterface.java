package services;

import java.util.ArrayList;
import java.util.Map;

import entities.Answer;
import entities.Game;
import entities.GameData;
import entities.GameResult;
import entities.PostAnswers;
import entities.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by Florian on 05/01/2018.
 */
public interface ApiServiceInterface {
    @GET("user")
    Call<User> getUser(@QueryMap Map<String, String> param);

    @GET("game")
    Call<GameData> getNewGame(@QueryMap Map<String, String> param);

    @GET("game/status")
    Call<GameData> checkNewGame(@QueryMap Map<String, String> param);

    @POST("response/")
    Call<GameResult> postAnswers(@Body PostAnswers postAnswers);

    @GET("game/current")
    Call<GameData> getCurrentGame(@QueryMap Map<String, String> param);

    @DELETE("game/{id}")
    Call<Boolean> deleteGame(@Path("id") String gameId);
}
