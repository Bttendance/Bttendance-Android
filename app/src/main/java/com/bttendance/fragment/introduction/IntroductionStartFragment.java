package com.bttendance.fragment.introduction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bttendance.R;
import com.bttendance.activity.sign.LogInActivity;
import com.bttendance.activity.sign.SignUpActivity;
import com.bttendance.fragment.BTFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by TheFinestArtist on 2014. 8. 13..
 */
public class IntroductionStartFragment extends BTFragment implements View.OnClickListener {

    @InjectView(R.id.introduction_start_facebook)
    Button mIntroductionFacebook;
    @InjectView(R.id.introduction_start_google)
    Button mIntroductionGoogle;
    @InjectView(R.id.introduction_start_sign_up)
    Button mIntroductionSignUp;
    @InjectView(R.id.introduction_start_log_in)
    Button mIntroductionLogIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_introduction_start, container, false);
        ButterKnife.inject(this, view);

        mIntroductionFacebook.setOnClickListener(this);
        mIntroductionGoogle.setOnClickListener(this);
        mIntroductionSignUp.setOnClickListener(this);
        mIntroductionLogIn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.introduction_start_facebook:
                startWithFacebook();
                break;
            case R.id.introduction_start_google:
                startWithGoogle();
                break;
            case R.id.introduction_start_sign_up:
                signUp();
                break;
            case R.id.introduction_start_log_in:
                logIn();
                break;
        }
    }

    private void startWithFacebook() {
    }

    private void startWithGoogle() {
    }

    private void signUp() {
        Intent intent = new Intent(getActivity(), SignUpActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.modal_activity_open_enter, R.anim.modal_activity_open_exit);
    }

    private void logIn() {
        Intent intent = new Intent(getActivity(), LogInActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.modal_activity_open_enter, R.anim.modal_activity_open_exit);
    }
}
