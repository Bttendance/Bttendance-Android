package com.bttendance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.UserJson;

public class SideListAdapter extends ArrayAdapter<SideListAdapter.SideItem> {

    private UserJson mUser;

    public SideListAdapter(Context context) {
        super(context, R.layout.empty_layout);
        refreshAdapter();
    }

    public void refreshAdapter() {
        clear();
        mUser = BTTable.getMe();

        add(new SideItem(SideItemType.Header, null));
        add(new SideItem(SideItemType.Section, getContext().getString(R.string.lectures)));
        add(new SideItem(SideItemType.AddCourse, null));

        for (int i = 0; i < BTTable.MyCourseTable.size(); i++)
            add(new SideItem(SideItemType.Course, BTTable.MyCourseTable.valueAt(i)));

        add(new SideItem(SideItemType.Margin, 20));
        add(new SideItem(SideItemType.Section, getContext().getString(R.string.app_name_capital)));
        add(new SideItem(SideItemType.Guide, null));
        add(new SideItem(SideItemType.Setting, null));
        add(new SideItem(SideItemType.Feedback, null));
        add(new SideItem(SideItemType.Margin, 30));

        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        SideItem sideItem = getItem(position);
        switch (sideItem.type) {
            case Header: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.side_header, null);
                TextView name = (TextView) convertView.findViewById(R.id.header_name);
                TextView type = (TextView) convertView.findViewById(R.id.header_type);
                name.setText(mUser.name);
                if (mUser.isProfessor())
                    type.setText(getContext().getString(R.string.professor_capital));
                else
                    type.setText(getContext().getString(R.string.student_capital));
                break;
            }
            case AddCourse: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.side_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.list_text);
                text.setText(getContext().getString(R.string.add_course));
                convertView.findViewById(R.id.list_image).setVisibility(View.VISIBLE);
                break;
            }
            case Guide: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.side_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.list_text);
                text.setText(getContext().getString(R.string.guide));
                convertView.findViewById(R.id.list_image).setVisibility(View.INVISIBLE);
                break;
            }
            case Setting: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.side_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.list_text);
                text.setText(getContext().getString(R.string.setting));
                convertView.findViewById(R.id.list_image).setVisibility(View.INVISIBLE);
                break;
            }
            case Feedback: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.side_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.list_text);
                text.setText(getContext().getString(R.string.feedback));
                convertView.findViewById(R.id.list_image).setVisibility(View.INVISIBLE);
                break;
            }
            case Course: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.side_course, null);
                TextView courseName = (TextView) convertView.findViewById(R.id.course_name);
                TextView schoolName = (TextView) convertView.findViewById(R.id.school_name);
                TextView courseCode = (TextView) convertView.findViewById(R.id.code);
                CourseJson courseJson = (CourseJson) sideItem.getObject();
                if (courseJson != null) {
                    courseName.setText(courseJson.name);
                    courseCode.setText(courseJson.code);
                    if (courseJson.school != null)
                        schoolName.setText(courseJson.school.name);
                }
                break;
            }
            case Section: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.side_section, null);
                TextView section = (TextView) convertView.findViewById(R.id.section_text);
                section.setText((String) sideItem.getObject());
                break;
            }
            case Margin: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.empty_layout, null);
                int height = (Integer) sideItem.getObject();
                if (height > 100 || height < 0)
                    height = 0;
                convertView.setMinimumHeight((int) DipPixelHelper.getPixel(getContext(), height));
                break;
            }
        }

        if (convertView.findViewById(R.id.selector) != null) {
            final View finalConvertView = convertView;
            convertView.findViewById(R.id.selector).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ListView) parent).performItemClick(finalConvertView, position, 0);
                }
            });
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

        public SideItemType getType() {
            return this.type;
        }

        public Object getObject() {
            return this.object;
        }
    }

    public enum SideItemType {
        Header, AddCourse, Guide, Setting, Feedback, Course, Section, Margin
    }

}
