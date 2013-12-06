package com.utopia.bttendance.model;

import android.util.SparseArray;

import com.utopia.bttendance.model.json.CourseJson;
import com.utopia.bttendance.model.json.PostJson;
import com.utopia.bttendance.model.json.SchoolJson;
import com.utopia.bttendance.model.json.UserJson;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class BTTable {

    public static String FILTER_MY_COURSE = "my_course";
    public static String FILTER_JOINABLE_COURSE = "joinable_course";
    public static String FILTER_TOTAL_POST = "total_post";
    private static String FILTER_COURSE_ID = "course_id";
    public static String getCourseIdFilter(int course_id) {
        return FILTER_COURSE_ID + course_id;
    }

    // key: post_id, value: post
    public static SparseArray<UserJson> UserTable = new SparseArray<UserJson>();
    // key: school_id, value: school
    public static SparseArray<SchoolJson> SchoolTable = new SparseArray<SchoolJson>();
    // key: course_id, value: course
    public static SparseArray<CourseJson> CourseTable = new SparseArray<CourseJson>();
    // key: post_id, value: post
    public static SparseArray<PostJson> PostTable = new SparseArray<PostJson>();
    // key: "course_id"
    private static HashMap<String, ArrayList<UserJson>> mUser = new HashMap<String, ArrayList<UserJson>>();
    // filter: "my_course", "joinable_course"
    private static HashMap<String, ArrayList<CourseJson>> mCourse = new HashMap<String, ArrayList<CourseJson>>();
    // filter: "total_post", "course_id"
    private static HashMap<String, ArrayList<PostJson>> mPost = new HashMap<String, ArrayList<PostJson>>();

    public static synchronized ArrayList<UserJson> getUsers(String filter) {
        ArrayList<UserJson> users = mUser.get(filter);
        if (users == null) {
            users = new ArrayList<UserJson>();
            mUser.put(filter, users);
        }
        return users;
    }

    public static synchronized ArrayList<CourseJson> getCourses(String filter) {
        ArrayList<CourseJson> courses = mCourse.get(filter);
        if (courses == null) {
            courses = new ArrayList<CourseJson>();
            mCourse.put(filter, courses);
        }
        return courses;
    }

    public static synchronized ArrayList<PostJson> getPosts(String filter) {
        ArrayList<PostJson> posts = mPost.get(filter);
        if (posts == null) {
            posts = new ArrayList<PostJson>();
            mPost.put(filter, posts);
        }
        return posts;
    }
}
