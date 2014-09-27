package com.bttendance.model.json;

import com.bttendance.BTApplication;
import com.bttendance.R;
import com.bttendance.adapter.kit.InstantText;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */

public class SchoolJson extends BTJson {

    public String name;
    public String type;
    public int courses_count;
    public int professors_count;
    public int students_count;

    @InstantText(viewId = R.id.name)
    public String getName() {
        return name;
    }

    @InstantText(viewId = R.id.course_count)
    public String getCourseCourse() {
        return String.format(BTApplication.getContext().getString(R.string._courses), courses_count);
    }
}