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

    public int getAttendanceRate() {
        if (mCourse != null)
            return mCourse.attendance_rate;
        return -1;
    }

    public int getClickerRate() {
        if (mCourse != null)
            return mCourse.clicker_rate;
        return -1;
    }

    public int getNoticeUnseen() {
        if (mCourse != null)
            return mCourse.notice_unseen;
        return -1;
    }

    public int getStudentCount() {
        if (mCourse != null)
            return mCourse.students_count;
        return -1;
    }
}
