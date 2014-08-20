package com.bttendance.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.activity.guide.GuideCourseAttendActivity;
import com.bttendance.activity.guide.GuideCourseCreateActivity;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.clicker.ClickerClickEvent;
import com.bttendance.event.dialog.ShowAlertDialogEvent;
import com.bttendance.fragment.BTDialogFragment;
import com.bttendance.fragment.SimpleWebViewFragment;
import com.bttendance.fragment.attendance.AttendanceDetailFragment;
import com.bttendance.fragment.clicker.ClickerDetailFragment;
import com.bttendance.helper.DateHelper;
import com.bttendance.helper.IntArrayHelper;
import com.bttendance.helper.PackagesHelper;
import com.bttendance.helper.ScreenHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.BTUrl;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.PostJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.view.Bttendance;
import com.bttendance.view.Clicker;
import com.squareup.otto.BTEventBus;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;

import static com.bttendance.helper.DipPixelHelper.getPixel;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class FeedAdapter extends CursorAdapter implements View.OnClickListener {

    Context mContext;
    int mCourseID;
    UserJson mUser;
    boolean mAuth;
    CourseJson mCourse;

    public enum Type {UPDATE, TIPS, CLICKER, ATTENDANCE, NOTICE}

    public FeedAdapter(Context context, Cursor c, int courseID) {
        super(context, c, android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mContext = context;
        mCourseID = courseID;
        mUser = BTPreference.getUser(context);
        mAuth = mUser.supervising(mCourseID);
        mCourse = BTTable.MyCourseTable.get(mCourseID);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.feed_item, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int id = cursor.getInt(0);
        if (id < 0) {
            switch (id) {
                case -1:
                    drawGuide(view, context, Type.TIPS);
                    break;
                case -2:
                    drawGuide(view, context, Type.CLICKER);
                    break;
                case -3:
                    drawGuide(view, context, Type.ATTENDANCE);
                    break;
                case -4:
                    drawGuide(view, context, Type.NOTICE);
                    break;
            }
        } else {
            PostJson post = BTTable.PostTable.get(id);
            if ("clicker".equals(post.type))
                drawClicker(view, context, post);
            else if ("attendance".equals(post.type))
                drawAttendance(view, context, post);
            else if ("notice".equals(post.type))
                drawNotice(view, context, post);
            else
                drawGuide(view, context, Type.UPDATE);
        }
    }

    private void drawClicker(View view, Context context, PostJson post) {

        UserJson user = BTPreference.getUser(mContext);
        long currentTime = System.currentTimeMillis();

        if (currentTime - DateHelper.getTime(post.createdAt) < Clicker.PROGRESS_DURATION
                && !IntArrayHelper.contains(user.supervising_courses, post.course.id)
                && !IntArrayHelper.contains(post.clicker.a_students, user.id)
                && !IntArrayHelper.contains(post.clicker.b_students, user.id)
                && !IntArrayHelper.contains(post.clicker.c_students, user.id)
                && !IntArrayHelper.contains(post.clicker.d_students, user.id)
                && !IntArrayHelper.contains(post.clicker.e_students, user.id)) {

            RelativeLayout normal = (RelativeLayout) view.findViewById(R.id.normal);
            RelativeLayout choice = (RelativeLayout) view.findViewById(R.id.choice);

            normal.setVisibility(View.GONE);
            choice.setVisibility(View.VISIBLE);

            View margin_a = view.findViewById(R.id.margin_a);
            Clicker choice_a = (Clicker) view.findViewById(R.id.choice_a);
            View margin_b = view.findViewById(R.id.margin_b);
            Clicker choice_b = (Clicker) view.findViewById(R.id.choice_b);
            View margin_c = view.findViewById(R.id.margin_c);
            Clicker choice_c = (Clicker) view.findViewById(R.id.choice_c);
            View margin_d = view.findViewById(R.id.margin_d);
            Clicker choice_d = (Clicker) view.findViewById(R.id.choice_d);
            View margin_e = view.findViewById(R.id.margin_e);
            Clicker choice_e = (Clicker) view.findViewById(R.id.choice_e);

            margin_a.setVisibility(View.VISIBLE);
            margin_b.setVisibility(View.VISIBLE);
            margin_c.setVisibility(View.VISIBLE);
            margin_d.setVisibility(View.VISIBLE);
            margin_e.setVisibility(View.VISIBLE);

            choice_a.setVisibility(View.VISIBLE);
            choice_b.setVisibility(View.VISIBLE);
            choice_c.setVisibility(View.VISIBLE);
            choice_d.setVisibility(View.VISIBLE);
            choice_e.setVisibility(View.VISIBLE);

            choice_a.setTag(R.id.post_id, post.id);
            choice_b.setTag(R.id.post_id, post.id);
            choice_c.setTag(R.id.post_id, post.id);
            choice_d.setTag(R.id.post_id, post.id);
            choice_e.setTag(R.id.post_id, post.id);

            choice_a.setOnClickListener(this);
            choice_b.setOnClickListener(this);
            choice_c.setOnClickListener(this);
            choice_d.setOnClickListener(this);
            choice_e.setOnClickListener(this);

            choice_a.setClickable(true);
            choice_b.setClickable(true);
            choice_c.setClickable(true);
            choice_d.setClickable(true);
            choice_e.setClickable(true);

            int progress = (int) (100.0f * (float) (Clicker.PROGRESS_DURATION - currentTime + DateHelper.getTime(post.createdAt)) / (float) Clicker.PROGRESS_DURATION);
            choice_a.startClicker(progress);
            choice_b.startClicker(progress);
            choice_c.startClicker(progress);
            choice_d.startClicker(progress);
            choice_e.startClicker(progress);

            switch (post.clicker.choice_count) {
                case 2:
                    margin_c.setVisibility(View.GONE);
                    choice_c.setVisibility(View.GONE);
                case 3:
                    margin_d.setVisibility(View.GONE);
                    choice_d.setVisibility(View.GONE);
                case 4:
                    margin_e.setVisibility(View.GONE);
                    choice_e.setVisibility(View.GONE);
                case 5:
                    break;
            }

            // Title, Message, Time
            TextView title = (TextView) view.findViewById(R.id.choice_title);
            TextView message = (TextView) view.findViewById(R.id.choice_message);
            TextView time = (TextView) view.findViewById(R.id.time);
            TextView guideMessage = (TextView) view.findViewById(R.id.guide_message);

            title.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
            time.setVisibility(View.VISIBLE);
            guideMessage.setVisibility(View.GONE);

            title.setTextColor(context.getResources().getColor(R.color.bttendance_silver));
            title.setText(post.course.name);
            message.setText(post.message);
            time.setText(DateHelper.getBTFormatString(post.createdAt));

            // Selector Events
            View selector = view.findViewById(R.id.item_selector);
            selector.setTag(R.id.post_id, null);
            selector.setTag(R.id.guide_type, null);
            selector.setClickable(false);
            selector.setVisibility(View.GONE);
        } else {
            RelativeLayout normal = (RelativeLayout) view.findViewById(R.id.normal);
            RelativeLayout choice = (RelativeLayout) view.findViewById(R.id.choice);

            normal.setVisibility(View.VISIBLE);
            choice.setVisibility(View.GONE);

            RelativeLayout clicker = (RelativeLayout) view.findViewById(R.id.clicker);
            Bttendance bttendance = ((Bttendance) view.findViewById(R.id.bttendance));
            View notice = view.findViewById(R.id.notice);
            View guide = view.findViewById(R.id.guide);

            clicker.setVisibility(View.VISIBLE);
            bttendance.setVisibility(View.GONE);
            notice.setVisibility(View.GONE);
            guide.setVisibility(View.GONE);

            DefaultRenderer renderer = post.clicker.getRenderer(context);
            CategorySeries series = post.clicker.getSeries();
            GraphicalView chartView = ChartFactory.getPieChartView(context, series, renderer);
            clicker.addView(chartView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            View ring = new View(context);
            ring.setBackgroundResource(R.drawable.ic_clicker_ring);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) getPixel(context, 52), (int) getPixel(context, 52));
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            clicker.addView(ring, params);

            // Title, Message, Time
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView message = (TextView) view.findViewById(R.id.message);
            TextView time = (TextView) view.findViewById(R.id.time);
            TextView guideMessage = (TextView) view.findViewById(R.id.guide_message);

            title.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
            time.setVisibility(View.VISIBLE);
            guideMessage.setVisibility(View.GONE);

            title.setTextColor(context.getResources().getColor(R.color.bttendance_silver));
            title.setText(post.course.name);
            message.setText(post.message + "\n" + post.clicker.getDetail());
            time.setText(DateHelper.getBTFormatString(post.createdAt));

            // Selector Events
            View selector = view.findViewById(R.id.item_selector);
            selector.setTag(R.id.post_id, post.id);
            selector.setTag(R.id.guide_type, null);
            selector.setOnClickListener(this);
            selector.setClickable(true);
            selector.setVisibility(View.VISIBLE);
        }
    }

    private void drawAttendance(View view, Context context, PostJson post) {
        UserJson user = BTPreference.getUser(context);

        RelativeLayout normal = (RelativeLayout) view.findViewById(R.id.normal);
        RelativeLayout choice = (RelativeLayout) view.findViewById(R.id.choice);

        normal.setVisibility(View.VISIBLE);
        choice.setVisibility(View.GONE);

        RelativeLayout clicker = (RelativeLayout) view.findViewById(R.id.clicker);
        Bttendance bttendance = ((Bttendance) view.findViewById(R.id.bttendance));
        View notice = view.findViewById(R.id.notice);
        View guide = view.findViewById(R.id.guide);

        clicker.setVisibility(View.GONE);
        bttendance.setVisibility(View.VISIBLE);
        notice.setVisibility(View.GONE);
        guide.setVisibility(View.GONE);

        long currentTime = DateHelper.getCurrentGMTTimeMillis();

        boolean mTime = currentTime - DateHelper.getTime(post.createdAt) < Bttendance.PROGRESS_DURATION;
        boolean included = IntArrayHelper.contains(post.attendance.checked_students, user.id);

        if (IntArrayHelper.contains(user.supervising_courses, post.course.id)) {
            if (mTime) {
                long time = currentTime - DateHelper.getTime(post.createdAt);
                int progress = (int) (100.0f * (float) (Bttendance.PROGRESS_DURATION - time) / (float) Bttendance.PROGRESS_DURATION);
                bttendance.setBttendance(Bttendance.STATE.CHECKING, progress);
            } else {
                int grade = 0;
//                if (post.grade != null)
//                    grade = Integer.parseInt(post.grade);
                bttendance.setBttendance(Bttendance.STATE.GRADE, grade);
            }
        } else {
            if (mTime && !included) {
                long time = currentTime - DateHelper.getTime(post.createdAt);
                int progress = (int) (100.0f * (float) (Bttendance.PROGRESS_DURATION - time) / (float) Bttendance.PROGRESS_DURATION);
                bttendance.setBttendance(Bttendance.STATE.CHECKING, progress);
            } else if (mTime || included) {
                bttendance.setBttendance(Bttendance.STATE.CHECKED, 0);
            } else {
                bttendance.setBttendance(Bttendance.STATE.FAIL, 0);
            }
        }

        // Title, Message, Time
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView message = (TextView) view.findViewById(R.id.message);
        TextView time = (TextView) view.findViewById(R.id.time);
        TextView guideMessage = (TextView) view.findViewById(R.id.guide_message);

        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        time.setVisibility(View.VISIBLE);
        guideMessage.setVisibility(View.GONE);

        title.setTextColor(context.getResources().getColor(R.color.bttendance_silver));
        title.setText(post.course.name);
        message.setText(post.message);
        time.setText(DateHelper.getBTFormatString(post.createdAt));

        // Attendance Icon Param
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(12.0f);
        paint.getTextBounds(post.message, 0, post.message.length(), bounds);
        float width = getPixel(context, (float) Math.ceil(bounds.width()));
        float textViewWidth = ScreenHelper.getWidth(context) - getPixel(context, 116);

        int pix_18 = (int) getPixel(context, 18);
        int pix_26 = (int) getPixel(context, 26);
        int pix_52 = (int) getPixel(context, 52);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(pix_52, pix_52);
        if (width > textViewWidth)
            params.setMargins(pix_18, pix_26, 0, 0);
        else
            params.setMargins(pix_18, pix_18, 0, 0);
        bttendance.setLayoutParams(params);

        // Selector Events
        View selector = view.findViewById(R.id.item_selector);
        selector.setTag(R.id.post_id, post.id);
        selector.setTag(R.id.guide_type, null);
        selector.setOnClickListener(this);
        selector.setClickable(true);
        selector.setVisibility(View.VISIBLE);
    }

    private void drawNotice(View view, Context context, PostJson post) {

        RelativeLayout normal = (RelativeLayout) view.findViewById(R.id.normal);
        RelativeLayout choice = (RelativeLayout) view.findViewById(R.id.choice);

        normal.setVisibility(View.VISIBLE);
        choice.setVisibility(View.GONE);

        RelativeLayout clicker = (RelativeLayout) view.findViewById(R.id.clicker);
        Bttendance bttendance = ((Bttendance) view.findViewById(R.id.bttendance));
        View notice = view.findViewById(R.id.notice);
        View guide = view.findViewById(R.id.guide);

        clicker.setVisibility(View.GONE);
        bttendance.setVisibility(View.GONE);
        notice.setVisibility(View.VISIBLE);
        guide.setVisibility(View.GONE);

        // Title, Message, Time
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView message = (TextView) view.findViewById(R.id.message);
        TextView time = (TextView) view.findViewById(R.id.time);
        TextView guideMessage = (TextView) view.findViewById(R.id.guide_message);

        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        time.setVisibility(View.VISIBLE);
        guideMessage.setVisibility(View.GONE);

        if (mAuth) {
            int studentCount = 0;
            int seenStudent = 0;
            if (mCourse != null)
                studentCount = mCourse.students_count;
            if (post.notice != null && post.notice.seen_students != null)
                seenStudent = post.notice.seen_students.length;
            title.setText(String.format(context.getString(R.string.notice_d_d_read), seenStudent, studentCount));
            title.setTextColor(context.getResources().getColor(R.color.bttendance_silver));
        } else if (post.notice.seen(mUser.id)) {
            title.setText(context.getString(R.string.notice));
            title.setTextColor(context.getResources().getColor(R.color.bttendance_silver));
        } else {
            title.setText(context.getString(R.string.unread_notice));
            title.setTextColor(context.getResources().getColor(R.color.bttendance_red));
        }

        message.setText(post.message);
        time.setText(DateHelper.getBTFormatString(post.createdAt));

        // Notice Icon Param
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(12.0f);
        paint.getTextBounds(post.message, 0, post.message.length(), bounds);
        float width = getPixel(context, (float) Math.ceil(bounds.width()));
        float textViewWidth = ScreenHelper.getWidth(context) - getPixel(context, 116);

        int pix_18 = (int) getPixel(context, 18);
        int pix_26 = (int) getPixel(context, 26);
        int pix_52 = (int) getPixel(context, 52);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(pix_52, pix_52);
        if (width > textViewWidth)
            params.setMargins(pix_18, pix_26, 0, 0);
        else
            params.setMargins(pix_18, pix_18, 0, 0);
        notice.setLayoutParams(params);

        // Selector Events
        View selector = view.findViewById(R.id.item_selector);
        selector.setTag(R.id.post_id, post.id);
        selector.setTag(R.id.guide_type, null);
        selector.setOnClickListener(this);
        selector.setClickable(true);
        selector.setVisibility(View.VISIBLE);
    }

    private void drawGuide(View view, Context context, Type type) {

        RelativeLayout normal = (RelativeLayout) view.findViewById(R.id.normal);
        RelativeLayout choice = (RelativeLayout) view.findViewById(R.id.choice);

        normal.setVisibility(View.VISIBLE);
        choice.setVisibility(View.GONE);

        RelativeLayout clicker = (RelativeLayout) view.findViewById(R.id.clicker);
        Bttendance bttendance = ((Bttendance) view.findViewById(R.id.bttendance));
        View notice = view.findViewById(R.id.notice);
        View guide = view.findViewById(R.id.guide);

        clicker.setVisibility(View.GONE);
        bttendance.setVisibility(View.GONE);
        notice.setVisibility(View.GONE);
        guide.setVisibility(View.VISIBLE);

        // Title, Message, Time
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView message = (TextView) view.findViewById(R.id.message);
        TextView time = (TextView) view.findViewById(R.id.time);
        TextView guideMessage = (TextView) view.findViewById(R.id.guide_message);

        title.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
        time.setVisibility(View.GONE);
        guideMessage.setVisibility(View.VISIBLE);

        switch (type) {
            case UPDATE:
                guideMessage.setText(context.getString(R.string.post_guide_update));
                guide.setBackgroundResource(R.drawable.ic_bttendance_big);
                break;
            case TIPS:
                guideMessage.setText(context.getString(R.string.post_guide_tips));
                guide.setBackgroundResource(R.drawable.ic_bttendance_big);
                break;
            case CLICKER:
                guideMessage.setText(context.getString(R.string.post_guide_clicker));
                guide.setBackgroundResource(R.drawable.ic_clicker_big);
                break;
            case ATTENDANCE:
                guideMessage.setText(context.getString(R.string.post_guide_attendance));
                guide.setBackgroundResource(R.drawable.ic_attendance_big);
                break;
            case NOTICE:
                guideMessage.setText(context.getString(R.string.post_guide_notice));
                guide.setBackgroundResource(R.drawable.ic_notice_big);
                break;
        }

        // Selector Events
        View selector = view.findViewById(R.id.item_selector);
        selector.setTag(R.id.post_id, null);
        selector.setTag(R.id.guide_type, type);
        selector.setOnClickListener(this);
        selector.setClickable(true);
        selector.setVisibility(View.VISIBLE);
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
                if (v.getTag(R.id.post_id) == null) {
                    Type type = (Type) v.getTag(R.id.guide_type);
                    switch (type) {
                        case UPDATE:
                            String title = mContext.getString(R.string.update_app_title);
                            String message = mContext.getString(R.string.update_app_message);
                            BTEventBus.getInstance().post(new ShowAlertDialogEvent(BTDialogFragment.DialogType.CONFIRM, title, message, new BTDialogFragment.OnDialogListener() {
                                @Override
                                public void onConfirmed(String edit) {
                                    PackagesHelper.updateApp(mContext);
                                }

                                @Override
                                public void onCanceled() {
                                }
                            }));
                            break;
                        case TIPS:
                            if (mAuth) {
                                String code = "";
                                if (mCourse != null)
                                    code = mCourse.code;
                                Intent intent = new Intent(mContext, GuideCourseCreateActivity.class);
                                intent.putExtra(GuideCourseCreateActivity.EXTRA_CLASS_CODE, code);
                                mContext.startActivity(intent);
                                if (mContext instanceof BTActivity)
                                    ((BTActivity) mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            } else {
                                Intent intent = new Intent(mContext, GuideCourseAttendActivity.class);
                                mContext.startActivity(intent);
                                if (mContext instanceof BTActivity)
                                    ((BTActivity) mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                            break;
                        case CLICKER: {
                            SimpleWebViewFragment fragment = new SimpleWebViewFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(SimpleWebViewFragment.EXTRA_URL, BTUrl.getTutorialClicker(mContext));
                            fragment.setArguments(bundle);
                            BTEventBus.getInstance().post(new AddFragmentEvent(fragment));
                            break;
                        }
                        case ATTENDANCE: {
                            SimpleWebViewFragment fragment = new SimpleWebViewFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(SimpleWebViewFragment.EXTRA_URL, BTUrl.getTutorialAttendance(mContext));
                            fragment.setArguments(bundle);
                            BTEventBus.getInstance().post(new AddFragmentEvent(fragment));
                            break;
                        }
                        case NOTICE: {
                            SimpleWebViewFragment fragment = new SimpleWebViewFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(SimpleWebViewFragment.EXTRA_URL, BTUrl.getTutorialNotice(mContext));
                            fragment.setArguments(bundle);
                            BTEventBus.getInstance().post(new AddFragmentEvent(fragment));
                            break;
                        }
                    }
                } else {
                    PostJson post = BTTable.PostTable.get((Integer) v.getTag(R.id.post_id));
                    if (post == null)
                        return;

                    if ("attendance".equals(post.type)) {
                        AttendanceDetailFragment frag = new AttendanceDetailFragment(post.id);
                        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
                    } else if ("clicker".equals(post.type)) {
                        ClickerDetailFragment frag = new ClickerDetailFragment(post.id);
                        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
                    } else if ("notice".equals(post.type)) {
                    } else {
                    }
                }
                break;
            case R.id.choice_a:
                BTEventBus.getInstance().post(new ClickerClickEvent((Integer) v.getTag(R.id.post_id), 1));
                break;
            case R.id.choice_b:
                BTEventBus.getInstance().post(new ClickerClickEvent((Integer) v.getTag(R.id.post_id), 2));
                break;
            case R.id.choice_c:
                BTEventBus.getInstance().post(new ClickerClickEvent((Integer) v.getTag(R.id.post_id), 3));
                break;
            case R.id.choice_d:
                BTEventBus.getInstance().post(new ClickerClickEvent((Integer) v.getTag(R.id.post_id), 4));
                break;
            case R.id.choice_e:
                BTEventBus.getInstance().post(new ClickerClickEvent((Integer) v.getTag(R.id.post_id), 5));
                break;
            default:
                break;
        }
    }
}
