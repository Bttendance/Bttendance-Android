package com.bttendance.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bttendance.event.fragment.ShowAddCourseEvent;
import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;
import com.bttendance.R;
import com.bttendance.adapter.CourseListAdapter;
import com.bttendance.event.attendance.AttdEndEvent;
import com.bttendance.event.attendance.AttdStartedEvent;
import com.bttendance.event.LoadingEvent;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.model.BTTable;
import com.bttendance.model.cursor.CourseCursor;
import com.bttendance.model.json.CourseJson;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class CourseListFragment extends BTFragment {

    ListView mListView;
    CourseListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);

        View header = new View(getActivity());
        header.setMinimumHeight((int) DipPixelHelper.getPixel(getActivity(), 7));
        mListView.addHeaderView(header);

        LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View footer = mInflater.inflate(R.layout.course_add, null);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BTEventBus.getInstance().post(new ShowAddCourseEvent());
            }
        });
        mListView.addFooterView(footer);

        mAdapter = new CourseListAdapter(getActivity(), null);
        mListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onServieConnected() {
        super.onServieConnected();
        getCourseList();
    }

    public void getCourseList() {
        if (getBTService() == null)
            return;

        BTEventBus.getInstance().post(new LoadingEvent(true));
        getBTService().courses(new Callback<CourseJson[]>() {
            @Override
            public void success(CourseJson[] courses, Response response) {
                mAdapter.swapCursor(new CourseCursor(BTTable.FILTER_MY_COURSE));
                BTEventBus.getInstance().post(new LoadingEvent(false));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                BTEventBus.getInstance().post(new LoadingEvent(false));
            }
        });
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        swapCursor();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Subscribe
    public void onAttdStarted(AttdStartedEvent event) {
        swapCursor();
    }

    @Subscribe
    public void onAttdEndEvent(AttdEndEvent event) {
        getCourseList();
    }

    private void swapCursor() {
        if (this.isAdded() && mAdapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.swapCursor(new CourseCursor(BTTable.FILTER_MY_COURSE));
                }
            });
        }
    }
}
