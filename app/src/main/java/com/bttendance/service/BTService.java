package com.bttendance.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.bttendance.BTDebug;
import com.bttendance.BuildConfig;
import com.bttendance.R;
import com.bttendance.activity.guide.IntroductionActivity;
import com.bttendance.event.ShowToastEvent;
import com.bttendance.event.attendance.AttdStartedEvent;
import com.bttendance.event.dialog.ShowAlertDialogEvent;
import com.bttendance.event.refresh.RefreshFeedEvent;
import com.bttendance.fragment.BTDialogFragment;
import com.bttendance.helper.BluetoothHelper;
import com.bttendance.helper.DateHelper;
import com.bttendance.helper.PackagesHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.AttendanceJson;
import com.bttendance.model.json.ClickerJson;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.CourseJsonArray;
import com.bttendance.model.json.EmailJson;
import com.bttendance.model.json.ErrorJson;
import com.bttendance.model.json.NoticeJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.PostJsonArray;
import com.bttendance.model.json.QuestionJson;
import com.bttendance.model.json.QuestionJsonArray;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.model.json.SchoolJsonArray;
import com.bttendance.model.json.UserJson;
import com.bttendance.model.json.UserJsonSimple;
import com.bttendance.model.json.UserJsonSimpleArray;
import com.google.gson.Gson;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.DisconnectCallback;
import com.koushikdutta.async.http.socketio.ErrorCallback;
import com.koushikdutta.async.http.socketio.EventCallback;
import com.koushikdutta.async.http.socketio.JSONCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;
import com.koushikdutta.async.http.socketio.StringCallback;
import com.squareup.otto.BTEventBus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 11. 9..
 */
public class BTService extends Service {

    private static final String SERVER_DOMAIN_PRODUCTION = "http://www.bttd.co";
    private static final String SERVER_DOMAIN_DEVELOPMENT = "http://bttendance-dev.herokuapp.com";
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
            .setEndpoint(getServerDomain() + "/api")
            .build();
    private BTAPI mBTAPI;
    private ConnectivityManager mConnectivityManager;
    private LocalBinder mBinder = new LocalBinder();
    private Thread mAttendanceThread;
    private Thread mRefreshThread;
    private long mAttdTimeTo;
    private long mRefreshTimeTo;
    private int mReconnectTry = 0;

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

