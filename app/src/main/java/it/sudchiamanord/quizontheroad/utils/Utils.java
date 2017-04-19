package it.sudchiamanord.quizontheroad.utils;

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
import java.io.OutputStream;
import java.util.Locale;


public class Utils
{
    private static final String TAG = Utils.class.getSimpleName();

    public static void createApplicationFolder()
    {
        File f = new File (Environment.getExternalStorageDirectory(), File.separator + Consts.appFolder);
        f.mkdirs();
        f = new File (Environment.getExternalStorageDirectory(), File.separator + Consts.appFolder +
                Consts.tempDir);
        f.mkdirs();
    }

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

        FileOutputStream os = null;
        try {
            os = new FileOutputStream (file);
            imageToSave.compress (Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
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
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File appDir = new File (storageDir.getAbsolutePath() + "/" + appFolder);
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        return appDir.getAbsolutePath();
    }

    public static File saveTempFile (String fileName, Context context, Uri uri)
    {

        File mFile = null;
        ContentResolver resolver = context.getContentResolver();
        InputStream in = null;
        FileOutputStream out = null;

        try {
            in = resolver.openInputStream (uri);

            mFile = new File (
                    Environment.getExternalStorageDirectory().getPath() + File.separator +
                            Consts.appFolder +
                            Consts.tempDir, fileName);

            out = new FileOutputStream (mFile, false);
            byte[] buffer = new byte[1024];

            int read;
            while ((read = in.read (buffer)) != -1) {
                out.write (buffer, 0, read);
            }

            out.flush();
        }
        catch (IOException e) {
            Log.e (TAG, "", e);
        }
        finally {
            safeClose (in);
            safeClose (out);
        }
        return mFile;
    }

    public static String convertStreamToString (InputStream is)
    {
        BufferedReader reader = new BufferedReader (new InputStreamReader (is));
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
            safeClose (is);
        }
        return sb.toString();
    }

    public static void safeClose (InputStream in)
    {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                Log.e(TAG, "", e);
            }
        }
    }

    public static void safeClose (OutputStream out)
    {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                Log.e(TAG, "", e);
            }
        }
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
