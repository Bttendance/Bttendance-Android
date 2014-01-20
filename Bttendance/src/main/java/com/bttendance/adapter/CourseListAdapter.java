package com.bttendance.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.BTEventBus;
import com.bttendance.R;
import com.bttendance.event.AttdInProgressEvent;
import com.bttendance.event.AttdStartEvent;
import com.bttendance.event.ShowAttdListEvent;
import com.bttendance.helper.DateHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.view.Bttendance;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class CourseListAdapter extends CursorAdapter implements View.OnClickListener {

    public CourseListAdapter(Context context, Cursor c) {
        super(context, c, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.course_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int course_id = cursor.getInt(0);
        CourseJson course = BTTable.CourseTable.get(course_id);

        Bttendance bttendance = (Bttendance) view.findViewById(R.id.bttendance);
        View selector = view.findViewById(R.id.item_selector);

        bttendance.setOnClickListener(this);
        selector.setOnClickListener(this);

        switch (BTPreference.getUserType(context)) {
            case PROFESSOR:
                bttendance.setClickable(true);
                selector.setVisibility(View.VISIBLE);
                break;
            case STUDENT:
            default:
                bttendance.setClickable(false);
                selector.setVisibility(View.GONE);
                break;
        }

        bttendance.setTag(R.id.course_id, course_id);
        selector.setTag(R.id.course_id, course_id);

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
                BTEventBus.getInstance().post(new ShowAttdListEvent(course.id));
                break;
            default:
                break;
        }
    }
}
