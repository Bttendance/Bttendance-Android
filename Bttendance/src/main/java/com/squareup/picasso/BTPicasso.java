package com.squareup.picasso;

import android.content.Context;
import android.net.Uri;

import com.squareup.okhttp.HttpResponseCache;
import com.squareup.okhttp.OkHttpClient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.squareup.picasso.Utils.parseResponseSourceHeader;

/**
 * Created by huewu on 13. 6. 19..
 */
public class BTPicasso {

    private static Picasso mSingleton;

    private BTPicasso(){}

//    public static Picasso with(Context context) {
//        if (mSingleton == null) {
//            mSingleton = new Picasso.Builder(context).loader(new VingleImageLoader(context)).build();
//        }
//
//        return mSingleton;
//    }
//
//    private static class VingleImageLoader implements Loader {
//
//        static final String RESPONSE_SOURCE = "X-Android-Response-Source";
//
//        private final OkHttpClient mClient;
//
//        /**
//         * Create new loader that uses OkHttp. This will install an image cache into your application
//         * cache directory.
//         */
//        public VingleImageLoader(final Context context) {
//            this(Utils.createDefaultCacheDir(context));
//        }
//
//        /**
//         * Create new loader that uses OkHttp. This will install an image cache into your application
//         * cache directory.
//         *
//         * @param cacheDir The directory in which the cache should be stored
//         */
//        public VingleImageLoader(final File cacheDir) {
//            this(cacheDir, Utils.calculateDiskCacheSize(cacheDir));
//        }
//
//        /**
//         * Create new loader that uses OkHttp. This will install an image cache into your application
//         * cache directory.
//         *
//         * @param cacheDir The directory in which the cache should be stored
//         * @param maxSize The size limit for the cache.
//         */
//        public VingleImageLoader(final File cacheDir, final int maxSize) {
//            this(new OkHttpClient());
//            try {
//                mClient.setResponseCache(new HttpResponseCache(cacheDir, maxSize));
//            } catch (IOException ignored) {
//            }
//        }
//
//        /**
//         * Create a new loader that uses the specified OkHttp instance. A response cache will not be
//         * automatically configured.
//         */
//        public VingleImageLoader(OkHttpClient client) {
//            this.mClient = client;
//        }
//
//        @Override public Response load(Uri uri, boolean localCacheOnly) throws IOException {
//            HttpURLConnection connection = mClient.open(new URL(uri.toString()));
//            connection.setUseCaches(true);
//            if (localCacheOnly) {
//                connection.setRequestProperty("Cache-Control", "only-if-cached");
//            }
//            int contentLength = connection.getContentLength();
//            if( contentLength == 0 || contentLength > 512 * 1024 ){
//                return new Response(new ByteArrayInputStream(new byte[]{}), false);
//            }else{
//                boolean fromCache = parseResponseSourceHeader(connection.getHeaderField(RESPONSE_SOURCE));
//                return new Response(connection.getInputStream(), fromCache);
//            }
//        }
//
//    }
}//end of class
