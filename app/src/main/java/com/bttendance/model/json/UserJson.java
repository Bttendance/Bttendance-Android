package com.bttendance.model.json;

import com.bttendance.service.BTAPI;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */

public class UserJson extends BTJson {

    public int id;
    public String email;
    public String password;
    public String name;
    public String locale;
    public SchoolUserJson[] school_users;

    public class SchoolUserJson extends BTJson {
        public String identity;
        public String state;
        public SimpleSchoolJson school;
    }

    public boolean isProfessor() {
        if (school_users == null)
            return false;

        for (SchoolUserJson schoolUserJson : school_users)
            if (BTAPI.SchoolUserState.student.name().equals(schoolUserJson.state))
                return true;

        return false;
    }

    public boolean hasSchool(int schoolId) {
        for (SchoolUserJson schoolUserJson : school_users)
            if (schoolUserJson.school != null && schoolId == schoolUserJson.school.id)
                return true;

        return false;
    }

    public String getIdentity(int schoolId) {
        for (SchoolUserJson schoolUserJson : school_users)
            if (schoolUserJson.school != null && schoolId == schoolUserJson.school.id)
                return schoolUserJson.identity;

        return null;
    }
}