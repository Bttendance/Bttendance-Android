package com.utopia.bttendance.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.utopia.bttendance.R;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class CourseListFragment extends BTFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_list, null);
        return view;
    }
}
