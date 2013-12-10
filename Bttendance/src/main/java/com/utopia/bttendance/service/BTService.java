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

import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.model.BTPreference;
import com.utopia.bttendance.model.BTTable;
import com.utopia.bttendance.model.json.CourseJson;
import com.utopia.bttendance.model.json.ErrorsJson;
import com.utopia.bttendance.model.json.PostJson;
import com.utopia.bttendance.model.json.SchoolJson;
import com.utopia.bttendance.model.json.UserJson;
import com.utopia.bttendance.model.json.ValidationJson;
import com.utopia.bttendance.view.BeautiToast;

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
            .setServer(SERVER_DOMAIN + "/api")
            .build();
    private BTAPI mBTAPI;
    private ConnectivityManager mConnectivityManager;
    private LocalBinder mBinder = new LocalBinder();

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

    public void joinCourse(int courseID, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.joinCourse(user.username, user.password, courseID, new Callback<UserJson>() {
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

    public void postCreate(String type, String title, String message, int courseID, final Callback<PostJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.postCreate(user.username, user.password, type, title, message, courseID, new Callback<PostJson>() {
            @Override
            public void success(PostJson post, Response response) {
                if (cb != null)
                    cb.success(post, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void postAttendanceStart(int courseID, final Callback<PostJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.postAttendanceStart(user.username, user.password, courseID, new Callback<PostJson>() {
            @Override
            public void success(PostJson post, Response response) {
                if (cb != null)
                    cb.success(post, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void postAttendanceCheck(int postID, Location location, String[] uuidList, final Callback<PostJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String longitude = "" + location.getLongitude();
        String latitude = "" + location.getLatitude();

        mBTAPI.postAttendanceCheck(user.username, user.password, postID, longitude, latitude, uuidList, new Callback<PostJson>() {
            @Override
            public void success(PostJson post, Response response) {
                if (cb != null)
                    cb.success(post, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void postStudentList(final int postID, final Callback<UserJson[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.postStudentList(user.username, user.password, postID, new Callback<UserJson[]>() {
            @Override
            public void success(UserJson[] users, Response response) {
                for (UserJson user : users)
                    BTTable.UserTable.append(user.id, user);
                PostJson post = BTTable.PostTable.get(postID);
                if (post != null)
                    for (UserJson user : users)
                        BTTable.getUsers(BTTable.getCourseIdFilter(post.course)).append(user.id, user);
                if (cb != null)
                    cb.success(users, response);
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
        BTDebug.LogError("Error : " + retrofitError.getMessage());
        if (retrofitError.isNetworkError())
            BTDebug.LogError("Network Error");
        else {
            try {
                ErrorsJson errors = (ErrorsJson) retrofitError.getBodyAs(ErrorsJson.class);
                BTDebug.LogError(errors.message);
                if (errors.toast != null)
                    BeautiToast.show(getApplicationContext(), errors.toast);
            } catch (Exception e) {
                BTDebug.LogError(e.getMessage());
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
