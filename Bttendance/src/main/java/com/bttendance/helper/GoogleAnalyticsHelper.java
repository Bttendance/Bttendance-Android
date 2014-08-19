//
//package com.bttendance.helper;
//
//import android.content.Context;
//import android.os.Bundle;
//
//import com.bttendance.BTDebug;
//import com.bttendance.R;
//import com.bttendance.view.BeautiToast;
//import com.google.analytics.tracking.android.EasyTracker;
//import com.google.analytics.tracking.android.Tracker;
//
//import java.util.Calendar;
//import java.util.Date;
//import java.util.TimeZone;
//
///**
// * @author The Finest Artist
// * @formatter:off
// * @since 2013-02-15 Google Analytics Helper Methods Use Google Analytics Id to
// *        initiate Tracker ***Every Methods are static!!! Init & Set Tracker
// *        Constructor Print Log Start Session Send View Send Timing Send Event
// */
//
//// @formatter:on
//public class GoogleAnalyticsHelper {
//
//    private static String TAG = "GoogleAnalyticsHelper";
//
//    private static long mSignedDateAsInt;
//    private static long mPartiesCount;
//    private static long mCollectionsCount;
//    private static long mInstalledDateAsInt;
//
//    /**
//     * **********************************
//     * Google Analytics Tracker Methods
//     * ***********************************
//     */
//
//    private static Tracker getTracker() {
//        return EasyTracker.getTracker();
//    }
//
//    public static void initTracker(Context context) {
//
//        // App Version
//        EasyTracker.getTracker().setAppVersion(context.getString(R.string.app_version));
//
//        // App Name
//        EasyTracker.getTracker().setAppName(context.getString(R.string.app_name_capital));
//
//        // Custom Dimension and Metric Setting
//
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeZone(TimeZone.getTimeZone("GMT-8"));
//
//        Long signedDateinLong = VinglePreference.getSignedDate(context);
//        Date signedDate = new Date(signedDateinLong);
//        cal.setTime(signedDate);
//        int signedYear = cal.get(Calendar.YEAR);
//        int signedMonth = cal.get(Calendar.MONTH) + 1;
//        int signedDay = cal.get(Calendar.DAY_OF_MONTH);
//        int signedDateAsInt = signedYear * 10000 + signedMonth * 100 + signedDay;
//        mSignedDateAsInt = signedDateAsInt;
//        if (signedDateinLong == 0)
//            mSignedDateAsInt = 0;
//
//        getTracker().setCustomMetric(1, mSignedDateAsInt);
//        getTracker().setCustomDimension(1, "" + mSignedDateAsInt);
//
//        AuthJson user = VingleInstanceData.getCurrentUser(context);
//        if (user != null) {
//            mPartiesCount = user.parties_count;
//            mCollectionsCount = user.following_collections_count;
//        } else {
//            mPartiesCount = 0l;
//            mPartiesCount = 0l;
//        }
//
//        getTracker().setCustomMetric(2, mPartiesCount);
//        getTracker().setCustomMetric(3, mCollectionsCount);
//
//        getTracker().setCustomDimension(2, "" + mPartiesCount);
//        getTracker().setCustomDimension(3, "" + mCollectionsCount);
//
//        Long installedDateinLong = VinglePreference.getInstalledDate(context);
//        Date installedDate = new Date(installedDateinLong);
//        cal.setTime(installedDate);
//        int installededYear = cal.get(Calendar.YEAR);
//        int installedMonth = cal.get(Calendar.MONTH) + 1;
//        int installedDay = cal.get(Calendar.DAY_OF_MONTH);
//        int installedDateAsInt = installededYear * 10000 + installedMonth * 100 + installedDay;
//        mInstalledDateAsInt = installedDateAsInt;
//        getTracker().setCustomMetric(4, mInstalledDateAsInt);
//        getTracker().setCustomDimension(4, "" + mInstalledDateAsInt);
//
//        BTDebug.LogDebug(TAG, "sign : " + mSignedDateAsInt + ", party : " + mPartiesCount
//                + ", collection : " + mCollectionsCount + ", install : " + mInstalledDateAsInt
//                + ", time : " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE));
//    }
//
//    /**
//     * **********************************
//     * Google Analytics Private Methods
//     * ***********************************
//     */
//    private static void printMethod() {
//        if (getTracker() != null)
//            BTDebug.LogDebug(TAG, getMethodName(2));
//        else
//            BTDebug.LogDebug(TAG, "ERROR : " + getMethodName(2));
//    }
//
//    private static void printMethod(int count) {
//        if (getTracker() != null)
//            BTDebug.LogDebug(TAG, getMethodName(2) + " : " + count);
//        else
//            BTDebug.LogDebug(TAG, "ERROR : " + getMethodName(2) + " : " + count);
//    }
//
//    private static void printMethod(String name) {
//        if (getTracker() != null)
//            BTDebug.LogDebug(TAG, getMethodName(2) + " : " + name);
//        else
//            BTDebug.LogDebug(TAG, "ERROR : " + getMethodName(2) + " : " + name);
//    }
//
//    private static void printMethod(long interval, String info) {
//        if (getTracker() != null)
//            BTDebug.LogDebug(TAG, getMethodName(2) + " : " + interval + ", " + info);
//        else
//            BTDebug.LogDebug(TAG, "ERROR : " + getMethodName(2) + " : " + interval + ", " + info);
//    }
//
//    private static void toastMethod(Context context) {
//        if (BTDebug.GOOGLE_TOAST)
//            if (getTracker() != null)
//                BeautiToast.show(context, getMethodName(2));
//            else
//                BeautiToast.show(context, "ERROR : " + getMethodName(2));
//    }
//
//    @SuppressWarnings("unused")
//    private static void toastMethod(Context context, int count) {
//        if (BTDebug.GOOGLE_TOAST)
//            if (getTracker() != null)
//                BeautiToast.show(context, getMethodName(2) + " : " + count);
//            else
//                BeautiToast.show(context, "ERROR : " + getMethodName(2) + " : " + count);
//    }
//
//    private static void toastMethod(Context context, String title) {
//        if (BTDebug.GOOGLE_TOAST)
//            if (getTracker() != null)
//                BeautiToast.show(context, getMethodName(2) + " : " + title);
//            else
//                BeautiToast.show(context, "ERROR : " + getMethodName(2) + " : " + title);
//    }
//
//    private static void toastMethod(Context context, long interval, String info) {
//        if (BTDebug.GOOGLE_TOAST)
//            if (getTracker() != null)
//                BeautiToast.show(context, getMethodName(2) + " : " + interval + ", " + info);
//            else
//                BeautiToast.show(context, "ERROR : " + getMethodName(2) + " : " + interval + ", "
//                        + info);
//    }
//
//    /**
//     * Get the method name for a depth in call stack. Utility function
//     *
//     * @param depth depth in the call stack (0 is getStackTrace(), 1 is
//     *              getMethodName and 2 is invoking method...)
//     * @return method name
//     */
//    private static String getMethodName(final int depth) {
//        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
//        return ste[2 + depth].getMethodName();
//    }
//
//    /**
//     * **********************************
//     * Google Analytics Start Session Methods
//     * ***********************************
//     */
//    public static void startSession(Context context) {
//        if (getTracker() != null)
//            getTracker().setStartSession(true);
//        printMethod();
//    }
//
//    /**
//     * **********************************
//     * Google Analytics Send View Methods
//     * ***********************************
//     */
//
//    // FEED_NEWS, FEED_USER, FEED_INTEREST, FEED_COLLECTION, CARD_SHOW, EXPLORE,
//    // FIND_FRIENDS, SETTINGS, DISCOVER_INTERESTS, DISCOVER_COLLECTIONS,
//    // CLIP, COMMENT,
//    // SIDE_MAIN, SIDE_PARTY, SIDE_COLLECTION, SIDE_EXPLORE,
//    // NOTIFICATION,
//    // NULL,
//    public static void sendView(Context context, VingleFragmentType type, Bundle args) {
//
//        int id = 0;
//        String title = "";
//        int child = 0;
//
//        if (args != null) {
//            id = args.getInt(BundleKey.EXTRA_GA_ID);
//            title = args.getString(BundleKey.EXTRA_GA_TITLE);
//            child = args.getInt(BundleKey.EXTRA_GA_CHILD);
//        }
//
//        switch (type) {
//            case FEED_NEWS:
//                sendViewFeed(context);
//                break;
//            case FEED_USER:
//                sendViewUser(context, id, title);
//                break;
//            case FEED_INTEREST:
//                sendViewInterest(context, id, title);
//                break;
//            case FEED_COLLECTION:
//                sendViewCollection(context, id, title);
//                break;
//            case CARD_SHOW:
//                sendViewCard(context, id, title);
//                break;
//            case MY_INTERESTS:
//                sendViewInterestMy(context);
//                break;
//            case MY_COLLECTIONS:
//                break;
//            case EXPLORE:
//                break;
//            case FIND_FRIENDS:
//                sendViewFindFriends(context);
//                break;
//            case INVITE_FRIENDS:
//                sendViewInviteFriends(context);
//                break;
//            case SETTINGS:
//                sendViewSetting(context);
//                break;
//            case SETTINGS_LANGUAGE:
//                sendViewSettingLanguage(context);
//                break;
//            case SETTINGS_NOTI:
//                sendViewSettingNoti(context);
//                break;
//            case SETTINGS_SOCIAL:
//                sendViewSettingSocial(context);
//                break;
//            case DISCOVER_INTERESTS:
//                sendViewDiscoverParty(context);
//                break;
//            case DISCOVER_COLLECTIONS:
//                sendViewDiscoverCollection(context);
//                break;
//            case WEB_VIEW:
//                sendViewWebView(context, title);
//                break;
//
//            default:
//                break;
//        }
//    }
//
//    /* Main View */
//    public static void sendViewSplash(Context context) {
//
//        if (getTracker() != null)
//            getTracker().sendView("Splash");
//        printMethod();
//        toastMethod(context);
//    }
//
//    private static void sendViewFeed(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("Feed");
//        printMethod();
//        toastMethod(context);
//    }
//
//    private static void sendViewCard(Context context, int card_id, String card_title) {
//        if (getTracker() != null)
//            getTracker().sendView("Card/" + card_id + "/" + card_title);
//        printMethod(card_id, card_title);
//        toastMethod(context, card_id, card_title);
//    }
//
//    public static void sendViewYouTube(Context context, int card_id, String card_title) {
//        if (getTracker() != null)
//            getTracker().sendView("YouTube/" + card_id + "/" + card_title);
//        printMethod(card_id, card_title);
//        toastMethod(context, card_id, card_title);
//    }
//
//    private static void sendViewInterest(Context context, int party_id, String party_title) {
//        if (getTracker() != null)
//            getTracker().sendView("Interest/" + party_id + "/" + party_title);
//        printMethod(party_id, party_title);
//        toastMethod(context, party_id, party_title);
//    }
//
//    private static void sendViewInterestMy(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("Interest-My");
//        printMethod();
//        toastMethod(context);
//    }
//
//    private static void sendViewCollection(Context context, int collection_id,
//                                           String collection_title) {
//        if (getTracker() != null)
//            getTracker().sendView(
//                    "Collection/" + collection_id + "/" + collection_title);
//        printMethod(collection_id, collection_title);
//        toastMethod(context, collection_id, collection_title);
//    }
//
//    public static void sendViewCollectionCurated(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("Collection-Curated");
//        printMethod();
//        toastMethod(context);
//    }
//
//    public static void sendViewCollectionFollowing(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("Collection-Following");
//        printMethod();
//        toastMethod(context);
//    }
//
//    private static void sendViewUser(Context context, int user_id, String user_name) {
//        if (VinglePreference.getUserName(context).equals(user_name)) {
//            if (getTracker() != null)
//                getTracker().sendView("My-Page/" + user_id + "/" + user_name);
//        } else {
//            if (getTracker() != null)
//                getTracker().sendView("User/" + user_id + "/" + user_name);
//        }
//        printMethod(user_id, user_name);
//        toastMethod(context, user_id, user_name);
//    }
//
//    public static void sendViewExploreParty(Context context, int keyword_id, String keyword_title) {
//        if (getTracker() != null)
//            getTracker().sendView(
//                    "Explore-Parties/" + keyword_id + "/" + keyword_title);
//        printMethod(keyword_id, keyword_title);
//        toastMethod(context, keyword_id, keyword_title);
//    }
//
//    public static void sendViewExploreCollection(Context context, int keyword_id, String keyword_title) {
//        if (getTracker() != null)
//            getTracker().sendView(
//                    "Explore-Collections/" + keyword_id + "/" + keyword_title);
//        printMethod(keyword_id, keyword_title);
//        toastMethod(context, keyword_id, keyword_title);
//    }
//
//    public static void sendViewExploreCard(Context context, int keyword_id, String keyword_title) {
//        if (getTracker() != null)
//            getTracker().sendView(
//                    "Explore-Cards/" + keyword_id + "/" + keyword_title);
//        printMethod(keyword_id, keyword_title);
//        toastMethod(context, keyword_id, keyword_title);
//    }
//
//    private static void sendViewDiscoverParty(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("Discover-Parties");
//        printMethod();
//        toastMethod(context);
//    }
//
//    private static void sendViewDiscoverCollection(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("Discover-Collections");
//        printMethod();
//        toastMethod(context);
//    }
//
//    private static void sendViewWebView(Context context, String url) {
//        if (getTracker() != null)
//            getTracker().sendView("Web-View/" + url);
//        printMethod(url);
//        toastMethod(context, url);
//    }
//
//    public static void sendViewFindFriends(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("Find-Friends");
//        printMethod();
//        toastMethod(context);
//    }
//
//    private static void sendViewInviteFriends(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("Invite-Friend");
//        printMethod();
//        toastMethod(context);
//    }
//
//    private static void sendViewSetting(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("Setting");
//        printMethod();
//        toastMethod(context);
//    }
//
//    private static void sendViewSettingLanguage(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("Setting-Language");
//        printMethod();
//        toastMethod(context);
//    }
//
//    private static void sendViewSettingNoti(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("Setting-Noti");
//        printMethod();
//        toastMethod(context);
//    }
//
//    private static void sendViewSettingSocial(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("Setting-Social");
//        printMethod();
//        toastMethod(context);
//    }
//
//    /* Notification */
//    public static void sendViewNotification(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("Notification");
//        printMethod();
//        toastMethod(context);
//    }
//
//    /* Sign In & Up */
//    public static void sendViewSignMain(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("Sign-Main");
//        printMethod();
//        toastMethod(context);
//    }
//
//    public static void sendViewSignIn(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("Sign-In");
//        printMethod();
//        toastMethod(context);
//    }
//
//    public static void sendViewSignUp(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("Sign-Up");
//        printMethod();
//        toastMethod(context);
//    }
//
//    public static void sendViewSignUpFacebook(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("Sign-Up-Facebook");
//        printMethod();
//        toastMethod(context);
//    }
//
//    public static void sendViewSignConnect(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("Sign-Connect");
//        printMethod();
//        toastMethod(context);
//    }
//
//    /* Sign Up Process */
//    public static void sendViewSPJoinParties(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("SP-Join-Parties");
//        printMethod();
//        toastMethod(context);
//    }
//
//    public static void sendViewSPFollowCollections(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("SP-Follow-Collections");
//        printMethod();
//        toastMethod(context);
//    }
//
//    public static void sendViewSPFindFriends(Context context) {
//        if (getTracker() != null)
//            getTracker().sendView("SP-Find-Friends");
//        printMethod();
//        toastMethod(context);
//    }
//
//    /**
//     * **********************************
//     * Google Analytics Send Timing Methods
//     * ***********************************
//     */
//
//    public static void sendTimingInitialize(Context context, long intervalInMilliseconds) {
//        if (getTracker() != null)
//            getTracker().sendTiming("Initialize", intervalInMilliseconds, "Initialize", null);
//        printMethod(intervalInMilliseconds, "");
//    }
//
//    /**
//     * **********************************
//     * Google Analytics Send Event Methods
//     * ***********************************
//     */
//
//    /* Event Category */
//    final static String EVENT_CATEGORY_ANDROID_SIGN_UP = "Android Sign Up";
//    final static String EVENT_CATEGORY_ANDROID_USER_ACTION = "Android User Action";
//
//    /* Event Action */
//    final static String EVENT_ACTION_BUTTON_PRESS_ONE_TIME = "Button Press One Time";
//    final static String EVENT_ACTION_BUTTON_PRESS_MULTI_TIME = "Button Press Multi Time";
//    final static String EVENT_ACTION_COUNT_NUMBER = "Count Number";
//
//    /* Sign In & Up */
//    public static void sendEventSignIn(Context context) {
//        if (getTracker() != null) {
//            getTracker()
//                    .sendEvent(EVENT_CATEGORY_ANDROID_SIGN_UP,
//                            EVENT_ACTION_BUTTON_PRESS_ONE_TIME, "Sign In",
//                            null);
//        }
//        printMethod();
//    }
//
//    public static void sendEventSignUpFacebook(Context context) {
//        if (getTracker() != null) {
//            getTracker().sendEvent(EVENT_CATEGORY_ANDROID_SIGN_UP,
//                    EVENT_ACTION_BUTTON_PRESS_ONE_TIME, "Sign Up Facebook",
//                    null);
//        }
//        printMethod();
//    }
//}// end of class
