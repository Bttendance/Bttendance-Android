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
    public SchoolUserJson[] schools_users;

    public class SchoolUserJson extends BTJson {
        public String identity;
        public boolean is_supervisor;
        public boolean is_student;
        public boolean is_administrator;
        public SimpleSchoolJson school;
    }

    public boolean isProfessor() {
        if (schools_users == null)
            return false;

        for (SchoolUserJson schoolUserJson : schools_users)
            if (schoolUserJson.is_supervisor)
                return true;

        return false;
    }

    public boolean isStudentOfSchool(int schoolId) {
        if (schools_users == null)
            return false;

        for (SchoolUserJson schoolUserJson : schools_users)
            if (schoolUserJson.school != null
                    && schoolId == schoolUserJson.school.id
                    && schoolUserJson.is_student)
                return true;

        return false;
    }

    public boolean hasSchool(int schoolId) {
        if (schools_users == null)
            return false;

        for (SchoolUserJson schoolUserJson : schools_users)
            if (schoolUserJson.school != null && schoolId == schoolUserJson.school.id)
                return true;

        return false;
    }

    public String getIdentity(int schoolId) {
        if (schools_users == null)
            return null;

        for (SchoolUserJson schoolUserJson : schools_users)
            if (schoolUserJson.school != null && schoolId == schoolUserJson.school.id)
                return schoolUserJson.identity;

        return null;
    }
}