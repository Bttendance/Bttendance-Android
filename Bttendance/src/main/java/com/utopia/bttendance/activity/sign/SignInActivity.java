package com.utopia.bttendance.activity.sign;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.R;
import com.utopia.bttendance.activity.BTActivity;
import com.utopia.bttendance.helper.UUIDHelper;
import com.utopia.bttendance.model.BTPreference;
import com.utopia.bttendance.model.json.ErrorJson;
import com.utopia.bttendance.model.json.UserJson;
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
    private TextView mForgetPwd = null;
    private int mUsernameCount = 0;
    private int mPasswordCount = 0;
    private String mUsernameString = null;
    private String mPasswordString = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

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
                            R.color.bttendance_blue_point));
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
                            R.color.bttendance_blue_point));
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
        mSignIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_blue_main));
                    v.setPressed(true);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_blue_point));
                    v.setPressed(false);
                    trySignIn();
                }
                if (event.getX() < 0
                        || event.getX() > v.getWidth()
                        || event.getY() < 0
                        || event.getY() > v.getHeight()) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_blue_point));
                    v.setPressed(false);
                }
                return true;
            }
        });


        SpannableStringBuilder builder = new SpannableStringBuilder();
        String forgot_password = getString(R.string.forgot_password);
        String forgot_password_html = "<a href=\"http://m.vingle.net/about/terms\">"
                + forgot_password + "</a>";
        SpannableString SpannableHTML = new SpannableString(Html.fromHtml(forgot_password_html));
        SpannableHTML.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.bttendance_blue_main)), 0, forgot_password.length(), 0);
        builder.append(SpannableHTML);

        mForgetPwd = (TextView) findViewById(R.id.forget_pwd);
        mForgetPwd.setText(builder, TextView.BufferType.SPANNABLE);
        mForgetPwd.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void isEnableSignIn() {

        if (mUsernameCount > 0 && mPasswordCount > 5) {
            mSignIn.setEnabled(true);
            mSignIn.setTextColor(getResources().getColor(R.color.bttendance_blue_point));
        } else {
            mSignIn.setEnabled(false);
            mSignIn.setTextColor(getResources().getColor(R.color.grey_hex_eb));

        }
    }

    protected void trySignIn() {
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();
        String uuid = UUIDHelper.getUUID(this);
        getBTService().signin(username, password, uuid, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                BTDebug.LogInfo(user.toJson());
                BTPreference.setUser(SignInActivity.this, user);
                startActivity(getNextIntent());
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

        if (mUsernameString != null)
            mUsername.setText(mUsernameString);
        if (mPasswordString != null)
            mPassword.setText(mPasswordString);

        mUsernameDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
        mPasswordDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
    }

    @Override
    public void onPause() {
        super.onPause();

        mUsernameString = mUsername.getText().toString();
        mPasswordString = mPassword.getText().toString();
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
