package com.utopia.bttendance.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;
import com.utopia.bttendance.R;
import com.utopia.bttendance.adapter.CourseListAdapter;
import com.utopia.bttendance.event.AddCourseEvent;
import com.utopia.bttendance.event.AttdCheckedEvent;
import com.utopia.bttendance.event.AttdEndEvent;
import com.utopia.bttendance.event.AttdStartedEvent;
import com.utopia.bttendance.event.LoadingEvent;
import com.utopia.bttendance.helper.DipPixelHelper;
import com.utopia.bttendance.model.BTTable;
import com.utopia.bttendance.model.cursor.CourseCursor;
import com.utopia.bttendance.model.json.CourseJson;

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
                BTEventBus.getInstance().post(new AddCourseEvent());
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
        if (this.isAdded())
            mAdapter.swapCursor(new CourseCursor(BTTable.FILTER_MY_COURSE));
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
        getCourseList();
    }

    @Subscribe
    public void onAttdChecked(AttdCheckedEvent event) {
        getCourseList();
    }

    @Subscribe
    public void onAttdEndEvent(AttdEndEvent event) {
        getCourseList();
    }
}
