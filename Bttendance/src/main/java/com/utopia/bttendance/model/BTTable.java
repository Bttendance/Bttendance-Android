package com.utopia.bttendance.model;

import android.util.SparseArray;

import com.utopia.bttendance.helper.DateHelper;
import com.utopia.bttendance.model.json.CourseJson;
import com.utopia.bttendance.model.json.PostJson;
import com.utopia.bttendance.model.json.SchoolJson;
import com.utopia.bttendance.model.json.UserJson;
import com.utopia.bttendance.view.Bttendance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
    private static HashMap<String, SparseArray<UserJson>> mUser = new HashMap<String, SparseArray<UserJson>>();
    // filter: "my_course", "joinable_course"
    private static HashMap<String, SparseArray<CourseJson>> mCourse = new HashMap<String, SparseArray<CourseJson>>();
    // filter: "total_post", "course_id"
    private static HashMap<String, SparseArray<PostJson>> mPost = new HashMap<String, SparseArray<PostJson>>();
    // Found UUID list
    public static Set<String> UUIDLIST = new HashSet<String>();

    public static synchronized SparseArray<UserJson> getUsers(String filter) {
        SparseArray<UserJson> users = mUser.get(filter);
        if (users == null) {
            users = new SparseArray<UserJson>();
            mUser.put(filter, users);
        }
        return users;
    }

    public static synchronized SparseArray<CourseJson> getCourses(String filter) {
        SparseArray<CourseJson> courses = mCourse.get(filter);
        if (courses == null) {
            courses = new SparseArray<CourseJson>();
            mCourse.put(filter, courses);
        }
        return courses;
    }

    public static synchronized SparseArray<PostJson> getPosts(String filter) {
        SparseArray<PostJson> posts = mPost.get(filter);
        if (posts == null) {
            posts = new SparseArray<PostJson>();
            mPost.put(filter, posts);
        }
        return posts;
    }

    public static Set<Integer> getCheckingPostIds() {
        Set<Integer> checking = new HashSet<Integer>();

        for(int i = 0; i < BTTable.PostTable.size(); i++) {
            int key = BTTable.PostTable.keyAt(i);
            PostJson post = BTTable.PostTable.get(key);
            if (post.createdAt != null
                    && DateHelper.getCurrentGMTTimeMillis() - DateHelper.getTime(post.createdAt) < Bttendance.PROGRESS_DURATION ) {
                checking.add(post.id);
            }
        }

        for(int i = 0; i < BTTable.CourseTable.size(); i++) {
            int key = BTTable.CourseTable.keyAt(i);
            CourseJson course = BTTable.CourseTable.get(key);
            if (course.attdCheckedAt != null
                    && DateHelper.getCurrentGMTTimeMillis() - DateHelper.getTime(course.attdCheckedAt) < Bttendance.PROGRESS_DURATION ) {
                if (course.posts.length > 0) {
                    int max = -1;
                    for (int j : course.posts)
                        if (max < j)
                            max = j;
                    checking.add(max);
                }
            }
        }

        return checking;
    }

    public static int ATTENDANCE_STARTING_COURSE = -1;
}
