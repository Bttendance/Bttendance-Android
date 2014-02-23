package com.bttendance.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bttendance.R;
import com.bttendance.adapter.BTListAdapter;
import com.bttendance.event.update.MySchoolsUpdateEvent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class JoinSchoolFragment extends BTFragment {

    BTListAdapter mAdapter;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_course, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new BTListAdapter(getActivity());
        mListView.setAdapter(mAdapter);
//        mListView.setFastScrollEnabled(true);
        return view;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        swapItems();
    }

    @Subscribe
    public void onMySchoolsUpdate(MySchoolsUpdateEvent event) {
        swapItems();
    }

    private void swapItems() {
        if (!this.isAdded())
            return;

        ArrayList<BTListAdapter.Item> items = new ArrayList<BTListAdapter.Item>();
//        SparseArray<SchoolJson> joinableSchools = BTTable.getSchools(BTTable.FILTER_JOINABLE_SCHOOL);
//        SparseArray<SchoolJson> mySchools = BTTable.getSchools(BTTable.FILTER_MY_SCHOOL);
//        for (int i = 0; i < joinableSchools.size(); i++) {
//            SchoolJson school = joinableSchools.valueAt(i);
//            boolean joined = mySchools.get(school.id) != null;
//            String title = school.name;
//            String message = school.website;
//            items.add(new BTListAdapter.Item(false, joined, title, message, school, -1));
//        }
        Collections.sort(items, new Comparator<BTListAdapter.Item>() {
            @Override
            public int compare(BTListAdapter.Item lhs, BTListAdapter.Item rhs) {
                return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
            }
        });
        mAdapter.setItems(items);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onServieConnected() {
        super.onServieConnected();
//        getBTService().joinableSchools(new Callback<SchoolJson[]>() {
//            @Override
//            public void success(SchoolJson[] schoolJsons, Response response) {
//                swapItems();
//            }
//
//            @Override
//            public void failure(RetrofitError retrofitError) {
//            }
//        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(getString(R.string.join_school));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