    private SocketIOClient mSocketClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mBTAPI = mRestAdapter.create(BTAPI.class);
        socketConnect();
    }

    public void socketConnect() {
        if (mSocketClient != null && mSocketClient.isConnected())
            return;

        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), BTService.getServerDomain(), new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, final SocketIOClient client) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }

                mSocketClient = client;
                socketConnectToServer();

                BTDebug.LogQueryAPI("Socket Connected-------");

                client.setStringCallback(new StringCallback() {
                    @Override
                    public void onString(String string, Acknowledge acknowledge) {
                        BTDebug.LogQueryAPI("string: " + string);
                    }
                });

                client.on("onConnect", new EventCallback() {
                    @Override
                    public void onEvent(JSONArray arguments, Acknowledge acknowledge) {
                        BTDebug.LogQueryAPI("args: " + arguments.toString());
                    }
                });

                client.on("clicker", new EventCallback() {
                    @Override
                    public void onEvent(JSONArray arguments, Acknowledge acknowledge) {
                        try {
                            BTDebug.LogQueryAPI("clicker : " + arguments.getJSONObject(0).toString());
                            ClickerJson clickerJson = new Gson().fromJson(arguments.getJSONObject(0).toString(), ClickerJson.class);
                            BTTable.updateClicker(clickerJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                client.on("attendance", new EventCallback() {
                    @Override
                    public void onEvent(JSONArray arguments, Acknowledge acknowledge) {
                        try {
                            BTDebug.LogQueryAPI("attendance : " + arguments.getJSONObject(0).toString());
                            AttendanceJson attendanceJson = new Gson().fromJson(arguments.getJSONObject(0).toString(), AttendanceJson.class);
                            BTTable.updateAttendance(attendanceJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                client.on("notice", new EventCallback() {
                    @Override
                    public void onEvent(JSONArray arguments, Acknowledge acknowledge) {
                        try {
                            BTDebug.LogQueryAPI("notice : " + arguments.getJSONObject(0).toString());
                            NoticeJson noticeJson = new Gson().fromJson(arguments.getJSONObject(0).toString(), NoticeJson.class);
                            BTTable.updateNotice(noticeJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                client.on("post", new EventCallback() {
                    @Override
                    public void onEvent(JSONArray arguments, Acknowledge acknowledge) {
                        try {
                            BTDebug.LogQueryAPI("post : " + arguments.getJSONObject(0).toString());
                            PostJson postJson = new Gson().fromJson(arguments.getJSONObject(0).toString(), PostJson.class);
                            BTTable.updatePost(postJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                client.setJSONCallback(new JSONCallback() {
                    @Override
                    public void onJSON(JSONObject json, Acknowledge acknowledge) {
                        BTDebug.LogQueryAPI("json: " + json.toString());
                    }
                });

                client.setErrorCallback(new ErrorCallback() {
                    @Override
                    public void onError(String error) {

                    }
                });

                client.setDisconnectCallback(new DisconnectCallback() {
                    @Override
                    public void onDisconnect(Exception e) {
                        if (!client.isConnected() && mReconnectTry < 5) {
                            try {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        client.reconnect();
                                        mReconnectTry++;
                                    }
                                }, 5000 * mReconnectTry);
                            } catch (Exception error) {
                            }
                            ;
                        }
                    }
                });
            }
        });
    }

    public void socketConnectToServer() {
        if (mSocketClient == null || !mSocketClient.isConnected()) {
            socketConnect();
            return;
        }

        try {
            UserJson user = BTPreference.getUser(getApplicationContext());
            String locale = getResources().getConfiguration().locale.getLanguage();
            JSONArray arr = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("url", String.format("/api/sockets/connect?email=%s&password=%s&locale=%s", user.email, user.password, locale));
            arr.put(obj);
            mSocketClient.emit("put", arr);
            BTDebug.LogError("SENT to Connect");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void refreshCheck() {

        long refreshTimeTo = BTTable.getRefreshTimeTo();
        BTDebug.LogError("mRefreshTimeTo = " + mRefreshTimeTo + ", refreshTimeTo = " + refreshTimeTo);

        if (refreshTimeTo == -1)
            return;

        if (mRefreshThread != null && mRefreshTimeTo == refreshTimeTo)
            return;

        if (mRefreshThread != null && mRefreshTimeTo != refreshTimeTo) {
            mRefreshThread.interrupt();
            mRefreshThread = null;
        }

        mRefreshTimeTo = refreshTimeTo;

        mRefreshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long timeout = mRefreshTimeTo - System.currentTimeMillis();
                    if (timeout > 0)
                        Thread.sleep(timeout);
                    BTEventBus.getInstance().post(new RefreshFeedEvent());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        mRefreshThread.start();
    }

    public void attendanceStart() {

        BTDebug.LogError("mAttdTimeTo = " + mAttdTimeTo + ", attdchecktimeto = " + BTTable.getAttdChekTimeTo());

        if (mAttendanceThread != null && mAttdTimeTo == BTTable.getAttdChekTimeTo())
            return;

        if (mAttendanceThread != null && mAttdTimeTo != BTTable.getAttdChekTimeTo()) {
            mAttendanceThread.interrupt();
            mAttendanceThread = null;
        }

        mAttdTimeTo = BTTable.getAttdChekTimeTo();

        mAttendanceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int i = 0;
                    while (DateHelper.getCurrentGMTTimeMillis() < mAttdTimeTo - 500) {
                        if (i++ % 20 == 0) {
                            BTDebug.LogError("Start Discovery");
                            BluetoothHelper.startDiscovery();
                        }
                        Thread.sleep(500);
                        Set<Integer> ids = BTTable.getAttdCheckingIds();
                        Map<String, String> list = new ConcurrentHashMap<String, String>();
                        for (String mac : BTTable.UUIDLIST().keySet())
                            list.put(mac, mac);

                        for (int id : ids) {
                            for (String mac : list.keySet()) {
                                if (!BTTable.UUIDLISTSENDED_contains(mac)) {
                                    attendanceFoundDevice(id, mac, new Callback<AttendanceJson>() {
                                        @Override
                                        public void success(AttendanceJson attendance, Response response) {
                                            if (attendance != null)
                                                BTDebug.LogInfo(attendance.toJson());
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        mAttendanceThread.start();
    }

    /**
     * User APIs
     */
    public void signup(UserJson user, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        String locale = getResources().getConfiguration().locale.getLanguage();
        mBTAPI.signup(
                user.full_name,
                user.email,
                user.password,
                user.device.type,
                user.device.uuid,
                locale,
                new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
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
        String locale = getResources().getConfiguration().locale.getLanguage();
        String version = BuildConfig.VERSION_NAME;
        if (user == null)
            return;

        mBTAPI.autoSignin(
                user.email,
                user.password,
                locale,
                user.device.uuid,
                BTAPI.ANDROID,
                version,
                new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
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

    public void signin(String email, String password, String uuid, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        String locale = getResources().getConfiguration().locale.getLanguage();
        mBTAPI.signin(
                email,
                password,
                locale,
                uuid,
                BTAPI.ANDROID,
                new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
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

    public void forgotPassword(String email, final Callback<EmailJson> cb) {
        if (!isConnected())
            return;

        String locale = getResources().getConfiguration().locale.getLanguage();
        mBTAPI.forgotPassword(
                email,
                locale,
                new Callback<EmailJson>() {
                    @Override
                    public void success(EmailJson email, Response response) {
                        if (cb != null)
                            cb.success(email, response);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    public void updatePassword(String passwordOld, String passwordNew, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.updatePassword(
                user.email,
                user.password,
                locale,
                passwordOld,
                passwordNew,
                new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
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
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.updateFullName(
                user.email,
                user.password,
                locale,
                fullName,
                new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
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

    public void updateEmail(String emailNew, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.updateEmail(
                user.email,
                user.password,
                locale,
                emailNew,
                new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
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

    public void searchUser(String searchID, final Callback<UserJsonSimple> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.searchUser(
                user.email,
                user.password,
                locale,
                searchID,
                new Callback<UserJsonSimple>() {
                    @Override
                    public void success(UserJsonSimple user, Response response) {
                        if (cb != null)
                            cb.success(user, response);
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
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.courses(
                user.email,
                user.password,
                locale,
                new Callback<CourseJson[]>() {
                    @Override
                    public void success(CourseJson[] courses, Response response) {

                        CourseJsonArray courseJsonArray = new CourseJsonArray(courses);
                        BTPreference.setCourses(getApplicationContext(), courseJsonArray);

                        for (CourseJson course : courses)
                            BTTable.MyCourseTable.append(course.id, course);

                        if (cb != null)
                            cb.success(courses, response);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    /**
     * Device APIs
     */
    public void updateNotificationKey(String notificationKey, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.updateNotificationKey(
                user.email,
                user.password,
                locale,
                user.device.uuid,
                notificationKey,
                new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
//                        BTPreference.setUser(getApplicationContext(), user);
                        if (cb != null)
                            cb.success(user, response);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    /**
     * Setting APIs
     */
    public void updateSettingAttendance(boolean attendance, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.updateSettingAttendance(
                user.email,
                user.password,
                locale,
                attendance,
                new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
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

    public void updateSettingClicker(boolean clicker, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.updateSettingClicker(
                user.email,
                user.password,
                locale,
                clicker,
                new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
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

    public void updateSettingNotice(boolean notice, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.updateSettingNotice(
                user.email,
                user.password,
                locale,
                notice,
                new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
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

    public void updateClickerDefaults(int progressTime, boolean showInfoOnSelect, String detailPrivacy, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.updateClickerDefaults(
                user.email,
                user.password,
                locale,
                progressTime,
                showInfoOnSelect,
                detailPrivacy,
                new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
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

    /**
     * Question APIs
     */
    public void myQuestions(final Callback<QuestionJson[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.myQuestions(
                user.email,
                user.password,
                locale,
                new Callback<QuestionJson[]>() {
                    @Override
                    public void success(QuestionJson[] questions, Response response) {
                        for (QuestionJson question : questions)
                            BTTable.MyQuestionTable.append(question.id, question);

                        if (cb != null)
                            cb.success(questions, response);

                        QuestionJsonArray questionJsonArray = new QuestionJsonArray(questions);
                        BTPreference.setMyQuestions(getApplicationContext(), questionJsonArray);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    public void createQuestion(String message, int choiceCount, int progressTime, boolean showInfoOnSelect, String detailPrivacy, final Callback<QuestionJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.createQuestion(
                user.email,
                user.password,
                locale,
                message,
                choiceCount,
                progressTime,
                showInfoOnSelect,
                detailPrivacy,
                new Callback<QuestionJson>() {
                    @Override
                    public void success(QuestionJson question, Response response) {
                        if (cb != null)
                            cb.success(question, response);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    public void editQuestion(int questionID, String message, int choiceCount, int progressTime, boolean showInfoOnSelect, String detailPrivacy, final Callback<QuestionJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.updateQuestion(
                user.email,
                user.password,
                locale,
                questionID,
                message,
                choiceCount,
                progressTime,
                showInfoOnSelect,
                detailPrivacy,
                new Callback<QuestionJson>() {
                    @Override
                    public void success(QuestionJson question, Response response) {
                        BTTable.MyQuestionTable.append(question.id, question);
                        if (cb != null)
                            cb.success(question, response);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    public void removeQuestion(int questionID, final Callback<QuestionJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.removeQuestion(
                user.email,
                user.password,
                locale,
                questionID,
                new Callback<QuestionJson>() {
                    @Override
                    public void success(QuestionJson question, Response response) {
                        BTTable.MyQuestionTable.remove(question.id);
                        if (cb != null)
                            cb.success(question, response);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    /**
     * Identification APIs
     */
    public void updateIdentity(int schoolID, String identity, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.updateIdentity(
                user.email,
                user.password,
                locale,
                schoolID,
                identity,
                new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
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

    /**
     * School APIs
     */
    public void createSchool(String name, String type, final Callback<SchoolJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.createSchool(
                user.email,
                user.password,
                locale,
                name,
                type,
                new Callback<SchoolJson>() {
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

    public void allSchools(final Callback<SchoolJson[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.allSchools(
                user.email,
                user.password,
                locale,
                new Callback<SchoolJson[]>() {
                    @Override
                    public void success(SchoolJson[] schools, Response response) {
                        for (SchoolJson school : schools)
                            BTTable.AllSchoolTable.append(school.id, school);

                        if (cb != null)
                            cb.success(schools, response);

                        SchoolJsonArray schoolJsonArray = new SchoolJsonArray(schools);
                        BTPreference.setAllSchools(getApplicationContext(), schoolJsonArray);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    public void enrollSchool(int schoolID, String studentID, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.enrollSchool(
                user.email,
                user.password,
                locale,
                schoolID,
                studentID,
                new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
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

    /**
     * Course APIs
     */
    public void courseInfo(int courseID, final Callback<CourseJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.courseInfo(
                user.email,
                user.password,
                locale,
                courseID,
                new Callback<CourseJson>() {
                    @Override
                    public void success(CourseJson course, Response response) {
                        BTPreference.updateCourse(getApplicationContext(), course);
                        BTTable.MyCourseTable.append(course.id, course);
                        if (cb != null)
                            cb.success(course, response);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    public void courseCreate(String name, int schoolID, String profName, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.courseCreate(
                user.email,
                user.password,
                locale,
                name,
                schoolID,
                profName,
                new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
                        BTPreference.setUser(getApplicationContext(), user);
                        if (cb != null)
                            cb.success(user, response);
                        socketConnectToServer();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    public void searchCourse(final int courseID, final String courseCode, final Callback<CourseJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.courseSearch(
                user.email,
                user.password,
                locale,
                courseID,
                courseCode,
                new Callback<CourseJson>() {
                    @Override
                    public void success(CourseJson course, Response response) {
                        BTTable.MyCourseTable.append(course.id, course);
                        if (cb != null)
                            cb.success(course, response);
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
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.attendCourse(
                user.email,
                user.password,
                locale,
                courseID,
                new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
                        BTPreference.setUser(getApplicationContext(), user);
                        if (cb != null)
                            cb.success(user, response);
                        socketConnectToServer();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    public void dettendCourse(final int courseID, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.dettendCourse(
                user.email,
                user.password,
                locale,
                courseID,
                new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
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

    public void courseFeed(final int courseID, int page, final Callback<PostJson[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.courseFeed(
                user.email,
                user.password,
                locale,
                courseID,
                page,
                new Callback<PostJson[]>() {
                    @Override
                    public void success(PostJson[] posts, Response response) {

                        PostJsonArray postJsonArray = new PostJsonArray(posts);
                        BTPreference.setPostsOfCourse(getApplicationContext(), postJsonArray, courseID);

                        for (int i = 0; i < BTTable.PostTable.size(); i++) {
                            int key = BTTable.PostTable.keyAt(i);
                            PostJson post = BTTable.PostTable.get(key);
                            if (post.course == null || post.course.id <= 0)
                                BTTable.PostTable.remove(key);
                        }

                        for (PostJson post : posts)
                            BTTable.PostTable.append(post.id, post);

                        if (BTTable.getAttdCheckingIds().size() > 0)
                            BTEventBus.getInstance().post(new AttdStartedEvent(true));

                        refreshCheck();

                        if (cb != null)
                            cb.success(posts, response);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    public void openCourse(final int courseID, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.openCourse(
                user.email,
                user.password,
                locale,
                courseID,
                new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
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

    public void closeCourse(final int courseID, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.closeCourse(
                user.email,
                user.password,
                locale,
                courseID,
                new Callback<UserJson>() {
                    @Override
                    public void success(UserJson user, Response response) {
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

    public void addManager(final String manager, final int courseId, final Callback<CourseJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.addManager(
                user.email,
                user.password,
                locale,
                manager,
                courseId,
                new Callback<CourseJson>() {
                    @Override
                    public void success(CourseJson course, Response response) {
                        if (cb != null)
                            cb.success(course, response);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    public void courseStudents(final int courseId, final Callback<UserJsonSimple[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.courseStudents(
                user.email,
                user.password,
                locale,
                courseId,
                new Callback<UserJsonSimple[]>() {
                    @Override
                    public void success(UserJsonSimple[] users, Response response) {
                        BTTable.updateStudentsOfCourse(courseId, users);

                        if (cb != null)
                            cb.success(users, response);

                        UserJsonSimpleArray userJsonSimpleArray = new UserJsonSimpleArray(users);
                        BTPreference.setStudentsOfCourse(getApplicationContext(), userJsonSimpleArray, courseId);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    public void courseAttendanceGrades(final int courseId, final Callback<UserJsonSimple[]> cb) {
        if (!isConnected())
            return;

        final UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.courseAttendanceGrades(
                user.email,
                user.password,
                locale,
                courseId,
                new Callback<UserJsonSimple[]>() {
                    @Override
                    public void success(UserJsonSimple[] users, Response response) {
                        BTTable.updateAttendanceRecordsOfCourse(courseId, users);

                        if (cb != null)
                            cb.success(users, response);

                        UserJsonSimpleArray userJsonSimpleArray = new UserJsonSimpleArray(users);
                        BTPreference.setStudentsOfCourse(getApplicationContext(), userJsonSimpleArray, courseId);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    public void courseClickerGrades(final int courseId, final Callback<UserJsonSimple[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.courseClickerGrades(
                user.email,
                user.password,
                locale,
                courseId,
                new Callback<UserJsonSimple[]>() {
                    @Override
                    public void success(UserJsonSimple[] users, Response response) {
                        BTTable.updateClickerRecordsOfCourse(courseId, users);

                        if (cb != null)
                            cb.success(users, response);

                        UserJsonSimpleArray userJsonSimpleArray = new UserJsonSimpleArray(users);
                        BTPreference.setStudentsOfCourse(getApplicationContext(), userJsonSimpleArray, courseId);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    public void courseExportGrades(final int courseId, final Callback<EmailJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.courseExportGrades(
                user.email,
                user.password,
                locale,
                courseId,
                new Callback<EmailJson>() {
                    @Override
                    public void success(EmailJson email, Response response) {
                        if (cb != null)
                            cb.success(email, response);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    /**
     * Post APIs
     */
    public void postStartAttendance(int courseID, String type, final Callback<PostJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.postStartAttendance(
                user.email,
                user.password,
                locale,
                courseID,
                type,
                new Callback<PostJson>() {
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

    public void postStartClicker(int courseID, String message, int choiceCount, int progressTime, boolean showInfoOnSelect, String detailPrivacy, final Callback<PostJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.postStartClicker(
                user.email,
                user.password,
                locale,
                courseID,
                message,
                choiceCount,
                progressTime,
                showInfoOnSelect,
                detailPrivacy,
                new Callback<PostJson>() {
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
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.postCreateNotice(
                user.email,
                user.password,
                locale,
                courseID,
                message,
                new Callback<PostJson>() {
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

    public void updatePostMessage(int postID, String message, final Callback<PostJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.postUpdateMessage(
                user.email,
                user.password,
                locale,
                postID,
                message,
                new Callback<PostJson>() {
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

    public void removePost(int postID, final Callback<PostJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.postRemove(
                user.email,
                user.password,
                locale,
                postID,
                new Callback<PostJson>() {
                    @Override
                    public void success(PostJson post, Response response) {
                        BTTable.PostTable.remove(post.id);
                        if (cb != null)
                            cb.success(post, response);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    /**
     * Attendance APIs
     */
    public void attendancesFromCourses(int[] courseIDs, final Callback<int[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.attendancesFromCourses(
                user.email,
                user.password,
                locale,
                courseIDs,
                new Callback<int[]>() {
                    @Override
                    public void success(int[] courseIDs, Response response) {
                        if (cb != null)
                            cb.success(courseIDs, response);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    public void attendanceFoundDevice(int attendanceID, final String uuid, final Callback<AttendanceJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.attendanceFoundDevice(
                user.email,
                user.password,
                locale,
                attendanceID,
                uuid,
                new Callback<AttendanceJson>() {
                    @Override
                    public void success(AttendanceJson attendance, Response response) {
                        if (cb != null)
                            cb.success(attendance, response);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                        BTTable.UUIDLISTSENDED_remove(uuid);
                    }
                });
    }

    public void attendanceToggleManually(int attendanceID, int userID, final Callback<AttendanceJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.attendanceToggleManually(
                user.email,
                user.password,
                locale,
                attendanceID,
                userID,
                new Callback<AttendanceJson>() {
                    @Override
                    public void success(AttendanceJson attendance, Response response) {
                        BTTable.updateAttendance(attendance);
                        if (cb != null)
                            cb.success(attendance, response);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    /**
     * Clicker APIs
     */
    public void clickerClick(int clickerID, int choice, final Callback<ClickerJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.clickerClick(
                user.email,
                user.password,
                locale,
                clickerID,
                choice,
                new Callback<ClickerJson>() {
                    @Override
                    public void success(ClickerJson clicker, Response response) {
                        BTTable.updateClicker(clicker);
                        if (cb != null)
                            cb.success(clicker, response);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        failureHandle(cb, retrofitError);
                    }
                });
    }

    /**
     * Notice APIs
     */
    public void noticeSeen(int noticeID, final Callback<NoticeJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        String locale = getResources().getConfiguration().locale.getLanguage();
        if (user == null)
            return;

        mBTAPI.seenNotice(
                user.email,
                user.password,
                locale,
                noticeID,
                new Callback<NoticeJson>() {
                    @Override
                    public void success(NoticeJson notice, Response response) {
                        if (cb != null)
                            cb.success(notice, response);
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
        else if (retrofitError.getResponse() != null) {
            if (retrofitError.getResponse().getStatus() == 503) {
                String title = getString(R.string.oopps);
                String message = getString(R.string.too_many_users_are_connecting);
                BTEventBus.getInstance().post(new ShowAlertDialogEvent(BTDialogFragment.DialogType.OK, title, message));
            } else {
                try {
                    ErrorJson errors = (ErrorJson) retrofitError.getBodyAs(ErrorJson.class);
                    if ("log".equals(errors.type))
                        BTDebug.LogError(retrofitError.getResponse().getStatus() + " : " + errors.message);
                    if ("toast".equals(errors.type))
                        BTEventBus.getInstance().post(new ShowToastEvent(errors.message));
                    if ("alert".equals(errors.type)) {

                        BTDialogFragment.DialogType type = BTDialogFragment.DialogType.OK;
                        String title = errors.title;
                        String message = errors.message;
                        BTDialogFragment.OnDialogListener listener = null;

                        if (retrofitError.getResponse().getStatus() == 441)
                            type = BTDialogFragment.DialogType.CONFIRM;

                        if (retrofitError.getResponse().getStatus() == 441 || retrofitError.getResponse().getStatus() == 442)
                            listener = new BTDialogFragment.OnDialogListener() {
                                @Override
                                public void onConfirmed(String edit) {
                                    PackagesHelper.updateApp(getApplicationContext());
                                }

                                @Override
                                public void onCanceled() {
                                }
                            };

                        if (retrofitError.getResponse().getStatus() == 401)
                            listener = new BTDialogFragment.OnDialogListener() {
                                @Override
                                public void onConfirmed(String edit) {
                                    BTPreference.clearUser(getApplicationContext());
                                    Intent intent = new Intent(getApplicationContext(), IntroductionActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }

                                @Override
                                public void onCanceled() {
                                    BTPreference.clearUser(getApplicationContext());
                                    Intent intent = new Intent(getApplicationContext(), IntroductionActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            };

                        BTEventBus.getInstance().post(new ShowAlertDialogEvent(type, title, message, listener));
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
