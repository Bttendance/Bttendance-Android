package com.bttendance.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.helper.KeyboardHelper;
import com.bttendance.helper.ScreenHelper;

/**
 * Created by TheFinestArtist on 2013. 12. 20..
 */
public class BTDialogFragment extends BTFragment implements View.OnClickListener {

    DialogType mType;
    OnConfirmListener mConfrimListener;
    String mTitle;
    String mMessage;
    EditText mEdit;
    String mEditPlaceholder;
    View mEditDivider;
    boolean mConfirmed = false;
    int mScreenHeight;

    public BTDialogFragment(DialogType type, String title, String message) {
        mType = type;
        mTitle = title;
        mMessage = message;
    }

    public void setOnConfirmListener(OnConfirmListener confrimListener) {
        mConfrimListener = confrimListener;
    }

    public void setPlaceholder(String placeholder) {
        mEditPlaceholder = placeholder;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.bt_alert_dialog, container, false);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        Button confirm = (Button) view.findViewById(R.id.confirm);
        View divider = view.findViewById(R.id.divider);
        mEdit = (EditText) view.findViewById(R.id.edit);
        mEditDivider = view.findViewById(R.id.edit_divider);
        switch (mType) {
            case EDIT:
                mEdit.setVisibility(View.VISIBLE);
                mEditDivider.setVisibility(View.VISIBLE);
                KeyboardHelper.show(getActivity(), mEdit);
            case CONFIRM:
                cancel.setText(getString(R.string.cancel));
                confirm.setText(getString(R.string.confrim));
                break;
            case OK:
                confirm.setText(getString(R.string.ok));
                cancel.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
                break;
        }
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);

        TextView title = (TextView) view.findViewById(R.id.title);
        TextView message = (TextView) view.findViewById(R.id.message);
        title.setText(mTitle);
        message.setText(mMessage);

        mScreenHeight = ScreenHelper.getHeight(getActivity());
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (view.getHeight() < mScreenHeight - 200) {
                    ((LinearLayout) view.findViewById(R.id.total_layout)).setWeightSum(60);
                    view.findViewById(R.id.padding_layout).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
                } else {
                    ((LinearLayout) view.findViewById(R.id.total_layout)).setWeightSum(100);
                    view.findViewById(R.id.padding_layout).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 40));
                }
            }
        });

        if (mEditPlaceholder != null)
            mEdit.setHint(mEditPlaceholder);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                mConfirmed = true;
                if (mConfrimListener != null)
                    mConfrimListener.onConfirmed(mEdit.getText().toString());
            case R.id.cancel:
                KeyboardHelper.hide(getActivity(), mEdit);
                getActivity().onBackPressed();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mConfrimListener != null && !mConfirmed)
            mConfrimListener.onCanceled();
    }

    // Confirm => "cancel" & "confirm"
    // OK => "ok"
    public enum DialogType {
        CONFIRM, OK, EDIT
    }

    public interface OnConfirmListener {
        void onConfirmed(String edit);

        void onCanceled();
    }
}
