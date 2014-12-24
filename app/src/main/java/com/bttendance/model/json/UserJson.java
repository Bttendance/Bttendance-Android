package com.bttendance.model.json;

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
        // 'supervisor', 'student', 'administrator'
        public String state;
        public SimpleSchoolJson school;
    }

    public class SimpleSchoolJson extends BTJson {
        public int id;
        public String name;
    }

    public boolean isProfessor() {
        if (school_users == null)
            return false;

        for (SchoolUserJson schoolUserJson : school_users)
            if (!"student".equals(schoolUserJson.state))
                return true;

        return false;
    }
}