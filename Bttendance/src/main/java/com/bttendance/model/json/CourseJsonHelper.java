package com.bttendance.model.json;

import com.bttendance.model.BTTable;

/**
 * Created by TheFinestArtist on 2014. 5. 12..
 */
public class CourseJsonHelper {

    private UserJson mUser;
    private CourseJson mCourse;
    private CourseJsonSimple mCourseSimple;

    public CourseJsonHelper(UserJson user, int courseID) {
        mUser = user;
        mCourse = BTTable.MyCourseTable.get(courseID);
        mCourseSimple = user.getCourse(courseID);
    }

    public int getID() {
        if (mCourse != null && mCourse.id != 0)
            return mCourse.id;
        if (mCourseSimple != null && mCourseSimple.id != 0)
            return mCourseSimple.id;
        return 0;
    }

    public String getName() {
        if (mCourse != null && mCourse.name != null)
            return mCourse.name;
        if (mCourseSimple != null && mCourseSimple.name != null)
            return mCourseSimple.name;
        return null;
    }

    public String getSchoolName() {
        if (mCourse != null && mCourse.school.name != null)
            return mCourse.school.name;
        if (mCourseSimple != null && mUser.getSchool(mCourseSimple.school) != null)
            return mUser.getSchool(mCourseSimple.school).name;
        return null;
    }

    public String getProfessorName() {
        if (mCourse != null && mCourse.professor_name != null)
            return mCourse.professor_name;
        if (mCourseSimple != null && mCourseSimple.professor_name != null)
            return mCourseSimple.professor_name;
        return null;
    }

    public String getGrade() {
        if (mCourse != null && mCourse.grade != null)
            return mCourse.grade;
        return "0";
    }

    public int getStudentCount() {
        if (mCourse != null && mCourse.students_count > 0)
            return mCourse.students_count;
        if (mCourseSimple != null && mCourseSimple.students_count > 0)
            return mCourseSimple.students_count;
        return 0;
    }

    public int getClickerUsage() {
        if (mCourse != null && mCourse.clicker_usage > 0)
            return mCourse.clicker_usage;
        if (mCourseSimple != null && mCourseSimple.clicker_usage > 0)
            return mCourseSimple.clicker_usage;
        return 0;
    }

    public int getNoticeUsage() {
        if (mCourse != null && mCourse.notice_usage > 0)
            return mCourse.notice_usage;
        if (mCourseSimple != null && mCourseSimple.notice_usage > 0)
            return mCourseSimple.notice_usage;
        return 0;
    }

    public String getAttdCheckedAt() {
        if (mCourse != null && mCourse.attdCheckedAt != null)
            return mCourse.attdCheckedAt;
        return null;
    }
}