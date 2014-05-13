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

    View mView;
    DialogType mType;
    OnConfirmListener mListener;
    String mTitle;
    String mMessage;
    EditText mEdit;
    String mPlaceholder;
    View mEditDivider;
    boolean mConfirmed = false;
    int mScreenHeight;

    /**
     * Constructor
     */
    public BTDialogFragment(String message) {
        mType = DialogType.PROGRESS;
        mMessage = message;
    }

    public BTDialogFragment(DialogType type, String title, String message, String placeholder, OnConfirmListener listener) {
        mType = type;
        mTitle = title;
        mMessage = message;
        mPlaceholder = placeholder;
        mListener = listener;
    }

    /**
     * Re Construct
     */
    public DialogType getType() {
        return mType;
    }

    public void toProgress(String message) {
        mType = DialogType.PROGRESS;
        mMessage = message;
        draw();
    }

    public void toAlert(DialogType type, String title, String message, String placeholder, OnConfirmListener listener) {
        mType = type;
        mTitle = title;
        mMessage = message;
        mPlaceholder = placeholder;
        mListener = listener;
        draw();
    }

    /**
     * Drawing
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.bt_dialog, container, false);
        draw();
        return mView;
    }

    private void draw() {
        switch (mType) {
            case PROGRESS:
                mView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                mView.findViewById(R.id.alert).setVisibility(View.GONE);
                mView.findViewById(R.id.padding_layout).setVisibility(View.GONE);
                drawProgress();
                break;
            default:
                mView.findViewById(R.id.progress).setVisibility(View.GONE);
                mView.findViewById(R.id.alert).setVisibility(View.VISIBLE);
                mView.findViewById(R.id.padding_layout).setVisibility(View.VISIBLE);
                drawAlert();
                break;
        }
    }

    private void drawAlert() {
        Button confirm = (Button) mView.findViewById(R.id.confirm);
        Button cancel = (Button) mView.findViewById(R.id.cancel);
        View divider = mView.findViewById(R.id.divider);
        mEdit = (EditText) mView.findViewById(R.id.edit);
        mEditDivider = mView.findViewById(R.id.edit_divider);
        switch (mType) {
            case EDIT:
                mEdit.setVisibility(View.VISIBLE);
                mEditDivider.setVisibility(View.VISIBLE);
                KeyboardHelper.show(getActivity(), mEdit);
            case CONFIRM:
                confirm.setText(getString(R.string.confrim));
                cancel.setText(getString(R.string.cancel));
                break;
            case OK:
                confirm.setText(getString(R.string.ok));
                cancel.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
                break;
        }
        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);

        TextView title = (TextView) mView.findViewById(R.id.title);
        TextView message = (TextView) mView.findViewById(R.id.message_alert);
        title.setText(mTitle);
        message.setText(mMessage);

        mScreenHeight = ScreenHelper.getHeight(getActivity());
        mView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mView.getHeight() < mScreenHeight - 200) {
                    ((LinearLayout) mView.findViewById(R.id.total_layout)).setWeightSum(60);
                    mView.findViewById(R.id.padding_layout).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
                } else {
                    ((LinearLayout) mView.findViewById(R.id.total_layout)).setWeightSum(100);
                    mView.findViewById(R.id.padding_layout).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 40));
                }
            }
        });

        if (mPlaceholder != null)
            mEdit.setHint(mPlaceholder);
    }

    private void drawProgress() {
        TextView message = (TextView) mView.findViewById(R.id.message_progress);
        message.setText(mMessage);
    }

    /**
     * View.OnClickListener
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                mConfirmed = true;
                if (mListener != null)
                    mListener.onConfirmed(mEdit.getText().toString());
            case R.id.cancel:
                KeyboardHelper.hide(getActivity(), mEdit);
                getActivity().onBackPressed();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mListener != null && !mConfirmed)
            mListener.onCanceled();
    }

    // Confirm => "confrim" & "cancel"
    // OK => "ok"
    public enum DialogType {
        CONFIRM, OK, EDIT, PROGRESS
    }

    public interface OnConfirmListener {
        void onConfirmed(String edit);

        void onCanceled();
    }
}
