package com.bttendance.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bttendance.BTDebug;
import com.bttendance.R;

/**
 * Created by TheFinestArtist on 2013. 12. 20..
 */
public class BTDialogFragment extends BTFragment implements View.OnClickListener {

    DialogType mType;
    OnConfirmListener mConfrimListener;
    String mTitle;
    String mMessage;
    boolean mConfirmed = false;

    public BTDialogFragment(DialogType type, String title, String message) {
        mType = type;
        mTitle = title;
        mMessage = message;
    }

    public void setOnConfirmListener(OnConfirmListener confrimListener) {
        mConfrimListener = confrimListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BTDebug.LogError("onCreateView : " + getActivity().getSupportFragmentManager().getBackStackEntryCount());
        View view = inflater.inflate(R.layout.alert_dialog, container, false);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        Button confirm = (Button) view.findViewById(R.id.confirm);
        View divider = view.findViewById(R.id.divider);
        switch (mType) {
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

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                mConfirmed = true;
                if (mConfrimListener != null)
                    mConfrimListener.onConfirmed();
            case R.id.cancel:
//                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
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
        CONFIRM, OK
    }

    public interface OnConfirmListener {
        void onConfirmed();

        void onCanceled();
    }
}
