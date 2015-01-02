package com.bttendance.activity.course;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.activity.guide.GuideCourseCreateActivity;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.service.request.UserPutRequest;
import com.bttendance.view.BTDialog;
import com.squareup.otto.BTEventBus;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CreateCourseActivity extends BTActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.name_edit)
    EditText mName;
    @InjectView(R.id.professor)
    EditText mProfessor;
    @InjectView(R.id.institution)
    EditText mInstitution;
    @InjectView(R.id.name_divider)
    View mNameDiv;
    @InjectView(R.id.professor_divider)
    View mProfessorDiv;

    private int mNameCount = 0;
    private int mProfessorCount = 0;
    private Button mCreateCourse = null;
    private String mNameString = null;
    private String mProfessorString = null;
    private String mInstitutionString = null;

    private Button mInstitutionBt = null;

    private SchoolJson mSchool = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        mProfessor.setText(BTTable.getMe().name);
        mProfessorCount = mProfessor.getText().toString().length();

        mName.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mNameDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_cyan));
                } else {
                    mNameDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
                }
            }
        });

        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNameCount = mName.getText().toString().length();
                isEnableCreateCourse();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mProfessor.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mProfessorDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_cyan));
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

        mInstitution.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isEnableCreateCourse();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mInstitutionBt = (Button) findViewById(R.id.institution_button);
        mInstitutionBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SchoolChooseFragment fragment = new SchoolChooseFragment();
//                BTEventBus.getInstance().post(new AddFragmentEvent(fragment));
//                KeyboardHelper.hide(CreateCourseActivity.this, mName);
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
        if (mNameCount > 0 && mProfessorCount > 0 && mSchool != null) {
            mCreateCourse.setEnabled(true);
            mCreateCourse.setTextColor(getResources().getColor(R.color.bttendance_cyan));
        } else {
            mCreateCourse.setEnabled(false);
            mCreateCourse.setTextColor(getResources().getColor(R.color.bttendance_silver_30));
        }
    }

    public void setSchool(SchoolJson school) {
        mSchool = school;
        mInstitutionString = mSchool.name;
        mInstitution.setText(mInstitutionString);
        isEnableCreateCourse();
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
        if (getBTService() == null || mSchool == null)
            return;

        int schoolId = mSchool.id;
        String name = mName.getText().toString();
        String professor = mProfessor.getText().toString();

        final MaterialDialog dialog = BTDialog.progress(this, getString(R.string.creating_course));
        getBTService().createCourse(schoolId, name, professor, new Callback<CourseJson>() {
            @Override
            public void success(final CourseJson courseJson, Response response) {
                getBTService().updateUser(new UserPutRequest().updateNewlyCreatedCourse(courseJson), new Callback<UserJson>() {
                    @Override
                    public void success(UserJson userJson, Response response) {
                        BTDialog.hide(dialog);
                        onBackPressed();
                        Intent intent = new Intent(CreateCourseActivity.this, GuideCourseCreateActivity.class);
                        intent.putExtra(GuideCourseCreateActivity.EXTRA_CLASS_CODE, courseJson.code);
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
            public void failure(RetrofitError error) {
                BTDialog.hide(dialog);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.create_course));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else if (fm.getBackStackEntryCount() > 0) {
            super.onBackPressed();
            KeyboardHelper.show(this, mName);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.modal_activity_close_enter, R.anim.modal_activity_close_exit);
        }
    }

}// end of class
