
package com.utopia.bttendance.helper;

import android.net.Uri;
import android.net.Uri.Builder;

import java.util.List;
import java.util.Locale;

public class CloudinaryImageUrlFilter {

    // use cloudanary stuffs.

    public static String getOriginalImageUrl(String url) {

        if (url == null)
            return null;

        Uri imageUri = Uri.parse(url);

        if (!isCloudninaryImage(imageUri))
            return url;

        String lastPath = imageUri.getLastPathSegment();

        Builder builder = imageUri.buildUpon();
        builder.encodedPath(lastPath);

        return builder.build().toString();
    }

    public static String getImageUrl(String url, int width, int height) {

        if (url == null)
            return null;

        Uri imageUri = Uri.parse(url);

        if (!isCloudninaryImage(imageUri))
            return url;

        String lastPath = imageUri.getLastPathSegment();
        String cloudinaryPath = imageUri.getPathSegments().get(0);

        int quality = parseCloudinaryQuality(cloudinaryPath);

        Builder builder = imageUri.buildUpon();
        String path = generateCloudinaryPath(quality, width, height);
        builder.encodedPath(path);
        builder.appendPath(lastPath);

        return builder.build().toString();
    }

    private static int parseCloudinaryQuality(String cloudinaryPath) {
        int index;
        // cloudinaryPath.matches("q_\\d\\d");
        index = cloudinaryPath.indexOf("q_");
        cloudinaryPath = cloudinaryPath.substring(index);
        index = cloudinaryPath.indexOf(",");
        String quality = cloudinaryPath.substring(2, index);
        try {
            return Integer.parseInt(quality);
        } catch (Exception e) {
            return 80;
        }
    }

    private static String generateCloudinaryPath(int quality, int width, int height) {
        return String.format(Locale.getDefault(), "c_fill,q_%d,w_%d,h_%d", quality, width, height);
    }

    private static boolean isCloudninaryImage(Uri imageUri) {
        if (imageUri == null)
            return false;
        List<String> pathSegments = imageUri.getPathSegments();
        if (pathSegments == null || pathSegments.size() == 0)
            return false;

        String path = pathSegments.get(0);
        return path.contains("c_");
    }

    private CloudinaryImageUrlFilter() {
    }

}// end of class
