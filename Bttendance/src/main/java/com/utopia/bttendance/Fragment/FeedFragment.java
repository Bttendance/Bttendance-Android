package com.utopia.bttendance.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;
import com.utopia.bttendance.R;
import com.utopia.bttendance.adapter.FeedAdapter;
import com.utopia.bttendance.event.AttdCheckedEvent;
import com.utopia.bttendance.event.AttdEndEvent;
import com.utopia.bttendance.event.AttdStartedEvent;
import com.utopia.bttendance.event.LoadingEvent;
import com.utopia.bttendance.helper.DipPixelHelper;
import com.utopia.bttendance.model.BTTable;
import com.utopia.bttendance.model.cursor.PostCursor;
import com.utopia.bttendance.model.json.PostJson;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class FeedFragment extends BTFragment {

    ListView mListView;
    FeedAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        View padding = new View(getActivity());
        padding.setMinimumHeight((int) DipPixelHelper.getPixel(getActivity(), 7));
        mListView.addHeaderView(padding);
        mListView.addFooterView(padding);
        mAdapter = new FeedAdapter(getActivity(), null);
        mListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onServieConnected() {
        super.onServieConnected();
        getFeed();
    }

    public void getFeed() {
        if (getBTService() == null)
            return;

        BTEventBus.getInstance().post(new LoadingEvent(true));
        getBTService().feed(0, new Callback<PostJson[]>() {
            @Override
            public void success(PostJson[] posts, Response response) {
                mAdapter.swapCursor(new PostCursor(BTTable.FILTER_TOTAL_POST));
                BTEventBus.getInstance().post(new LoadingEvent(false));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                BTEventBus.getInstance().post(new LoadingEvent(false));
            }
        });
    }

    @Subscribe
    public void onAttdStarted(AttdStartedEvent event) {
        getFeed();
    }

    @Subscribe
    public void onAttdChecked(AttdCheckedEvent event) {
        getFeed();
    }

    @Subscribe
    public void onAttdEnd(AttdEndEvent event) {
        getFeed();
    }
}
