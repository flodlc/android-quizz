package services;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import entities.Answer;
import entities.Game;
import entities.GameData;
import entities.GameResult;
import entities.PostAnswers;
import entities.Question;
import entities.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by Florian on 05/01/2018.
 */
public interface ApiServiceInterface {
    @GET("user")
    Call<User> getUser();

    @GET("game")
    Call<GameData> getNewGame();

    @GET("game/status")
    Call<GameData> checkNewGame(@QueryMap Map<String, String> param);

    @POST("response/")
    Call<GameResult> postAnswers(@Body PostAnswers postAnswers);

    @GET("game/current")
    Call<GameData> getCurrentGame();

    @DELETE("game/{id}")
    Call<Boolean> deleteGame(@Path("id") String gameId);

    @GET("question/all")
    Call<List<Question>> getAllQuestions();

    @FormUrlEncoded
    @POST("login_check")
    Call<User> connect(@Field("_username") String username, @Field("_password") String password);

    @POST("createUser")
    Call<Boolean> createUser(@Body User user);

    @GET("game/all")
    Call<List<Game>> getMyGames();

    @POST("editUser")
    Call<User> editUser(@Body User user);
}
