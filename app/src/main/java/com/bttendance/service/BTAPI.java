package com.bttendance.service;

import com.bttendance.model.json.UserJson;
import com.bttendance.service.request.LogInRequest;
import com.bttendance.service.request.PasswordResetRequest;
import com.bttendance.service.request.UserPostRequest;
import com.squareup.okhttp.Response;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public interface BTAPI {

    public static String ANDROID = "android";

    /**
     * Users APIs
     */
    @POST("/users")
    void signUp(@Body UserPostRequest body, Callback<UserJson> cb);

    @POST("/users/login")
    void logIn(@Body LogInRequest body, Callback<UserJson> cb);

    @POST("/users/reset")
    void forgotPassword(@Body PasswordResetRequest body, Callback<Object> cb);

    @GET("/users/{id}")
    void autoSignIn(@Path("id") int userId, Callback<UserJson> cb);

    @PUT("/users/{id}")
    void updateUser(@Path("id") int userId, Callback<UserJson> cb);
}
