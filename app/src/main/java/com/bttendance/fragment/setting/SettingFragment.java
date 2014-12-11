package com.bttendance.fragment.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bttendance.R;
import com.bttendance.adapter.SettingAdapter;
import com.bttendance.event.AddFragmentEvent;
import com.bttendance.event.update.UserUpdatedEvent;
import com.bttendance.fragment.BTFragment;
import com.bttendance.fragment.SimpleWebViewFragment;
import com.bttendance.helper.PackagesHelper;
import com.bttendance.model.BTKey;
import com.squareup.otto.BTEventBus;
import com.squareup.otto.Subscribe;

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
        if (getActivity() == null)
            return;

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
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
                    getBTService().updateSettingAttendance(((SwitchCompat) view.findViewById(R.id.setting_noti)).isChecked(), null);
                break;
            case Clicker:
                if (getBTService() != null && view.findViewById(R.id.setting_noti) != null)
                    getBTService().updateSettingClicker(((SwitchCompat) view.findViewById(R.id.setting_noti)).isChecked(), null);
                break;
            case Notice:
                if (getBTService() != null && view.findViewById(R.id.setting_noti) != null)
                    getBTService().updateSettingNotice(((SwitchCompat) view.findViewById(R.id.setting_noti)).isChecked(), null);
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
        bundle.putString(BTKey.EXTRA_URL, url);
        bundle.putString(BTKey.EXTRA_TITLE, getString(R.string.terms_of_service));
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
        bundle.putString(BTKey.EXTRA_URL, url);
        bundle.putString(BTKey.EXTRA_TITLE, getString(R.string.privacy_policy));
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
        bundle.putString(BTKey.EXTRA_URL, url);
        bundle.putString(BTKey.EXTRA_TITLE, getString(R.string.blog));
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                facebookIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            else
                facebookIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            startActivity(facebookIntent);
        } else {
            SimpleWebViewFragment frag = new SimpleWebViewFragment();
            Bundle bundle = new Bundle();
            bundle.putString(BTKey.EXTRA_URL, url);
            bundle.putString(BTKey.EXTRA_TITLE, getString(R.string.facebook));
            frag.setArguments(bundle);
            BTEventBus.getInstance().post(new AddFragmentEvent(frag));
        }
    }
}
