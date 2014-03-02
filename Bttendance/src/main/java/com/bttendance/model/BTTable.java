package com.bttendance.model;

import android.util.SparseArray;

import com.bttendance.helper.DateHelper;
import com.bttendance.helper.IntArrayHelper;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.view.Bttendance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class BTTable {

    public static String FILTER_MY_SCHOOL = "my_school";
    public static String FILTER_MY_COURSE = "my_course";
    public static String FILTER_MY_POST = "my_post";

    // key: post_id, value: post
    public static SparseArray<UserJson> UserTable = new SparseArray<UserJson>();
    // key: school_id, value: school
    public static SparseArray<SchoolJson> SchoolTable = new SparseArray<SchoolJson>();
    // key: course_id, value: course
    public static SparseArray<CourseJson> CourseTable = new SparseArray<CourseJson>();
    // key: post_id, value: post
    public static SparseArray<PostJson> PostTable = new SparseArray<PostJson>();

    public static SparseArray<Integer> PostGradeTable = new SparseArray<Integer>();
    public static SparseArray<Integer> CourseGradeTable = new SparseArray<Integer>();

    // filter: none
    private static HashMap<String, SparseArray<UserJson>> mUser = new HashMap<String, SparseArray<UserJson>>();
    // filter: my_course
    private static HashMap<String, SparseArray<CourseJson>> mCourse = new HashMap<String, SparseArray<CourseJson>>();
    // filter: my_post
    private static HashMap<String, SparseArray<PostJson>> mPost = new HashMap<String, SparseArray<PostJson>>();
    // filter: my_school
    private static HashMap<String, SparseArray<SchoolJson>> mSchool = new HashMap<String, SparseArray<SchoolJson>>();

    // Found UUID list
    private static Set<String> UUIDLIST = new HashSet<String>();
    private static Set<String> UUIDLISTSENDED = new HashSet<String>();

    public static synchronized SparseArray<UserJson> getUsers(String filter) {
        SparseArray<UserJson> users = mUser.get(filter);
        if (users == null) {
            users = new SparseArray<UserJson>();
            mUser.put(filter, users);
        }
        return users;
    }

    public static synchronized SparseArray<UserJson> getUsersOfCourse(int courseID) {
        SparseArray<UserJson> users = new SparseArray<UserJson>();
        for (int i = 0; i < UserTable.size(); i++)
            if (IntArrayHelper.contains(UserTable.valueAt(i).attending_courses, courseID))
                users.append(UserTable.keyAt(i), UserTable.valueAt(i));
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

    public static synchronized SparseArray<CourseJson> getCoursesOfSchool(int schoolID) {
        SparseArray<CourseJson> courses = new SparseArray<CourseJson>();
        for (int i = 0; i < CourseTable.size(); i++)
            if (CourseTable.valueAt(i).school == schoolID)
                courses.append(CourseTable.keyAt(i), CourseTable.valueAt(i));
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

    public static synchronized SparseArray<PostJson> getPostsOfCourse(int courseID) {
        SparseArray<PostJson> posts = new SparseArray<PostJson>();
        for (int i = 0; i < PostTable.size(); i++)
            if (PostTable.valueAt(i).course == courseID)
                posts.append(PostTable.keyAt(i), PostTable.valueAt(i));
        return posts;
    }

    public static synchronized SparseArray<SchoolJson> getSchools(String filter) {
        SparseArray<SchoolJson> schools = mSchool.get(filter);
        if (schools == null) {
            schools = new SparseArray<SchoolJson>();
            mSchool.put(filter, schools);
        }
        return schools;
    }

    public static synchronized void UUIDLIST_add(String mac) {
        UUIDLIST.add(mac);
    }

    public static synchronized Set<String> UUIDLIST() {
        return UUIDLIST;
    }

    public static synchronized void UUIDLIST_refresh() {
        UUIDLIST = new HashSet<String>();
    }

    public static synchronized Set<String> UUIDLISTSENDED() {
        return UUIDLISTSENDED;
    }

    public static synchronized void UUIDLISTSENDED_addAll(Set<String> list) {
        UUIDLISTSENDED.addAll(list);
    }

    public static synchronized void UUIDLISTSENDED_refresh() {
        UUIDLISTSENDED = new HashSet<String>();
    }

    public static synchronized boolean UUIDLISTSENDED_contains(String mac) {
        return BTTable.UUIDLISTSENDED.contains(mac);
    }

    public static synchronized void UUIDLISTSENDED_remove(String mac) {
        BTTable.UUIDLISTSENDED.remove(mac);
    }

    public static synchronized long getAttdChekTimeTo() {
        long currentTime = DateHelper.getCurrentGMTTimeMillis();
        long timeTo = currentTime;

        for(int i = 0; i < BTTable.PostTable.size(); i++) {
            int key = BTTable.PostTable.keyAt(i);
            PostJson post = BTTable.PostTable.get(key);
            if (post.createdAt != null
                    && currentTime - DateHelper.getTime(post.createdAt) < Bttendance.PROGRESS_DURATION
                    && timeTo < DateHelper.getTime(post.createdAt) + Bttendance.PROGRESS_DURATION) {
                timeTo = DateHelper.getTime(post.createdAt) + Bttendance.PROGRESS_DURATION;
            }
        }

        for(int i = 0; i < BTTable.CourseTable.size(); i++) {
            int key = BTTable.CourseTable.keyAt(i);
            CourseJson course = BTTable.CourseTable.get(key);
            if (course.attdCheckedAt != null
                    && currentTime - DateHelper.getTime(course.attdCheckedAt) < Bttendance.PROGRESS_DURATION
                    && timeTo < DateHelper.getTime(course.attdCheckedAt) + Bttendance.PROGRESS_DURATION) {
                timeTo = DateHelper.getTime(course.attdCheckedAt) + Bttendance.PROGRESS_DURATION;
            }
        }

        return timeTo;
    }

    public static synchronized Set<Integer> getCheckingPostIds() {
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
