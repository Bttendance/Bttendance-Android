package com.bttendance.service.request;

import com.bttendance.model.json.CourseJson;
import com.bttendance.service.BTAPI;

/**
 * Created by TheFinestArtist on 12/23/14.
 */
public class UserPutRequest {

    public User user;

    public UserPutRequest() {
        this.user = new User();
    }

    public class User {
        public String email;
        public String password;
        public String new_password;
        public String name;
        public String locale;
        public Device[] devices_attributes;
        public School[] schools_users_attributes;
        public Course[] courses_users_attributes;
    }

    public class Device {
        public String id;
        public String platform;
        public String uuid;
        public String mac_address;
        public String notification_key;
        public String _destroy;
    }

    public class School {
        public String school_id;
        public String identity;
        public String is_supervisor;
        public String is_student;
        public String is_administrator;
        public String _destroy;
    }

    public class Course {
        public String course_id;
        public String state;
        public String _destroy;
    }

    /**
     * Public Request Create
     */
    public UserPutRequest updateEmail(String email) {
        this.user.email = email;
        return this;
    }

    public UserPutRequest updateName(String name) {
        this.user.name = name;
        return this;
    }

    public UserPutRequest updatePassword(String oldPass, String newPass) {
        this.user.password = oldPass;
        this.user.new_password = newPass;
        return this;
    }

    public UserPutRequest updateNotificationKey(String macAddress, String notiKey) {
        this.user.devices_attributes = new Device[]{new Device()};
        this.user.devices_attributes[0].platform = BTAPI.DevicePlatform.android.name();
        this.user.devices_attributes[0].mac_address = macAddress;
        this.user.devices_attributes[0].notification_key = notiKey;
        return this;
    }

    public UserPutRequest updateIdentity(int schoolId, String identity) {
        this.user.schools_users_attributes = new School[]{new School()};
        this.user.schools_users_attributes[0].school_id = "" + schoolId;
        this.user.schools_users_attributes[0].identity = identity;
        return this;
    }

    public UserPutRequest updateNewlyCreatedCourse(CourseJson course) {
        if (course.school != null) {
            this.user.schools_users_attributes = new School[]{new School()};
            this.user.schools_users_attributes[0].school_id = "" + course.school.id;
            this.user.schools_users_attributes[0].is_supervisor = Boolean.toString(true);
        }

        this.user.courses_users_attributes = new Course[]{new Course()};
        this.user.courses_users_attributes[0].course_id = "" + course.id;
        this.user.courses_users_attributes[0].state = BTAPI.CourseUserState.supervising.name();
        return this;
    }

    public UserPutRequest updateAttendingCourse(CourseJson course, String identity) {
        if (course.school != null && identity != null) {
            this.user.schools_users_attributes = new School[]{new School()};
            this.user.schools_users_attributes[0].school_id = "" + course.school.id;
            this.user.schools_users_attributes[0].is_student = Boolean.toString(true);
            this.user.schools_users_attributes[0].identity = identity;
        }

        this.user.courses_users_attributes = new Course[]{new Course()};
        this.user.courses_users_attributes[0].course_id = "" + course.id;
        this.user.courses_users_attributes[0].state = BTAPI.CourseUserState.attending.name();
        return this;
    }

}