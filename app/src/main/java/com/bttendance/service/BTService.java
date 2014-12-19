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
import com.bttendance.BuildConfig;
import com.bttendance.R;
import com.bttendance.activity.guide.IntroductionActivity;
import com.bttendance.event.ShowToastEvent;
import com.bttendance.helper.PackagesHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.json.ErrorJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.view.BTDialog;
import com.squareup.otto.BTEventBus;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by TheFinestArtist on 2013. 11. 9..
 */
public class BTService extends Service {

    private static final String SERVER_DOMAIN_PRODUCTION = "http://www.bttendance.com";
    private static final String SERVER_DOMAIN_DEVELOPMENT = "http://bttendance-staging.herokuapp.com";
    private static RestAdapter mRestAdapter = new RestAdapter.Builder()
            .setLog(new RestAdapter.Log() {
                @Override
                public void log(String log) {
                    if (log != null) {
                        if (log.contains("<--- HTTP") || log.contains("---> HTTP"))
                            BTDebug.LogQueryAPI(log);
                        else if (log.contains("created_at"))
                            BTDebug.LogResponseAPI(log);
                    }
                }
            })
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint(getServerDomain() + "/api/v1")
            .build();
    private BTAPI mBTAPI;
    private ConnectivityManager mConnectivityManager;
    private LocalBinder mBinder = new LocalBinder();

    public static String getServerDomain() {
        if (!BuildConfig.DEBUG)
            return SERVER_DOMAIN_PRODUCTION;
        else
            return SERVER_DOMAIN_DEVELOPMENT;
    }

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
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Users APIs
     */
    void signup(String email, String password, String name, Callback<UserJson> cb) {
        if (!isConnected())
            return;


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

        if (retrofitError.getResponse() != null) {
            if (retrofitError.getResponse().getStatus() == 503) {
                String title = getString(R.string.oopps);
                String message = getString(R.string.too_many_users_are_connecting);
                BTDialog.ok(getApplicationContext(), title, message, null);
            } else {
                try {
                    ErrorJson errors = (ErrorJson) retrofitError.getBodyAs(ErrorJson.class);
                    switch (ErrorJson.Type.valueOf(errors.type)) {
                        case log:
                            BTDebug.LogError(retrofitError.getResponse().getStatus() + " : " + errors.message);
                            break;
                        case toast:
                            BTEventBus.getInstance().post(new ShowToastEvent(errors.message));
                            break;
                        case alert:
                            String title = errors.title;
                            String message = errors.message;
                            switch (retrofitError.getResponse().getStatus()) {
                                case 441:
                                    BTDialog.alert(getApplicationContext(), title, message, new BTDialog.OnDialogListener() {
                                        @Override
                                        public void onConfirmed(String edit) {
                                            PackagesHelper.updateApp(getApplicationContext());
                                        }

                                        @Override
                                        public void onCanceled() {
                                        }
                                    });
                                    break;
                                case 444:
                                    BTDialog.ok(getApplicationContext(), title, message, new BTDialog.OnDialogListener() {
                                        @Override
                                        public void onConfirmed(String edit) {
                                            autoSignOut();
                                        }

                                        @Override
                                        public void onCanceled() {
                                            autoSignOut();
                                        }
                                    });
                                    break;
                                default:
                                    BTDialog.alert(getApplicationContext(), title, message, null);
                                    break;
                            }
                            break;
                    }
                } catch (Exception e) {
                    BTDebug.LogError(e.getMessage() + " : " + retrofitError.getMessage());
                }
            }
        }
        if (cb != null)
            cb.failure(retrofitError);
    }

    private void autoSignOut() {
        BTPreference.clearUser(getApplicationContext());
        Intent intent = new Intent(getApplicationContext(), IntroductionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
