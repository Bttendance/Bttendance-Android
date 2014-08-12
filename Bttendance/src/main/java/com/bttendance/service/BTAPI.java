package com.bttendance.service;

import com.bttendance.model.json.AttendanceJson;
import com.bttendance.model.json.ClickerJson;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.EmailJson;
import com.bttendance.model.json.NoticeJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.QuestionJson;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.model.json.UserJsonSimple;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public interface BTAPI {

    public static String ANDROID = "android";

    /**
     * User APIs
     */
    @POST("/users/signup")
    void signup(@Query("full_name") String fullName,
                @Query("email") String email,
                @Query("password") String password,
                @Query("device_type") String deviceType,
                @Query("device_uuid") String deviceUUID,
                @Query("locale") String locale,
                Callback<UserJson> cb);

    @GET("/users/auto/signin")
    void autoSignin(@Query("email") String email,
                    @Query("password") String password,
                    @Query("locale") String locale,
                    @Query("device_uuid") String deviceUUID,
                    @Query("device_type") String deviceType,
                    @Query("app_version") String app_version,
                    Callback<UserJson> cb);

    @GET("/users/signin")
    void signin(@Query("email") String email,
                @Query("password") String password,
                @Query("locale") String locale,
                @Query("device_uuid") String deviceUUID,
                @Query("device_type") String deviceType,
                Callback<UserJson> cb);

    @PUT("/users/forgot/password")
    void forgotPassword(@Query("email") String email,
                        @Query("locale") String locale,
                        Callback<EmailJson> cb);

    @PUT("/users/update/password")
    void updatePassword(@Query("email") String email,
                        @Query("password") String password,
                        @Query("locale") String locale,
                        @Query("password_old") String passwordOld,
                        @Query("password_new") String passwordNew,
                        Callback<UserJson> cb);

    @PUT("/users/update/full_name")
    void updateFullName(@Query("email") String email,
                        @Query("password") String password,
                        @Query("locale") String locale,
                        @Query("full_name") String fullName,
                        Callback<UserJson> cb);

    @PUT("/users/update/email")
    void updateEmail(@Query("email") String email,
                     @Query("password") String password,
                     @Query("locale") String locale,
                     @Query("email_new") String emailNew,
                     Callback<UserJson> cb);

    @GET("/users/search")
    void searchUser(@Query("email") String email,
                    @Query("password") String password,
                    @Query("locale") String locale,
                    @Query("search_id") String searchID,
                    Callback<UserJsonSimple> cb);

    @GET("/users/courses")
    void courses(@Query("email") String email,
                 @Query("password") String password,
                 @Query("locale") String locale,
                 Callback<CourseJson[]> cb);

    /**
     * Device APIs
     */
    @PUT("/devices/update/notification_key")
    void updateNotificationKey(@Query("email") String email,
                               @Query("password") String password,
                               @Query("locale") String locale,
                               @Query("device_uuid") String deviceUUID,
                               @Query("notification_key") String notificationKey,
                               Callback<UserJson> cb);

    /**
     * Setting APIs
     */
    @PUT("/settings/update/attendance")
    void updateSettingAttendance(@Query("email") String email,
                                 @Query("password") String password,
                                 @Query("locale") String locale,
                                 @Query("attendance") boolean attendance,
                                 Callback<UserJson> cb);

    @PUT("/settings/update/clicker")
    void updateSettingClicker(@Query("email") String email,
                              @Query("password") String password,
                              @Query("locale") String locale,
                              @Query("clicker") boolean clicker,
                              Callback<UserJson> cb);

    @PUT("/settings/update/notice")
    void updateSettingNotice(@Query("email") String email,
                             @Query("password") String password,
                             @Query("locale") String locale,
                             @Query("notice") boolean notice,
                             Callback<UserJson> cb);

    /**
     * Question APIs
     */
    @GET("/questions/mine")
    void myQuestions(@Query("email") String email,
                     @Query("password") String password,
                     @Query("locale") String locale,
                     Callback<QuestionJson[]> cb);

    @POST("/questions/create")
    void createQuestion(@Query("email") String email,
                        @Query("password") String password,
                        @Query("locale") String locale,
                        @Query("message") String message,
                        @Query("choice_count") int choiceCount,
                        Callback<QuestionJson> cb);

    @PUT("/questions/edit")
    void updateQuestion(@Query("email") String email,
                        @Query("password") String password,
                        @Query("locale") String locale,
                        @Query("question_id") int questionID,
                        @Query("message") String message,
                        @Query("choice_count") int choiceCount,
                        Callback<QuestionJson> cb);

    @DELETE("/questions/remove")
    void removeQuestion(@Query("email") String email,
                        @Query("password") String password,
                        @Query("locale") String locale,
                        @Query("question_id") int questionID,
                        Callback<QuestionJson> cb);

    /**
     * Identification APIs
     */
    @PUT("/identifications/update/identity")
    void updateIdentity(@Query("email") String email,
                        @Query("password") String password,
                        @Query("locale") String locale,
                        @Query("school_id") int schoolID,
                        @Query("identify") String identify,
                        Callback<UserJson> cb);

    /**
     * School APIs
     */
    @POST("/schools/create")
    void createSchool(@Query("email") String email,
                      @Query("password") String password,
                      @Query("locale") String locale,
                      @Query("name") String name,
                      @Query("type") String type,
                      Callback<SchoolJson> cb);

    @GET("/schools/all")
    void allSchools(@Query("email") String email,
                    @Query("password") String password,
                    @Query("locale") String locale,
                    Callback<SchoolJson[]> cb);

    @PUT("/schools/enroll")
    void enrollSchool(@Query("email") String email,
                      @Query("password") String password,
                      @Query("locale") String locale,
                      @Query("school_id") int schoolID,
                      @Query("identity") String identity,
                      Callback<UserJson> cb);

    /**
     * Course APIs
     */
    @POST("/courses/create/instant")
    void courseCreate(@Query("email") String email,
                      @Query("password") String password,
                      @Query("locale") String locale,
                      @Query("name") String name,
                      @Query("school_id") int schoolID,
                      @Query("professor_name") String profName,
                      Callback<UserJson> cb);

    @GET("/courses/search")
    void courseSearch(@Query("email") String email,
                      @Query("password") String password,
                      @Query("locale") String locale,
                      @Query("course_id") int courseID,
                      @Query("course_code") String courseCode,
                      Callback<CourseJson> cb);

    @PUT("/courses/attend")
    void attendCourse(@Query("email") String email,
                      @Query("password") String password,
                      @Query("locale") String locale,
                      @Query("course_id") int courseID,
                      Callback<UserJson> cb);

    @PUT("/courses/dettend")
    void dettendCourse(@Query("email") String email,
                       @Query("password") String password,
                       @Query("locale") String locale,
                       @Query("course_id") int courseID,
                       Callback<UserJson> cb);

    @GET("/courses/feed")
    void courseFeed(@Query("email") String email,
                    @Query("password") String password,
                    @Query("locale") String locale,
                    @Query("course_id") int courseID,
                    @Query("page") int page,
                    Callback<PostJson[]> cb);

    @PUT("/courses/open")
    void openCourse(@Query("email") String email,
                    @Query("password") String password,
                    @Query("locale") String locale,
                    @Query("course_id") int courseID,
                    Callback<UserJson> cb);

    @PUT("/courses/close")
    void closeCourse(@Query("email") String email,
                     @Query("password") String password,
                     @Query("locale") String locale,
                     @Query("course_id") int courseID,
                     Callback<UserJson> cb);

    @PUT("/courses/add/manager")
    void addManager(@Query("email") String email,
                    @Query("password") String password,
                    @Query("locale") String locale,
                    @Query("manager") String manager,
                    @Query("course_id") int courseID,
                    Callback<CourseJson> cb);

    @GET("/courses/students")
    void courseStudents(@Query("email") String email,
                        @Query("password") String password,
                        @Query("locale") String locale,
                        @Query("course_id") int courseID,
                        Callback<UserJsonSimple[]> cb);

    @GET("/courses/attendance/grades")
    void courseAttendanceGrades(@Query("email") String email,
                                @Query("password") String password,
                                @Query("locale") String locale,
                                @Query("course_id") int courseID,
                                Callback<UserJsonSimple[]> cb);

    @GET("/courses/clicker/grades")
    void courseClickerGrades(@Query("email") String email,
                             @Query("password") String password,
                             @Query("locale") String locale,
                             @Query("course_id") int courseID,
                             Callback<UserJsonSimple[]> cb);

    @GET("/courses/grades")
    void courseGrades(@Query("email") String email,
                      @Query("password") String password,
                      @Query("locale") String locale,
                      @Query("course_id") int courseID,
                      Callback<UserJsonSimple[]> cb);

    @PUT("/courses/export/grades")
    void courseExportGrades(@Query("email") String email,
                            @Query("password") String password,
                            @Query("locale") String locale,
                            @Query("course_id") int courseID,
                            Callback<EmailJson> cb);

    /**
     * Post APIs
     */
    @POST("/posts/start/attendance")
    void postStartAttendance(@Query("email") String email,
                             @Query("password") String password,
                             @Query("locale") String locale,
                             @Query("course_id") int courseID,
                             @Query("type") String type,
                             Callback<PostJson> cb);

    @POST("/posts/start/clicker")
    void postStartClicker(@Query("email") String email,
                          @Query("password") String password,
                          @Query("locale") String locale,
                          @Query("course_id") int courseID,
                          @Query("message") String message,
                          @Query("choice_count") int choiceCount,
                          Callback<PostJson> cb);

    @POST("/posts/create/notice")
    void postCreateNotice(@Query("email") String email,
                          @Query("password") String password,
                          @Query("locale") String locale,
                          @Query("course_id") int courseID,
                          @Query("message") String message,
                          Callback<PostJson> cb);

    @PUT("/posts/update/message")
    void postUpdateMessage(@Query("email") String email,
                           @Query("password") String password,
                           @Query("locale") String locale,
                           @Query("post_id") int postID,
                           @Query("message") String message,
                           Callback<PostJson> cb);

    @DELETE("/posts/remove")
    void postRemove(@Query("email") String email,
                    @Query("password") String password,
                    @Query("locale") String locale,
                    @Query("post_id") int postID,
                    Callback<PostJson> cb);

    /**
     * Attendance APIs
     */
    @GET("/attendances/from/courses")
    void attendancesFromCourses(@Query("email") String email,
                                @Query("password") String password,
                                @Query("locale") String locale,
                                @Query("course_ids") int[] courseIDs,
                                Callback<int[]> cb);

    @PUT("/attendances/found/device")
    void attendanceFoundDevice(@Query("email") String email,
                               @Query("password") String password,
                               @Query("locale") String locale,
                               @Query("attendance_id") int attendanceID,
                               @Query("uuid") String uuid,
                               Callback<AttendanceJson> cb);

    @PUT("/attendances/toggle/manually")
    void attendanceToggleManually(@Query("email") String email,
                                  @Query("password") String password,
                                  @Query("locale") String locale,
                                  @Query("attendance_id") int attendanceID,
                                  @Query("user_id") int userId,
                                  Callback<AttendanceJson> cb);

    /**
     * Clicker APIs
     */
    @PUT("/clickers/click")
    void clickerClick(@Query("email") String email,
                      @Query("password") String password,
                      @Query("locale") String locale,
                      @Query("clicker_id") int clickerID,
                      @Query("choice_number") int choice,
                      Callback<ClickerJson> cb);

    /**
     * Notice APIs
     */
    @PUT("/notices/seen")
    void seenNotice(@Query("email") String email,
                    @Query("password") String password,
                    @Query("locale") String locale,
                    @Query("notice_id") int noticeID,
                    Callback<NoticeJson> cb);
}
