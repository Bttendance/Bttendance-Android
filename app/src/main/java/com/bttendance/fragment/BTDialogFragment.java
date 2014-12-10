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
import com.bttendance.model.BTKey;

/**
 * Created by TheFinestArtist on 2013. 12. 20..
 */
public class BTDialogFragment extends BTFragment implements View.OnClickListener, ViewTreeObserver.OnGlobalLayoutListener {

    View mView;
    DialogType mType;
    OnDialogListener mListener;
    String[] mOptions;
    String mTitle;
    String mMessage;
    EditText mEdit;
    String mPlaceholder;
    View mEditDivider;
    boolean mConfirmed = false;
    boolean mDying = false;
    int mScreenHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mType = getArguments() != null ? (DialogType) getArguments().getSerializable(BTKey.EXTRA_TYPE) : null;
        mTitle = getArguments() != null ? getArguments().getString(BTKey.EXTRA_TITLE) : null;
        mMessage = getArguments() != null ? getArguments().getString(BTKey.EXTRA_MESSAGE) : null;
        mPlaceholder = getArguments() != null ? getArguments().getString(BTKey.EXTRA_PLACEHOLDER) : null;
        mOptions = getArguments() != null ? getArguments().getStringArray(BTKey.EXTRA_OPTIONS) : null;
        super.onCreate(savedInstanceState);
    }

    public void setOnDialogListener(OnDialogListener listener) {
        mListener = listener;
    }

    /**
     * Re Construct
     */
    public DialogType getType() {
        return mType;
    }

    public boolean isDying() {
        return mDying;
    }

    public void toProgress(String message) {
        mType = DialogType.PROGRESS;
        mMessage = message;
        draw();
    }

    public void toContext(String[] options, OnDialogListener listener) {
        mType = DialogType.CONTEXT;
        mOptions = options;
        mListener = listener;
    }

    public void toAlert(DialogType type, String title, String message, String placeholder, OnDialogListener listener) {
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
                mView.findViewById(R.id.context).setVisibility(View.GONE);
                mView.findViewById(R.id.alert).setVisibility(View.GONE);
                mView.findViewById(R.id.padding_layout).setVisibility(View.GONE);
                drawProgress();
                break;
            case CONTEXT:
                mView.findViewById(R.id.progress).setVisibility(View.GONE);
                mView.findViewById(R.id.context).setVisibility(View.VISIBLE);
                mView.findViewById(R.id.alert).setVisibility(View.GONE);
                mView.findViewById(R.id.padding_layout).setVisibility(View.GONE);
                drawContext();
                break;
            default:
                mView.findViewById(R.id.progress).setVisibility(View.GONE);
                mView.findViewById(R.id.context).setVisibility(View.GONE);
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

        mView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        if (mPlaceholder != null)
            mEdit.setHint(mPlaceholder);
    }

    private void drawProgress() {
        TextView message = (TextView) mView.findViewById(R.id.message_progress);
        message.setText(mMessage);
        mView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    private void drawContext() {
        if (mOptions == null || mOptions.length > 4 || mOptions.length == 0)
            getActivity().onBackPressed();

        Button context1 = (Button) mView.findViewById(R.id.context_1);
        Button context2 = (Button) mView.findViewById(R.id.context_2);
        Button context3 = (Button) mView.findViewById(R.id.context_3);
        Button context4 = (Button) mView.findViewById(R.id.context_4);

        switch (mOptions.length) {
            case 1:
                context2.setVisibility(View.GONE);
                mView.findViewById(R.id.context_divider_2).setVisibility(View.GONE);
            case 2:
                context3.setVisibility(View.GONE);
                mView.findViewById(R.id.context_divider_3).setVisibility(View.GONE);
            case 3:
                context4.setVisibility(View.GONE);
                mView.findViewById(R.id.context_divider_4).setVisibility(View.GONE);
                break;
        }

        switch (mOptions.length) {
            case 4:
                context4.setText(mOptions[3]);
                context4.setOnClickListener(this);
            case 3:
                context3.setText(mOptions[2]);
                context3.setOnClickListener(this);
            case 2:
                context2.setText(mOptions[1]);
                context2.setOnClickListener(this);
            case 1:
                context1.setText(mOptions[0]);
                context1.setOnClickListener(this);
                break;
        }

        mView.findViewById(R.id.cancel_context).setOnClickListener(this);
        mView.findViewById(R.id.total_layout).setOnClickListener(this);
        mView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    /**
     * ViewTreeObserver.onGlobalLayout
     */
    @Override
    public void onGlobalLayout() {

        if (getActivity() == null)
            return;

        mScreenHeight = ScreenHelper.getHeight(getActivity());

        switch (mType) {
            case EDIT:
            case CONFIRM:
            case OK:
                if (mView.getHeight() < mScreenHeight - 200) {
                    ((LinearLayout) mView.findViewById(R.id.total_layout)).setWeightSum(60);
                    mView.findViewById(R.id.padding_layout).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
                } else {
                    ((LinearLayout) mView.findViewById(R.id.total_layout)).setWeightSum(100);
                    mView.findViewById(R.id.padding_layout).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 40));
                }
                break;

            case PROGRESS:
                if (mView.getHeight() < mScreenHeight - 200) {
                    ((LinearLayout) mView.findViewById(R.id.total_layout)).setWeightSum(65);
                    mView.findViewById(R.id.padding_layout).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
                } else {
                    ((LinearLayout) mView.findViewById(R.id.total_layout)).setWeightSum(100);
                    mView.findViewById(R.id.padding_layout).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 35));
                }
                break;

            case CONTEXT:
            default:
                ((LinearLayout) mView.findViewById(R.id.total_layout)).setWeightSum(60);
                mView.findViewById(R.id.padding_layout).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
                break;
        }
    }

    /**
     * View.OnClickListener
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.context_1:
            case R.id.context_2:
            case R.id.context_3:
            case R.id.context_4:
                mDying = true;
                mConfirmed = true;
                getActivity().onBackPressed();
                if (mListener != null)
                    mListener.onConfirmed(((Button) v).getText().toString());
                break;
            case R.id.total_layout:
            case R.id.cancel_context:
                mDying = true;
                if (mType == DialogType.CONTEXT)
                    getActivity().onBackPressed();
                break;
            case R.id.confirm:
                mDying = true;
                mConfirmed = true;
                KeyboardHelper.hide(getActivity(), mEdit);
                getActivity().onBackPressed();
                if (mListener != null)
                    mListener.onConfirmed(mEdit.getText().toString());
                break;
            case R.id.cancel:
                mDying = true;
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
        CONFIRM, OK, EDIT, PROGRESS, CONTEXT
    }

    public interface OnDialogListener {
        void onConfirmed(String edit);

        void onCanceled();
    }
}
