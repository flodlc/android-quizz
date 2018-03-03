package com.quizz.services;

import java.util.List;
import java.util.Map;

import com.quizz.entities.Game;
import com.quizz.entities.GameData;
import com.quizz.entities.GameResult;
import com.quizz.entities.OfflineGame;
import com.quizz.entities.Invitation;
import com.quizz.entities.InvitationSend;
import com.quizz.entities.PostAnswers;
import com.quizz.entities.Question;
import com.quizz.entities.Stat;
import com.quizz.entities.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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

    @GET("game/nbCurrent")
    Call<Integer> getnbCurrentGame();

    @DELETE("game/{id}")
    Call<Boolean> deleteGame(@Path("id") String gameId);

    @GET("question/all")
    Call<List<Question>> getAllQuestions(@QueryMap Map<String, String> param);

    @FormUrlEncoded
    @POST("login_check")
    Call<User> connect(@Field("_username") String username, @Field("_password") String password);

    @POST("createUser")
    Call<Boolean> createUser(@Body User user);

    @GET("game/all")
    Call<List<Game>> getMyGames();

    @GET("logout")
    Call<Boolean> logout();

    @POST("editUser")
    Call<User> editUser(@Body User user);

    @POST("offGame/create")
    Call<String> createOffGame(@Body OfflineGame offlineGame);

    @GET("stats")
    Call<Stat> getStats();

    @GET("invitation/all")
    Call<List<Invitation>> getMyInvitations();

    @GET("invitation/me")
    Call<Integer> getNbInvitations();

    @GET("invitation/accept")
    Call<GameData> acceptInvitation(@QueryMap Map<String, String> param);

    @GET("invitation/refuse")
    Call<Boolean> refuseInvitation(@QueryMap Map<String, String> param);

    @POST("invitation/")
    Call<Invitation> sendInvitation(@Body InvitationSend invitationSend);

    @GET("user/findByText/{text}")
    Call<List<User>> findUsersByText(@Path("text") String text);

    @POST("question/create")
    Call<Boolean> createQuestion(@Body Question[] question);
}
