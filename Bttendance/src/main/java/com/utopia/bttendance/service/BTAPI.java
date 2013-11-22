package com.utopia.bttendance.service;

import com.utopia.bttendance.model.json.UserJson;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public interface BTAPI {

    @GET("/user/signin")
    void signin(@Query("username") String username,
                @Query("password") String password,
                @Query("device_uuid") String uuid,
                Callback<UserJson> cb);

    @POST("/user/signup")
    void signup(@Query("username") String username,
                @Query("full_name") String fullName,
                @Query("email") String email,
                @Query("password") String password,
                @Query("device_type") String type,
                @Query("device_uuid") String uuid,
                Callback<UserJson> cb);

}
