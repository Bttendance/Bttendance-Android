
package com.utopia.bttendance.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BitmapHelper {

    public static Bitmap getThumbnail(Context context, Uri uri, int size)
            throws FileNotFoundException, IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);

        Options onlyBoundsOptions = new Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;// optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight
                : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > size) ? (originalSize / size) : 1.0;

        Options bitmapOptions = new Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;// optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0)
            return 1;
        else
            return k;
    }

    public static Bitmap getBitmap(String filePath) {

        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Only scale if we need to
        // (16384 buffer for img processing)
        Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math
                .abs(options.outWidth - 100);
        if (options.outHeight * options.outWidth * 2 >= 16384) {
            // Load, scaling to smallest power of 2 that'll get it <= desired
            // dimensions
            double sampleSize = scaleByHeight
                    ? options.outHeight / 100
                    : options.outWidth / 100;
            options.inSampleSize =
                    (int) Math.pow(2d, Math.floor(
                            Math.log(sampleSize) / Math.log(2d)));
        }

        // Do the actual decoding
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[512];
        Bitmap output = BitmapFactory.decodeFile(filePath, options);

        return output;
    }

    public static Bitmap readShrink(Context context, Uri uri, int size) {

        InputStream readStream = null;
        InputStream writeStream = null;
        try {
            readStream = context.getContentResolver().openInputStream(uri);
            Options bmpFactoryOptions = new Options();
            bmpFactoryOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(readStream, null, bmpFactoryOptions);

            int ratio = (int) Math.min(bmpFactoryOptions.outWidth / (float) size,
                    bmpFactoryOptions.outHeight / (float) size);
            bmpFactoryOptions.inSampleSize = ratio;
            bmpFactoryOptions.inJustDecodeBounds = false;

            writeStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(writeStream, null, bmpFactoryOptions);
            return bitmap;
        } catch (FileNotFoundException e) {
        } finally {
            try {
                readStream.close();
            } catch (IOException e) {
            }
            try {
                writeStream.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    public static Bitmap cropCenter(Bitmap bitmap) {

        int minSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        int diffSize = Math.abs(bitmap.getWidth() - bitmap.getHeight());

        Bitmap targetBitmap;
        targetBitmap = Bitmap.createBitmap(minSize, minSize, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Matrix matrix = new Matrix();
        if (bitmap.getWidth() >= bitmap.getHeight())
            matrix.setTranslate(diffSize, 0);
        else
            matrix.setTranslate(0, diffSize);

        canvas.drawBitmap(targetBitmap, new Matrix(), new Paint());

        bitmap.recycle();
        return targetBitmap;
    }

    public static Bitmap centerCrop(Bitmap srcBmp) {
        Bitmap dstBmp = null;
        if (srcBmp.getWidth() >= srcBmp.getHeight()) {

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        } else {

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }
        return dstBmp;
    }

    public static Bitmap rotate(Bitmap bitmap, int rotation) {

        int targetWidth = bitmap.getWidth();
        int targetHeight = bitmap.getHeight();

        if (rotation == 90 || rotation == 270) {
            targetHeight = bitmap.getWidth();
            targetWidth = bitmap.getHeight();
        }

        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);
        Matrix matrix = new Matrix();
        matrix.setRotate(rotation, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        canvas.drawBitmap(bitmap, matrix, new Paint());

        bitmap.recycle();
        return targetBitmap;
    }
}
