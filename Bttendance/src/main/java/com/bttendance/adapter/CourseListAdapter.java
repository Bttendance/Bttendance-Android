package com.bttendance.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.event.attendance.AttdInProgressEvent;
import com.bttendance.event.attendance.AttdStartEvent;
import com.bttendance.event.fragment.ShowCourseDetailEvent;
import com.bttendance.event.fragment.ShowSchoolChooseEvent;
import com.bttendance.helper.DateHelper;
import com.bttendance.model.BTTable;
import com.bttendance.model.cursor.MyCourseCursor;
import com.bttendance.model.json.CourseJson;
import com.bttendance.view.Bttendance;
import com.squareup.otto.BTEventBus;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class CourseListAdapter extends CursorAdapter implements View.OnClickListener {

    private static final int VIEW_TYPE_ADD = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    public CourseListAdapter(Context context, Cursor c) {
        super(context, c, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (getItemViewType(cursor.getPosition())) {
            case VIEW_TYPE_ADD:
                return inflater.inflate(R.layout.course_add, null);
            case VIEW_TYPE_ITEM:
            default:
                return inflater.inflate(R.layout.course_item, null);
        }
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        switch (getItemViewType(getCursor().getPosition())) {
            case VIEW_TYPE_ADD:
                view.findViewById(R.id.course_add_btn).setTag(cursor.getInt(0));
                view.findViewById(R.id.course_add_btn).setOnClickListener(this);
                view.findViewById(R.id.course_add_btn).setClickable(true);
                break;
            case VIEW_TYPE_ITEM:
            default:
                CourseJson course = BTTable.CourseTable.get(cursor.getInt(0));

                Bttendance bttendance = (Bttendance) view.findViewById(R.id.bttendance);
                View selector = view.findViewById(R.id.item_selector);

                bttendance.setOnClickListener(this);
                selector.setOnClickListener(this);

                bttendance.setClickable(true);
                selector.setVisibility(View.VISIBLE);

                bttendance.setTag(R.id.course_id, course.id);
                selector.setTag(R.id.course_id, course.id);

                long currentTime = DateHelper.getCurrentGMTTimeMillis();
                if (course.attdCheckedAt != null && currentTime - DateHelper.getTime(course.attdCheckedAt) < Bttendance.PROGRESS_DURATION) {
                    long time = currentTime - DateHelper.getTime(course.attdCheckedAt);
                    int progress = (int) ((float) 100 * ((float) Bttendance.PROGRESS_DURATION - (float) time) / (float) Bttendance.PROGRESS_DURATION);
                    bttendance.setBttendance(Bttendance.STATE.CHECKING, progress);
                    bttendance.setTag(R.id.checking, true);
                } else {
                    bttendance.setBttendance(Bttendance.STATE.CHECKED, 0);
                    bttendance.setTag(R.id.checking, false);
                }

                TextView title = (TextView) view.findViewById(R.id.title);
                TextView message = (TextView) view.findViewById(R.id.message);
                title.setText(course.number + " " + course.name);
                message.setText(context.getString(R.string.prof_) + course.professor_name + "\n" + course.school_name);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch ((int) getItemId(position)) {
            case MyCourseCursor.ADD_BUTTON_CREATE_COURSE:
            case MyCourseCursor.ADD_BUTTON_ATTEND_COURSE:
                return VIEW_TYPE_ADD;
            default:
                return VIEW_TYPE_ITEM;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        Cursor cursor = (Cursor) getItem(position);
        if (cursor == null)
            return -1;

        int id = cursor.getInt(0);
        return id;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bttendance:
                boolean checking = (Boolean) v.getTag(R.id.checking);
                if (checking)
                    BTEventBus.getInstance().post(new AttdInProgressEvent());
                else
                    BTEventBus.getInstance().post(new AttdStartEvent((Integer) v.getTag(R.id.course_id)));
                break;
            case R.id.item_selector:
                int course_id = (Integer) v.getTag(R.id.course_id);
                CourseJson course = BTTable.CourseTable.get(course_id);
                BTEventBus.getInstance().post(new ShowCourseDetailEvent(course.id));
                break;
            case R.id.course_add_btn:
                BTEventBus.getInstance().post(new ShowSchoolChooseEvent((Integer) v.getTag()));
                break;
            default:
                break;
        }
    }
}
