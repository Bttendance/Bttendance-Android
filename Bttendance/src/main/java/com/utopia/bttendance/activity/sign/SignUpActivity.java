package com.utopia.bttendance.activity.sign;

import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.R;
import com.utopia.bttendance.helper.BluetoothHelper;
import com.utopia.bttendance.helper.KeyboardHelper;
import com.utopia.bttendance.helper.UUIDHelper;
import com.utopia.bttendance.model.json.*;
import com.utopia.bttendance.service.BTService;
import com.utopia.bttendance.view.BeautiToast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignUpActivity extends SherlockFragmentActivity {

    private EditText mFullName = null;
    private EditText mUsername = null;
    private EditText mEmail = null;
    private EditText mPassword = null;
    private EditText mPasswordHint = null;
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
    private String mFullnameString = null;
    private String mUsernameString = null;
    private String mEmailString = null;
    private String mPasswordString = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setTitle(getString(R.string.sign_up));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        mFullName = (EditText) findViewById(R.id.full_name);
        mUsername = (EditText) findViewById(R.id.username);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mPasswordHint = (EditText) findViewById(R.id.password_hint);
        mFullNameDiv = findViewById(R.id.full_name_divider);
        mUsernameDiv = findViewById(R.id.username_divider);
        mEmailDiv = findViewById(R.id.email_divider);
        mPasswordDiv = findViewById(R.id.password_divider);

        mFullName.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mFullNameDiv.setBackgroundColor(getResources().getColor(
                            R.color.grey_hex_66));
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
                            .setBackgroundColor(getResources().getColor(R.color.grey_hex_66));
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
                isEnableSignUp();
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

        mSignUp = (Button) findViewById(R.id.signup);
        mSignUp.setEnabled(false);
        mSignUp.setTextColor(getResources().getColor(R.color.grey_hex_eb));
        mSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                trySignUp();
            }
        });

        String string_format = getString(R.string.by_creating_an_account_you_agree_to_our_s);
        String term_and_condition = getString(R.string.terms_and_conditions);
        String term_and_condition_html = "<a href=\"http://m.vingle.net/about/terms\">"
                + term_and_condition + "</a>";
        String html_string = String.format(string_format, term_and_condition_html);

        mTermOfUse = (TextView) findViewById(R.id.term_of_use);
        mTermOfUse.setText(Html.fromHtml(html_string));
        mTermOfUse.setMovementMethod(LinkMovementMethod.getInstance());

        ColorStateList cl = null;
        try {
            XmlResourceParser xpp = getResources().getXml(R.color.grey_hex_cc);
            cl = ColorStateList.createFromXml(getResources(), xpp);
            mTermOfUse.setLinkTextColor(cl);
        } catch (Exception e) {
        }
    }

    public void isEnableSignUp() {
        if (mFullNameCount > 0 && mUsernameCount > 0 && mEmailCount > 0 && mPasswordCount > 5) {
            mSignUp.setEnabled(true);
            mSignUp.setTextColor(getResources().getColor(R.color.bttendance_blue));
        } else {
            mSignUp.setEnabled(false);
            mSignUp.setTextColor(getResources().getColor(R.color.grey_hex_eb));

        }
    }

    @Override
    public void onResume() {
        super.onResume();

//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            mUsername.setText(bundle.getString(BundleKey.EXTRA_USERNAME));
//            mEmail.setText(bundle.getString(BundleKey.EXTRA_EMAIL));
//        }

        if (mFullnameString != null)
            mFullName.setText(mFullnameString);
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

        KeyboardHelper.show(this, mFullName);
    }

    @Override
    public void onPause() {
        super.onPause();

        mFullnameString = mFullName.getText().toString();
        mUsernameString = mUsername.getText().toString();
        mEmailString = mEmail.getText().toString();
        mPasswordString = mPassword.getText().toString();

        KeyboardHelper.hide(this, mFullName);
    }

    private void trySignUp() {
        String fullName = mFullName.getText().toString();
        String username = mUsername.getText().toString();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        UserJson user = new UserJson();
        user.username = username;
        user.full_name = fullName;
        user.email = email;
        user.password = password;
        user.device_type = "Android";
        user.device_uuid = UUIDHelper.getUUID(this);

        BTDebug.LogInfo(user.toJson());

        BTService.signup(user, new Callback<UserJson>() {
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
