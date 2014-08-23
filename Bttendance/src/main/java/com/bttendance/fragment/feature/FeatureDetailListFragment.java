package com.bttendance.fragment.feature;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bttendance.R;
import com.bttendance.fragment.BTFragment;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.PostJson;

/**
 * Created by TheFinestArtist on 2014. 8. 22..
 */
public class FeatureDetailListFragment extends BTFragment {

    public Type mType;
    public PostJson mPost;

    public enum Type {Clicker, Attendance, Notice}

    FeatureDetailListFragment(Type type, int postID) {
        mType = type;
        mPost = BTTable.PostTable.get(postID);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feature_detail_list, container, false);
        return view;
    }
}
