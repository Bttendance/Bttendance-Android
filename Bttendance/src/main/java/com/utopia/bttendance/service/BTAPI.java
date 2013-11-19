package com.utopia.bttendance.service;

import com.utopia.bttendance.model.json.User;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public interface BTAPI {

    @GET("/user/signin")
    void signin(@Query("username") String username, @Query("password") String password, Callback<User> cb);
}
