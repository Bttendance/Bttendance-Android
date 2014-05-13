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
import com.bttendance.event.dialog.HideProgressDialogEvent;
import com.bttendance.event.dialog.ShowAlertDialogEvent;
import com.bttendance.event.dialog.ShowProgressDialogEvent;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.json.EmailJson;
import com.squareup.otto.BTEventBus;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class ForgotPasswordFragment extends BTFragment {

    private EditText mEmail = null;
    private View mEmailDiv = null;
    private int mEmailCount = 0;
    private Button mSignUp = null;
    private String mEmailString = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        mEmail = (EditText) view.findViewById(R.id.email);
        mEmailDiv = view.findViewById(R.id.email_divider);
        mSignUp = (Button) view.findViewById(R.id.signup);

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

        if (mEmailString != null)
            mEmail.setText(mEmailString);

        mEmailDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
        isEnableSignUp();
        KeyboardHelper.show(getActivity(), mEmail);
        mEmail.requestFocus();
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (!isAdded())
            return;

        if (mEmailString != null)
            mEmail.setText(mEmailString);

        mEmailDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
        isEnableSignUp();
        KeyboardHelper.show(getActivity(), mEmail);
        mEmail.requestFocus();
    }

    @Override
    public void onPause() {
        super.onPause();
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

        String email = mEmail.getText().toString();

        BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.recovering_password)));
        getBTService().forgotPassword(email, new Callback<EmailJson>() {
            @Override
            public void success(EmailJson email, Response response) {
                BTDialogFragment.DialogType type = BTDialogFragment.DialogType.OK;
                String title = getString(R.string.password_recovery_success);
                String message = String.format(getString(R.string.password_recovery_has_been_succeeded), email.email);
                BTEventBus.getInstance().post(new ShowAlertDialogEvent(type, title, message));
                BTEventBus.getInstance().post(new HideProgressDialogEvent());
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                BTEventBus.getInstance().post(new HideProgressDialogEvent());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getSherlockActivity() == null)
            return;
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(getString(R.string.forgot_password));
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
