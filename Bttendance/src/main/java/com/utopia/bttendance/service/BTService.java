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
import com.utopia.bttendance.model.json.ErrorsJson;
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

    public void signin(String username, String password, String uuid, final Callback cb) {
        if (!isConnected())
            return;

        mBTAPI.signin(username, password, uuid, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                failureHandle(cb, retrofitError);
            }
        });
    }

    public void signup(UserJson user, final Callback cb) {
        if (!isConnected())
            return;

        mBTAPI.signup(user.username, user.full_name, user.email, user.password, user.device_type, user.device_uuid, user.type, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                cb.success(user, response);
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
                BTDebug.LogError(errors.getMessage());
                BeautiToast.show(getApplicationContext(), errors.getToast());
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
