package com.bttendance.activity.sign;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.fragment.sign.ForgotPasswordFragment;
import com.bttendance.helper.BluetoothHelper;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.json.UserJson;
import com.bttendance.view.BTDialog;
import com.squareup.otto.BTEventBus;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 11. 19..
 */

public class SignInActivity extends BTActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.username)
    EditText mUsername;
    @InjectView(R.id.password)
    EditText mPassword;
    @InjectView(R.id.username_divider)
    View mUsernameDiv;
    @InjectView(R.id.password_divider)
    View mPasswordDiv;
    @InjectView(R.id.signin)
    Button mSignIn;
    @InjectView(R.id.forget_pwd)
    Button mForgetPwd;

    private int mUsernameCount = 0;
    private int mPasswordCount = 0;
    private String mUsernameString = null;
    private String mPasswordString = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        mUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mUsernameDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_cyan));
                } else {
                    mUsernameDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
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
                isEnableSignIn();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSignIn.setEnabled(false);
        mSignIn.setTextColor(getResources().getColor(R.color.bttendance_silver_30));
        mSignIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_navy));
                    v.setPressed(true);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_cyan));
                    v.setPressed(false);
                    trySignIn();
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

        mForgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardHelper.hide(SignInActivity.this, mUsername);
                ForgotPasswordFragment frag = new ForgotPasswordFragment();
                BTEventBus.getInstance().post(new AddFragmentEvent(frag));
            }
        });
    }

    public void isEnableSignIn() {

        if (mUsernameCount > 0 && mPasswordCount > 5) {
            mSignIn.setEnabled(true);
            mSignIn.setTextColor(getResources().getColor(R.color.bttendance_cyan));
        } else {
            mSignIn.setEnabled(false);
            mSignIn.setTextColor(getResources().getColor(R.color.bttendance_silver_30));
        }
    }

    protected void trySignIn() {
        if (getBTService() == null)
            return;

        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();
        String uuid = BluetoothHelper.getMacAddress();
        BTDialog.progress(this, getString(R.string.loging_in_bttendance));
//        getBTService().signin(username, password, uuid, new Callback<UserJson>() {
//            @Override
//            public void success(UserJson user, Response response) {
//                BTEventBus.getInstance().post(new HideProgressDialogEvent());
//                SignInActivity.this.runOnUiThread(new Runnable() {
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
    public void onResume() {
        super.onResume();

        if (mUsernameString != null)
            mUsername.setText(mUsernameString);
        if (mPasswordString != null)
            mPassword.setText(mPasswordString);

        mUsernameDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
        mPasswordDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));

        isEnableSignIn();
        BTEventBus.getInstance().register(mEventDispatcher);
    }

    @Override
    public void onPause() {
        super.onPause();
        mUsernameString = mUsername.getText().toString();
        mPasswordString = mPassword.getText().toString();
        BTEventBus.getInstance().unregister(mEventDispatcher);
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
        actionBar.setTitle(getString(R.string.log_in));
        actionBar.setDisplayHomeAsUpEnabled(true);
        KeyboardHelper.show(this, mUsername);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.modal_activity_close_enter, R.anim.modal_activity_close_exit);
    }
}// end of class
