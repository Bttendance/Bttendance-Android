
package com.bttendance.helper;

//import com.cloudinary.Cloudinary;

/**
 * CloudinaryHelper
 *
 * @author The Finest Artist
 */
public class CloudinaryHelper {

    private static final int PROFILE_BITMAP_SIZE = 138;

//    public static String uploadPhoto(Context context, Uri uri, String filename, int rotation)
//            throws FileNotFoundException,
//            IOException, JSONException {
//
//        Cloudinary cloudinary = new Cloudinary(context);
//        InputStream inputStream;
//        if (rotation == 0)
//            inputStream = context.getContentResolver().openInputStream(uri);
//        else {
//            Bitmap bitmap = BitmapHelper.getThumbnail(context, uri, PROFILE_BITMAP_SIZE);
//            bitmap = BitmapHelper.centerCrop(bitmap);
//            bitmap = BitmapHelper.rotate(bitmap, rotation);
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            bitmap.compress(CompressFormat.PNG, 0 /* ignored for PNG */, bos);
//            byte[] bitmapdata = bos.toByteArray();
//            inputStream = new ByteArrayInputStream(bitmapdata);
//        }
//
//        JSONObject cloud_image = cloudinary.uploader().upload(inputStream, Cloudinary.emptyMap());
//        return cloud_image.getString("public_id");
//    }
}
