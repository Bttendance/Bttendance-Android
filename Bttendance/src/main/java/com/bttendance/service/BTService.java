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
import com.bttendance.R;
import com.bttendance.activity.sign.CatchPointActivity;
import com.bttendance.event.ShowDialogEvent;
import com.bttendance.event.refresh.RefreshCourseListEvent;
import com.bttendance.event.refresh.RefreshFeedEvent;
import com.bttendance.fragment.BTDialogFragment;
import com.bttendance.helper.PackagesHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.AttendanceJson;
import com.bttendance.model.json.ClickerJson;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.EmailJson;
import com.bttendance.model.json.ErrorJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.model.json.UserJsonSimple;
import com.bttendance.view.BeautiToast;
import com.squareup.otto.BTEventBus;

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
            .setServer(getServerDomain() + "/api")
            .build();
    private BTAPI mBTAPI;
    private ConnectivityManager mConnectivityManager;
    private LocalBinder mBinder = new LocalBinder();
    private Thread mAttendanceThread;
    private long mTimeTo;

    private static String getServerDomain() {
        if (!BTDebug.DEBUG)
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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void attendanceStart() {

//        BTDebug.LogError("mTimeTo = " + mTimeTo + ", attdchecktimeto = " + BTTable.getAttdChekTimeTo());
//
//        if (mAttendanceThread != null && mTimeTo == BTTable.getAttdChekTimeTo())
//            return;
//
//        if (mAttendanceThread != null && mTimeTo != BTTable.getAttdChekTimeTo()) {
//            mAttendanceThread.interrupt();
//            mAttendanceThread = null;
//        }
//
//        mTimeTo = BTTable.getAttdChekTimeTo();
//
//        mAttendanceThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    int i = 0;
//                    while (DateHelper.getCurrentGMTTimeMillis() < mTimeTo - 500) {
//                        if (i++ % 20 == 0) {
//                            BTDebug.LogError("Start Discovery");
//                            BluetoothHelper.startDiscovery();
//                        }
//                        Thread.sleep(500);
//                        Set<Integer> ids = BTTable.getCheckingPostIds();
//                        Set<String> list = new HashSet<String>();
//                        for (String mac : BTTable.UUIDLIST())
//                            list.add(mac);
//
//                        for (int id : ids) {
//                            for (String mac : list) {
//                                if (!BTTable.UUIDLISTSENDED_contains(mac)) {
//                                    attendanceFoundDevice(id, mac, new Callback<PostJson>() {
//                                        @Override
//                                        public void success(PostJson post, Response response) {
//                                            if (post != null)
//                                                BTDebug.LogInfo(post.toJson());
//                                        }
//
//                                        @Override
//                                        public void failure(RetrofitError retrofitError) {
//                                        }
//                                    });
//                                }
//                            }
//                        }
//                        BTTable.UUIDLISTSENDED_addAll(list);
//                        BTTable.UUIDLIST_refresh();
//                    }
//                    BTTable.UUIDLISTSENDED_refresh();
//                    attendanceStop();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        mAttendanceThread.start();
    }

    public void attendanceStop() {
        if (mAttendanceThread != null)
            mAttendanceThread.interrupt();
        mAttendanceThread = null;
        BTDebug.LogError("attendanceStop");
        BTEventBus.getInstance().post(new RefreshCourseListEvent());
        BTEventBus.getInstance().post(new RefreshFeedEvent());
    }

    public void signup(UserJson user, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.signup(
                user.username,
                user.full_name,
                user.email,
                user.password,
                user.device.type,
                user.device.uuid,
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
        String version = getString(R.string.app_version);
        mBTAPI.autoSignin(
                user.username,
                user.password,
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

    public void signin(String username, String password, String uuid, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        mBTAPI.signin(
                username,
                password,
                uuid,
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

        mBTAPI.forgotPassword(
                email,
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

    public void updateProfileImage(String profileImage, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.updateProfileImage(
                user.username,
                user.password,
                user.device.uuid,
                profileImage,
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

    public void updateEmail(String email, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.updateEmail(
                user.username,
                user.password,
                user.device.uuid,
                email,
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
        mBTAPI.updateFullName(
                user.username,
                user.password,
                user.device.uuid,
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

    public void feed(int page, final Callback<PostJson[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.feed(
                user.username,
                user.password,
                page,
                new Callback<PostJson[]>() {
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

    public void courses(final Callback<CourseJson[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.courses(
                user.username,
                user.password,
                new Callback<CourseJson[]>() {
                    @Override
                    public void success(CourseJson[] courses, Response response) {
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

    public void searchUser(String searchID, final Callback<UserJsonSimple> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.searchUser(
                user.username,
                user.password,
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

    public void updateNotificationKey(String notificationKey, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.updateNotificationKey(
                user.username,
                user.password,
                user.device.uuid,
                notificationKey,
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

    public void allSchools(final Callback<SchoolJson[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.allSchools(
                user.username,
                user.password,
                new Callback<SchoolJson[]>() {
                    @Override
                    public void success(SchoolJson[] schools, Response response) {
                        for (SchoolJson school : schools)
                            BTTable.AllSchoolTable.append(school.id, school);
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
        mBTAPI.schoolCourses(
                user.username,
                user.password,
                schoolId,
                new Callback<CourseJson[]>() {
                    @Override
                    public void success(CourseJson[] courses, Response response) {
                        BTTable.updateCoursesOfSchool(schoolId, courses);
                        if (cb != null)
                            cb.success(courses, response);
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
        mBTAPI.enrollSchool(
                user.username,
                user.password,
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

    public void courseCreate(String name, String number, int schoolID, String profName, final Callback<EmailJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.courseCreate(
                user.username,
                user.password,
                name,
                number,
                schoolID,
                profName,
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

    public void attendCourse(final int courseID, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.attendCourse(
                user.username,
                user.password,
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

    public void dettendCourse(final int courseID, final Callback<UserJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.dettendCourse(
                user.username,
                user.password,
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
        mBTAPI.courseFeed(
                user.username,
                user.password,
                courseID,
                page,
                new Callback<PostJson[]>() {
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

    public void courseStudents(final int courseId, final Callback<UserJsonSimple[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.courseStudents(
                user.username,
                user.password,
                courseId,
                new Callback<UserJsonSimple[]>() {
                    @Override
                    public void success(UserJsonSimple[] users, Response response) {
                        BTTable.updateStudentsOfCourse(courseId, users);
                        if (cb != null)
                            cb.success(users, response);
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
        mBTAPI.addManager(
                user.username,
                user.password,
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

    public void courseGrades(final int courseId, final Callback<UserJsonSimple[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.courseGrades(
                user.username,
                user.password,
                courseId,
                new Callback<UserJsonSimple[]>() {
                    @Override
                    public void success(UserJsonSimple[] users, Response response) {
                        BTTable.updateStudentsOfCourse(courseId, users);
                        if (cb != null)
                            cb.success(users, response);
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
        mBTAPI.courseExportGrades(
                user.username,
                user.password,
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

    public void postStartAttendance(int courseID, final Callback<PostJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.postStartAttendance(
                user.username,
                user.password,
                courseID,
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

    public void postStartClicker(int courseID, String message, int choiceCount, final Callback<PostJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.postStartClicker(
                user.username,
                user.password,
                courseID,
                message,
                choiceCount,
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
        mBTAPI.postCreateNotice(
                user.username,
                user.password,
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

    public void attendancesFromCourses(int[] courseIDs, final Callback<int[]> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.attendancesFromCourses(
                user.username,
                user.password,
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
        mBTAPI.attendanceFoundDevice(
                user.username,
                user.password,
                attendanceID,
                uuid,
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
//                        BTTable.UUIDLISTSENDED_remove(uuid);
                    }
                });
    }

    public void attendanceCheckManually(int attendanceID, int userID, final Callback<AttendanceJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.attendanceCheckManually(
                user.username,
                user.password,
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

    public void clickerConnect(int clickerID, String socketID, final Callback<ClickerJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.clickerConnect(
                user.username,
                user.password,
                clickerID,
                socketID,
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

    public void clickerClick(int clickerID, String choice, final Callback<ClickerJson> cb) {
        if (!isConnected())
            return;

        UserJson user = BTPreference.getUser(getApplicationContext());
        mBTAPI.clickerClick(
                user.username,
                user.password,
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
                BTDialogFragment dialog = new BTDialogFragment(BTDialogFragment.DialogType.OK, title, message);
                BTEventBus.getInstance().post(new ShowDialogEvent(dialog, null));
            } else {
                try {
                    ErrorJson errors = (ErrorJson) retrofitError.getBodyAs(ErrorJson.class);
                    if ("log".equals(errors.type))
                        BTDebug.LogError(retrofitError.getResponse().getStatus() + " : " + errors.message);
                    if ("toast".equals(errors.type))
                        BeautiToast.show(getApplicationContext(), errors.message);
                    if ("alert".equals(errors.type)) {
                        BTDialogFragment dialog = new BTDialogFragment(BTDialogFragment.DialogType.OK, errors.title, errors.message);
                        if (retrofitError.getResponse().getStatus() == 441)
                            dialog = new BTDialogFragment(BTDialogFragment.DialogType.CONFIRM, errors.title, errors.message);

                        if (retrofitError.getResponse().getStatus() == 441 || retrofitError.getResponse().getStatus() == 442)
                            dialog.setOnConfirmListener(new BTDialogFragment.OnConfirmListener() {
                                @Override
                                public void onConfirmed(String edit) {
                                    PackagesHelper.updateApp(getApplicationContext());
                                }

                                @Override
                                public void onCanceled() {
                                }
                            });

                        if (retrofitError.getResponse().getStatus() == 401)
                            dialog.setOnConfirmListener(new BTDialogFragment.OnConfirmListener() {
                                @Override
                                public void onConfirmed(String edit) {
                                    BTPreference.clearUser(getApplicationContext());
                                    Intent intent = new Intent(getApplicationContext(), CatchPointActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onCanceled() {
                                    BTPreference.clearUser(getApplicationContext());
                                    Intent intent = new Intent(getApplicationContext(), CatchPointActivity.class);
                                    startActivity(intent);
                                }
                            });

                        BTEventBus.getInstance().post(new ShowDialogEvent(dialog, null));
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
