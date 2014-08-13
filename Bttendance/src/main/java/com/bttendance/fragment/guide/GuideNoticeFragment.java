package com.bttendance.fragment.guide;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bttendance.R;
import com.bttendance.fragment.BTFragment;

/**
 * Created by TheFinestArtist on 2014. 8. 13..
 */
public class GuideNoticeFragment extends BTFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guid_notice, container, false);
        if (Build.VERSION.SDK_INT < 11)
            ((ImageView) view.findViewById(R.id.guide_notice)).setImageResource(R.drawable.notice_bg);
        return view;
    }
}
