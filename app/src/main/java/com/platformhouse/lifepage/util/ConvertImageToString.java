package com.platformhouse.lifepage.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/*
 * Created by Shehab Salah on 10/4/16.
 * This Class responsible on converting the image into String using Base64 ENCODE Algorithm
 */
public class ConvertImageToString {
    /**
     * encodeToBase64 Method convert a bitmap image into string using Base64 algorithm.
     *
     * @param image Bitmap image that will convert to String.
     * @return the image as string.
     */
    public static String encodeToBase64(Bitmap image) {
        System.gc();
        if (image == null)return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT); // min minSdkVersion 8;
    }
    /**
     * getImageFromLocalDir Method is responsible on getting the image from the local directory
     * and convert it to bitmap image
     * @param context the activity context used to fitch the image from the Media Directory
     * @param image the image URI
     * @return Bitmap
     * */
    public static Bitmap getImageFromLocalDir(Context context,String image){
        Uri pickedImage = Uri.parse(image);
        String[] filePath = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(pickedImage, filePath, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            cursor.close();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeFile(imagePath, options);
        }
        return null;
    }
}