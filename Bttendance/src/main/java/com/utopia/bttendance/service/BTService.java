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
import com.utopia.bttendance.model.json.UserJson;

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
                cb.failure(retrofitError);
            }
        });
    }

    public void signup(UserJson user, final Callback cb) {
        if (!isConnected())
            return;

        mBTAPI.signup(user, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                cb.success(user, response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                cb.failure(retrofitError);
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

    /**
     * Inner Class LocalBinder
     */
    public class LocalBinder extends Binder {

        public BTService getService() {
            return BTService.this;
        }

    }
}
