package com.bttendance.fragment.introduction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bttendance.R;
import com.bttendance.activity.guide.GuideActivity;
import com.bttendance.fragment.BTFragment;
import com.bttendance.fragment.SimpleWebViewFragment;
import com.bttendance.model.BTUrl;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by TheFinestArtist on 2014. 8. 13..
 */
public class IntroductionAttendanceFragment extends BTFragment {

    @InjectView(R.id.see_more)
    Button mSeeMore;
    @InjectView(R.id.introduction_attendance)
    ImageView mImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_introduction_attendance, container, false);
        ButterKnife.inject(this, view);

        if (Build.VERSION.SDK_INT < 11)
            mImage.setImageResource(R.drawable.attendance_bg);

        mSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GuideActivity.class);
                intent.putExtra(SimpleWebViewFragment.EXTRA_URL, BTUrl.getTutorialAttendance(getActivity()));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out_slow);
            }
        });
        return view;
    }
}
