package com.bttendance.fragment.introduction;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bttendance.R;
import com.bttendance.fragment.BTFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by TheFinestArtist on 2014. 8. 13..
 */
public class IntroductionStartFragment extends BTFragment {

    @InjectView(R.id.introduction_start_bt1)
    Button mIntroductionBt1;
    @InjectView(R.id.introduction_start_bt2)
    Button mIntroductionBt2;
    @InjectView(R.id.introduction_start_sign_up)
    Button mIntroductionSignUp;
    @InjectView(R.id.introduction_start_log_in)
    Button mIntroductionLogIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_introduction_start, container, false);
        ButterKnife.inject(this, view);

        if (Build.VERSION.SDK_INT < 11)
            view.findViewById(R.id.introduction_start).setBackgroundColor(getResources().getColor(R.color.bttendance_white));


        return view;
    }
}
