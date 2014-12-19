package com.bttendance.service;

import com.bttendance.model.json.UserJson;
import com.bttendance.service.request.UserPostRequest;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public interface BTAPI {

    public static String ANDROID = "android";

    /**
     * Users APIs
     */
    @POST("/users")
    void signup(@Body UserPostRequest body, Callback<UserJson> cb);
}
