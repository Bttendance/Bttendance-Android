package com.bttendance.activity.guide;

import android.os.Bundle;
import android.view.View;

import com.bttendance.R;
import com.bttendance.activity.BTActivity;

/**
 * Created by TheFinestArtist on 2014. 8. 18..
 */
public class GuideCourseAttendActivity extends BTActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_attend_course);

        findViewById(R.id.continue_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
