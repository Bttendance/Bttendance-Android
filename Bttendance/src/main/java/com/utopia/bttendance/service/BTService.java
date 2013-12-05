package com.utopia.bttendance.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
                cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void updateNotificationKey(String username, String password, String deviceUUID, String notificationKey, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.updateNotificationKey(username, password, deviceUUID, notificationKey, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                BTDebug.LogInfo(user.toJson());
                BTPreference.setUser(getApplicationContext(), user);
                cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void updateProfileImage(String username, String password, String deviceUUID, String profileImage, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.updateProfileImage(username, password, deviceUUID, profileImage, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                BTDebug.LogInfo(user.toJson());
                BTPreference.setUser(getApplicationContext(), user);
                cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void updateEmail(String username, String password, String deviceUUID, String email, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.updateEmail(username, password, deviceUUID, email, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                BTDebug.LogInfo(user.toJson());
                BTPreference.setUser(getApplicationContext(), user);
                cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void updateFullName(String username, String password, String deviceUUID, String fullName, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.updateFullName(username, password, deviceUUID, fullName, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                BTDebug.LogInfo(user.toJson());
                BTPreference.setUser(getApplicationContext(), user);
                cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void joinSchool(String username, String password, int schoolID, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.joinSchool(username, password, schoolID, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                BTDebug.LogInfo(user.toJson());
                BTPreference.setUser(getApplicationContext(), user);
                cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void joinCourse(String username, String password, int courseID, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.joinCourse(username, password, courseID, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                BTDebug.LogInfo(user.toJson());
                BTPreference.setUser(getApplicationContext(), user);
                cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void schools(String username, String password, final Callback<SchoolJson[]> cb) {
        if (!isConnected())
            return;

        mBTAPI.schools(username, password, new Callback<SchoolJson[]>() {
            @Override
            public void success(SchoolJson[] schools, Response response) {
                cb.success(schools, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void courses(String username, String password, final Callback<CourseJson[]> cb) {
        if (!isConnected())
            return;

        mBTAPI.courses(username, password, new Callback<CourseJson[]>() {
            @Override
            public void success(CourseJson[] courses, Response response) {
                cb.success(courses, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void feed(String username, String password, int page, final Callback<PostJson[]> cb) {
        if (!isConnected())
            return;

        mBTAPI.feed(username, password, page, new Callback<PostJson[]>() {
            @Override
            public void success(PostJson[] posts, Response response) {
                for (PostJson post : posts)
                    BTTable.PostTable.append(post.id, post);
                cb.success(posts, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void schoolCreate(String username, String password, String name, String website, final Callback<SchoolJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.schoolCreate(username, password, name, website, new Callback<SchoolJson>() {
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

    public void courseCreate(String username, String password, String name, String number, int schoolID, final Callback<CourseJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.courseCreate(username, password, name, number, schoolID, new Callback<CourseJson>() {
            @Override
            public void success(CourseJson course, Response response) {
                cb.success(course, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void courseFeed(String username, String password, int courseID, int page, final Callback<PostJson[]> cb) {
        if (!isConnected())
            return;

        mBTAPI.courseFeed(username, password, courseID, page, new Callback<PostJson[]>() {
            @Override
            public void success(PostJson[] posts, Response response) {
                cb.success(posts, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void postCreate(String username, String password, String type, String title, String message, int courseID, final Callback<PostJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.postCreate(username, password, type, title, message, courseID, new Callback<PostJson>() {
            @Override
            public void success(PostJson post, Response response) {
                cb.success(post, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void postCheck(String username, String password, int courseID, final Callback<PostJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.postCheck(username, password, courseID, new Callback<PostJson>() {
            @Override
            public void success(PostJson post, Response response) {
                cb.success(post, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void postStudentList(String username, String password, int postID, final Callback<UserJson[]> cb) {
        if (!isConnected())
            return;

        mBTAPI.postStudentList(username, password, postID, new Callback<UserJson[]>() {
            @Override
            public void success(UserJson[] users, Response response) {
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

    public void sendNotification(String username, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.sendNotification(username, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                if (response != null && response.getStatus() == 202)
                    cb.success(user, response);
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
