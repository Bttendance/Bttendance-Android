package com.bttendance.activity.sign;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.view.BeautiToast;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public class CatchPointActivity extends BTActivity implements Button.OnClickListener {

    Button mSignUp;
    Button mSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.clear(this);
        setContentView(R.layout.activity_catch_point);
        mSignUp = (Button) findViewById(R.id.sign_up);
        mSignIn = (Button) findViewById(R.id.sign_in);
        mSignUp.setOnClickListener(this);
        mSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up:
                Intent intent_up = new Intent(CatchPointActivity.this, SignUpActivity.class);
                startActivity(intent_up);
                overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
                break;
            case R.id.sign_in:
                Intent intent_in = new Intent(CatchPointActivity.this, SignInActivity.class);
                startActivity(intent_in);
                overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        tryToFinish();
    }

    private boolean mFinishApplication = false;
    private static Handler mUIHandler = new Handler();

    private void tryToFinish() {
        if (mFinishApplication) {
            finish();
        } else {
            BeautiToast.show(this, getString(R.string.please_press_back_button_again_to_exit_));
            mFinishApplication = true;
            mUIHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mFinishApplication = false;
                }
            }, 3000);
        }
    }
}