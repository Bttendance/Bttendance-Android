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
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bttendance.R;
import com.bttendance.fragment.BTFragment;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.BTKey;
import com.bttendance.model.json.UserJson;
import com.bttendance.service.request.UserPutRequest;
import com.bttendance.widget.BTDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2014. 1. 23..
 */
public class ProfileEditFragment extends BTFragment implements Callback<UserJson> {

    @InjectView(R.id.text)
    TextView mText;
    @InjectView(R.id.edit)
    EditText mEdit;
    @InjectView(R.id.edit_divider)
    View mEditDiv;
    @InjectView(R.id.save)
    Button mSave;

    private String mTitle;
    private int mEditCount = 0;
    private String mEditString = null;
    private Type mType;
    private MaterialDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mTitle = getArguments() != null ? getArguments().getString(BTKey.EXTRA_TITLE) : null;
        mEditString = getArguments() != null ? getArguments().getString(BTKey.EXTRA_MESSAGE) : null;
        mType = getArguments() != null ? (Type) getArguments().getSerializable(BTKey.EXTRA_TYPE) : Type.IMAGE;

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        ButterKnife.inject(this, view);

        mText.setText(mTitle);
        mEdit.setText(mEditString);
        KeyboardHelper.show(getActivity(), mEdit);

        mEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEditDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_cyan));
                } else {
                    mEditDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
                }
            }
        });

        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEditCount = mEdit.getText().toString().length();
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
        dialog = BTDialog.progress(getActivity(), getString(R.string.updating_profile));
        String editString = mEdit.getText().toString();
        switch (mType) {
            case NAME:
                getBTService().updateUser(new UserPutRequest().updateName(editString), this);
                break;
            case MAIL:
                getBTService().updateUser(new UserPutRequest().updateEmail(editString), this);
                break;
            case IDENTITY:
                getBTService().updateUser(new UserPutRequest().updateIdentity(getArguments().getInt(BTKey.EXTRA_SCHOOL_ID), editString), this);
                break;
            default:
                BTDialog.hide(dialog);
                break;
        }
    }

    private void isEnableSave() {

        if (mEditCount > 0) {
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

        if (mEditString != null)
            mEdit.setText(mEditString);
        mEditDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_silver_30));
        mEdit.setSelection(mEdit.getText().length(), mEdit.getText().length());
        isEnableSave();
        KeyboardHelper.show(getActivity(), mEdit);
    }

    @Override
    public void onPause() {
        super.onPause();
        mEditString = mEdit.getText().toString();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KeyboardHelper.hide(getActivity(), mEdit);
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
        actionBar.setTitle(String.format(getString(R.string.edit_), mTitle));
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

    public enum Type {IMAGE, NAME, MAIL, IDENTITY}
}
