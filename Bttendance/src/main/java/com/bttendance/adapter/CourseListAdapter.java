package com.bttendance.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.attendance.AttdStartEvent;
import com.bttendance.fragment.CourseDetailFragment;
import com.bttendance.fragment.CreateNoticeFragment;
import com.bttendance.fragment.StartClickerFragment;
import com.bttendance.helper.DateHelper;
import com.bttendance.helper.IntArrayHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.json.CourseJsonHelper;
import com.bttendance.model.json.UserJson;
import com.bttendance.view.Bttendance;
import com.squareup.otto.BTEventBus;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class CourseListAdapter extends CursorAdapter implements View.OnClickListener {

    private Context mContext;
    private UserJson mUser;

    public CourseListAdapter(Context context, Cursor c) {
        super(context, c, false);
        mContext = context;
        mUser = BTPreference.getUser(mContext);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.course_item, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mUser = BTPreference.getUser(mContext);
        if (mUser == null)
            return;

        CourseJsonHelper courseHelper = new CourseJsonHelper(mUser, cursor.getInt(0));

        Bttendance bttendance = (Bttendance) view.findViewById(R.id.bttendance);
        View selector = view.findViewById(R.id.item_selector);

        selector.setOnClickListener(this);
        selector.setVisibility(View.VISIBLE);
        selector.setTag(R.id.course_id, courseHelper.getID());
        view.findViewById(R.id.clicker_bt).setTag(R.id.course_id, courseHelper.getID());
        view.findViewById(R.id.attendance_bt).setTag(R.id.course_id, courseHelper.getID());
        view.findViewById(R.id.notice_bt).setTag(R.id.course_id, courseHelper.getID());

        long currentTime = DateHelper.getCurrentGMTTimeMillis();

        boolean mTime = courseHelper.getAttdCheckedAt() != null && currentTime - DateHelper.getTime(courseHelper.getAttdCheckedAt()) < Bttendance.PROGRESS_DURATION;
        if (mTime) {
            long time = currentTime - DateHelper.getTime(courseHelper.getAttdCheckedAt());
            int progress = (int) ((float) 100 * ((float) Bttendance.PROGRESS_DURATION - (float) time) / (float) Bttendance.PROGRESS_DURATION);
            bttendance.setBttendance(Bttendance.STATE.CHECKING, progress);
        } else {
            int grade = Integer.parseInt(courseHelper.getGrade());
            bttendance.setBttendance(Bttendance.STATE.GRADE, grade);
        }

        boolean supved = IntArrayHelper.contains(mUser.supervising_courses, courseHelper.getID());
        if (supved) {
            view.findViewById(R.id.manager_layout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.clicker_bt).setOnClickListener(this);
            view.findViewById(R.id.attendance_bt).setOnClickListener(this);
            view.findViewById(R.id.notice_bt).setOnClickListener(this);
        } else {
            view.findViewById(R.id.manager_layout).setVisibility(View.GONE);
            view.findViewById(R.id.clicker_bt).setOnClickListener(null);
            view.findViewById(R.id.attendance_bt).setOnClickListener(null);
            view.findViewById(R.id.notice_bt).setOnClickListener(null);
        }

        TextView title = (TextView) view.findViewById(R.id.title);
        TextView message = (TextView) view.findViewById(R.id.message);
        title.setText(courseHelper.getName());
        message.setText(context.getString(R.string.prof_) + courseHelper.getProfessorName() + "\n" + courseHelper.getSchoolName());
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
            case R.id.item_selector: {
                int course_id = (Integer) v.getTag(R.id.course_id);
                CourseJsonHelper courseHelper = new CourseJsonHelper(mUser, course_id);
                CourseDetailFragment frag = new CourseDetailFragment(courseHelper.getID());
                BTEventBus.getInstance().post(new AddFragmentEvent(frag));
            }
            break;
            case R.id.clicker_bt: {
                int course_id = (Integer) v.getTag(R.id.course_id);
                StartClickerFragment frag = new StartClickerFragment(course_id);
                BTEventBus.getInstance().post(new AddFragmentEvent(frag));
            }
            break;
            case R.id.attendance_bt: {
                int course_id = (Integer) v.getTag(R.id.course_id);
                BTEventBus.getInstance().post(new AttdStartEvent(course_id));
            }
            break;
            case R.id.notice_bt: {
                int course_id = (Integer) v.getTag(R.id.course_id);
                CreateNoticeFragment frag = new CreateNoticeFragment(course_id);
                BTEventBus.getInstance().post(new AddFragmentEvent(frag));
            }
            break;
            default:
                break;
        }
    }
}
