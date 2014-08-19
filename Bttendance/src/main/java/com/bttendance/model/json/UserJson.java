package com.bttendance.model.json;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */

public class UserJson extends BTJson {

    public String email;
    public String password;
    public String full_name;
    public DeviceJsonSimple device;
    public SettingJsonSimple setting;
    public CourseJsonSimple[] supervising_courses;
    public CourseJsonSimple[] attending_courses;
    public SchoolJsonSimple[] employed_schools;
    public SchoolJsonSimple[] enrolled_schools;
    public IdentificationJsonSimple[] identifications;
    public QuestionJsonSimple[] questions;

    public boolean supervising(int courseID) {
        for (CourseJsonSimple course : supervising_courses)
            if (course.id == courseID)
                return true;

        return false;
    }

    public boolean employed(int schoolID) {
        for (SchoolJsonSimple school : employed_schools)
            if (school.id == schoolID)
                return true;

        return false;
    }

    public boolean enrolled(int schoolID) {
        for (SchoolJsonSimple school : enrolled_schools)
            if (school.id == schoolID)
                return true;

        return false;
    }

    public String getIdentity(int schoolID) {
        for (IdentificationJsonSimple identification : identifications)
            if (identification.school == schoolID)
                return identification.identity;

        return null;
    }

    public CourseJsonSimple[] getOpenedCourses() {

        int opened = 0;

        for (CourseJsonSimple course : supervising_courses)
            if (course.opened)
                opened++;
        for (CourseJsonSimple course : attending_courses)
            if (course.opened)
                opened++;

        CourseJsonSimple[] newArray = new CourseJsonSimple[opened];

        int i = 0;

        for (CourseJsonSimple course : supervising_courses)
            if (course.opened) {
                newArray[i] = course;
                i++;
            }

        for (CourseJsonSimple course : attending_courses)
            if (course.opened) {
                newArray[i] = course;
                i++;
            }

        return newArray;

    }

    public CourseJsonSimple[] getClosedCourses() {

        int closed = 0;

        for (CourseJsonSimple course : supervising_courses)
            if (!course.opened)
                closed++;
        for (CourseJsonSimple course : attending_courses)
            if (!course.opened)
                closed++;

        CourseJsonSimple[] newArray = new CourseJsonSimple[closed];

        int i = 0;

        for (CourseJsonSimple course : supervising_courses)
            if (!course.opened) {
                newArray[i] = course;
                i++;
            }

        for (CourseJsonSimple course : attending_courses)
            if (!course.opened) {
                newArray[i] = course;
                i++;
            }

        return newArray;

    }

    public CourseJsonSimple[] getCourses() {

        CourseJsonSimple[] newArray = new CourseJsonSimple[supervising_courses.length + attending_courses.length];

        int i = 0;

        for (CourseJsonSimple course : supervising_courses) {
            newArray[i] = course;
            i++;
        }

        for (CourseJsonSimple course : attending_courses) {
            newArray[i] = course;
            i++;
        }

        return newArray;

    }

    public CourseJsonSimple getCourse(int courseID) {

        for (CourseJsonSimple course : supervising_courses)
            if (course.id == courseID)
                return course;

        for (CourseJsonSimple course : attending_courses)
            if (course.id == courseID)
                return course;

        return null;
    }

    public SchoolJsonSimple getSchool(int schoolID) {

        for (SchoolJsonSimple school : employed_schools)
            if (school.id == schoolID)
                return school;

        for (SchoolJsonSimple school : enrolled_schools)
            if (school.id == schoolID)
                return school;

        return null;
    }
}