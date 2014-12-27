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
import com.bttendance.model.json.PreferencesJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.service.request.LogInRequest;
import com.bttendance.service.request.PasswordResetRequest;
import com.bttendance.service.request.PreferencesPutRequest;
import com.bttendance.service.request.UserPostRequest;
import com.bttendance.service.request.UserPutRequest;
import com.bttendance.view.BTDialog;
import com.squareup.otto.BTEventBus;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 11. 9..
 */
public class BTService extends Service {

    private static final String SERVER_DOMAIN_PRODUCTION = "http://www.bttendance.com";
    private static final String SERVER_DOMAIN_DEVELOPMENT = "http://bttendance-staging.herokuapp.com";
    private RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("Content-Type", "application/json; charset=UTF-8");
            request.addHeader("Platform", "Android");
            request.addHeader("Version", "" + BuildConfig.VERSION_CODE);
            request.addHeader("Accept-Language", getResources().getConfiguration().locale.getLanguage());
//            request.addHeader("Authorization", "");
//            OAuth oauth_consumer_key="xvz1evFS4wEEPTGEFPHBog",
//                    oauth_nonce="kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg",
//                    oauth_signature="tnnArxj06cWHq44gCs1OSKk%2FjLY%3D",
//                    oauth_signature_method="HMAC-SHA1",
//                    oauth_timestamp="1318622958",
//                    oauth_token="370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb",
//                    oauth_version="1.0"
//            https://dev.twitter.com/oauth/overview/authorizing-requests
        }
    };
    private RestAdapter mRestAdapter = new RestAdapter.Builder()
            .setLog(new RestAdapter.Log() {
                @Override
                public void log(String log) {
                    if (log != null) {
                        if (log.contains("<--- HTTP") || log.contains("---> HTTP"))
                            BTDebug.LogQueryAPI(log);
                        else
                            BTDebug.LogResponseAPI(log);
                    }
                }
            })
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint(getServerDomain() + "/api/v1")
            .setRequestInterceptor(requestInterceptor)
            .build();
    private BTAPI mBTAPI;
    private ConnectivityManager mConnectivityManager;
    private LocalBinder mBinder = new LocalBinder();

    public String getServerDomain() {
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
    public void signup(String email, String password, String name, String mac_address, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        BTPreference.setMacAddress(getApplicationContext(), mac_address);
        UserPostRequest request = new UserPostRequest(email, password, name, mac_address);
        mBTAPI.signUp(request, new Callback<UserJson>() {
            @Override
            public void success(UserJson userJson, Response response) {
                BTPreference.setUser(getApplicationContext(), userJson);
                successHandle(cb, userJson, response);
            }

            @Override
            public void failure(RetrofitError error) {
                failureHandle(cb, error);
            }
        });

    }

    public void login(String email, String password, String mac_address, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        BTPreference.setMacAddress(getApplicationContext(), mac_address);
        LogInRequest request = new LogInRequest(email, password, mac_address);
        mBTAPI.logIn(request, new Callback<UserJson>() {
            @Override
            public void success(UserJson userJson, Response response) {
                BTPreference.setUser(getApplicationContext(), userJson);
                successHandle(cb, userJson, response);
            }

            @Override
            public void failure(RetrofitError error) {
                failureHandle(cb, error);
            }
        });
    }

    public void forgotPassword(String email, final Callback<Object> cb) {
        if (!isConnected())
            return;

        PasswordResetRequest request = new PasswordResetRequest(email);
        mBTAPI.forgotPassword(request, new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {
                if (response.getStatus() == 204 && cb != null)
                    successHandle(cb, o, response);
            }

            @Override
            public void failure(RetrofitError error) {
                failureHandle(cb, error);
            }
        });
    }

    public void autoSignIn(final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.autoSignIn(user.id, new Callback<UserJson>() {
            @Override
            public void success(UserJson userJson, Response response) {
                BTPreference.setUser(getApplicationContext(), userJson);
                successHandle(cb, userJson, response);
            }

            @Override
            public void failure(RetrofitError error) {
                failureHandle(cb, error);
            }
        });
    }

    public void updateUser(UserPutRequest body, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.updateUser(user.id, body, new Callback<UserJson>() {
            @Override
            public void success(UserJson userJson, Response response) {
                BTPreference.setUser(getApplicationContext(), userJson);
                successHandle(cb, userJson, response);
            }

            @Override
            public void failure(RetrofitError error) {
                failureHandle(cb, error);
            }
        });
    }

    public void signOut() {
        BTPreference.clearUser(getApplicationContext());
        Intent intent = new Intent(getApplicationContext(), IntroductionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Preferences APIs
     */
    public void getPreferences(final Callback<PreferencesJson> cb) {
        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.getPreferences(user.id, new Callback<PreferencesJson>() {
            @Override
            public void success(PreferencesJson preferencesJson, Response response) {
                BTPreference.setPreference(getApplicationContext(), preferencesJson);
                successHandle(cb, preferencesJson, response);
            }

            @Override
            public void failure(RetrofitError error) {
                failureHandle(cb, error);
            }
        });
    }


    public void updatePreferences(PreferencesPutRequest preferencesPutRequest, final Callback<PreferencesJson> cb) {
        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.updatePreferences(user.id, preferencesPutRequest, new Callback<PreferencesJson>() {
            @Override
            public void success(PreferencesJson preferencesJson, Response response) {
                BTPreference.setPreference(getApplicationContext(), preferencesJson);
                successHandle(cb, preferencesJson, response);
            }

            @Override
            public void failure(RetrofitError error) {
                failureHandle(cb, error);
            }
        });
    }

    /**
     * Private Methods
     */
    private boolean isConnected() {
        if (mConnectivityManager == null || mBTAPI == null)
            return false;

        final NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void successHandle(Callback cb, Object object, Response response) {
        if (cb != null)
            cb.success(object, response);
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
                                            signOut();
                                        }

                                        @Override
                                        public void onCanceled() {
                                            signOut();
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

    /**
     * Inner Class LocalBinder
     */
    public class LocalBinder extends Binder {
        public BTService getService() {
            return BTService.this;
        }
    }
}
