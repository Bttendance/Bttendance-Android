package com.bttendance.activity.course;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.event.dialog.ShowContextDialogEvent;
import com.bttendance.fragment.BTDialogFragment;
import com.squareup.otto.BTEventBus;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */

public class AddCourseActivity extends BTActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
    }

    @Override
    public void onResume() {
        super.onResume();
        BTEventBus.getInstance().register(mEventDispatcher);

        String[] options = {getString(R.string.create_course), getString(R.string.attend_course)};
        BTEventBus.getInstance().post(new ShowContextDialogEvent(options, new BTDialogFragment.OnDialogListener() {
            @Override
            public void onConfirmed(String edit) {
                if (getString(R.string.create_course).equals(edit)) {
                    onBackPressed();
                    Intent intent = new Intent(AddCourseActivity.this, CreateCourseActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out_slow);
                }

                if (getString(R.string.attend_course).equals(edit)) {
                    onBackPressed();
                    Intent intent = new Intent(AddCourseActivity.this, AttendCourseActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out_slow);
                }
            }

            @Override
            public void onCanceled() {
                onBackPressed();
            }
        }));
    }

    @Override
    public void onPause() {
        super.onPause();
        BTEventBus.getInstance().unregister(mEventDispatcher);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case android.R.id.home:
                onBackPressed();
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.add_course));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}// end of class
