package com.bttendance.activity.sign;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.helper.BluetoothHelper;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.BTUrl;
import com.bttendance.view.BTDialog;
import com.squareup.otto.BTEventBus;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignUpActivity extends BTActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.full_name)
    EditText mFullName;
    @InjectView(R.id.email)
    EditText mEmail;
    @InjectView(R.id.password)
    EditText mPassword;
    @InjectView(R.id.full_name_divider)
    View mFullNameDiv;
    @InjectView(R.id.email_divider)
    View mEmailDiv;
    @InjectView(R.id.password_divider)
    View mPasswordDiv;
    @InjectView(R.id.signup)
    Button mSignUp;
    @InjectView(R.id.term_of_use)
    TextView mTermOfUse;

    private int mFullNameCount = 0;
    private int mEmailCount = 0;
    private int mPasswordCount = 0;
    private String mFullNameString = null;
    private String mEmailString = null;
    private String mPasswordString = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        mFullName.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mFullNameDiv.setBackgroundColor(getResources().getColor(
                            R.color.bttendance_cyan));
                } else {
                    mFullNameDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
                }
            }
        });

        mFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFullNameCount = mFullName.getText().toString().length();
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
                    mEmailDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
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
                    mPasswordDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
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
                    if (BluetoothHelper.getMacAddress() == null) {
                        String title = getString(R.string.turn_on_bt_title);
                        String message = getString(R.string.turn_on_bt_message);
                        BTDialog.alert(SignUpActivity.this, title, message, new BTDialog.OnDialogListener() {
                            @Override
                            public void onConfirmed(String edit) {
                                BluetoothHelper.enable(SignUpActivity.this);
                            }

                            @Override
                            public void onCanceled() {
                            }
                        });
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

        String string_format = getString(R.string.by_tapping_i_agree_to_the_start);
        SpannableString SpannableFormat = new SpannableString(string_format);
        builder.append(SpannableFormat);

        String term_and_condition = getString(R.string.terms_of_service);
        String term_and_condition_html = "<a href=\"" + BTUrl.TERMS_EN + "\">"
                + term_and_condition + "</a>";

        String locale = getResources().getConfiguration().locale.getLanguage();
        if ("ko".equals(locale))
            term_and_condition_html = "<a href=\"" + BTUrl.TERMS_KR + "\">" + term_and_condition + "</a>";

        SpannableString SpannableTermHTML = new SpannableString(Html.fromHtml(term_and_condition_html));
        SpannableTermHTML.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.bttendance_navy)), 0, term_and_condition.length(), 0);
        builder.append(SpannableTermHTML);

        SpannableString SpannableAnd = new SpannableString(getString(R.string._and_));
        builder.append(SpannableAnd);

        String privacy_policy = getString(R.string.privacy_policy);
        String privacy_policy_html = "<a href=\"" + BTUrl.PRIVACY_EN + "\">" + privacy_policy + "</a>";
        if ("ko".equals(locale))
            privacy_policy_html = "<a href=\"" + BTUrl.PRIVACY_KR + "\">" + privacy_policy + "</a>";

        SpannableString SpannablePrivacyHTML = new SpannableString(Html.fromHtml(privacy_policy_html));
        SpannablePrivacyHTML.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.bttendance_navy)), 0, privacy_policy.length(), 0);
        builder.append(SpannablePrivacyHTML);

        SpannableString SpannableComma = new SpannableString(getString(R.string.by_tapping_i_agree_to_the_end));
        builder.append(SpannableComma);

        mTermOfUse.setText(builder, TextView.BufferType.SPANNABLE);
        mTermOfUse.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void isEnableSignUp() {
        if (mFullNameCount > 0 && mEmailCount > 0 && mPasswordCount > 5) {
            mSignUp.setEnabled(true);
            mSignUp.setTextColor(getResources().getColor(R.color.bttendance_cyan));
        } else {
            mSignUp.setEnabled(false);
            mSignUp.setTextColor(getResources().getColor(R.color.bttendance_silver_30));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mFullNameString != null)
            mFullName.setText(mFullNameString);
        if (mEmailString != null)
            mEmail.setText(mEmailString);
        if (mPasswordString != null)
            mPassword.setText(mPasswordString);

        mFullNameDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
        mEmailDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
        mPasswordDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));

        isEnableSignUp();
        BTEventBus.getInstance().register(mEventDispatcher);
    }

    @Override
    public void onPause() {
        super.onPause();

        mFullNameString = mFullName.getText().toString();
        mEmailString = mEmail.getText().toString();
        mPasswordString = mPassword.getText().toString();
        BTEventBus.getInstance().unregister(mEventDispatcher);
    }

    private void trySignUp() {
        if (getBTService() == null)
            return;

        String fullName = mFullName.getText().toString();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

//        UserJson user = new UserJson();
//        user.full_name = fullName;
//        user.email = email;
//        user.password = password;
//        user.device = new DeviceJsonSimple();
//        user.device.type = BTAPI.ANDROID;
//        user.device.uuid = BluetoothHelper.getMacAddress();

        BTDialog.progress(this, getString(R.string.signing_up_bttendance));
//        getBTService().signup(user, new Callback<UserJson>() {
//            @Override
//            public void success(UserJson user, Response response) {
//                BTEventBus.getInstance().post(new HideProgressDialogEvent());
//                SignUpActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        startActivity(getNextIntent());
//                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                    }
//                });
//            }
//
//            @Override
//            public void failure(RetrofitError retrofitError) {
//                BTEventBus.getInstance().post(new HideProgressDialogEvent());
//            }
//        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.sign_up));
        actionBar.setDisplayHomeAsUpEnabled(true);
        KeyboardHelper.show(this, mFullName);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.modal_activity_close_enter, R.anim.modal_activity_close_exit);
    }

}// end of class
