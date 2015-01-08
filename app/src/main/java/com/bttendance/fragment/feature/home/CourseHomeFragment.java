package com.bttendance.fragment.feature.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.fragment.BTFragment;
import com.bttendance.model.BTKey;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by TheFinestArtist on 1/8/15.
 */
public class CourseHomeFragment extends BTFragment {

    @InjectView(R.id.code)
    TextView code;
    @InjectView(R.id.course_name)
    TextView courseName;

    @InjectView(R.id.school_name)
    TextView schoolName;
    @InjectView(R.id.prof_name)
    TextView profName;
    @InjectView(R.id.students)
    TextView students;
    @InjectView(R.id.calendar)
    TextView calendar;

    @InjectView(R.id.course_info)
    TextView info;

    int courseId;
    CourseJson course;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courseId = getArguments() != null ? getArguments().getInt(BTKey.EXTRA_COURSE_ID) : 0;
        course = BTTable.MyCourseTable.get(courseId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_home, container, false);
        ButterKnife.inject(this, view);
        if (course != null) {
            code.setText(course.code);
            courseName.setText(course.name);
            schoolName.setText(course.school != null ? course.school.name : null);
            profName.setText(course.instructor_name);
            students.setText(course.attending_users_count);
            info.setText(course.information);
        }
        return view;
    }
}
