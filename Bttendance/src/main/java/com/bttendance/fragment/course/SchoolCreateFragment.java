package com.bttendance.fragment.course;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.bttendance.R;
import com.bttendance.activity.course.CreateCourseActivity;
import com.bttendance.event.dialog.HideProgressDialogEvent;
import com.bttendance.event.dialog.ShowProgressDialogEvent;
import com.bttendance.fragment.BTFragment;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.SchoolJson;
import com.squareup.otto.BTEventBus;

import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2014. 1. 23..
 */
public class SchoolCreateFragment extends BTFragment implements Callback<SchoolJson>, View.OnClickListener {

    private EditText mEdit = null;
    private View mEditDiv = null;
    private Button mCreate = null;
    private int mEditCount = 0;
    private String mEditString = null;

    private LinearLayout mUniversityBg = null;
    private ImageView mUniversityImg = null;
    private TextView mUniversityTv = null;
    private LinearLayout mSchoolBg = null;
    private ImageView mSchoolImg = null;
    private TextView mSchoolTv = null;
    private LinearLayout mInstituteBg = null;
    private ImageView mInstituteImg = null;
    private TextView mInstituteTv = null;
    private LinearLayout mEtcBg = null;
    private ImageView mEtcImg = null;
    private TextView mEtcTv = null;

    private String mType = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school_create, container, false);

        mEdit = (EditText) view.findViewById(R.id.edit);
        mEditDiv = view.findViewById(R.id.edit_divider);

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

        mUniversityBg = (LinearLayout) view.findViewById(R.id.university_bg);
        mUniversityImg = (ImageView) view.findViewById(R.id.university_image);
        mUniversityTv = (TextView) view.findViewById(R.id.university_text);
        mSchoolBg = (LinearLayout) view.findViewById(R.id.school_bg);
        mSchoolImg = (ImageView) view.findViewById(R.id.school_image);
        mSchoolTv = (TextView) view.findViewById(R.id.school_text);
        mInstituteBg = (LinearLayout) view.findViewById(R.id.institute_bg);
        mInstituteImg = (ImageView) view.findViewById(R.id.institute_image);
        mInstituteTv = (TextView) view.findViewById(R.id.institute_text);
        mEtcBg = (LinearLayout) view.findViewById(R.id.etc_bg);
        mEtcImg = (ImageView) view.findViewById(R.id.etc_image);
        mEtcTv = (TextView) view.findViewById(R.id.etc_text);

        mUniversityBg.setOnClickListener(this);
        mSchoolBg.setOnClickListener(this);
        mInstituteBg.setOnClickListener(this);
        mEtcBg.setOnClickListener(this);

        mCreate = (Button) view.findViewById(R.id.create_school);
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

        BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.creating_school)));
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
        if (getSherlockActivity() == null)
            return;

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(false);
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
        BTEventBus.getInstance().post(new HideProgressDialogEvent());
        ((CreateCourseActivity) getActivity()).setSchool(schoolJson);
        getActivity().onBackPressed();
        getActivity().onBackPressed();
    }

    @Override
    public void failure(RetrofitError retrofitError) {
        BTEventBus.getInstance().post(new HideProgressDialogEvent());
    }

    /**
     * View.OnClickListener
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        mUniversityBg.setBackgroundResource(R.drawable.bt_selector_round);
        mUniversityImg.setImageResource(R.drawable.attendance_silver);
        mUniversityTv.setTextColor(getResources().getColor(R.color.bttendance_silver));
        mSchoolBg.setBackgroundResource(R.drawable.bt_selector_round);
        mSchoolImg.setImageResource(R.drawable.attendance_silver);
        mSchoolTv.setTextColor(getResources().getColor(R.color.bttendance_silver));
        mInstituteBg.setBackgroundResource(R.drawable.bt_selector_round);
        mInstituteImg.setImageResource(R.drawable.attendance_silver);
        mInstituteTv.setTextColor(getResources().getColor(R.color.bttendance_silver));
        mEtcBg.setBackgroundResource(R.drawable.bt_selector_round);
        mEtcImg.setImageResource(R.drawable.attendance_silver);
        mEtcTv.setTextColor(getResources().getColor(R.color.bttendance_silver));

        switch (view.getId()) {
            case R.id.university_bg:
                mUniversityBg.setBackgroundResource(R.drawable.bt_selected_round);
                mUniversityImg.setImageResource(R.drawable.attendance_navy);
                mUniversityTv.setTextColor(getResources().getColor(R.color.bttendance_navy));
                mType = "university";
                break;
            case R.id.school_bg:
                mSchoolBg.setBackgroundResource(R.drawable.bt_selected_round);
                mSchoolImg.setImageResource(R.drawable.attendance_navy);
                mSchoolTv.setTextColor(getResources().getColor(R.color.bttendance_navy));
                mType = "school";
                break;
            case R.id.institute_bg:
                mInstituteBg.setBackgroundResource(R.drawable.bt_selected_round);
                mInstituteImg.setImageResource(R.drawable.attendance_navy);
                mInstituteTv.setTextColor(getResources().getColor(R.color.bttendance_navy));
                mType = "institute";
                break;
            case R.id.etc_bg:
                mEtcBg.setBackgroundResource(R.drawable.bt_selected_round);
                mEtcImg.setImageResource(R.drawable.attendance_navy);
                mEtcTv.setTextColor(getResources().getColor(R.color.bttendance_navy));
                mType = "etc";
                break;
        }

        isEnableCreate();
    }
}
