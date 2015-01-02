package com.bttendance;

import android.app.Application;
import android.content.Context;

import com.bttendance.model.BTDatabase;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.SchoolJson;

/**
 * Created by TheFinestArtist on 2014. 8. 17..
 */
public class BTApplication extends Application {

    private static BTApplication instance;

    public static BTApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();

        BTTable.setMe(BTDatabase.getUser(this));

        SchoolJson[] schools = BTDatabase.getSchools(this);
        if (schools != null)
            for (SchoolJson school : schools)
                BTTable.SchoolTable.put(school.id, school);

        CourseJson[] courses = BTDatabase.getCourses(this);
        if (courses != null)
            for (CourseJson course : courses)
                BTTable.CourseTable.put(course.id, course);
    }
}
