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
import com.bttendance.model.BTPreference;
import com.bttendance.model.BTTable;
import com.bttendance.model.json.UserJson;

/**
 * Created by TheFinestArtist on 2013. 12. 3..
 */
public class ProfileAdapter extends ArrayAdapter<ProfileAdapter.ProfileItem> {

    private UserJson mUser;

    public ProfileAdapter(Context context) {
        super(context, R.layout.empty_layout);
        refreshAdapter();
    }

    public void refreshAdapter() {
        mUser = BTTable.getMe();
        clear();

        add(new ProfileItem(ProfileItemType.Name, null));
        add(new ProfileItem(ProfileItemType.Email, null));

//        if (mUser.employed_schools.length + mUser.enrolled_schools.length > 0) {
//            add(new ProfileItem(ProfileItemType.Section, getContext().getString(R.string.institution_capital)));
//            for (SchoolJsonSimple school : mUser.employed_schools)
//                add(new ProfileItem(ProfileItemType.Employed, school));
//            for (SchoolJsonSimple school : mUser.enrolled_schools)
//                add(new ProfileItem(ProfileItemType.Enrolled, school));
//        }

        add(new ProfileItem(ProfileItemType.Margin, 55));
        add(new ProfileItem(ProfileItemType.Password, null));
        add(new ProfileItem(ProfileItemType.Margin, 33));

        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ProfileItem profileItem = getItem(position);
        switch (profileItem.type) {
            case Name: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_edit, null);
                TextView type = (TextView) convertView.findViewById(R.id.profile_type);
                TextView info = (TextView) convertView.findViewById(R.id.profile_info);
                type.setText(getContext().getString(R.string.name));
                info.setText(mUser.name);
                break;
            }
            case Email: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_edit, null);
                TextView type = (TextView) convertView.findViewById(R.id.profile_type);
                TextView info = (TextView) convertView.findViewById(R.id.profile_info);
                type.setText(getContext().getString(R.string.email));
                info.setText(mUser.email);
                break;
            }
            case Employed:
            case Enrolled: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_detail, null);
//                SchoolJsonSimple school = (SchoolJsonSimple) profileItem.getObject();
//                convertView.setTag(R.id.school_id, school.id);
//                TextView text = (TextView) convertView.findViewById(R.id.profile_text);
//                TextView message = (TextView) convertView.findViewById(R.id.profile_message);
//                text.setText(school.name);
//                text.setTextColor(getContext().getResources().getColor(R.color.bttendance_navy));
//                if (profileItem.type == ProfileItemType.Employed) {
//                    message.setText(getContext().getString(R.string.professor));
//                    convertView.findViewById(R.id.selector).setVisibility(View.GONE);
//                    convertView.findViewById(R.id.profile_arrow).setVisibility(View.GONE);
//                } else
//                    message.setText(String.format(getContext().getString(R.string.student__), mUser.getIdentity(school.id)));
                break;
            }
            case Password: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.profile_text);
                text.setText(getContext().getString(R.string.update_password));
                text.setTextColor(getContext().getResources().getColor(R.color.bttendance_red));
                break;
            }
            case SignOut: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_list, null);
                TextView text = (TextView) convertView.findViewById(R.id.profile_text);
                text.setText(getContext().getString(R.string.sign_out));
                text.setTextColor(getContext().getResources().getColor(R.color.bttendance_red));
                break;
            }
            case Section: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_section, null);
                TextView text = (TextView) convertView.findViewById(R.id.section_text);
                text.setText((String) profileItem.getObject());
                break;
            }
            case Margin: {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.empty_layout, null);
                int height = (Integer) profileItem.getObject();
                if (height > 100 || height < 0)
                    height = 0;
                convertView.setMinimumHeight((int) DipPixelHelper.getPixel(getContext(), height));
                break;
            }
        }

        if (profileItem.type == ProfileItemType.Employed
                || profileItem.type == ProfileItemType.Section
                || profileItem.type == ProfileItemType.Margin)
            convertView.setClickable(true);

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

    public static class ProfileItem {
        private ProfileItemType type;
        private Object object;

        public ProfileItem(ProfileItemType type, Object object) {
            this.type = type;
            this.object = object;
        }

        public ProfileItemType getType() {
            return this.type;
        }

        public Object getObject() {
            return this.object;
        }
    }

    public enum ProfileItemType {
        Name, Email, Employed, Enrolled, Password, SignOut, Section, Margin
    }
}
