package com.utopia.bttendance.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.squareup.otto.BTEventBus;
import com.utopia.bttendance.R;
import com.utopia.bttendance.adapter.CourseListAdapter;
import com.utopia.bttendance.event.AddCourseEvent;
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
        View view = inflater.inflate(R.layout.fragment_course_list, null);
        mListView = (ListView) view.findViewById(android.R.id.list);

        View header = new View(getActivity());
        header.setMinimumHeight((int) DipPixelHelper.getPixel(getActivity(), 7));
        mListView.addHeaderView(header);

        LayoutInflater  mInflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
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

        getBTService().courses(new Callback<CourseJson[]>() {
            @Override
            public void success(CourseJson[] courses, Response response) {
                mAdapter.swapCursor(new CourseCursor(BTTable.FILTER_MY_COURSE));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }
}
