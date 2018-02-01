package services;

import java.util.ArrayList;
import java.util.Map;

import entities.Answer;
import entities.Game;
import entities.GameResult;
import entities.User;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by Florian on 05/01/2018.
 */
public interface ApiServiceInterface {
    @GET("user")
    Call<User> getUser(@QueryMap Map<String, String> param);

    @GET("game")
    Call<Game> getNewGame(@QueryMap Map<String, String> param);

    @POST("game")
    Call<GameResult> postAnswers(@Field("answers") ArrayList<Answer> answers,
                                 @Field("user") int user,
                                 @Field("game") int gameId);
}
