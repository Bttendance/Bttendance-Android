package com.bttendance.service;

import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.PreferencesJson;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.service.request.CourseFindRequest;
import com.bttendance.service.request.CoursePostRequest;
import com.bttendance.service.request.LogInRequest;
import com.bttendance.service.request.PasswordResetRequest;
import com.bttendance.service.request.PreferencesPutRequest;
import com.bttendance.service.request.SchoolPostRequest;
import com.bttendance.service.request.SchoolSearchRequest;
import com.bttendance.service.request.UserFindRequest;
import com.bttendance.service.request.UserPostRequest;
import com.bttendance.service.request.UserPutRequest;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public interface BTAPI {

    /**
     * Enums
     */
    public enum AttendanceUserState {
        attended, tardy, absent, claimed
    }

    public enum ClickerPrivacy {all, none, professor}

    public enum ClickerType {ox, star, mult2, mult3, mult4, mult5, essay}

    public enum ClickerChoiceChoice {o, x, star1, star2, star3, star4, star5, a, b, c, d, e, text}

    public enum CourseUserState {supervising, attending, dropped, kicked}

    public enum DevicePlatform {ios, android, xiaomi}

    public enum ScheduleDayOfWeek {mon, tue, wed, thu, fri, sat}

    public enum SchoolClassification {university, school, institute, other}

    public enum SchoolUserState {supervisor, student, administrator}

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
    void updateUser(@Path("id") int userId, @Body UserPutRequest body, Callback<UserJson> cb);

    @GET("/users/find")
    void findUser(@Body UserFindRequest body, Callback<UserJson> cb);

    /**
     * Preferences APIs
     */
    @GET("/users/{id}/preferences")
    void getPreferences(@Path("id") int userId, Callback<PreferencesJson> cb);

    @PUT("/users/{id}/preferences")
    void updatePreferences(@Path("id") int userId, @Body PreferencesPutRequest body, Callback<PreferencesJson> cb);

    /**
     * Courses APIs
     */
    @GET("/courses/find")
    void findCourse(@Body CourseFindRequest body, Callback<CourseJson> cb);

    @POST("/courses")
    void createCourse(@Body CoursePostRequest body, Callback<CourseJson> cb);

    @GET("/users/{id}/courses")
    void getMyCourses(@Path("id") int userId, Callback<CourseJson[]> cb);

    /**
     * Schools APIs
     */
    @GET("/schools")
    void schools(@Query("page") int page, Callback<SchoolJson[]> cb);

    @POST("/schools/search")
    void searchSchool(@Body SchoolSearchRequest body, Callback<SchoolJson[]> cb);

    @POST("/schools")
    void createSchool(@Body SchoolPostRequest body, Callback<SchoolJson> cb);

    @GET("/users/{id}/schools")
    void getMySchools(@Path("id") int userId, Callback<SchoolJson[]> cb);
}
