package com.bttendance.model.json;

import com.bttendance.BTApplication;
import com.bttendance.R;
import com.bttendance.adapter.kit.InstantText;

/**
 * Created by TheFinestArtist on 12/19/14.
 */
public class SchoolJson extends BTJson {

    public int id;
    public String name;
    public String classification;
    public int users_count;
    public int courses_count;

    @InstantText(viewId = R.id.name)
    public String getName() {
        return name;
    }

    @InstantText(viewId = R.id.course_count)
    public String getCourseCourse() {
        return String.format(BTApplication.getContext().getString(R.string._courses), courses_count);
    }
}
