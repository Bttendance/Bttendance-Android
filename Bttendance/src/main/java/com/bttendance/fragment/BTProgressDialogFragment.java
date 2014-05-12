package com.bttendance.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bttendance.R;

/**
 * Created by TheFinestArtist on 2013. 12. 20..
 */
public class BTProgressDialogFragment extends BTFragment {

    String mMessage;

    public BTProgressDialogFragment(String message) {
        mMessage = message;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.bt_progress_dialog, container, false);
        TextView message = (TextView) view.findViewById(R.id.message);
        message.setText(mMessage);
        return view;
    }
}
