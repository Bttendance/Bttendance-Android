package com.bttendance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.bttendance.R;
import com.bttendance.helper.DipPixelHelper;
import com.bttendance.model.BTPreference;
import com.bttendance.model.json.UserJson;

import org.jraf.android.backport.switchwidget.Switch;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class SettingAdapter extends ArrayAdapter<SettingAdapter.SettingItem> {

    private UserJson user;

    public SettingAdapter(Context context) {
        super(context, R.layout.empty_layout);
        refreshAdapter();
    }

    public void refreshAdapter() {
        user = BTPreference.getUser(getContext());
        clear();

        add(new SettingItem(SettingItemType.Attendance, null));
        add(new SettingItem(SettingItemType.Clicker, null));
        add(new SettingItem(SettingItemType.Notice, null));
        add(new SettingItem(SettingItemType.PushInfo, null));
        add(new SettingItem(SettingItemType.Terms, null));
        add(new SettingItem(SettingItemType.Privacy, null));
        add(new SettingItem(SettingItemType.Blog, null));
        add(new SettingItem(SettingItemType.Facebook, null));
        add(new SettingItem(SettingItemType.Margin, 33));

        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        SettingItem settingItem = getItem(position);
        switch (settingItem.type) {
            case Attendance: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.setting_noti, null);
                TextView text = (TextView) convertView.findViewById(R.id.setting_text);
                text.setText(getContext().getString(R.string.attendance_notification));
                Switch noti = (Switch) convertView.findViewById(R.id.setting_noti);
                if (user.setting != null)
                    noti.setChecked(user.setting.attendance);
                break;
            }
            case Clicker: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.setting_noti, null);
                TextView text = (TextView) convertView.findViewById(R.id.setting_text);
                text.setText(getContext().getString(R.string.clicker_notification));
                Switch noti = (Switch) convertView.findViewById(R.id.setting_noti);
                if (user.setting != null)
                    noti.setChecked(user.setting.clicker);
                break;
            }
            case Notice: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.setting_noti, null);
                TextView text = (TextView) convertView.findViewById(R.id.setting_text);
                text.setText(getContext().getString(R.string.notice_notification));
                Switch noti = (Switch) convertView.findViewById(R.id.setting_noti);
                if (user.setting != null)
                    noti.setChecked(user.setting.notice);
                break;
            }
            case PushInfo: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.setting_info, null);
                break;
            }
            case Terms: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.setting_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.setting_text);
                text.setText(getContext().getString(R.string.terms_of_service));
                break;
            }
            case Privacy: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.setting_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.setting_text);
                text.setText(getContext().getString(R.string.privacy_policy));
                break;
            }
            case Blog: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.setting_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.setting_text);
                text.setText(getContext().getString(R.string.blog));
                break;
            }
            case Facebook: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.setting_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.setting_text);
                text.setText(getContext().getString(R.string.facebook));
                break;
            }
            case Margin: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.empty_layout, null);
                int height = (Integer) settingItem.getObject();
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

        if (convertView != null && convertView.findViewById(R.id.setting_noti) != null) {
            final View finalConvertView = convertView;
            ((Switch) convertView.findViewById(R.id.setting_noti)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    ((ListView) parent).performItemClick(finalConvertView, position, 0);
                }
            });
        }

        return convertView;
    }

    public static class SettingItem {
        private SettingItemType type;
        private Object object;

        public SettingItem(SettingItemType type, Object object) {
            this.type = type;
            this.object = object;
        }

        public SettingItemType getType() {
            return this.type;
        }

        public Object getObject() {
            return this.object;
        }
    }

    public enum SettingItemType {
        Attendance, Clicker, Notice, PushInfo, Terms, Privacy, Blog, Facebook, Margin
    }
}
