package com.bttendance.activity.course;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.BTDebug;
import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.activity.guide.GuideCourseAttendActivity;
import com.bttendance.event.dialog.HideProgressDialogEvent;
import com.bttendance.event.dialog.ShowAlertDialogEvent;
import com.bttendance.event.dialog.ShowProgressDialogEvent;
import com.bttendance.event.main.ResetMainFragmentEvent;
import com.bttendance.fragment.BTDialogFragment;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.CourseJsonSimple;
import com.bttendance.model.json.UserJson;
import com.squareup.otto.BTEventBus;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */

public class AttendCourseActivity extends BTActivity {

    private EditText mCode = null;
    private View mCodeDiv = null;
    private Button mAttendCourse = null;
    private int mCodeCount = 0;
    private String mCodeString = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_course);

        mCode = (EditText) findViewById(R.id.code);
        mCodeDiv = findViewById(R.id.code_divider);

        mCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mCodeDiv.setBackgroundColor(getResources().getColor(
                            R.color.bttendance_cyan));
                } else {
                    mCodeDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
                }
            }
        });

        mCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCodeCount = mCode.getText().toString().length();
                isEnableAttendCourse();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mAttendCourse = (Button) findViewById(R.id.attend_course);
        mAttendCourse.setEnabled(false);
        mAttendCourse.setTextColor(getResources().getColor(R.color.bttendance_silver_30));
        mAttendCourse.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_navy));
                    v.setPressed(true);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_cyan));
                    v.setPressed(false);
                    tryAttendCourse();
                }
                if (event.getX() < 0
                        || event.getX() > v.getWidth()
                        || event.getY() < 0
                        || event.getY() > v.getHeight()) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_cyan));
                    v.setPressed(false);
                }
                return true;
            }
        });
    }

    public void isEnableAttendCourse() {

        if (mCodeCount > 0) {
            mAttendCourse.setEnabled(true);
            mAttendCourse.setTextColor(getResources().getColor(R.color.bttendance_cyan));
        } else {
            mAttendCourse.setEnabled(false);
            mAttendCourse.setTextColor(getResources().getColor(R.color.bttendance_silver_30));
        }
    }

    protected void tryAttendCourse() {
        if (getBTService() == null)
            return;

        String code = mCode.getText().toString();
        BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.attending_course)));
        getBTService().searchCourse(0, code, new Callback<CourseJson>() {
            @Override
            public void success(final CourseJson courseJson, Response response) {

                if (BTPreference.getUser(AttendCourseActivity.this).enrolled(courseJson.school.id)) {
                    attendCourse(courseJson.id);
                } else {
                    String title;
                    String message;

                    if ("university".equals(courseJson.school.type)) {
                        title = getString(R.string.student_number_univ);
                        message = String.format(getString(R.string.before_you_join_univ), courseJson.name);
                    } else if ("school".equals(courseJson.school.type)) {
                        title = getString(R.string.student_number_school);
                        message = String.format(getString(R.string.before_you_join_school), courseJson.name);
                    } else if ("institute".equals(courseJson.school.type)) {
                        title = getString(R.string.phone_number);
                        message = String.format(getString(R.string.before_you_join_institute), courseJson.name);
                    } else {
                        title = getString(R.string.identity);
                        message = String.format(getString(R.string.before_you_join_etc), courseJson.name);
                    }

                    BTEventBus.getInstance().post(new ShowAlertDialogEvent(BTDialogFragment.DialogType.EDIT, title, message, new BTDialogFragment.OnDialogListener() {
                        @Override
                        public void onConfirmed(String edit) {
                            BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.attending_course)));
                            if (BTPreference.getUser(getApplicationContext()).enrolled(courseJson.school.id)) {
                                attendCourse(courseJson.id);
                            } else {
                                getBTService().enrollSchool(courseJson.school.id, edit, new Callback<UserJson>() {
                                    @Override
                                    public void success(UserJson userJson, Response response) {
                                        attendCourse(courseJson.id);
                                    }

                                    @Override
                                    public void failure(RetrofitError retrofitError) {
                                        BTEventBus.getInstance().post(new HideProgressDialogEvent());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCanceled() {
                        }
                    }));
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                BTEventBus.getInstance().post(new HideProgressDialogEvent());
            }
        });
    }

    private void attendCourse(int courseID) {

        final UserJson user = BTPreference.getUser(this);

        getBTService().attendCourse(courseID, new Callback<UserJson>() {
            @Override
            public void success(UserJson userJson, Response response) {

                BTEventBus.getInstance().post(new HideProgressDialogEvent());

                int newCourseID = 0;
                for (CourseJsonSimple new_course : userJson.getOpenedCourses()) {
                    boolean hasCourse = false;
                    for (CourseJsonSimple old_course : user.getOpenedCourses()) {
                        if (new_course.id == old_course.id)
                            hasCourse = true;
                    }
                    if (!hasCourse)
                        newCourseID = new_course.id;
                }
                BTDebug.LogError("newCourseID : " + newCourseID);
                BTEventBus.getInstance().post(new ResetMainFragmentEvent(newCourseID));

                onBackPressed();
                Intent intent = new Intent(AttendCourseActivity.this, GuideCourseAttendActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                BTEventBus.getInstance().post(new HideProgressDialogEvent());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mCodeString != null)
            mCode.setText(mCodeString);

        mCodeDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));

        isEnableAttendCourse();
        BTEventBus.getInstance().register(mEventDispatcher);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCodeString = mCode.getText().toString();
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
        actionBar.setTitle(getString(R.string.attend_course));
        actionBar.setDisplayHomeAsUpEnabled(true);
        KeyboardHelper.show(this, mCode);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_down);
    }
}// end of class
