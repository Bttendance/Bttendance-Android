package com.bttendance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.CourseJsonSimple;
import com.bttendance.model.json.UserJson;

public class SideListAdapter extends ArrayAdapter<SideListAdapter.SideItem> {

    private UserJson user;

    public SideListAdapter(Context context) {
        super(context, R.layout.empty_layout);
        refreshAdapter();
    }

    public void refreshAdapter() {
        user = BTPreference.getUser(getContext());
        clear();

        add(new SideItem(SideItemType.Header, null));
        add(new SideItem(SideItemType.Section, getContext().getString(R.string.lectures)));
        add(new SideItem(SideItemType.CreateCourse, null));

        for (CourseJsonSimple course : user.supervising_courses)
            add(new SideItem(SideItemType.Course, course));

        for (CourseJsonSimple course : user.attending_courses)
            add(new SideItem(SideItemType.Course, course));

        add(new SideItem(SideItemType.Margin, 23));
        add(new SideItem(SideItemType.Section, getContext().getString(R.string.app_name_capital)));
        add(new SideItem(SideItemType.Guide, null));
        add(new SideItem(SideItemType.Setting, null));
        add(new SideItem(SideItemType.Feedback, null));
        add(new SideItem(SideItemType.Margin, 30));

        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SideItem sideItem = getItem(position);
        switch (sideItem.type) {
            case Header: {
                if (convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.side_header, null);
                TextView name = (TextView) convertView.findViewById(R.id.header_name);
                TextView type = (TextView) convertView.findViewById(R.id.header_type);
                name.setText(user.full_name);
                if (user.supervising_courses.length > 0)
                    type.setText(getContext().getString(R.string.professor_capital));
                else
                    type.setText(getContext().getString(R.string.student_capital));
                break;
            }
            case Section: {
                if (convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.side_section, null);
                TextView section = (TextView) convertView.findViewById(R.id.section_text);
                section.setText((String) sideItem.getObject());
                break;
            }
            case CreateCourse: {
                if (convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.side_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.list_text);
                text.setText(getContext().getString(R.string.add_course));
                convertView.findViewById(R.id.list_image).setVisibility(View.VISIBLE);
                break;
            }
            case Guide: {
                if (convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.side_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.list_text);
                text.setText(getContext().getString(R.string.guide));
                convertView.findViewById(R.id.list_image).setVisibility(View.INVISIBLE);
                break;
            }
            case Setting: {
                if (convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.side_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.list_text);
                text.setText(getContext().getString(R.string.setting));
                convertView.findViewById(R.id.list_image).setVisibility(View.INVISIBLE);
                break;
            }
            case Feedback: {
                if (convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.side_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.list_text);
                text.setText(getContext().getString(R.string.feedback));
                convertView.findViewById(R.id.list_image).setVisibility(View.INVISIBLE);
                break;
            }
            case Course: {
                if (convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.side_course, null);
                CourseJsonSimple course = (CourseJsonSimple) sideItem.getObject();
                TextView name = (TextView) convertView.findViewById(R.id.course_name);
                name.setText(course.name);

                TextView detail1 = (TextView) convertView.findViewById(R.id.course_detail1);
                TextView detail2 = (TextView) convertView.findViewById(R.id.course_detail2);

                CourseJson mCourse = BTTable.MyCourseTable.get(course.id);
                if (mCourse != null) {
                    detail1.setText(String.format(getContext().getString(R.string.clicker_rate_attendance_rate), mCourse.clicker_rate, mCourse.attendance_rate));
                    if (user.supervising(course.id))
                        detail2.setText(String.format(getContext().getString(R.string.students_read_recent_notice), mCourse.students_count - mCourse.notice_unseen, mCourse.students_count));
                    else
                        detail2.setText(String.format(getContext().getString(R.string.unread_notices), mCourse.notice_unseen));
                } else {
                    detail1.setText(getContext().getString(R.string.clicker_rate_attendance_rate));
                    if (user.supervising(course.id))
                        detail2.setText(getContext().getString(R.string.students_read_recent_notice));
                    else
                        detail2.setText(getContext().getString(R.string.unread_notices));
                }
                break;
            }
            case Margin: {
                if (convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.empty_layout, null);
                int height = (Integer) sideItem.getObject();
                if (height > 100 || height < 0)
                    height = 0;
                convertView.setMinimumHeight((int) DipPixelHelper.getPixel(getContext(), height));
                break;
            }
        }

        return convertView;
    }

    public static class SideItem {
        private SideItemType type;
        private Object object;

        public SideItem(SideItemType type, Object object) {
            this.type = type;
            this.object = object;
        }

        SideItemType getType() {
            return this.type;
        }

        Object getObject() {
            return this.object;
        }
    }

    public enum SideItemType {
        Header, Section, CreateCourse, Guide, Setting, Feedback, Course, Margin
    }

}
