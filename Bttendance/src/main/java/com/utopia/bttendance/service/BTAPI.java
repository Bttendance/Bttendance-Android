package com.utopia.bttendance.service;

import com.utopia.bttendance.model.json.UserJson;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public interface BTAPI {

    @GET("/user/signin")
    void signin(@Query("username") String username, @Query("password") String password, Callback<UserJson> cb);

    @POST("/user/signup")
    void signup(@Body UserJson user, Callback<UserJson> cb);
}