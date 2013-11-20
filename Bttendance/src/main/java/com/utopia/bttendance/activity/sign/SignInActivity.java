package com.utopia.bttendance.activity.sign;

import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.R;
import com.utopia.bttendance.activity.BTActivity;
import com.utopia.bttendance.helper.KeyboardHelper;
import com.utopia.bttendance.model.json.ErrorJson;
import com.utopia.bttendance.model.json.UserJson;
import com.utopia.bttendance.service.BTService;
import com.utopia.bttendance.view.BeautiToast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */

public class SignInActivity extends BTActivity {

    private EditText mUsername = null;
    private EditText mPassword = null;
    private EditText mPasswordHint = null;
    private View mUsernameDiv = null;
    private View mPasswordDiv = null;
    private Button mSignIn = null;
    private Button mForgetPwd = null;
    private int mUsernameCount = 0;
    private int mPasswordCount = 0;
    private boolean mRestoreUserInfo = true;
    private String mUsernameString = null;
    private String mPasswordString = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getSupportActionBar().setTitle(getString(R.string.sign_in));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mPasswordHint = (EditText) findViewById(R.id.password_hint);
        mUsernameDiv = findViewById(R.id.username_divider);
        mPasswordDiv = findViewById(R.id.password_divider);

        mUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mUsernameDiv.setBackgroundColor(getResources().getColor(
                            R.color.grey_hex_66));
                } else {
                    mUsernameDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
                }
            }
        });

        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUsernameCount = mUsername.getText().toString().length();
                isEnableSignIn();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mPasswordDiv.setBackgroundColor(getResources().getColor(
                            R.color.grey_hex_66));
                } else {
                    mPasswordDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
                }
            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPasswordCount = mPassword.getText().toString().length();
                isEnableSignIn();
                if (mPasswordCount == 0) {
                    mPasswordHint.setVisibility(View.VISIBLE);
                } else {
                    mPasswordHint.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSignIn = (Button) findViewById(R.id.signin);
        mSignIn.setEnabled(false);
        mSignIn.setTextColor(getResources().getColor(R.color.grey_hex_eb));
        mSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                trySignIn();
            }
        });

        mForgetPwd = (Button) findViewById(R.id.forget_pwd);
        SpannableString text = new SpannableString(getString(R.string.forgot_your_password));
        text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, text.length(), 0);
        text.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        mForgetPwd.setText(text);
        mForgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Forgot Password
            }
        });
    }

    public void isEnableSignIn() {

        if (mUsernameCount > 0 && mPasswordCount > 5) {
            mSignIn.setEnabled(true);
            mSignIn.setTextColor(getResources().getColor(R.color.bttendance_blue));
        } else {
            mSignIn.setEnabled(false);
            mSignIn.setTextColor(getResources().getColor(R.color.grey_hex_eb));

        }
    }

    protected void trySignIn() {
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();
        BTService.signin(username, password, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                BeautiToast.show(getApplicationContext(), user.toJson());
                BTDebug.LogInfo(user.toJson());
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                String error = retrofitError.getBodyAs(ErrorJson.class).toString();
                BeautiToast.show(getApplicationContext(), error);
                BTDebug.LogError(error);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mRestoreUserInfo)
            restoreStoredInfo();

        if (mUsernameString != null)
            mUsername.setText(mUsernameString);
        if (mPasswordString != null)
            mPassword.setText(mPasswordString);

        mUsernameDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
        mPasswordDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));

        KeyboardHelper.show(this, mUsername);
    }

    private void restoreStoredInfo() {
        String username = null;

        // first check argument.
//        if (getArguments() != null)
//            username = getArguments().getString(BundleKey.EXTRA_USERNAME);
//
//        // then try preference
//        if (username == null)
//            username = VinglePreference.getUserName(getActivity());
//
//        if (username != null && username.length() > 0
//                && getArguments().getString(BundleKey.EXTRA_UID) == null)
//            mUsername.setText(username);

        mRestoreUserInfo = false;
    }

    @Override
    public void onPause() {
        super.onPause();

        mUsernameString = mUsername.getText().toString();
        mPasswordString = mPassword.getText().toString();

        KeyboardHelper.hide(this, mUsername);
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
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
    }
}// end of class
