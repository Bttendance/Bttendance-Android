package com.utopia.bttendance.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.utopia.bttendance.R;
import com.utopia.bttendance.model.BTPreference;
import com.utopia.bttendance.model.json.PostJson;
import com.utopia.bttendance.model.json.UserJson;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class FeedFragment extends BTFragment {

    ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, null);
        mListView = (ListView) view.findViewById(android.R.id.list);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getFeed();
    }

    public void getFeed() {
        if (getBTService() == null)
            return;

        UserJson user = BTPreference.getUser(getActivity());
        getBTService().feed(user.username, user.password, 0, new Callback<PostJson[]>() {
            @Override
            public void success(PostJson[] posts, Response response) {

            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }
}
