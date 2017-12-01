package com.notesxchange.notesxchange.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Get Bitmap of image
 */

public class ImageManager {

    private static final String TAG = "ImageManager";

    public static Bitmap getBitmap(String imgUrl) {
        File imageFile = new File(imgUrl);
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            try {
                fis = new FileInputStream(imageFile);
            }catch (FileNotFoundException e) {
                Log.e(TAG, "getBitmap: FileNotFoundException: " + e.getMessage());
                return null;
            }

            Log.d(TAG, "getBitmap: I'm here ********************");
            return BitmapFactory.decodeStream(fis);
            //bitmap = BitmapFactory.decodeStream(fis, options);

            //final BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inPreferredConfig = Bitmap.Config.RGB_565;
            //return BitmapFactory.decodeFile(imgUrl, options);

        } catch (OutOfMemoryError e) {

            Log.e(TAG, "getBitmap: OutOfMemoryError: " + e.getMessage());

            return null;

            // First decode with inJustDecodeBounds=true to check dimensions
            //final BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inJustDecodeBounds = true;
            //BitmapFactory.decodeFile(imgUrl, options);
            //BitmapFactory.decodeResource(mContext.getResources(), R.id.imageShare, options);

            //Log.d(TAG, "*************** HANDLING BITMAP *_*_*_*_*_*_*_*_*_**_*");

            // Calculate inSampleSize
            //options.inSampleSize = calculateInSampleSize(options, 3000, 4000);

            // Decode bitmap with inSampleSize set
            //options.inJustDecodeBounds = false;
            //options.inPreferredConfig = Bitmap.Config.ALPHA_8;
            //return BitmapFactory.decodeFile(imgUrl, options);
            //return BitmapFactory.decodeResource(mContext.getResources(), R.id.imageShare, options);


            /*final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ALPHA_8;
            return BitmapFactory.decodeFile(imgUrl, options);*/

        }
//        catch (FileNotFoundException e){
//            Log.e(TAG, "getBitmap: FileNotFoundException: " + e.getMessage() );
////        }finally {
//            try{
//                fis.close();
//            }catch (IOException e){
//                Log.e(TAG, "getBitmap: FileNotFoundException: " + e.getMessage() );
//            }
    }
//        return bitmap;
//}

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * return byte array from a bitmap
     * quality is greater than 0 but less than 100
     * @param bm
     * @param quality
     * @return
     */
    public static byte[] getBytesFromBitmap(Bitmap bm, int quality){
        Log.d(TAG, "getBytesFromBitmap: I have entered *_*_*_*_*_*_*_*_*_**_*");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        Log.d(TAG, "getBytesFromBitmap: I'm working fine *_*_*_*_*_*_*_*_*_**_*");
        return stream.toByteArray();
    }
}