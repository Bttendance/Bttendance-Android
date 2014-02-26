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
import com.bttendance.event.fragment.ShowCourseCreateEvent;
import com.bttendance.event.fragment.ShowSerialRequestEvent;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.model.json.SchoolJson;
import com.bttendance.view.BeautiToast;
import com.squareup.otto.BTEventBus;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 11. 27..
 */
public class SerialFragment extends BTFragment {


    private EditText mSerial = null;
    private View mSerialDiv = null;
    private Button mEnter = null;
    private Button mReqSerial = null;
    private int mSerialCount = 0;
    private int mSchoolId;

    public SerialFragment(int schoolId) {
        mSchoolId = schoolId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_serial, container, false);

        mSerial = (EditText) view.findViewById(R.id.serial);
        mSerialDiv = view.findViewById(R.id.serial_divider);

        mSerial.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mSerialDiv.setBackgroundColor(getResources().getColor(R.color.bttendance_cyan));
                } else {
                    mSerialDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
                }
            }
        });

        mSerial.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSerialCount = mSerial.getText().toString().length();
                isEnableSignIn();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEnter = (Button) view.findViewById(R.id.enter);
        mEnter.setEnabled(false);
        mEnter.setTextColor(getResources().getColor(R.color.grey_hex_cc));
        mEnter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_navy));
                    v.setPressed(true);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((Button) v).setTextColor(getResources().getColor(R.color.bttendance_cyan));
                    v.setPressed(false);
                    serialValidate();
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

        mReqSerial = (Button) view.findViewById(R.id.req_serial);
        if (mSchoolId != 1)
            mReqSerial.setVisibility(View.GONE);

        mReqSerial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BTEventBus.getInstance().post(new ShowSerialRequestEvent());
            }
        });

        return view;
    }

    public void isEnableSignIn() {

        if (mSerialCount > 0) {
            mEnter.setEnabled(true);
            mEnter.setTextColor(getResources().getColor(R.color.bttendance_cyan));
        } else {
            mEnter.setEnabled(false);
            mEnter.setTextColor(getResources().getColor(R.color.grey_hex_cc));
        }
    }

    protected void serialValidate() {
        if (getBTService() == null)
            return;

        String serial = mSerial.getText().toString();
        getBTService().serialValidate(serial, new Callback<SchoolJson>() {
            @Override
            public void success(SchoolJson school, Response response) {
                if (school != null && school.id == mSchoolId) {
                    BTEventBus.getInstance().post(new ShowCourseCreateEvent(mSchoolId));
                } else
                    BeautiToast.show(getActivity(), getString(R.string.wrong_serial));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                BeautiToast.show(getActivity(), getString(R.string.wrong_serial));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mSerialDiv.setBackgroundColor(getResources().getColor(R.color.grey_hex_cc));
        isEnableSignIn();
        KeyboardHelper.show(getActivity(), mSerial);
    }

    @Override
    public void onPause() {
        super.onPause();
        KeyboardHelper.hide(getActivity(), mSerial);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(getString(R.string.enter_serial));
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
}
