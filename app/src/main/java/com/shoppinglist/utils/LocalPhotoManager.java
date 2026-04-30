package com.shoppinglist.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class LocalPhotoManager {
    private static final String TAG = "LocalPhotoManager";
    private static final String PHOTO_DIR = "profile_photos";
    
    /**
     * Save photo to local storage
     * @param context Application context
     * @param userId User ID
     * @param imageUri URI of the image to save
     * @return Local file path or null if failed
     */
    public static String savePhotoLocally(Context context, String userId, Uri imageUri) {
        try {
            // Create directory if it doesn't exist
            File photoDir = new File(context.getFilesDir(), PHOTO_DIR);
            if (!photoDir.exists()) {
                photoDir.mkdirs();
            }
            
            // Create file for this user
            File photoFile = new File(photoDir, userId + ".jpg");
            
            // Load and compress image
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }
            
            if (bitmap == null) {
                Log.e(TAG, "Failed to decode image");
                return null;
            }
            
            // Compress and save
            FileOutputStream outputStream = new FileOutputStream(photoFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
            outputStream.flush();
            outputStream.close();
            bitmap.recycle();
            
            String localPath = photoFile.getAbsolutePath();
            Log.d(TAG, "Photo saved locally: " + localPath);
            return localPath;
            
        } catch (Exception e) {
            Log.e(TAG, "Error saving photo locally", e);
            return null;
        }
    }
    
    /**
     * Get local photo file
     * @param context Application context
     * @param userId User ID
     * @return File object or null if doesn't exist
     */
    public static File getLocalPhoto(Context context, String userId) {
        File photoDir = new File(context.getFilesDir(), PHOTO_DIR);
        File photoFile = new File(photoDir, userId + ".jpg");
        return photoFile.exists() ? photoFile : null;
    }
    
    /**
     * Delete local photo
     * @param context Application context
     * @param userId User ID
     * @return true if deleted successfully
     */
    public static boolean deleteLocalPhoto(Context context, String userId) {
        File photoFile = getLocalPhoto(context, userId);
        if (photoFile != null) {
            return photoFile.delete();
        }
        return false;
    }
    
    /**
     * Check if local photo exists
     * @param context Application context
     * @param userId User ID
     * @return true if photo exists locally
     */
    public static boolean hasLocalPhoto(Context context, String userId) {
        return getLocalPhoto(context, userId) != null;
    }
}
