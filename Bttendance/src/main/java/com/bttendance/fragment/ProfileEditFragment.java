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
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.event.HideProgressDialogEvent;
import com.bttendance.event.ShowProgressDialogEvent;
import com.bttendance.event.update.UpdateProfileEvent;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.BTKey;
import com.bttendance.model.json.UserJson;
import com.squareup.otto.BTEventBus;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2014. 1. 23..
 */
public class ProfileEditFragment extends BTFragment implements Callback<UserJson> {

    private String mTitle = null;
    private TextView mText = null;
    private EditText mEdit = null;
    private View mEditDiv = null;
    private Button mSave = null;
    private int mEditCount = 0;
    private String mEditString = null;
    private Type mType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle = getArguments().getString(BTKey.EXTRA_TITLE);
        mEditString = getArguments().getString(BTKey.EXTRA_MESSAGE);
        mType = (Type) getArguments().getSerializable(BTKey.EXTRA_TYPE);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        mText = (TextView) view.findViewById(R.id.text);
        mEdit = (EditText) view.findViewById(R.id.edit);
        mEditDiv = view.findViewById(R.id.edit_divider);

        mText.setText(mTitle);
        mEdit.setText(mEditString);
        KeyboardHelper.show(getActivity(), mEdit);

        mEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEditDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_cyan));
                } else {
                    mEditDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
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

        mSave = (Button) view.findViewById(R.id.save);
        mSave.setEnabled(false);
        mSave.setTextColor(getResources().getColor(R.color.grey_hex_cc));
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
        BTEventBus.getInstance().post(new ShowProgressDialogEvent(getString(R.string.updating_profile)));
        switch (mType) {
            case NAME:
                getBTService().updateFullName(mEdit.getText().toString(), this);
                break;
            case MAIL:
                getBTService().updateEmail(mEdit.getText().toString(), this);
                break;
            default:
                BTEventBus.getInstance().post(new HideProgressDialogEvent());
                break;
        }
    }

    private void isEnableSave() {

        if (mEditCount > 0) {
            mSave.setEnabled(true);
            mSave.setTextColor(getResources().getColor(R.color.bttendance_cyan));
        } else {
            mSave.setEnabled(false);
            mSave.setTextColor(getResources().getColor(R.color.grey_hex_cc));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mEditString != null)
            mEdit.setText(mEditString);
        mEditDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
        isEnableSave();
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
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(getString(R.string.edit_profile));
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

    /**
     * Callback from BTService
     *
     * @param userJson
     * @param response
     */
    @Override
    public void success(UserJson userJson, Response response) {
        BTEventBus.getInstance().post(new HideProgressDialogEvent());
        BTEventBus.getInstance().post(new UpdateProfileEvent());
        getActivity().onBackPressed();
    }

    @Override
    public void failure(RetrofitError retrofitError) {
        BTEventBus.getInstance().post(new HideProgressDialogEvent());
    }

    public enum Type {IMAGE, NAME, MAIL}
}
