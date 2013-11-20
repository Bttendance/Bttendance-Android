package com.utopia.bttendance.activity.sign;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.R;
import com.utopia.bttendance.activity.BTActivity;
import com.utopia.bttendance.activity.ProfessorActivity;
import com.utopia.bttendance.activity.StudentActivity;
import com.utopia.bttendance.helper.UUIDHelper;
import com.utopia.bttendance.model.BTPersistent;
import com.utopia.bttendance.model.BTPreference;
import com.utopia.bttendance.model.json.UserJson;

import java.util.UUID;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */
public class CatchPointActivity extends BTActivity implements Button.OnClickListener{

    Button mSignUp;
    Button mSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}