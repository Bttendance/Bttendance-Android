package com.bttendance.model;

/**
 * Created by TheFinestArtist on 2013. 11. 26..
 */
public class BTKey {

    public final static String EXTRA_TYPE = "type";
    public final static String EXTRA_SORT = "sort";
    public final static String EXTRA_COURSE_ID = "course_id";
    public final static String EXTRA_USER_ID = "user_id";
    public final static String EXTRA_POST_ID = "post_id";
    public final static String EXTRA_SCHOOL_ID = "school_id";
    public final static String EXTRA_QUESTION_ID = "question_id";

    public final static String EXTRA_TITLE = "title";
    public final static String EXTRA_MESSAGE = "message";

    public final static String EXTRA_FOR_PROFILE = "for_profile";


    public static class IntentKey {
        private static final String ACTION_PREFIX = "com.bttendance.intent.action.";
        public static final String ACTION_SHOW_COURSE = ACTION_PREFIX + "SHOW_COURSE";

    }
}
