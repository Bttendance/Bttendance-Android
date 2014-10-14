package com.bttendance.model;

import android.util.SparseArray;

import com.bttendance.event.socket.AttendanceUpdatedEvent;
import com.bttendance.event.socket.ClickerUpdatedEvent;
import com.bttendance.event.socket.NoticeUpdatedEvent;
import com.bttendance.event.socket.PostUpdatedEvent;
import com.bttendance.helper.DateHelper;
import com.bttendance.model.json.AttendanceJson;
import com.bttendance.model.json.ClickerJson;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.NoticeJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.QuestionJson;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.model.json.UserJsonSimple;
import com.bttendance.view.Bttendance;
import com.squareup.otto.BTEventBus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class BTTable {

    public static String FILTER_STUDENTS_OF_COURSE = "students_of_course";
    public static String FILTER_ATTENDANCE_RECORDS_OF_COURSE = "attendance_records_of_course";
    public static String FILTER_CLICKER_RECORDS_OF_COURSE = "clicker_records_of_course";
    public static String FILTER_COURSE_OF_SCHOOL = "course_of_school";
    public static SparseArray<SchoolJson> AllSchoolTable = new SparseArray<SchoolJson>();
    public static SparseArray<CourseJson> MyCourseTable = new SparseArray<CourseJson>();
    public static SparseArray<PostJson> PostTable = new SparseArray<PostJson>();
    public static SparseArray<QuestionJson> MyQuestionTable = new SparseArray<QuestionJson>();
    public static int ATTENDANCE_STARTING_COURSE = -1;
    private static HashMap<String, SparseArray<UserJsonSimple>> mUser = new HashMap<String, SparseArray<UserJsonSimple>>();
    private static HashMap<String, SparseArray<CourseJson>> mCourse = new HashMap<String, SparseArray<CourseJson>>();
    // Found UUID list
    private static Map<String, String> UUIDLIST = new ConcurrentHashMap<String, String>();
    private static Map<String, String> UUIDLISTSENDED = new ConcurrentHashMap<String, String>();

    public static synchronized SparseArray<UserJsonSimple> getStudentsOfCourse(int courseID) {

        String filter = FILTER_STUDENTS_OF_COURSE + courseID;
        SparseArray<UserJsonSimple> array = mUser.get(filter);
        if (array == null) {
            array = new SparseArray<UserJsonSimple>();
            mUser.put(filter, array);
        }
        return array;
    }

    public static synchronized void updateStudentsOfCourse(int courseID, UserJsonSimple[] users) {

        String filter = FILTER_STUDENTS_OF_COURSE + courseID;
        SparseArray<UserJsonSimple> array = mUser.get(filter);
        if (array == null) {
            array = new SparseArray<UserJsonSimple>();
            mUser.put(filter, array);
        }

        for (UserJsonSimple user : users)
            array.append(user.id, user);
    }

    public static synchronized SparseArray<UserJsonSimple> getAttendanceRecordsOfCourse(int courseID) {

        String filter = FILTER_ATTENDANCE_RECORDS_OF_COURSE + courseID;
        SparseArray<UserJsonSimple> array = mUser.get(filter);
        if (array == null) {
            array = new SparseArray<UserJsonSimple>();
            mUser.put(filter, array);
        }
        return array;
    }

    public static synchronized void updateAttendanceRecordsOfCourse(int courseID, UserJsonSimple[] users) {

        String filter = FILTER_ATTENDANCE_RECORDS_OF_COURSE + courseID;
        SparseArray<UserJsonSimple> array = mUser.get(filter);
        if (array == null) {
            array = new SparseArray<UserJsonSimple>();
            mUser.put(filter, array);
        }

        for (UserJsonSimple user : users)
            array.append(user.id, user);
    }

    public static synchronized SparseArray<UserJsonSimple> getClickerRecordsOfCourse(int courseID) {

        String filter = FILTER_CLICKER_RECORDS_OF_COURSE + courseID;
        SparseArray<UserJsonSimple> array = mUser.get(filter);
        if (array == null) {
            array = new SparseArray<UserJsonSimple>();
            mUser.put(filter, array);
        }
        return array;
    }

    public static synchronized void updateClickerRecordsOfCourse(int courseID, UserJsonSimple[] users) {

        String filter = FILTER_CLICKER_RECORDS_OF_COURSE + courseID;
        SparseArray<UserJsonSimple> array = mUser.get(filter);
        if (array == null) {
            array = new SparseArray<UserJsonSimple>();
            mUser.put(filter, array);
        }

        for (UserJsonSimple user : users)
            array.append(user.id, user);
    }

    public static synchronized SparseArray<CourseJson> getCoursesOfSchool(int schoolID) {

        String filter = FILTER_COURSE_OF_SCHOOL + schoolID;
        SparseArray<CourseJson> array = mCourse.get(filter);
        if (array == null) {
            array = new SparseArray<CourseJson>();
            mCourse.put(filter, array);
        }
        return array;
    }

    public static synchronized void updateCoursesOfSchool(int schoolID, CourseJson[] courses) {

        String filter = FILTER_COURSE_OF_SCHOOL + schoolID;
        SparseArray<CourseJson> array = mCourse.get(filter);
        if (array == null) {
            array = new SparseArray<CourseJson>();
            mCourse.put(filter, array);
        }

        for (CourseJson course : courses)
            array.append(course.id, course);
    }

    public static synchronized SparseArray<PostJson> getPostsOfCourse(int courseID) {
        SparseArray<PostJson> posts = new SparseArray<PostJson>();
        for (int i = 0; i < PostTable.size(); i++)
            if (PostTable.valueAt(i).course.id == courseID)
                posts.append(PostTable.keyAt(i), PostTable.valueAt(i));
        return posts;
    }

    public static synchronized void updateClicker(ClickerJson clicker) {
        if (clicker == null)
            return;

        boolean isChanged = false;
        for (int i = 0; i < PostTable.size(); i++) {
            if (PostTable.valueAt(i).id == clicker.post.id
                    && PostTable.valueAt(i).clicker != null
                    && (PostTable.valueAt(i).clicker.updatedAt == null
                    || DateHelper.getTime(PostTable.valueAt(i).clicker.updatedAt) < DateHelper.getTime(clicker.updatedAt))) {
                PostTable.valueAt(i).clicker.choice_count = clicker.choice_count;
                PostTable.valueAt(i).clicker.a_students = clicker.a_students;
                PostTable.valueAt(i).clicker.b_students = clicker.b_students;
                PostTable.valueAt(i).clicker.c_students = clicker.c_students;
                PostTable.valueAt(i).clicker.d_students = clicker.d_students;
                PostTable.valueAt(i).clicker.e_students = clicker.e_students;
                isChanged = true;
            }
        }

        if (isChanged) {
            BTEventBus.getInstance().post(new ClickerUpdatedEvent());
        }
    }

    public static synchronized void updateAttendance(AttendanceJson attendance) {
        if (attendance == null)
            return;

        boolean isChanged = false;
        for (int i = 0; i < PostTable.size(); i++) {
            if (PostTable.valueAt(i).id == attendance.post.id
                    && PostTable.valueAt(i).attendance != null
                    && (PostTable.valueAt(i).attendance.updatedAt == null
                    || DateHelper.getTime(PostTable.valueAt(i).attendance.updatedAt) < DateHelper.getTime(attendance.updatedAt))) {
                PostTable.valueAt(i).attendance.checked_students = attendance.checked_students;
                PostTable.valueAt(i).attendance.late_students = attendance.late_students;
                isChanged = true;
            }
        }

        if (isChanged)
            BTEventBus.getInstance().post(new AttendanceUpdatedEvent());
    }

    public static synchronized void updateNotice(NoticeJson notice) {
        if (notice == null)
            return;

        boolean isChanged = false;
        for (int i = 0; i < PostTable.size(); i++) {
            if (PostTable.valueAt(i).id == notice.post.id
                    && PostTable.valueAt(i).notice != null
                    && (PostTable.valueAt(i).notice.updatedAt == null
                    || DateHelper.getTime(PostTable.valueAt(i).notice.updatedAt) < DateHelper.getTime(notice.updatedAt))) {
                PostTable.valueAt(i).notice.seen_students = notice.seen_students;
                isChanged = true;
            }
        }

        if (isChanged)
            BTEventBus.getInstance().post(new NoticeUpdatedEvent());
    }

    public static synchronized void updatePost(PostJson post) {
        if (post == null)
            return;

        boolean isChanged = false;
        for (int i = 0; i < PostTable.size(); i++) {
            if (PostTable.valueAt(i).id == post.id) {
                PostTable.append(post.id, post);
                isChanged = true;
            }
        }

        if (isChanged)
            BTEventBus.getInstance().post(new PostUpdatedEvent());
    }

    public static synchronized void UUIDLIST_add(String mac) {
        UUIDLIST.put(mac, mac);
    }

    public static synchronized Map<String, String> UUIDLIST() {
        return UUIDLIST;
    }

    public static synchronized void UUIDLIST_refresh() {
        UUIDLIST = new ConcurrentHashMap<String, String>();
    }

    public static synchronized Map<String, String> UUIDLISTSENDED() {
        return UUIDLISTSENDED;
    }

    public static synchronized void UUIDLISTSENDED_addAll(Map<String, String> map) {
        UUIDLISTSENDED.putAll(map);
    }

    public static synchronized void UUIDLISTSENDED_refresh() {
        UUIDLISTSENDED = new ConcurrentHashMap<String, String>();
    }

    public static synchronized boolean UUIDLISTSENDED_contains(String mac) {
        return BTTable.UUIDLISTSENDED.containsKey(mac);
    }

    public static synchronized void UUIDLISTSENDED_remove(String mac) {
        BTTable.UUIDLISTSENDED.remove(mac);
    }

    public static synchronized long getRefreshTimeTo() {

        long currentTime = DateHelper.getCurrentGMTTimeMillis();
        long timeTo = currentTime;

        for (int i = 0; i < BTTable.PostTable.size(); i++) {
            int key = BTTable.PostTable.keyAt(i);
            PostJson post = BTTable.PostTable.get(key);
            if (post.createdAt != null
                    && "attendance".equals(post.type)
                    && AttendanceJson.TYPE_AUTO.equals(post.attendance.type)
                    && currentTime - DateHelper.getTime(post.createdAt) < Bttendance.PROGRESS_DURATION) {
                if (timeTo > currentTime)
                    timeTo = Math.min(timeTo, DateHelper.getTime(post.createdAt) + Bttendance.PROGRESS_DURATION);
                else
                    timeTo = DateHelper.getTime(post.createdAt) + Bttendance.PROGRESS_DURATION;
            }

            if (post.createdAt != null
                    && "clicker".equals(post.type)
                    && currentTime - DateHelper.getTime(post.createdAt) < (post.clicker.progress_time + 5) * 1000) {
                if (timeTo > currentTime)
                    timeTo = Math.min(timeTo, DateHelper.getTime(post.createdAt) + (post.clicker.progress_time + 5) * 1000);
                else
                    timeTo = DateHelper.getTime(post.createdAt) + (post.clicker.progress_time + 5) * 1000;
            }
        }

        if (timeTo > currentTime)
            return timeTo;

        return -1;
    }

    public static synchronized long getAttdChekTimeTo() {
        long currentTime = DateHelper.getCurrentGMTTimeMillis();
        long timeTo = currentTime;

        for (int i = 0; i < BTTable.PostTable.size(); i++) {
            int key = BTTable.PostTable.keyAt(i);
            PostJson post = BTTable.PostTable.get(key);
            if (post.createdAt != null
                    && "attendance".equals(post.type)
                    && AttendanceJson.TYPE_AUTO.equals(post.attendance.type)
                    && currentTime - DateHelper.getTime(post.createdAt) < Bttendance.PROGRESS_DURATION
                    && timeTo < DateHelper.getTime(post.createdAt) + Bttendance.PROGRESS_DURATION) {
                timeTo = DateHelper.getTime(post.createdAt) + Bttendance.PROGRESS_DURATION;
            }
        }

        return timeTo;
    }

    public static synchronized Set<Integer> getAttdCheckingIds() {
        Set<Integer> checking = new HashSet<Integer>();

        for (int i = 0; i < BTTable.PostTable.size(); i++) {
            int key = BTTable.PostTable.keyAt(i);
            PostJson post = BTTable.PostTable.get(key);
            if (post.createdAt != null
                    && "attendance".equals(post.type)
                    && AttendanceJson.TYPE_AUTO.equals(post.attendance.type)
                    && DateHelper.getCurrentGMTTimeMillis() - DateHelper.getTime(post.createdAt) < Bttendance.PROGRESS_DURATION) {
                checking.add(post.attendance.id);
            }
        }

        return checking;
    }
}