package com.bttendance.fragment.profile;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bttendance.R;
import com.bttendance.fragment.BTFragment;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.json.UserJson;
import com.bttendance.service.request.UserPutRequest;
import com.bttendance.view.BTDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2014. 1. 23..
 */
public class UpdatePasswordFragment extends BTFragment implements Callback<UserJson> {

    @InjectView(R.id.edit_old)
    EditText mOld;
    @InjectView(R.id.edit_new)
    EditText mNew;
    @InjectView(R.id.divider_old)
    View mOldDiv;
    @InjectView(R.id.divider_new)
    View mNewDiv;
    @InjectView(R.id.save)
    Button mSave;

    private int mOldCount = 0;
    private int mNewCount = 0;
    private String mOldString = null;
    private String mNewString = null;
    private MaterialDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_password, container, false);
        ButterKnife.inject(this, view);
        KeyboardHelper.show(getActivity(), mOld);

        mOld.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mOldDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_cyan));
                } else {
                    mOldDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
                }
            }
        });

        mOld.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mOldCount = mOld.getText().toString().length();
                isEnableSave();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mNew.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mNewDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_cyan));
                } else {
                    mNewDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
                }
            }
        });

        mNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNewCount = mNew.getText().toString().length();
                isEnableSave();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSave.setEnabled(false);
        mSave.setTextColor(getResources().getColor(R.color.bttendance_silver_30));
        mSave.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_navy));
                    v.setPressed(true);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_cyan));
                    v.setPressed(false);
                    save();
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

    private void save() {
        dialog = BTDialog.progress(getActivity(), getString(R.string.updating_password));
        getBTService().updateUser(new UserPutRequest().updatePassword(mOld.getText().toString(), mNew.getText().toString()), this);
    }

    private void isEnableSave() {

        if (mOldCount > 0 && mNewCount > 0) {
            mSave.setEnabled(true);
            mSave.setTextColor(getResources().getColor(R.color.bttendance_cyan));
        } else {
            mSave.setEnabled(false);
            mSave.setTextColor(getResources().getColor(R.color.bttendance_silver_30));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mOldString != null)
            mOld.setText(mOldString);
        if (mNewString != null)
            mNew.setText(mNewString);

        mOldDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
        mOld.setSelection(mOld.getText().length(), mOld.getText().length());

        mNewDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
        mNew.setSelection(mNew.getText().length(), mNew.getText().length());

        isEnableSave();
        KeyboardHelper.show(getActivity(), mOld);
    }

    @Override
    public void onPause() {
        super.onPause();
        mOldString = mOld.getText().toString();
        mNewString = mNew.getText().toString();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KeyboardHelper.hide(getActivity(), mOld);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getActivity() == null)
            return;

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.update_password));
    }

    /**
     * Callback from BTService
     *
     * @param userJson
     * @param response
     */
    @Override
    public void success(UserJson userJson, Response response) {
        BTDialog.hide(dialog);
        getActivity().onBackPressed();
    }

    @Override
    public void failure(RetrofitError retrofitError) {
        BTDialog.hide(dialog);
    }
}
