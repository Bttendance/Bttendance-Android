package com.bttendance.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.CourseJson;
import com.bttendance.model.json.UserJson;
import com.bttendance.model.json.UserJsonSimple;

import static android.view.LayoutInflater.from;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class CourseSettingAdapter extends ArrayAdapter<CourseSettingAdapter.CourseSettingItem> {

    private int mCourseID;
    private UserJson mUser;
    private CourseJson mCourse;

    public CourseSettingAdapter(Context context, int courseID) {
        super(context, R.layout.empty_layout);
        mCourseID = courseID;
        refreshAdapter();
    }

    public void refreshAdapter() {
        mUser = BTPreference.getUser(getContext());
        mCourse = BTTable.MyCourseTable.get(mCourseID);
        clear();

        add(new CourseSettingItem(CourseSettingItemType.Header, getContext().getString(R.string.managers_capital)));
        if (mCourse != null)
            for (UserJsonSimple manager : mCourse.managers)
                add(new CourseSettingItem(CourseSettingItemType.Manager, manager.id));
        add(new CourseSettingItem(CourseSettingItemType.AddManager, null));

        add(new CourseSettingItem(CourseSettingItemType.Section, getContext().getString(R.string.students_capital)));
        add(new CourseSettingItem(CourseSettingItemType.ShowStudentList, null));

        add(new CourseSettingItem(CourseSettingItemType.Section, getContext().getString(R.string.records_capital)));
        add(new CourseSettingItem(CourseSettingItemType.ExportRecords, null));
        add(new CourseSettingItem(CourseSettingItemType.ClickerRecords, null));
        add(new CourseSettingItem(CourseSettingItemType.AttendanceRecords, null));

        add(new CourseSettingItem(CourseSettingItemType.Margin, 60));
        add(new CourseSettingItem(CourseSettingItemType.CloseCourse, null));
        add(new CourseSettingItem(CourseSettingItemType.Margin, 45));

        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        CourseSettingItem courseSettingItem = getItem(position);
        switch (courseSettingItem.type) {
            case Manager: {
                convertView = from(getContext()).inflate(R.layout.course_setting_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.setting_text);
                if (mCourse != null) {
                    UserJsonSimple manager = mCourse.getManager((Integer) courseSettingItem.getObject());
                    if (manager != null)
                        text.setText(manager.full_name + " - " + manager.email);
                    else
                        text.setText("");
                } else
                    text.setText("");
                convertView.findViewById(R.id.selector).setVisibility(View.GONE);
                convertView.findViewById(R.id.setting_arrow).setVisibility(View.GONE);
                break;
            }
            case AddManager: {
                convertView = from(getContext()).inflate(R.layout.course_setting_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.setting_text);
                text.setText(getContext().getString(R.string.add_manager));
                break;
            }
            case ShowStudentList: {
                convertView = from(getContext()).inflate(R.layout.course_setting_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.setting_text);
                text.setText(getContext().getString(R.string.show_the_student_list));
                break;
            }
            case ExportRecords: {
                convertView = from(getContext()).inflate(R.layout.course_setting_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.setting_text);
                text.setText(getContext().getString(R.string.export_records));
                break;
            }
            case ClickerRecords: {
                convertView = from(getContext()).inflate(R.layout.course_setting_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.setting_text);
                text.setText(getContext().getString(R.string.clicker_records));
                break;
            }
            case AttendanceRecords: {
                convertView = from(getContext()).inflate(R.layout.course_setting_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.setting_text);
                text.setText(getContext().getString(R.string.attendance_records));
                break;
            }
            case CloseCourse: {
                convertView = from(getContext()).inflate(R.layout.course_setting_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.setting_text);
                text.setText(getContext().getString(R.string.close_course));
                text.setTextColor(getContext().getResources().getColor(R.color.bttendance_red));
                break;
            }
            case Header: {
                convertView = from(getContext()).inflate(R.layout.course_setting_header, null);
                TextView text = (TextView) convertView.findViewById(R.id.section_text);
                text.setText((String) courseSettingItem.getObject());
                break;
            }
            case Section: {
                convertView = from(getContext()).inflate(R.layout.course_setting_section, null);
                TextView text = (TextView) convertView.findViewById(R.id.section_text);
                text.setText((String) courseSettingItem.getObject());
                break;
            }
            case Margin: {
                convertView = from(getContext()).inflate(R.layout.empty_layout, null);
                int height = (Integer) courseSettingItem.getObject();
                if (height > 100 || height < 0)
                    height = 0;
                convertView.setMinimumHeight((int) DipPixelHelper.getPixel(getContext(), height));
                break;
            }
        }

        if (convertView != null && convertView.findViewById(R.id.selector) != null) {
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

    public static class CourseSettingItem {
        private CourseSettingItemType type;
        private Object object;

        public CourseSettingItem(CourseSettingItemType type, Object object) {
            this.type = type;
            this.object = object;
        }

        public CourseSettingItemType getType() {
            return this.type;
        }

        public Object getObject() {
            return this.object;
        }
    }

    public enum CourseSettingItemType {
        Manager, AddManager, ShowStudentList, ExportRecords, ClickerRecords, AttendanceRecords, CloseCourse, Header, Section, Margin
    }
}
