package com.bttendance.fragment.sign;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bttendance.R;
import com.bttendance.fragment.BTFragment;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.view.BTDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class ForgotPasswordFragment extends BTFragment {

    @InjectView(R.id.email)
    EditText mEmail;
    @InjectView(R.id.email_divider)
    View mEmailDiv;
    @InjectView(R.id.signup)
    Button mSignUp;

    private int mEmailCount = 0;
    private String mEmailString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        ButterKnife.inject(this, view);

        mEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEmailDiv
                            .setBackgroundColor(getResources().getColor(R.color.bttendance_cyan));
                } else {
                    mEmailDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
                }
            }
        });

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEmailString = mEmail.getText().toString();
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
        mSignUp.setTextColor(getResources().getColor(R.color.bttendance_silver_30));
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

        mEmailDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
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

        mEmailDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
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
            mSignUp.setTextColor(getResources().getColor(R.color.bttendance_silver_30));
        }
    }

    private void trySignUp() {
        if (getBTService() == null)
            return;

        final String email = mEmail.getText().toString();

        final MaterialDialog dialog = BTDialog.progress(getActivity(), getString(R.string.recovering_password));
        getBTService().forgotPassword(email, new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {
                BTDialog.hide(dialog);
                String title = getString(R.string.password_recovery_success);
                String message = String.format(getString(R.string.password_recovery_has_been_succeeded), email);
                BTDialog.ok(getActivity(), title, message, null);
            }

            @Override
            public void failure(RetrofitError error) {
                BTDialog.hide(dialog);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getActivity() == null)
            return;
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(getString(R.string.forgot_password));
        actionBar.setDisplayHomeAsUpEnabled(true);
        KeyboardHelper.show(getActivity(), mEmail);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
