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
import com.bttendance.fragment.PostAttendanceFragment;
import com.bttendance.helper.DateHelper;
import com.bttendance.helper.IntArrayHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.view.Bttendance;
import com.squareup.otto.BTEventBus;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class FeedAdapter extends CursorAdapter implements View.OnClickListener {

    public FeedAdapter(Context context, Cursor c) {
        super(context, c, android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.feed_item, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int id = cursor.getInt(0);
        PostJson post = BTTable.PostTable.get(id);
        UserJson user = BTPreference.getUser(mContext);

        Bttendance bttendance = ((Bttendance) view.findViewById(R.id.bttendance));

        // Notice Icon
        if ("attendance".equals(post.type)) {
            bttendance.setTag(R.id.post_id, post.id);
            bttendance.setVisibility(View.VISIBLE);
            view.findViewById(R.id.notice).setVisibility(View.GONE);
        } else {
            bttendance.setVisibility(View.GONE);
            view.findViewById(R.id.notice).setVisibility(View.VISIBLE);
        }

        long currentTime = DateHelper.getCurrentGMTTimeMillis();

        boolean mTime = currentTime - DateHelper.getTime(post.createdAt) < Bttendance.PROGRESS_DURATION;
//        boolean included = IntArrayHelper.contains(post.attendance.checked_students, BTPreference.getUserId(context));
//
//        if (IntArrayHelper.contains(user.supervising_courses, post.course.id)) {
//            if (mTime) {
//                long time = currentTime - DateHelper.getTime(post.createdAt);
//                int progress = (int) ((float) 100 * ((float) Bttendance.PROGRESS_DURATION - (float) time) / (float) Bttendance.PROGRESS_DURATION);
//                bttendance.setBttendance(Bttendance.STATE.CHECKING, progress);
//            } else {
//                int grade = 0;
//                if (post.grade != null)
//                    grade = Integer.parseInt(post.grade);
//                bttendance.setBttendance(Bttendance.STATE.GRADE, grade);
//            }
//        } else {
//            if (mTime && !included) {
//                long time = currentTime - DateHelper.getTime(post.createdAt);
//                int progress = (int) ((float) 100 * ((float) Bttendance.PROGRESS_DURATION - (float) time) / (float) Bttendance.PROGRESS_DURATION);
//                bttendance.setBttendance(Bttendance.STATE.CHECKING, progress);
//            } else if (mTime || included) {
//                bttendance.setBttendance(Bttendance.STATE.CHECKED, 0);
//            } else {
//                bttendance.setBttendance(Bttendance.STATE.FAIL, 0);
//            }
//        }

        TextView title = (TextView) view.findViewById(R.id.title);
        TextView message = (TextView) view.findViewById(R.id.message);
        title.setText(post.course.name);
        message.setText(post.message + "\n" + DateHelper.getBTFormatString(post.createdAt));

        // Selector Events
        View selector = view.findViewById(R.id.item_selector);
        selector.setTag(R.id.post_id, post.id);
        selector.setOnClickListener(this);
        selector.setClickable(true);
        if (IntArrayHelper.contains(user.supervising_courses, post.course.id)
                && "attendance".equals(post.type))
            selector.setVisibility(View.VISIBLE);
        else
            selector.setVisibility(View.GONE);
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
            case R.id.item_selector:
                PostAttendanceFragment frag = new PostAttendanceFragment((Integer) v.getTag(R.id.post_id));
                BTEventBus.getInstance().post(new AddFragmentEvent(frag));
                break;
            default:
                break;
        }
    }
}
