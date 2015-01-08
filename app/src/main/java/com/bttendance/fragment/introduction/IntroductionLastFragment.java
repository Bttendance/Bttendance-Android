package com.bttendance.fragment.introduction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bttendance.R;
import com.bttendance.activity.course.AttendCourseActivity;
import com.bttendance.activity.course.CreateCourseActivity;
import com.bttendance.fragment.BTFragment;
import com.bttendance.model.BTTable;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by TheFinestArtist on 2014. 8. 13..
 */
public class IntroductionLastFragment extends BTFragment {

    @InjectView(R.id.introduction_last_bt1)
    Button mIntroductionBt1;
    @InjectView(R.id.introduction_last_bt2)
    Button mIntroductionBt2;
    Boolean mHasOpenedCourse = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_introduction_last, container, false);
        ButterKnife.inject(this, view);

        if (BTTable.MyCourseTable.size() > 0)
            mHasOpenedCourse = true;

        if (mHasOpenedCourse) {
            mIntroductionBt1.setText(getString(R.string.continue_));
            mIntroductionBt2.setVisibility(View.GONE);
        }

        mIntroductionBt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
                if (!mHasOpenedCourse) {
                    Intent intent = new Intent(getActivity(), CreateCourseActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.modal_activity_open_enter, R.anim.modal_activity_open_exit);
                }
            }
        });

        mIntroductionBt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
                Intent intent = new Intent(getActivity(), AttendCourseActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.modal_activity_open_enter, R.anim.modal_activity_open_exit);
            }
        });

        return view;
    }
}
