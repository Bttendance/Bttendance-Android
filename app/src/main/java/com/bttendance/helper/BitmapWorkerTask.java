package com.bttendance.helper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 21.07.12
 * Time: 16:05
 */
public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

    private static LruCache<String, Bitmap> cache = null;
    private static DiskLruCache diskCache = null;

    private final WeakReference<ImageView> weakReference;
    private String url = "";

    public BitmapWorkerTask(ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        if (cache == null) {
            final int memClass = ((ActivityManager) imageView.getContext().getSystemService(
                    Context.ACTIVITY_SERVICE)).getMemoryClass();
            cache = new LruCache<String, Bitmap>(1024 * 1024 * memClass / 3) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes() * value.getHeight();
                }
            };
        }

        if (diskCache == null) {
            try {
                diskCache = DiskLruCache.open(imageView.getContext().getCacheDir(), 1, 1, 1024 * 1024 * 10);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        weakReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        try {
            url = strings[0];
            Bitmap bitmap = getBitmapFromCache(url);

            if (bitmap != null) {
                return bitmap;
            }

            bitmap = BitmapFactory.decodeStream(new URL(url).openStream());
            addBitmapToCache(url, bitmap);
            return bitmap;
        } catch (IOException e) {
            return null;
        }
    }

    public Bitmap getBitmapFromCache(String key) {
        Bitmap bitmap = getBitmapFromMemCache(key);
        if (bitmap == null) {
            bitmap = getBitmapFromDiskCache(key);
            if (bitmap != null) {
                addBitmapToCache(key, bitmap);
            }
        }

        return bitmap;
    }

    public void addBitmapToCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            addBitmapToMemoryCache(key, bitmap);
        }

        if (getBitmapFromDiskCache(key) == null) {
            addBitmapToDiskCache(bitmap, key);
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (weakReference != null && bitmap != null) {
            final ImageView imageView = weakReference.get();
            final BitmapWorkerTask bitmapWorkerTask =
                    getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
//                FadeUtils.show(imageView);
            }
        }
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            cache.put(key, bitmap);
        }
    }

    public void addBitmapToDiskCache(Bitmap bitmap, String key) {
        try {
            DiskLruCache.Editor editor = diskCache.edit(key.hashCode() + "");
            if (editor != null) {
                OutputStream os = editor.newOutputStream(0);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                editor.commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return cache.get(key);
    }

    public Bitmap getBitmapFromDiskCache(String key) {
        Bitmap bitmap = null;
        try {
            DiskLruCache.Snapshot snapshot = diskCache.get(key.hashCode() + "");
            if (snapshot != null) {
                bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
            }
        } catch (IOException e) {
            bitmap = null;
        }

        return bitmap;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public static boolean cancelPotentialWork(String url, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.url;
            if (!bitmapData.equals(url)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    public static void loadBitmap(Context context, String url, ImageView imageView) {
        if (cancelPotentialWork(url, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(context.getResources(), null, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(url);
        }
    }

    public static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }
}