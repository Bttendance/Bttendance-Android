package com.bttendance.activity.sign;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.model.BTKey;

/**
 * Created by TheFinestArtist on 2013. 11. 27..
 */
public class StudentConfirm extends BTActivity {

    Button mConfirm;
    Button mCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_confirm);
        mConfirm = (Button) findViewById(R.id.confirm);
        mCancel = (Button) findViewById(R.id.cancel);

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentConfirm.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
