package com.bttendance.fragment.guide;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bttendance.R;
import com.bttendance.activity.course.AttendCourseActivity;
import com.bttendance.activity.course.CreateCourseActivity;
import com.bttendance.fragment.BTFragment;
import com.bttendance.model.BTPreference;

/**
 * Created by TheFinestArtist on 2014. 8. 13..
 */
public class GuideLastFragment extends BTFragment {

    Button mGuideBt1;
    Button mGuideBt2;
    Boolean mHasOpenedCourse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guid_last, container, false);
        if (Build.VERSION.SDK_INT < 11)
            view.findViewById(R.id.guide_last).setBackgroundColor(getResources().getColor(R.color.bttendance_white));

        mGuideBt1 = (Button) view.findViewById(R.id.guide_last_bt1);
        mGuideBt2 = (Button) view.findViewById(R.id.guide_last_bt2);

        mHasOpenedCourse = BTPreference.getUser(getActivity()).getOpenedCourses().length > 0;
        if (mHasOpenedCourse) {
            mGuideBt1.setText(getString(R.string.continue_));
            mGuideBt2.setVisibility(View.GONE);
        }

        mGuideBt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
                if (!mHasOpenedCourse) {
                    Intent intent = new Intent(getActivity(), CreateCourseActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out_slow);
                }
            }
        });

        mGuideBt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
                Intent intent = new Intent(getActivity(), AttendCourseActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out_slow);
            }
        });

        return view;
    }
}
