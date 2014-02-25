package com.bttendance.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;

import com.bttendance.BTDebug;
import com.bttendance.event.attendance.AttdEndEvent;
import com.bttendance.helper.BluetoothHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.ErrorsJson;
import com.bttendance.model.json.GradeJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.model.json.SerialJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.view.BeautiToast;
import com.squareup.otto.BTEventBus;

import java.util.HashSet;
import java.util.Set;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 11. 9..
 */
public class BTService extends Service {

    private static final String SERVER_DOMAIN_PRODUCTION = "http://www.bttd.co";
    private static final String SERVER_DOMAIN_TEST = "http://bttendance-dev.herokuapp.com";
    private static String getServerDomain() {
        if (!BTDebug.DEBUG)
            return SERVER_DOMAIN_PRODUCTION;
        else
            return SERVER_DOMAIN_TEST;
    }
    private static RestAdapter mRestAdapter = new RestAdapter.Builder()
            .setLog(new RestAdapter.Log() {
                @Override
                public void log(String log) {
                    if (log != null) {
                        if (log.contains("<--- HTTP") || log.contains("---> HTTP"))
                            BTDebug.LogQueryAPI(log);
                        else if (log.contains("createdAt"))
                            BTDebug.LogResponseAPI(log);
                    }
                }
            })
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setServer(getServerDomain() + "/api")
            .build();
    private BTAPI mBTAPI;
    private ConnectivityManager mConnectivityManager;
    private LocalBinder mBinder = new LocalBinder();
    private Thread mAttendanceThread;

