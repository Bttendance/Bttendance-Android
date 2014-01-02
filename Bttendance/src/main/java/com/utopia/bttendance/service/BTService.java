package com.utopia.bttendance.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;

import com.squareup.otto.BTEventBus;
import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.event.LocationChangedEvent;
import com.utopia.bttendance.helper.BluetoothHelper;
import com.utopia.bttendance.helper.GPSTracker;
import com.utopia.bttendance.model.BTPreference;
import com.utopia.bttendance.model.BTTable;
import com.utopia.bttendance.model.json.CourseJson;
import com.utopia.bttendance.model.json.ErrorsJson;
import com.utopia.bttendance.model.json.PostJson;
import com.utopia.bttendance.model.json.SchoolJson;
import com.utopia.bttendance.model.json.UserJson;
import com.utopia.bttendance.model.json.ValidationJson;
import com.utopia.bttendance.view.BeautiToast;

import java.util.Arrays;
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

    private static final String SERVER_DOMAIN = "http://www.bttendance.com";
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
            .setServer(SERVER_DOMAIN + "/api")
            .build();
    private BTAPI mBTAPI;
    private ConnectivityManager mConnectivityManager;
    private LocalBinder mBinder = new LocalBinder();
    private GPSTracker mGPS;
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
        mGPS = new GPSTracker(this);
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
        if (mGPS != null)
            mGPS.startUsingGPS();

        BTEventBus.getInstance().post(new LocationChangedEvent(mGPS.getLocation()));

        if (mAttendanceThread != null) {
            mAttendanceThread.interrupt();
            mAttendanceThread = null;
        }

        mAttendanceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 30; i++) {
                        BluetoothHelper.startDiscovery();
                        Thread.sleep(10000);
                        if (i % 3 == 1) {
                            Set<Integer> ids = BTTable.getCheckingPostIds();
                            for (int id : ids) {
                                for (String mac : BTTable.UUIDLIST) {
                                    if (!BTTable.UUIDLISTSENDED.contains(mac)) {
                                        postAttendanceFoundDevice(id, mac, new Callback<PostJson>() {
                                            @Override
                                            public void success(PostJson postJson, Response response) {
                                                BTDebug.LogInfo(postJson.toJson());
                                            }

                                            @Override
                                            public void failure(RetrofitError retrofitError) {
                                            }
                                        });
                                    }
                                }
                            }
                            BTTable.UUIDLISTSENDED.addAll(BTTable.UUIDLIST);
                            BTTable.UUIDLIST = new HashSet<String>();
                        }
                    }
                    BTTable.UUIDLISTSENDED = new HashSet<String>();
                    attendanceStop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        mAttendanceThread.start();
    }

    public void attendanceStop() {
        if (mGPS != null)
            mGPS.stopUsingGPS();
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

    public void signup(UserJson user, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.signup(user.username, user.full_name, user.email, user.password, user.device_type, user.device_uuid, user.type, new Callback<UserJson>() {
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

    public void joinSchool(int schoolID, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.joinSchool(user.username, user.password, schoolID, new Callback<UserJson>() {
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

    public void joinCourse(final int courseID, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.joinCourse(user.username, user.password, courseID, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                BTDebug.LogInfo(user.toJson());
                BTPreference.setUser(getApplicationContext(), user);
                CourseJson course = BTTable.CourseTable.get(courseID);
                if (course != null)
                    BTTable.getCourses(BTTable.FILTER_MY_COURSE).append(course.id, course);
                if (cb != null)
                    cb.success(user, response);
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

    public void courses(final Callback<CourseJson[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.courses(user.username, user.password, new Callback<CourseJson[]>() {
            @Override
            public void success(CourseJson[] courses, Response response) {
                for (CourseJson course : courses)
                    BTTable.CourseTable.append(course.id, course);
                for (CourseJson course : courses)
                    BTTable.getCourses(BTTable.FILTER_MY_COURSE).append(course.id, course);
                if (cb != null)
                    cb.success(courses, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void joinableCourses(final Callback<CourseJson[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.joinableCourses(user.username, user.password, new Callback<CourseJson[]>() {
            @Override
            public void success(CourseJson[] courses, Response response) {
                for (CourseJson course : courses)
                    BTTable.CourseTable.append(course.id, course);
                for (CourseJson course : courses)
                    BTTable.getCourses(BTTable.FILTER_JOINABLE_COURSE).append(course.id, course);
                if (cb != null)
                    cb.success(courses, response);
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
                for (PostJson post : posts)
                    BTTable.PostTable.append(post.id, post);
                for (PostJson post : posts)
                    BTTable.getPosts(BTTable.FILTER_TOTAL_POST).append(post.id, post);
                if (cb != null)
                    cb.success(posts, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void schoolCreate(String name, String website, final Callback<SchoolJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.schoolCreate(user.username, user.password, name, website, new Callback<SchoolJson>() {
            @Override
            public void success(SchoolJson school, Response response) {
                if (cb != null)
                    cb.success(school, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void courseCreate(String name, String number, int schoolID, final Callback<CourseJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.courseCreate(user.username, user.password, name, number, schoolID, new Callback<CourseJson>() {
            @Override
            public void success(CourseJson course, Response response) {
                BTTable.CourseTable.append(course.id, course);
                BTTable.getCourses(BTTable.FILTER_MY_COURSE).append(course.id, course);
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
                for (PostJson post : posts)
                    BTTable.getPosts(BTTable.getCourseIdFilter(courseID)).append(post.id, post);
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
                CourseJson course = BTTable.CourseTable.get(courseId);
                if (course != null)
                    for (UserJson user : users)
                        BTTable.getUsers(BTTable.getCourseIdFilter(course.id)).append(user.id, user);
                if (cb != null)
                    cb.success(users, response);
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

        mBTAPI.post(postId, new Callback<PostJson>() {
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

    public void postAttendanceFoundDevice(int postID, String uuid, final Callback<PostJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.postAttendanceFoundDevice(user.username, user.password, postID, uuid, new Callback<PostJson>() {
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

    public void postAttendanceCurrentLocation(int postID, Location location, final Callback<PostJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String latitude = "0";
        String longitude = "0";
        if (location != null) {
            latitude = "" + location.getLatitude();
            longitude = "" + location.getLongitude();
        }

        mBTAPI.postAttendanceCurrentLocation(user.username, user.password, postID, latitude, longitude, new Callback<PostJson>() {
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

    public void serialValidate(String serial, final Callback<ValidationJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.serialValidate(serial, new Callback<ValidationJson>() {
            @Override
            public void success(ValidationJson validation, Response response) {
                if (cb != null)
                    if (response != null && response.getStatus() == 202)
                        cb.success(validation, response);
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
