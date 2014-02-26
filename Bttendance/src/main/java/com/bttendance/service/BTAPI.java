package com.bttendance.service;

import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.GradeJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.model.json.SerialJson;
import com.bttendance.model.json.UserJson;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public interface BTAPI {

    public static String ANDROID = "android";

    @GET("/user/signin")
    void signin(@Query("username") String username,
                @Query("password") String password,
                @Query("device_uuid") String uuid,
                Callback<UserJson> cb);

    @GET("/user/auto/signin")
    void autoSignin(@Query("username") String username,
                    @Query("password") String password,
                    @Query("device_uuid") String uuid,
                    Callback<UserJson> cb);

    @PUT("/user/forgot/password")
    void forgotPassword(@Query("email") String email,
                        Callback<UserJson> cb);

    @POST("/user/signup")
    void signup(@Query("username") String username,
                @Query("full_name") String fullName,
                @Query("email") String email,
                @Query("password") String password,
                @Query("device_type") String deviceType,
                @Query("device_uuid") String deviceUUID,
                Callback<UserJson> cb);

    @PUT("/user/update/notification_key")
    void updateNotificationKey(@Query("username") String username,
                               @Query("password") String password,
                               @Query("device_uuid") String deviceUUID,
                               @Query("notification_key") String notificationKey,
                               Callback<UserJson> cb);

    @PUT("/user/update/profile_image")
    void updateProfileImage(@Query("username") String username,
                            @Query("password") String password,
                            @Query("device_uuid") String deviceUUID,
                            @Query("profile_image") String profileImage,
                            Callback<UserJson> cb);

    @PUT("/user/update/email")
    void updateEmail(@Query("username") String username,
                     @Query("password") String password,
                     @Query("device_uuid") String deviceUUID,
                     @Query("email") String email,
                     Callback<UserJson> cb);

    @PUT("/user/update/full_name")
    void updateFullName(@Query("username") String username,
                        @Query("password") String password,
                        @Query("device_uuid") String deviceUUID,
                        @Query("full_name") String fullName,
                        Callback<UserJson> cb);

    @GET("/user/feed")
    void feed(@Query("username") String username,
              @Query("password") String password,
              @Query("page") int page,
              Callback<PostJson[]> cb);

    @GET("/user/courses")
    void courses(@Query("username") String username,
                 @Query("password") String password,
                 Callback<CourseJson[]> cb);

    @GET("/user/schools")
    void schools(@Query("username") String username,
                 @Query("password") String password,
                 Callback<SchoolJson[]> cb);

    @PUT("/user/attend/course")
    void attendCourse(@Query("username") String username,
                      @Query("password") String password,
                      @Query("course_id") int courseID,
                      Callback<UserJson> cb);

    @PUT("/user/employ/school")
    void employSchool(@Query("username") String username,
                      @Query("password") String password,
                      @Query("school_id") int schoolID,
                      @Query("serial") String serial,
                      Callback<UserJson> cb);

    @PUT("/user/enroll/school")
    void enrollSchool(@Query("username") String username,
                      @Query("password") String password,
                      @Query("school_id") int schoolID,
                      Callback<UserJson> cb);

    @GET("/school/all")
    void allSchools(@Query("username") String username,
                    @Query("password") String password,
                    Callback<SchoolJson[]> cb);

    @GET("/school/courses")
    void schoolCourses(@Query("username") String username,
                       @Query("password") String password,
                       @Query("school_id") int schoolID,
                       Callback<CourseJson[]> cb);

    @POST("/course/create")
    void courseCreate(@Query("username") String username,
                      @Query("password") String password,
                      @Query("name") String name,
                      @Query("number") String number,
                      @Query("school_id") int schoolID,
                      @Query("professor_name") String profName,
                      Callback<CourseJson> cb);

    @GET("/course/feed")
    void courseFeed(@Query("username") String username,
                    @Query("password") String password,
                    @Query("course_id") int courseID,
                    @Query("page") int page,
                    Callback<PostJson[]> cb);

    @POST("/course/students")
    void courseStudents(@Query("username") String username,
                        @Query("password") String password,
                        @Query("course_id") int courseID,
                        Callback<UserJson[]> cb);

    @POST("/course/grades")
    void courseGrades(@Query("username") String username,
                      @Query("password") String password,
                      @Query("course_id") int courseID,
                      Callback<GradeJson[]> cb);

    @GET("/post/{id}")
    void post(@Query("username") String username,
              @Query("password") String password,
              @Path("id") int postId,
              Callback<PostJson> cb);

    @POST("/post/create")
    void postCreate(@Query("username") String username,
                    @Query("password") String password,
                    @Query("type") String type,
                    @Query("title") String title,
                    @Query("message") String message,
                    @Query("course_id") int courseID,
                    Callback<PostJson> cb);

    @POST("/post/attendance/start")
    void postAttendanceStart(@Query("username") String username,
                             @Query("password") String password,
                             @Query("course_id") int courseID,
                             Callback<CourseJson> cb);

    @PUT("/post/attendance/found/device")
    void postAttendanceFoundDevice(@Query("username") String username,
                                   @Query("password") String password,
                                   @Query("post_id") int postID,
                                   @Query("uuid") String uuid,
                                   Callback<PostJson> cb);

    @PUT("/post/attendance/check/manually")
    void postAttendanceCheckManually(@Query("username") String username,
                                     @Query("password") String password,
                                     @Query("post_id") int postID,
                                     @Query("user_id") int userId,
                                     Callback<PostJson> cb);

    @POST("/post/create/notice")
    void postCreateNotice(@Query("username") String username,
                          @Query("password") String password,
                          @Query("course_id") int courseID,
                          @Query("message") String message,
                          Callback<PostJson> cb);

    @GET("/serial/validate")
    void serialValidate(@Query("serial") String serial,
                        Callback<SchoolJson> cb);

    @POST("/serial/request")
    void serialRequest(@Query("email") String email,
                       Callback<SerialJson> cb);

}
