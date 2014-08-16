package com.bttendance.activity.course;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.event.dialog.HideProgressDialogEvent;
import com.bttendance.event.dialog.ShowProgressDialogEvent;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.json.UserJson;
import com.squareup.otto.BTEventBus;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CreateCourseActivity extends BTActivity {

    private EditText mName = null;
    private EditText mProfessor = null;
    private EditText mInstitution = null;
    private View mNameDiv = null;
    private View mProfessorDiv = null;
    private View mInstitutionDiv = null;
    private int mNameCount = 0;
    private int mProfessorCount = 0;
    private int mInstitutionCount = 0;
    private Button mCreateCourse = null;
    private String mNameString = null;
    private String mProfessorString = null;
    private String mInstitutionString = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);
        getSupportActionBar().setTitle(getString(R.string.create_course));

        mName = (EditText) findViewById(R.id.name_edit);
        mProfessor = (EditText) findViewById(R.id.professor);
        mInstitution = (EditText) findViewById(R.id.institution);
        mNameDiv = findViewById(R.id.name_divider);
        mProfessorDiv = findViewById(R.id.professor_divider);
        mInstitutionDiv = findViewById(R.id.institution_divider);

        mProfessor.setText(BTPreference.getUser(this).full_name);

        mName.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mNameDiv.setBackgroundColor(getResources().getColor(
                            R.color.bttendance_cyan));
                } else {
                    mNameDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
                }
            }
        });

        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mNameCount = mName.getText().toString().length();
                isEnableCreateCourse();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mProfessor.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mProfessorDiv
                            .setBackgroundColor(getResources().getColor(R.color.bttendance_cyan));
                } else {
                    mProfessorDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
                }
            }
        });

        mProfessor.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mProfessorCount = mProfessor.getText().toString().length();
                isEnableCreateCourse();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mInstitution.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mInstitutionDiv.setBackgroundColor(getResources().getColor(
                            R.color.bttendance_cyan));
                } else {
                    mInstitutionDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
                }
            }
        });

        mInstitution.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mInstitutionCount = mInstitution.getText().toString().length();
                isEnableCreateCourse();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mCreateCourse = (Button) findViewById(R.id.create_course);
        mCreateCourse.setEnabled(false);
        mCreateCourse.setTextColor(getResources().getColor(R.color.bttendance_silver_30));
        mCreateCourse.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_navy));
                    v.setPressed(true);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_cyan));
                    v.setPressed(false);
                    tryCreateCourse();
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

    public void isEnableCreateCourse() {
        if (mNameCount > 0 && mProfessorCount > 0 && mInstitutionCount > 0) {
            mCreateCourse.setEnabled(true);
            mCreateCourse.setTextColor(getResources().getColor(R.color.bttendance_cyan));
        } else {
            mCreateCourse.setEnabled(false);
            mCreateCourse.setTextColor(getResources().getColor(R.color.bttendance_silver_30));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mNameString != null)
            mName.setText(mNameString);
        if (mProfessorString != null)
            mProfessor.setText(mProfessorString);
        if (mInstitutionString != null)
            mInstitution.setText(mInstitutionString);

        mNameDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
        mProfessorDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
        mInstitutionDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));

        KeyboardHelper.show(this, mName);

        isEnableCreateCourse();
        BTEventBus.getInstance().register(mEventDispatcher);
    }

    @Override
    public void onPause() {
        super.onPause();

        mNameString = mName.getText().toString();
        mProfessorString = mProfessor.getText().toString();
        mInstitutionString = mInstitution.getText().toString();
        BTEventBus.getInstance().unregister(mEventDispatcher);
    }

    private void tryCreateCourse() {
        if (getBTService() == null)
            return;

        String name = mName.getText().toString();
        String professor = mProfessor.getText().toString();
        String institution = mInstitution.getText().toString();

        BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.creating_course)));
        getBTService().courseCreate(name, 1, professor, new Callback<UserJson>() {
            @Override
            public void success(UserJson userJson, Response response) {
                BTEventBus.getInstance().post(new HideProgressDialogEvent());
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                BTEventBus.getInstance().post(new HideProgressDialogEvent());
            }
        });
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_down);
    }

}// end of class
