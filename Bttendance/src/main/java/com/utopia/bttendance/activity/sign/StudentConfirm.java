package com.utopia.bttendance.activity.sign;

import android.os.Bundle;

import com.utopia.bttendance.R;
import com.utopia.bttendance.activity.BTActivity;

/**
 * Created by TheFinestArtist on 2013. 11. 27..
 */
public class StudentConfirm extends BTActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_confirm);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
