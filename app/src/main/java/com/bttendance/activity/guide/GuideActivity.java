package com.bttendance.activity.guide;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bttendance.R;
import com.bttendance.activity.BTActivity;
import com.bttendance.fragment.SimpleWebViewFragment;
import com.bttendance.model.BTKey;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by TheFinestArtist on 2014. 08. 14..
 */
public class GuideActivity extends BTActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.adView)
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        String url = getIntent().getStringExtra(BTKey.EXTRA_URL);
        String title = getIntent().getStringExtra(BTKey.EXTRA_TITLE);

        SimpleWebViewFragment frag = new SimpleWebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BTKey.EXTRA_URL, url);
        bundle.putString(BTKey.EXTRA_TITLE, title);
        frag.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                R.anim.no_anim,
                R.anim.no_anim,
                R.anim.no_anim,
                R.anim.no_anim);
        ft.add(R.id.content, frag, ((Object) frag).getClass().getSimpleName());
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadAd();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.modal_activity_close_enter, R.anim.modal_activity_close_exit);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void loadAd() {
        try {
            mAdView.loadAd(new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("BE5D7D1E701EF21AB93369A353CAA3ED")
                    .addTestDevice("A642C45F5DD4C0E09AA896DDABD36789")
                    .build());
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }
}
