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
import com.bttendance.R;
import com.bttendance.event.dialog.ShowAlertDialogEvent;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.UserJsonSimple;
import com.bttendance.view.BeautiToast;
import com.squareup.otto.BTEventBus;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class AddManagerFragment extends BTFragment {

    private EditText mEmail = null;
    private View mEmailDiv = null;
    private int mEmailCount = 0;
    private Button mSignUp = null;
    private String mEmailString = null;
    private CourseJson mCourse;

    public AddManagerFragment(int courseID) {
        mCourse = BTTable.MyCourseTable.get(courseID);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_manager, container, false);

        mEmail = (EditText) view.findViewById(R.id.email);
        mEmailDiv = view.findViewById(R.id.email_divider);
        mSignUp = (Button) view.findViewById(R.id.signup);

//        mEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    mEmailDiv
//                            .setBackgroundColor(getResources().getColor(R.color.bttendance_cyan));
//                } else {
//                    mEmailDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
//                }
//            }
//        });

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
    public void onFragmentResume() {
        super.onFragmentResume();

        if (mEmailString != null)
            mEmail.setText(mEmailString);

        mEmailDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
        isEnableSignUp();
        KeyboardHelper.show(getActivity(), mEmail);
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        KeyboardHelper.hide(getActivity(), mEmail);
    }

    public void isEnableSignUp() {
        if (mEmailCount > 0) {
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

        final String email = mEmail.getText().toString();

        getBTService().searchUser(email, new Callback<UserJsonSimple>() {
            @Override
            public void success(final UserJsonSimple user, Response response) {

                String title = getString(R.string.add_manager);
                String message = String.format(getString(R.string.would_you_like_to_add), user.full_name, mCourse.name);
                BTDialogFragment.OnDialogListener listener = new BTDialogFragment.OnDialogListener() {
                    @Override
                    public void onConfirmed(String edit) {
                        getBTService().addManager(user.email, mCourse.id, new Callback<CourseJson>() {
                            @Override
                            public void success(CourseJson courseJson, Response response) {
                                BeautiToast.show(getActivity(), String.format(getActivity().getString(R.string.is_now_manager_of_course), user.full_name, mCourse.name));
                                getActivity().getSupportFragmentManager().popBackStack();
                            }

                            @Override
                            public void failure(RetrofitError retrofitError) {
                            }
                        });
                    }

                    @Override
                    public void onCanceled() {
                    }
                };

                BTEventBus.getInstance().post(new ShowAlertDialogEvent(BTDialogFragment.DialogType.CONFIRM, title, message, listener));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getSherlockActivity() == null)
            return;

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(getString(R.string.add_manager));
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
