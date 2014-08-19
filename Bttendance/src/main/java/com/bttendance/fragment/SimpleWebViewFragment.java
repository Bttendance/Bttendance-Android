/**
 * Copyright 2013 The Finest Artist
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *limitations under the License.
 */

package com.bttendance.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.bttendance.R;
import com.bttendance.fragment.BTFragment;

public class SimpleWebViewFragment extends BTFragment {

    public static final String EXTRA_URL = "url";

    private String mUrl = null;

    private FrameLayout mView = null;
    private WebView mWebview = null;
    private ProgressBar mPbar = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getArguments().getString(EXTRA_URL);
        if (!mUrl.startsWith("http"))
            mUrl = "http://" + mUrl;
        setHasOptionsMenu(true);
    }

    @SuppressLint({
            "SetJavaScriptEnabled", "NewApi"
    })
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.web_view, container, false);

        mPbar = (ProgressBar) view.findViewById(R.id.web_view_progress);

        mView = (FrameLayout) view.findViewById(R.id.web_view);
        mView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        mWebview = new WebView(getActivity());
        mWebview.setVisibility(View.GONE);
        mWebview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        if (mUrl != null) {
            mWebview.setWebViewClient(new MyWebViewClient());
            mWebview.setWebChromeClient(new MyWebChromeClient());
            mWebview.getSettings().setPluginState(PluginState.ON);
            mWebview.getSettings().setUseWideViewPort(true);
            mWebview.getSettings().setDefaultZoom(ZoomDensity.FAR);
            mWebview.getSettings().setBuiltInZoomControls(true);
            mWebview.getSettings().setSupportZoom(true);
            mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            mWebview.getSettings().setAllowFileAccess(true);
            mWebview.getSettings().setDomStorageEnabled(true);
            mWebview.getSettings().setJavaScriptEnabled(true);
            mWebview.getSettings().setAppCacheEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                mWebview.getSettings().setDisplayZoomControls(false);

            mWebview.loadUrl(mUrl);
        }
        mView.addView(mWebview);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getSherlockActivity() == null)
            return;

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.app_name_capital));
    }

    public class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int progress) {
            if (progress < 100 && mPbar.getVisibility() == ProgressBar.GONE)
                mPbar.setVisibility(ProgressBar.VISIBLE);
            mPbar.setProgress(progress);
            if (progress == 100)
                mPbar.setVisibility(ProgressBar.GONE);
        }
    }

    public class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            view.setVisibility(View.VISIBLE);
            final Animation fade = new AlphaAnimation(0.0f, 1.0f);
            fade.setDuration(200);
            view.startAnimation(fade);
            view.setVisibility(View.VISIBLE);

            if (getSherlockActivity() != null) {
                ActionBar actionBar = getSherlockActivity().getSupportActionBar();
                if (view.getTitle() != null && view.getTitle().length() != 0)
                    actionBar.setTitle(view.getTitle());
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.endsWith(".mp4")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "video/*");
                view.getContext().startActivity(intent);
                return true;
            } else {
                return super.shouldOverrideUrlLoading(view, url);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mView.removeView(mWebview);
        mWebview.removeAllViews();
        mWebview.destroy();
    }
}