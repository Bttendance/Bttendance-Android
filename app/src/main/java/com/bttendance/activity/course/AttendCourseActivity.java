package com.bttendance.activity.course;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.activity.guide.GuideCourseAttendActivity;
import com.bttendance.event.main.ResetMainFragmentEvent;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.service.BTAPI;
import com.bttendance.service.request.UserPutRequest;
import com.bttendance.widget.BTDialog;
import com.squareup.otto.BTEventBus;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */

public class AttendCourseActivity extends BTActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.code)
    EditText mCode;
    @InjectView(R.id.code_divider)
    View mCodeDiv;
    @InjectView(R.id.attend_course)
    Button mAttendCourse;

    private int mCodeCount = 0;
    private String mCodeString = null;
    private MaterialDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_course);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

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
        dialog = BTDialog.progress(this, getString(R.string.attending_course));
        getBTService().searchCourse(code, new Callback<CourseJson>() {
            @Override
            public void success(final CourseJson courseJson, Response response) {
                BTDialog.hide(dialog);
                if (courseJson.school != null && BTTable.getMe().isStudentOfSchool(courseJson.school.id)) {
                    attendCourse(courseJson, null);
                } else {
                    final String title;
                    final String message;

                    BTAPI.SchoolClassification classification;
                    if (courseJson.school != null)
                        classification = BTAPI.SchoolClassification.valueOf(courseJson.school.classification);
                    else
                        classification = BTAPI.SchoolClassification.university;

                    switch (classification) {
                        case university:
                            title = getString(R.string.student_number_univ);
                            message = String.format(getString(R.string.before_you_join_univ), courseJson.name);
                            break;
                        case school:
                            title = getString(R.string.student_number_school);
                            message = String.format(getString(R.string.before_you_join_school), courseJson.name);
                            break;
                        case institute:
                            title = getString(R.string.phone_number);
                            message = String.format(getString(R.string.before_you_join_institute), courseJson.name);
                            break;
                        case other:
                        default:
                            title = getString(R.string.identity);
                            message = String.format(getString(R.string.before_you_join_etc), courseJson.name);
                            break;
                    }

                    BTDialog.edit(AttendCourseActivity.this, title, message, new BTDialog.OnDialogListener() {
                        @Override
                        public void onConfirmed(String edit) {
                            if (edit == null || edit.length() == 0) {
                                Vibrator vibrator = (Vibrator) AttendCourseActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(200);
                            } else {
                                attendCourse(courseJson, edit);
                            }
                        }

                        @Override
                        public void onCanceled() {
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {
                BTDialog.hide(dialog);
            }
        });
    }

    private void attendCourse(final CourseJson courseJson, String identity) {

        BTDialog.show(dialog);
        getBTService().updateUser(new UserPutRequest().updateAttendingCourse(courseJson, identity), new Callback<UserJson>() {
            @Override
            public void success(UserJson userJson, Response response) {
                BTDialog.hide(dialog);
                BTEventBus.getInstance().post(new ResetMainFragmentEvent(courseJson.id));
                onBackPressed();
                Intent intent = new Intent(AttendCourseActivity.this, GuideCourseAttendActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void failure(RetrofitError error) {
                BTDialog.hide(dialog);
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
        overridePendingTransition(R.anim.modal_activity_close_enter, R.anim.modal_activity_close_exit);
    }
}// end of class
