package com.bttendance.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.BTDebug;
import com.bttendance.R;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class CourseCreateFragment extends BTFragment {

    private int mSchoolID;
    private EditText mFullName = null;
    private EditText mUsername = null;
    private EditText mName = null;
    private EditText mEmail = null;
    private View mFullNameDiv = null;
    private View mUsernameDiv = null;
    private View mNameDiv = null;
    private View mEmailDiv = null;
    private int mFullNameCount = 0;
    private int mUsernameCount = 0;
    private int mNameCount = 0;
    private int mEmailCount = 0;
    private Button mSignUp = null;
    private String mFullNameString = null;
    private String mUsernameString = null;
    private String mNameString = null;
    private String mEmailString = null;

    public CourseCreateFragment(int schoolID) {
        mSchoolID = schoolID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_create, container, false);

        mFullName = (EditText) view.findViewById(R.id.full_name);
        mUsername = (EditText) view.findViewById(R.id.username);
        mName = (EditText) view.findViewById(R.id.name);
        mEmail = (EditText) view.findViewById(R.id.email);
        mFullNameDiv = view.findViewById(R.id.full_name_divider);
        mUsernameDiv = view.findViewById(R.id.username_divider);
        mNameDiv = view.findViewById(R.id.name_divider);
        mEmailDiv = view.findViewById(R.id.email_divider);
        mSignUp = (Button) view.findViewById(R.id.signup);

        mFullName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mFullNameDiv.setBackgroundColor(getResources().getColor(
                            R.color.bttendance_cyan));
                } else {
                    mFullNameDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
                }
            }
        });

        mFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mFullNameCount = mFullName.getText().toString().length();
                isEnableSignUp();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mUsernameDiv.setBackgroundColor(getResources().getColor(
                            R.color.bttendance_cyan));
                } else {
                    mUsernameDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
                }
            }
        });

        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUsernameCount = mUsername.getText().toString().length();
                isEnableSignUp();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mNameDiv.setBackgroundColor(getResources().getColor(
                            R.color.bttendance_cyan));
                } else {
                    mNameDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
                }
            }
        });

        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNameCount = mName.getText().toString().length();
                isEnableSignUp();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEmailDiv
                            .setBackgroundColor(getResources().getColor(R.color.bttendance_cyan));
                } else {
                    mEmailDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
                }
            }
        });

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEmailCount = mEmail.getText().toString().length();
                isEnableSignUp();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mUsername.setText(BTTable.SchoolTable.get(mSchoolID).name);
        mUsername.setClickable(false);
        mUsername.setFocusable(false);

        mName.setText(BTPreference.getUser(getActivity()).full_name);

        mSignUp.setEnabled(false);
        mSignUp.setTextColor(getResources().getColor(R.color.grey_hex_cc));
        mSignUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_navy));
                    v.setPressed(true);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_cyan));
                    v.setPressed(false);
                    trySignUp();
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

    @Override
    public void onResume() {
        super.onResume();

        if (mFullNameString != null)
            mFullName.setText(mFullNameString);
        if (mUsernameString != null)
            mUsername.setText(mUsernameString);
        if (mNameString != null)
            mName.setText(mNameString);
        if (mEmailString != null)
            mEmail.setText(mEmailString);

        mFullNameDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
        mUsernameDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
        mNameDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
        mEmailDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));

        isEnableSignUp();

        KeyboardHelper.show(getActivity(), mFullName);
    }

    @Override
    public void onPause() {
        super.onPause();
        KeyboardHelper.hide(getActivity(), mFullName);
    }

    public void isEnableSignUp() {
        if (mFullNameCount > 0 && mUsernameCount > 0 && mEmailCount > 0) {
            mSignUp.setEnabled(true);
            mSignUp.setTextColor(getResources().getColor(R.color.bttendance_cyan));
        } else {
            mSignUp.setEnabled(false);
            mSignUp.setTextColor(getResources().getColor(R.color.grey_hex_cc));
        }
    }

    private void trySignUp() {
        if (getBTService() == null)
            return;

        String fullName = mFullName.getText().toString();
        String email = mEmail.getText().toString();
        String name = mName.getText().toString();

        getBTService().courseCreate(fullName, email, mSchoolID, name, new Callback<CourseJson>() {
            @Override
            public void success(CourseJson courseJson, Response response) {

                int count = getActivity().getSupportFragmentManager().getBackStackEntryCount();
                getActivity().getSupportFragmentManager().popBackStack();
                while (count-- >= 0)
                    getActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(getString(R.string.create_course));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
