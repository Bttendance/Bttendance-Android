package com.bttendance.service;

import com.bttendance.model.json.AttendanceJson;
import com.bttendance.model.json.ClickerJson;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.EmailJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.model.json.UserJsonSimple;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public interface BTAPI {

    public static String ANDROID = "android";

    @POST("/users/signup")
    void signup(@Query("username") String username,
                @Query("full_name") String fullName,
                @Query("email") String email,
                @Query("password") String password,
                @Query("device_type") String deviceType,
                @Query("device_uuid") String deviceUUID,
                Callback<UserJson> cb);

    @GET("/users/auto/signin")
    void autoSignin(@Query("username") String username,
                    @Query("password") String password,
                    @Query("device_uuid") String deviceUUID,
                    @Query("device_type") String deviceType,
                    @Query("app_version") String app_version,
                    Callback<UserJson> cb);

    @GET("/users/signin")
    void signin(@Query("username") String username,
                @Query("password") String password,
                @Query("device_uuid") String deviceUUID,
                Callback<UserJson> cb);

    @PUT("/users/forgot/password")
    void forgotPassword(@Query("email") String email,
                        Callback<EmailJson> cb);

    @PUT("/users/update/profile_image")
    void updateProfileImage(@Query("username") String username,
                            @Query("password") String password,
                            @Query("device_uuid") String deviceUUID,
                            @Query("profile_image") String profileImage,
                            Callback<UserJson> cb);

    @PUT("/users/update/full_name")
    void updateFullName(@Query("username") String username,
                        @Query("password") String password,
                        @Query("device_uuid") String deviceUUID,
                        @Query("full_name") String fullName,
                        Callback<UserJson> cb);

    @PUT("/users/update/email")
    void updateEmail(@Query("username") String username,
                     @Query("password") String password,
                     @Query("device_uuid") String deviceUUID,
                     @Query("email") String email,
                     Callback<UserJson> cb);

    @GET("/users/feed")
    void feed(@Query("username") String username,
              @Query("password") String password,
              @Query("page") int page,
              Callback<PostJson[]> cb);

    @GET("/users/courses")
    void courses(@Query("username") String username,
                 @Query("password") String password,
                 Callback<CourseJson[]> cb);

    @GET("/users/search")
    void searchUser(@Query("username") String username,
                    @Query("password") String password,
                    @Query("search_id") String searchID,
                    Callback<UserJsonSimple> cb);

    @PUT("/devices/update/notification_key")
    void updateNotificationKey(@Query("username") String username,
                               @Query("password") String password,
                               @Query("device_uuid") String deviceUUID,
                               @Query("notification_key") String notificationKey,
                               Callback<UserJson> cb);

    @GET("/schools/all")
    void allSchools(@Query("username") String username,
                    @Query("password") String password,
                    Callback<SchoolJson[]> cb);

    @GET("/schools/courses")
    void schoolCourses(@Query("username") String username,
                       @Query("password") String password,
                       @Query("school_id") int schoolID,
                       Callback<CourseJson[]> cb);

    @PUT("/schools/enroll")
    void enrollSchool(@Query("username") String username,
                      @Query("password") String password,
                      @Query("school_id") int schoolID,
                      @Query("student_id") String studentID,
                      Callback<UserJson> cb);

    @POST("/courses/create/request")
    void courseCreate(@Query("username") String username,
                      @Query("password") String password,
                      @Query("name") String name,
                      @Query("number") String number,
                      @Query("school_id") int schoolID,
                      @Query("professor_name") String profName,
                      Callback<EmailJson> cb);

    @PUT("/courses/attend")
    void attendCourse(@Query("username") String username,
                      @Query("password") String password,
                      @Query("course_id") int courseID,
                      Callback<UserJson> cb);

    @PUT("/courses/dettend")
    void dettendCourse(@Query("username") String username,
                       @Query("password") String password,
                       @Query("course_id") int courseID,
                       Callback<UserJson> cb);

    @GET("/courses/feed")
    void courseFeed(@Query("username") String username,
                    @Query("password") String password,
                    @Query("course_id") int courseID,
                    @Query("page") int page,
                    Callback<PostJson[]> cb);

    @GET("/courses/students")
    void courseStudents(@Query("username") String username,
                        @Query("password") String password,
                        @Query("course_id") int courseID,
                        Callback<UserJsonSimple[]> cb);

    @PUT("/courses/add/manager")
    void addManager(@Query("username") String username,
                    @Query("password") String password,
                    @Query("manager") String manager,
                    @Query("course_id") int courseID,
                    Callback<CourseJson> cb);

    @GET("/courses/grades")
    void courseGrades(@Query("username") String username,
                      @Query("password") String password,
                      @Query("course_id") int courseID,
                      Callback<UserJsonSimple[]> cb);

    @PUT("/courses/export/grades")
    void courseExportGrades(@Query("username") String username,
                            @Query("password") String password,
                            @Query("course_id") int courseID,
                            Callback<EmailJson> cb);

    @POST("/posts/start/attendance")
    void postStartAttendance(@Query("username") String username,
                             @Query("password") String password,
                             @Query("course_id") int courseID,
                             Callback<PostJson> cb);

    @POST("/posts/start/clicker")
    void postStartClicker(@Query("username") String username,
                          @Query("password") String password,
                          @Query("course_id") int courseID,
                          @Query("message") String message,
                          @Query("choice_count") int choiceCount,
                          Callback<PostJson> cb);

    @POST("/posts/create/notice")
    void postCreateNotice(@Query("username") String username,
                          @Query("password") String password,
                          @Query("course_id") int courseID,
                          @Query("message") String message,
                          Callback<PostJson> cb);

    @GET("/attendances/from/courses")
    void attendancesFromCourses(@Query("username") String username,
                                @Query("password") String password,
                                @Query("course_ids") int[] courseIDs,
                                Callback<int[]> cb);

    @PUT("/attendances/found/device")
    void attendanceFoundDevice(@Query("username") String username,
                               @Query("password") String password,
                               @Query("attendance_id") int attendanceID,
                               @Query("uuid") String uuid,
                               Callback<AttendanceJson> cb);

    @PUT("/attendances/check/manually")
    void attendanceCheckManually(@Query("username") String username,
                                 @Query("password") String password,
                                 @Query("attendance_id") int attendanceID,
                                 @Query("user_id") int userId,
                                 Callback<AttendanceJson> cb);

    @PUT("/clickers/connect")
    void clickerConnect(@Query("username") String username,
                        @Query("password") String password,
                        @Query("clicker_id") int clickerID,
                        @Query("socket_id") String socketID,
                        Callback<ClickerJson> cb);

    @PUT("/clickers/click")
    void clickerClick(@Query("username") String username,
                      @Query("password") String password,
                      @Query("clicker_id") int clickerID,
                      @Query("choice_number") int choice,
                      Callback<ClickerJson> cb);
}
