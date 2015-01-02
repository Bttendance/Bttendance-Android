package com.bttendance.service.request;

/**
 * Created by TheFinestArtist on 1/2/15.
 */
public class SchoolPostRequest {

    public School school;

    public SchoolPostRequest(String name, String classification) {
        this.school = new School();
        this.school.name = name;
        this.school.classification = classification;
    }

    public class School {
        public String name;
        public String classification;
    }
}
