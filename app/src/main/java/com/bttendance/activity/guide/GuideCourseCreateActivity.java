package com.bttendance.activity.guide;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.activity.BTActivity;

/**
 * Created by TheFinestArtist on 2014. 8. 18..
 */
public class GuideCourseCreateActivity extends BTActivity {

    public final static String EXTRA_CLASS_CODE = "class_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_create_course);

        TextView classCode = (TextView) findViewById(R.id.code_text);
        String code = getIntent().getStringExtra(EXTRA_CLASS_CODE);
        if (code != null)
            code = code.toUpperCase();
        classCode.setText(code);

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