    public static void bind(Context context, ServiceConnection connection) {
        Intent intent = new Intent(context, BTService.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public static void unbind(Context context, ServiceConnection connection) {
        context.unbindService(connection);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mBTAPI = mRestAdapter.create(BTAPI.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void attendanceStart() {

        if (mAttendanceThread != null)
            mAttendanceThread.interrupt();
        mAttendanceThread = null;

        mAttendanceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BTDebug.LogError("TimeLeft : " + BTTable.getAttdChekingLeftTime());
                    for (int i = 0; i < (int) (BTTable.getAttdChekingLeftTime() / 3000); i++) {
                        BluetoothHelper.startDiscovery();
                        Thread.sleep(3000);
                        Set<Integer> ids = BTTable.getCheckingPostIds();
                        Set<String> list = new HashSet<String>();
                        for (String mac : BTTable.UUIDLIST())
                            list.add(mac);

                        for (int id : ids) {
                            for (String mac : list) {
                                if (!BTTable.UUIDLISTSENDED_contains(mac)) {
                                    postAttendanceFoundDevice(id, mac, new Callback<PostJson>() {
                                        @Override
                                        public void success(PostJson post, Response response) {
                                            if (post != null)
                                                BTDebug.LogInfo(post.toJson());
                                        }

                                        @Override
                                        public void failure(RetrofitError retrofitError) {
                                        }
                                    });
                                }
                            }
                        }
                        BTTable.UUIDLISTSENDED_addAll(list);
                        BTTable.UUIDLIST_refresh();
                    }
                    BTTable.UUIDLISTSENDED_refresh();
                    attendanceStop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        mAttendanceThread.start();
    }

    public void attendanceStop() {
        if (mAttendanceThread != null)
            mAttendanceThread.interrupt();
        mAttendanceThread = null;
        BTEventBus.getInstance().post(new AttdEndEvent());
    }

    public void signin(String username, String password, String uuid, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.signin(username, password, uuid, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                BTDebug.LogInfo(user.toJson());
                BTPreference.setUser(getApplicationContext(), user);
                if (cb != null)
                    cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void autoSignin(final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.autoSignin(user.username, user.password, user.device_uuid, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                BTDebug.LogInfo(user.toJson());
                BTPreference.setUser(getApplicationContext(), user);
                if (cb != null)
                    cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void forgotPassword(String email, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.forgotPassword(email, new Callback<UserJson>() {
            @Override
            public void success(UserJson validation, Response response) {
                cb.success(validation, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void signup(UserJson user, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.signup(
                user.username,
                user.full_name,
                user.email,
                user.password,
                user.device_type,
                user.device_uuid,
                new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
                        BTDebug.LogInfo(user.toJson());
                        BTPreference.setUser(getApplicationContext(), user);
                        if (cb != null)
                            cb.success(user, response);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    public void updateNotificationKey(String notificationKey, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.updateNotificationKey(user.username, user.password, user.device_uuid, notificationKey, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                BTDebug.LogInfo(user.toJson());
                BTPreference.setUser(getApplicationContext(), user);
                if (cb != null)
                    cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void updateProfileImage(String profileImage, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.updateProfileImage(user.username, user.password, user.device_uuid, profileImage, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                BTDebug.LogInfo(user.toJson());
                BTPreference.setUser(getApplicationContext(), user);
                if (cb != null)
                    cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void updateEmail(String email, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.updateEmail(user.username, user.password, user.device_uuid, email, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                BTDebug.LogInfo(user.toJson());
                BTPreference.setUser(getApplicationContext(), user);
                if (cb != null)
                    cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void updateFullName(String fullName, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.updateFullName(user.username, user.password, user.device_uuid, fullName, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                BTDebug.LogInfo(user.toJson());
                BTPreference.setUser(getApplicationContext(), user);
                if (cb != null)
                    cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void feed(int page, final Callback<PostJson[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.feed(user.username, user.password, page, new Callback<PostJson[]>() {
            @Override
            public void success(PostJson[] posts, Response response) {
                for (PostJson post : posts) {
                    BTTable.PostTable.append(post.id, post);
                    BTTable.getPosts(BTTable.FILTER_MY_POST).append(post.id, post);
                }
                if (cb != null)
                    cb.success(posts, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void courses(final Callback<CourseJson[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.courses(user.username, user.password, new Callback<CourseJson[]>() {
            @Override
            public void success(CourseJson[] courses, Response response) {
                for (CourseJson course : courses) {
                    BTTable.CourseTable.append(course.id, course);
                    BTTable.getCourses(BTTable.FILTER_MY_COURSE).append(course.id, course);
                }
                if (cb != null)
                    cb.success(courses, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void schools(final Callback<SchoolJson[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.schools(user.username, user.password, new Callback<SchoolJson[]>() {
            @Override
            public void success(SchoolJson[] schools, Response response) {
                for (SchoolJson school : schools) {
                    BTTable.SchoolTable.append(school.id, school);
                    BTTable.getSchools(BTTable.FILTER_MY_SCHOOL).append(school.id, school);
                }
                if (cb != null)
                    cb.success(schools, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void attendCourse(final int courseID, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.attendCourse(user.username, user.password, courseID, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                BTDebug.LogInfo(user.toJson());
                BTPreference.setUser(getApplicationContext(), user);
                if (cb != null)
                    cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void employSchool(int schoolID, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.employSchool(user.username, user.password, schoolID, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                BTDebug.LogInfo(user.toJson());
                BTPreference.setUser(getApplicationContext(), user);
                if (cb != null)
                    cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void enrollSchool(int schoolID, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.enrollSchool(user.username, user.password, schoolID, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                BTDebug.LogInfo(user.toJson());
                BTPreference.setUser(getApplicationContext(), user);
                if (cb != null)
                    cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void allSchools(final Callback<SchoolJson[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.allSchools(user.username, user.password, new Callback<SchoolJson[]>() {
            @Override
            public void success(SchoolJson[] schools, Response response) {
                for (SchoolJson school : schools)
                    BTTable.SchoolTable.append(school.id, school);
                if (cb != null)
                    cb.success(schools, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void schoolCourses(final int schoolId, final Callback<CourseJson[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.schoolCourses(user.username, user.password, schoolId, new Callback<CourseJson[]>() {
            @Override
            public void success(CourseJson[] courses, Response response) {
                for (CourseJson course : courses)
                    BTTable.CourseTable.append(course.id, course);
                if (cb != null)
                    cb.success(courses, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void courseCreate(String name, String number, int schoolID, String profName, final Callback<CourseJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.courseCreate(user.username, user.password, name, number, schoolID, profName, new Callback<CourseJson>() {
            @Override
            public void success(CourseJson course, Response response) {
                BTTable.CourseTable.append(course.id, course);
                if (cb != null)
                    cb.success(course, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void courseFeed(final int courseID, int page, final Callback<PostJson[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.courseFeed(user.username, user.password, courseID, page, new Callback<PostJson[]>() {
            @Override
            public void success(PostJson[] posts, Response response) {
                for (PostJson post : posts)
                    BTTable.PostTable.append(post.id, post);
                if (cb != null)
                    cb.success(posts, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void courseStudents(final int courseId, final Callback<UserJson[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.courseStudents(user.username, user.password, courseId, new Callback<UserJson[]>() {
            @Override
            public void success(UserJson[] users, Response response) {
                for (UserJson user : users)
                    BTTable.UserTable.append(user.id, user);
                if (cb != null)
                    cb.success(users, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void courseGrades(final int courseId, final Callback<GradeJson[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.courseGrades(user.username, user.password, courseId, new Callback<GradeJson[]>() {
            @Override
            public void success(GradeJson[] grades, Response response) {
                if (cb != null)
                    cb.success(grades, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void post(int postId, final Callback<PostJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.post(user.username, user.password, postId, new Callback<PostJson>() {
            @Override
            public void success(PostJson post, Response response) {
                BTTable.PostTable.append(post.id, post);
                if (cb != null)
                    cb.success(post, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    public void postCreate(String type, String title, String message, int courseID, final Callback<PostJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.postCreate(user.username, user.password, type, title, message, courseID, new Callback<PostJson>() {
            @Override
            public void success(PostJson post, Response response) {
                BTTable.PostTable.append(post.id, post);
                if (cb != null)
                    cb.success(post, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void postAttendanceStart(int courseID, final Callback<CourseJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.postAttendanceStart(user.username, user.password, courseID, new Callback<CourseJson>() {
            @Override
            public void success(CourseJson course, Response response) {
                BTTable.CourseTable.append(course.id, course);
                if (cb != null)
                    cb.success(course, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void postAttendanceFoundDevice(int postID, final String uuid, final Callback<PostJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.postAttendanceFoundDevice(user.username, user.password, postID, uuid, new Callback<PostJson>() {
            @Override
            public void success(PostJson post, Response response) {
                if (post != null)
                    BTTable.PostTable.append(post.id, post);
                if (cb != null)
                    cb.success(post, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
                BTTable.UUIDLISTSENDED_remove(uuid);
            }
        });
    }

    public void postAttendanceCheckManually(int postID, int userID, final Callback<PostJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.postAttendanceCheckManually(user.username, user.password, postID, userID, new Callback<PostJson>() {
            @Override
            public void success(PostJson post, Response response) {
                BTTable.PostTable.append(post.id, post);
                if (cb != null)
                    cb.success(post, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void postCreateNotice(int courseID, String message, final Callback<PostJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.postCreateNotice(user.username, user.password, courseID, message, new Callback<PostJson>() {
            @Override
            public void success(PostJson post, Response response) {
                BTTable.PostTable.append(post.id, post);
                if (cb != null)
                    cb.success(post, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void serialValidate(String serial, final Callback<SchoolJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.serialValidate(serial, new Callback<SchoolJson>() {
            @Override
            public void success(SchoolJson school, Response response) {
                cb.success(school, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void serialRequest(String email, final Callback<SerialJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.serialRequest(email, new Callback<SerialJson>() {
            @Override
            public void success(SerialJson serial, Response response) {
                if (cb != null)
                    if (response != null && response.getStatus() == 202)
                        cb.success(serial, response);
                    else
                        cb.failure(null);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    private boolean isConnected() {
        if (mConnectivityManager == null || mBTAPI == null)
            return false;

        final NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting())
            return false;

        return true;
    }

    private void failureHandle(Callback cb, RetrofitError retrofitError) {
        if (retrofitError == null)
            return;

        if (retrofitError.isNetworkError())
            BTDebug.LogError("Network Error");
        else {
            try {
                ErrorsJson errors = (ErrorsJson) retrofitError.getBodyAs(ErrorsJson.class);
                BTDebug.LogError(retrofitError.getResponse().getStatus() + " : " + errors.message);
                if (errors.toast != null)
                    BeautiToast.show(getApplicationContext(), errors.toast);
            } catch (Exception e) {
                BTDebug.LogError(e.getMessage() + " : " + retrofitError.getMessage());
            }
        }
        if (cb != null)
            cb.failure(retrofitError);
    }

    /**
     * Inner Class LocalBinder
     */
    public class LocalBinder extends Binder {

        public BTService getService() {
            return BTService.this;
        }
    }
}
