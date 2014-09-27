package com.bttendance.fragment.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.bttendance.R;
import com.bttendance.activity.MainActivity;
import com.bttendance.adapter.SettingAdapter;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.update.UserUpdatedEvent;
import com.bttendance.fragment.BTFragment;
import com.bttendance.fragment.SimpleWebViewFragment;
import com.bttendance.helper.PackagesHelper;
import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;

import org.jraf.android.backport.switchwidget.Switch;

/**
 * Created by TheFinestArtist on 2013. 12. 1..
 */
public class SettingFragment extends BTFragment implements AdapterView.OnItemClickListener {

    ListView mListView;
    SettingAdapter mAdapter;

    /**
     * Action Bar Menu
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getSherlockActivity() == null)
            return;

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        if (!((MainActivity) getActivity()).isDrawerOpen())
            actionBar.setTitle(getString(R.string.setting));
    }

    /**
     * Drawing View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new SettingAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        refreshAdapter();
    }

    @Subscribe
    public void onUpdate(UserUpdatedEvent event) {
        refreshAdapter();
    }

    private void refreshAdapter() {
        if (this.isAdded() && mAdapter != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.refreshAdapter();
                }
            });
        }
    }

    /**
     * ************************
     * onItemClick
     * *************************
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (mAdapter.getItem(position).getType()) {
            case Attendance:
                if (getBTService() != null && view.findViewById(R.id.setting_noti) != null)
                    getBTService().updateSettingAttendance(((Switch) view.findViewById(R.id.setting_noti)).isChecked(), null);
                break;
            case Clicker:
                if (getBTService() != null && view.findViewById(R.id.setting_noti) != null)
                    getBTService().updateSettingClicker(((Switch) view.findViewById(R.id.setting_noti)).isChecked(), null);
                break;
            case Notice:
                if (getBTService() != null && view.findViewById(R.id.setting_noti) != null)
                    getBTService().updateSettingNotice(((Switch) view.findViewById(R.id.setting_noti)).isChecked(), null);
                break;
            case PushInfo:
                break;
            case Terms:
                showTerms();
                break;
            case Privacy:
                showPrivacy();
                break;
            case Blog:
                showBlog();
                break;
            case Facebook:
                showFacebook();
                break;
            case Margin:
                break;
        }
    }

    /**
     * Private Methods
     */
    private void showTerms() {
        String url;
        String locale = getResources().getConfiguration().locale.getLanguage();
        if ("ko".equals(locale))
            url = "http://www.bttendance.com/terms";
        else
            url = "http://www.bttendance.com/terms-en";

        SimpleWebViewFragment frag = new SimpleWebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SimpleWebViewFragment.EXTRA_URL, url);
        frag.setArguments(bundle);
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void showPrivacy() {
        String url;
        String locale = getResources().getConfiguration().locale.getLanguage();
        if ("ko".equals(locale))
            url = "http://www.bttendance.com/privacy";
        else
            url = "http://www.bttendance.com/privacy-en";

        SimpleWebViewFragment frag = new SimpleWebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SimpleWebViewFragment.EXTRA_URL, url);
        frag.setArguments(bundle);
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void showBlog() {
        String url;
        String locale = getResources().getConfiguration().locale.getLanguage();
        if ("ko".equals(locale))
            url = "http://bttendance.tistory.com";
        else
            url = "http://www.bttendance.com/blog";

        SimpleWebViewFragment frag = new SimpleWebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SimpleWebViewFragment.EXTRA_URL, url);
        frag.setArguments(bundle);
        BTEventBus.getInstance().post(new AddFragmentEvent(frag));
    }

    private void showFacebook() {
        String url;
        String locale = getResources().getConfiguration().locale.getLanguage();
        String facebookScheme;
        if ("ko".equals(locale)) {
            facebookScheme = "fb://profile/226844200832003";
            url = "http://www.facebook.com/226844200832003";
        } else {
            facebookScheme = "fb://profile/633914856683639";
            url = "http://www.facebook.com/633914856683639";
        }

        if (PackagesHelper.isInstalled(getActivity(), PackagesHelper.FACEBOOK)) {
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookScheme));
            facebookIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            startActivity(facebookIntent);
        } else {
            SimpleWebViewFragment frag = new SimpleWebViewFragment();
            Bundle bundle = new Bundle();
            bundle.putString(SimpleWebViewFragment.EXTRA_URL, url);
            frag.setArguments(bundle);
            BTEventBus.getInstance().post(new AddFragmentEvent(frag));
        }
    }
}
