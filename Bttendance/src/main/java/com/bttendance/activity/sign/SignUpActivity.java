package com.bttendance.activity.sign;

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
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.bttendance.BTDebug;
import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.event.dialog.ShowEnableBluetoothDialog;
import com.bttendance.helper.BluetoothHelper;
import com.bttendance.model.BTKey;
import com.bttendance.model.json.UserJson;
import com.bttendance.service.BTAPI;
import com.bttendance.service.BTUrl;
import com.bttendance.view.BeautiToast;
import com.squareup.otto.BTEventBus;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignUpActivity extends BTActivity {

    private EditText mFullName = null;
    private EditText mUsername = null;
    private EditText mEmail = null;
    private EditText mPassword = null;
    private View mFullNameDiv = null;
    private View mUsernameDiv = null;
    private View mEmailDiv = null;
    private View mPasswordDiv = null;
    private int mFullNameCount = 0;
    private int mUsernameCount = 0;
    private int mEmailCount = 0;
    private int mPasswordCount = 0;
    private Button mSignUp = null;
    private TextView mTermOfUse = null;
    private String mFullNameString = null;
    private String mUsernameString = null;
    private String mEmailString = null;
    private String mPasswordString = null;

    private void typeError() {
        BeautiToast.show(this, getString(R.string.user_type_error_occurred));
        onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle(getString(R.string.sign_up));

        mFullName = (EditText) findViewById(R.id.full_name);
        mUsername = (EditText) findViewById(R.id.username);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mFullNameDiv = findViewById(R.id.full_name_divider);
        mUsernameDiv = findViewById(R.id.username_divider);
        mEmailDiv = findViewById(R.id.email_divider);
        mPasswordDiv = findViewById(R.id.password_divider);

        mFullName.setOnFocusChangeListener(new OnFocusChangeListener() {
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

        mUsername.setOnFocusChangeListener(new OnFocusChangeListener() {
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

        mEmail.setOnFocusChangeListener(new OnFocusChangeListener() {
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

        mPassword.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mPasswordDiv.setBackgroundColor(getResources().getColor(
                            R.color.bttendance_cyan));
                } else {
                    mPasswordDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
                }
            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPasswordCount = mPassword.getText().toString().length();
                isEnableSignUp();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSignUp = (Button) findViewById(R.id.signup);
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
                    if (BluetoothHelper.getMacAddress() == null) {
                        BTEventBus.getInstance().post(new ShowEnableBluetoothDialog());
                    } else {
                        trySignUp();
                    }
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


        SpannableStringBuilder builder = new SpannableStringBuilder();

        String string_format = getString(R.string.by_tapping_i_agree_to_the);
        SpannableString SpannableFormat = new SpannableString(string_format);
        builder.append(SpannableFormat);

        String term_and_condition = getString(R.string.terms_of_service);
        String term_and_condition_html = "<a href=\"" + BTUrl.TERMS + "\">"
                + term_and_condition + "</a>";
        SpannableString SpannableTermHTML = new SpannableString(Html.fromHtml(term_and_condition_html));
        SpannableTermHTML.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.bttendance_navy)), 0, term_and_condition.length(), 0);
        builder.append(SpannableTermHTML);

        SpannableString SpannableAnd = new SpannableString(" and ");
        builder.append(SpannableAnd);

        String privacy_policy = getString(R.string.privacy_policy);
        String privacy_policy_html = "<a href=\"" + BTUrl.POLICY + "\">"
                + privacy_policy + "</a>";
        SpannableString SpannablePrivacyHTML = new SpannableString(Html.fromHtml(privacy_policy_html));
        SpannablePrivacyHTML.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.bttendance_navy)), 0, privacy_policy.length(), 0);
        builder.append(SpannablePrivacyHTML);

        SpannableString SpannableComma = new SpannableString(".");
        builder.append(SpannableComma);

        mTermOfUse = (TextView) findViewById(R.id.term_of_use);
        mTermOfUse.setText(builder, TextView.BufferType.SPANNABLE);
        mTermOfUse.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void isEnableSignUp() {
        if (mFullNameCount > 0 && mUsernameCount > 0 && mEmailCount > 0 && mPasswordCount > 5) {
            mSignUp.setEnabled(true);
            mSignUp.setTextColor(getResources().getColor(R.color.bttendance_cyan));
        } else {
            mSignUp.setEnabled(false);
            mSignUp.setTextColor(getResources().getColor(R.color.grey_hex_cc));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mFullNameString != null)
            mFullName.setText(mFullNameString);
        if (mUsernameString != null)
            mUsername.setText(mUsernameString);
        if (mEmailString != null)
            mEmail.setText(mEmailString);
        if (mPasswordString != null)
            mPassword.setText(mPasswordString);

        mFullNameDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
        mUsernameDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
        mEmailDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
        mPasswordDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));

        isEnableSignUp();
    }

    @Override
    public void onPause() {
        super.onPause();

        mFullNameString = mFullName.getText().toString();
        mUsernameString = mUsername.getText().toString();
        mEmailString = mEmail.getText().toString();
        mPasswordString = mPassword.getText().toString();
    }

    private void trySignUp() {
        if (getBTService() == null)
            return;

        String fullName = mFullName.getText().toString();
        String username = mUsername.getText().toString();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        UserJson user = new UserJson();
        user.username = username;
        user.full_name = fullName;
        user.email = email;
        user.password = password;
        user.device_type = BTAPI.ANDROID;
        user.device_uuid = BluetoothHelper.getMacAddress();

        getBTService().signup(user, new Callback<UserJson>() {
            @Override
            public void success(UserJson user, Response response) {
                startActivity(getNextIntent());
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
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
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right);
    }

}// end of class
