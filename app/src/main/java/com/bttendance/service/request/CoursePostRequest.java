package com.bttendance.service.request;

/**
 * Created by TheFinestArtist on 1/2/15.
 */
public class CoursePostRequest {

    public Course course;

    public CoursePostRequest(int schoolId, String name, String instructor) {
        this.course = new Course();
        this.course.school_id = schoolId;
        this.course.name = name;
        this.course.instructor_name = instructor;
    }

    public class Course {
        public int school_id;
        public String name;
        public String instructor_name;
        public boolean open;
        public String information;
        public String start_date;
        public String end_date;
    }
}
