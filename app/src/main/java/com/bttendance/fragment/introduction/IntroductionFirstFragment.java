package com.bttendance.fragment.introduction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bttendance.R;
import com.bttendance.fragment.BTFragment;

/**
 * Created by TheFinestArtist on 2014. 8. 13..
 */
public class IntroductionFirstFragment extends BTFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_introduction_first, container, false);
        return view;
    }
}
