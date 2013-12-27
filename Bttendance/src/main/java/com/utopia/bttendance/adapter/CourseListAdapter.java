package com.utopia.bttendance.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.BTEventBus;
import com.utopia.bttendance.R;
import com.utopia.bttendance.event.AttdStartEvent;
import com.utopia.bttendance.helper.DateHelper;
import com.utopia.bttendance.model.BTPreference;
import com.utopia.bttendance.model.BTTable;
import com.utopia.bttendance.model.json.CourseJson;
import com.utopia.bttendance.view.Bttendance;

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
        Bttendance bttendance = (Bttendance) view.findViewById(R.id.bttendance);
        CourseJson course = BTTable.CourseTable.get(course_id);
        switch (BTPreference.getUserType(context)) {
            case PROFESSOR:
                bttendance.setTag(R.id.course_id, course_id);
                bttendance.setClickable(true);
                bttendance.setOnClickListener(this);
                break;
            case STUDENT:
            default:
                bttendance.setTag(R.id.course_id, course_id);
                bttendance.setClickable(false);
                break;
        }

        long currentTime = DateHelper.getCurrentGMTTimeMillis();
        if (course.attdCheckedAt != null && currentTime - DateHelper.getTime(course.attdCheckedAt) < Bttendance.PROGRESS_DURATION) {
            long time = currentTime - DateHelper.getTime(course.attdCheckedAt);
            int progress = (int) (100 * (Bttendance.PROGRESS_DURATION - time) / Bttendance.PROGRESS_DURATION);
            bttendance.setBttendance(Bttendance.STATE.CHECKING, progress);
        } else {
            bttendance.setBttendance(Bttendance.STATE.CHECKED, 0);
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
                BTEventBus.getInstance().post(new AttdStartEvent((Integer) v.getTag(R.id.course_id)));
                break;
            default:
                break;
        }
    }
}
