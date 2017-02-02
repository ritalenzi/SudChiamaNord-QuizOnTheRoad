package nord.chiama.sud.caccia.utils;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;


public class Utils
{
    private static final String TAG = Utils.class.getSimpleName();

    public static Uri createDirectoryAndSaveFile (Context context, Bitmap imageToSave,
                                                  String fileName, String directoryPathname)
    {
        if (imageToSave == null) {
            return null;
        }

        File directory = new File (directoryPathname);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File (directory, getTemporaryFilename (fileName));
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream outputStream = new FileOutputStream (file);
            imageToSave.compress (Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
        }
        catch (Exception e) {
            Log.e (TAG, "Error in saving the image on the device", e);
            return null;
        }

        String absolutePathToImage = file.getAbsolutePath();

        // Provide metadata so the downloaded image is viewable in the Gallery.
        ContentValues values = new ContentValues();
        values.put (Images.Media.TITLE, fileName);
        values.put (Images.Media.DESCRIPTION, fileName);
        values.put (Images.Media.DATE_TAKEN, System.currentTimeMillis ());
        values.put (Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getName().toLowerCase (Locale.US));
        values.put ("_data", absolutePathToImage);

        ContentResolver cr = context.getContentResolver();

        // Store the metadata for the image into the Gallery.
        cr.insert (MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Log.d (TAG, "Absolute path to image file is " + absolutePathToImage);

        return Uri.parse (absolutePathToImage);
    }

    static private String getTemporaryFilename (final String url)
    {
        return Base64.encodeToString (url.getBytes(), Base64.NO_WRAP);
    }

    public static String createDirectory (String appFolder)
    {
        File storageDir = Environment.getExternalStorageDirectory();
        storageDir.mkdirs();
        File appDir = new File (storageDir.getAbsolutePath() + "/" + appFolder);
        appDir.mkdir();

        return appDir.getAbsolutePath();
    }

    public static String convertStreamToString (InputStream is)
    {
        BufferedReader reader = new BufferedReader (new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append (line).append ('\n');
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static boolean isServiceRunning (Context context, Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) context.getSystemService (Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices (Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals (service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
