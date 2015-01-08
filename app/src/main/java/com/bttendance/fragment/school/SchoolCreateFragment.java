package com.bttendance.fragment.school;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bttendance.R;
import com.bttendance.activity.course.CreateCourseActivity;
import com.bttendance.fragment.BTFragment;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.widget.BTDialog;

import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2014. 1. 23..
 */
public class SchoolCreateFragment extends BTFragment implements Callback<SchoolJson>, View.OnClickListener {

    @InjectView(R.id.edit)
    EditText mEdit;
    @InjectView(R.id.edit_divider)
    View mEditDiv;
    @InjectView(R.id.create_school)
    Button mCreate;

    int mEditCount = 0;
    String mEditString;

    @InjectView(R.id.university_bg)
    LinearLayout mUniversityBg;
    @InjectView(R.id.university_image)
    ImageView mUniversityImg;
    @InjectView(R.id.university_text)
    TextView mUniversityTv;
    @InjectView(R.id.school_bg)
    LinearLayout mSchoolBg;
    @InjectView(R.id.school_image)
    ImageView mSchoolImg;
    @InjectView(R.id.school_text)
    TextView mSchoolTv;
    @InjectView(R.id.institute_bg)
    LinearLayout mInstituteBg;
    @InjectView(R.id.institute_image)
    ImageView mInstituteImg;
    @InjectView(R.id.institute_text)
    TextView mInstituteTv;
    @InjectView(R.id.etc_bg)
    LinearLayout mEtcBg;
    @InjectView(R.id.etc_image)
    ImageView mEtcImg;
    @InjectView(R.id.etc_text)
    TextView mEtcTv;

    String mType;
    MaterialDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school_create, container, false);
        ButterKnife.inject(this, view);

        mEdit.setText(mEditString);
        KeyboardHelper.show(getActivity(), mEdit);

        mEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEditDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_cyan));
                } else {
                    mEditDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
                }
            }
        });

        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEditCount = mEdit.getText().toString().length();
                mEditString = mEdit.getText().toString();
                isEnableCreate();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mUniversityBg.setOnClickListener(this);
        mSchoolBg.setOnClickListener(this);
        mInstituteBg.setOnClickListener(this);
        mEtcBg.setOnClickListener(this);

        mCreate.setEnabled(false);
        mCreate.setTextColor(getResources().getColor(R.color.bttendance_silver_30));
        mCreate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_navy));
                    v.setPressed(true);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_cyan));
                    v.setPressed(false);
                    save();
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

        return view;
    }

    private void save() {
        if (getBTService() == null)
            return;

        if (mEditString == null || !Pattern.matches("[A-Za-z0-9 .,]+", mEditString)) {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(200);
            mEdit.setText("");
            mEdit.setHintTextColor(getResources().getColor(R.color.bttendance_red));
            return;
        }

        dialog = BTDialog.progress(getActivity(), getString(R.string.creating_school));
        getBTService().createSchool(mEditString, mType, this);
    }

    private void isEnableCreate() {

        if (mEditCount > 0 && mType != null) {
            mCreate.setEnabled(true);
            mCreate.setTextColor(getResources().getColor(R.color.bttendance_cyan));
        } else {
            mCreate.setEnabled(false);
            mCreate.setTextColor(getResources().getColor(R.color.bttendance_silver_30));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mEditString != null)
            mEdit.setText(mEditString);
        mEditDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
        mEdit.setSelection(mEdit.getText().length(), mEdit.getText().length());
        isEnableCreate();
        KeyboardHelper.show(getActivity(), mEdit);
    }

    @Override
    public void onPause() {
        super.onPause();
        mEditString = mEdit.getText().toString();
        KeyboardHelper.hide(getActivity(), mEdit);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KeyboardHelper.hide(getActivity(), mEdit);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getActivity() == null)
            return;

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.create_institution));
    }

    /**
     * Callback from BTService
     *
     * @param schoolJson
     * @param response
     */
    @Override
    public void success(SchoolJson schoolJson, Response response) {
        BTDialog.hide(dialog);
        ((CreateCourseActivity) getActivity()).setSchool(schoolJson);
        getActivity().onBackPressed();
        getActivity().onBackPressed();
    }

    @Override
    public void failure(RetrofitError retrofitError) {
        BTDialog.hide(dialog);
    }

    /**
     * View.OnClickListener
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        mUniversityBg.setBackgroundResource(R.drawable.bt_selector_round);
        mUniversityImg.setImageResource(R.drawable.check_default);
        mUniversityTv.setTextColor(getResources().getColor(R.color.bttendance_silver));
        mSchoolBg.setBackgroundResource(R.drawable.bt_selector_round);
        mSchoolImg.setImageResource(R.drawable.check_default);
        mSchoolTv.setTextColor(getResources().getColor(R.color.bttendance_silver));
        mInstituteBg.setBackgroundResource(R.drawable.bt_selector_round);
        mInstituteImg.setImageResource(R.drawable.check_default);
        mInstituteTv.setTextColor(getResources().getColor(R.color.bttendance_silver));
        mEtcBg.setBackgroundResource(R.drawable.bt_selector_round);
        mEtcImg.setImageResource(R.drawable.check_default);
        mEtcTv.setTextColor(getResources().getColor(R.color.bttendance_silver));

        switch (view.getId()) {
            case R.id.university_bg:
                mUniversityBg.setBackgroundResource(R.drawable.bt_selected_round);
                mUniversityImg.setImageResource(R.drawable.check_selected);
                mUniversityTv.setTextColor(getResources().getColor(R.color.bttendance_navy));
                mType = "university";
                break;
            case R.id.school_bg:
                mSchoolBg.setBackgroundResource(R.drawable.bt_selected_round);
                mSchoolImg.setImageResource(R.drawable.check_selected);
                mSchoolTv.setTextColor(getResources().getColor(R.color.bttendance_navy));
                mType = "school";
                break;
            case R.id.institute_bg:
                mInstituteBg.setBackgroundResource(R.drawable.bt_selected_round);
                mInstituteImg.setImageResource(R.drawable.check_selected);
                mInstituteTv.setTextColor(getResources().getColor(R.color.bttendance_navy));
                mType = "institute";
                break;
            case R.id.etc_bg:
                mEtcBg.setBackgroundResource(R.drawable.bt_selected_round);
                mEtcImg.setImageResource(R.drawable.check_selected);
                mEtcTv.setTextColor(getResources().getColor(R.color.bttendance_navy));
                mType = "etc";
                break;
        }

        isEnableCreate();
    }
}
