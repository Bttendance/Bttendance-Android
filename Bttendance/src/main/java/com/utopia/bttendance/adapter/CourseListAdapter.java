package com.utopia.bttendance.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.utopia.bttendance.BTDebug;
import com.utopia.bttendance.R;
import com.utopia.bttendance.model.BTPreference;
import com.utopia.bttendance.model.BTTable;
import com.utopia.bttendance.model.json.CourseJson;
import com.utopia.bttendance.view.Bttendance;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class CourseListAdapter extends CursorAdapter implements View.OnClickListener{

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
        View bttendance = view.findViewById(R.id.bttendance);
        switch (BTPreference.getUserType(context)) {
            case PROFESSOR:
                bttendance.setClickable(true);
                bttendance.setOnClickListener(this);
                break;
            case STUDENT:
            default:
                bttendance.setClickable(false);
                break;
        }
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView message = (TextView) view.findViewById(R.id.message);
        int id = cursor.getInt(0);
        CourseJson course = BTTable.CourseTable.get(id);
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
                
                break;
            default:
                break;
        }
    }
}
