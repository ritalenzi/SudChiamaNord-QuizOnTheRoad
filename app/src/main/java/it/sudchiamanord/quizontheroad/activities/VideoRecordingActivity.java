package it.sudchiamanord.quizontheroad.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.stage.Test;
import it.sudchiamanord.quizontheroad.utils.BarProgressDialog;
import it.sudchiamanord.quizontheroad.utils.IntentIds;
import it.sudchiamanord.quizontheroad.utils.Tags;
import it.sudchiamanord.quizontheroad.utils.Utils;


public class VideoRecordingActivity extends SendingActivity
{
    private static final String TAG = "VideoRecordingActivity";

    private static final String VIDEO_TYPE = "video/mp4";
    private static final String VIDEO_EXTENSION = ".mp4";

    private String mFilePath = null;
    private String mFileName = null;
    private String mAppFolder;

    private Button mPlayVideo;
    private Button mRecordVideo;
    private Button mUploadVideo;
    private String mSessionKey;
    private String mIdInd;  // Id of the current clue


    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);
        super.onCreate (savedInstanceState);

        setContentView (R.layout.activity_video_recording);

        mOpProgressDialog = new BarProgressDialog (VideoRecordingActivity.this);

        mAppFolder = getIntent().getStringExtra (Tags.APP_FOLDER);
        mSessionKey = getIntent().getStringExtra (Tags.SESSION_KEY);
        mIdInd = getIntent().getStringExtra (Tags.ID_IND);

        mPlayVideo = (Button) findViewById (R.id.playVideo);
        mPlayVideo.setEnabled (false);
        mPlayVideo.setOnClickListener (new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {

                if ((mFilePath == null) || (mFileName == null)) {
                    Toast.makeText (getApplicationContext(), R.string.noVideoRecorded,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent (getApplicationContext(), VideoPlayerActivity.class);
                intent.putExtra (Tags.RESOURCE_ABSOLUTE_PATH, mFilePath);
                startActivity (intent);
//                Intent intent = new Intent (Intent.ACTION_VIEW);
//                Uri data = Uri.parse (mFilePath);
//                intent.setDataAndType (data, VIDEO_TYPE);
//                startActivity (intent);
            }
        });

        mRecordVideo = (Button) findViewById (R.id.recordVideo);
        mRecordVideo.setOnClickListener (new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                if (!hasPermissions()) {
                    Toast.makeText (getApplicationContext(),
                            R.string.writeExternalStoragePermissionDenied, Toast.LENGTH_SHORT).show();
                    return;
                }

                recordVideo();
            }
        });

        mUploadVideo = (Button) findViewById (R.id.uploadVideo);
        mUploadVideo.setEnabled (false);
        mUploadVideo.setVisibility (View.INVISIBLE);
        mUploadVideo.setOnClickListener (new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                if ((mFilePath == null) || (mFileName == null)) {
                    Toast.makeText (getApplicationContext(), R.string.noVideoRecorded,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder (VideoRecordingActivity.this);
                builder.setMessage (R.string.confirmVideoUpload)
                        .setCancelable (false)
                        .setPositiveButton (R.string.yesOption, new DialogInterface.OnClickListener()
                        {
                            public void onClick (final DialogInterface dialog, final int id)
                            {
                                getOps().sendData (mSessionKey, mFileName, mFilePath, mIdInd, Test.video);
                            }
                        })
                        .setNegativeButton (R.string.noOption, new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }
        });

        if (!hasPermissions()) {
            Toast.makeText (getApplicationContext(),
                    R.string.writeExternalStoragePermissionDenied, Toast.LENGTH_SHORT).show();
            return;
        }

        recordVideo();

//        if (takeVideoIntent.resolveActivity (getPackageManager()) != null) {
//            File videoFile = null;
//            try {
//                videoFile = createImageFile (appFolder);
//            }
//            catch (IOException ex) {
//                Log.e (TAG, "Error: ", ex);
//                Toast.makeText (getApplicationContext(), "Problem in saving data on the SD card",
//                        Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if (photoFile != null) {
//                //photoName = photoFile.getName().substring (0, photoFile.getName().lastIndexOf ("."));
//                photoName = photoFile.getName();
//                photoAbsolutePath = photoFile.getAbsolutePath();
//                takePictureIntent.putExtra (MediaStore.EXTRA_OUTPUT, Uri.fromFile (photoFile));
//                startActivityForResult (takePictureIntent, IntentIds.REQUEST_TAKE_PHOTO);
//            }
//        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        getMenuInflater().inflate (R.menu.info_action, menu);
        getMenuInflater().inflate (R.menu.settings_actions, menu);

        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        Intent intent;
        switch (item.getItemId()) {

            case R.id.action_logout:
                intent = new Intent(this, LogoutActivity.class);
                intent.putExtra (Tags.SESSION_KEY, mSessionKey);
                startActivity (intent);
                break;

            case R.id.action_info:
                intent = new Intent(this, InfoActivity.class);
                startActivity (intent);
                break;
        }

        return true;
    }

    @Override
    public void onRestoreInstanceState (Bundle savedInstanceState)
    {
        super.onRestoreInstanceState (savedInstanceState);

        mFilePath = savedInstanceState.getString (Tags.RESOURCE_ABSOLUTE_PATH);
        mFileName = savedInstanceState.getString (Tags.RESOURCE_NAME);
    }

    @Override
    public void onSaveInstanceState (Bundle savedInstanceState)
    {
        savedInstanceState.putString (Tags.RESOURCE_ABSOLUTE_PATH, mFilePath);
        savedInstanceState.putString (Tags.RESOURCE_NAME, mFileName);

        super.onSaveInstanceState (savedInstanceState);
    }

    private void recordVideo()
    {
        Intent captureVideoIntent = new Intent (MediaStore.ACTION_VIDEO_CAPTURE);
        captureVideoIntent.putExtra (MediaStore.Video.Media.MIME_TYPE, VIDEO_TYPE);
        captureVideoIntent.putExtra (MediaStore.EXTRA_VIDEO_QUALITY, 0);

        File videoFile = null;
        try {
            videoFile = createEmptyVideoFile (mAppFolder);
        }
        catch (IOException e) {
            Log.e (TAG, "Impossible to save video", e);
            Toast.makeText (this, R.string.errorSavingVideo, Toast.LENGTH_SHORT).show();
            mFilePath = null;
            mFileName = null;
            return;
        }

        mFilePath = videoFile.getAbsolutePath();
        mFileName = videoFile.getName();
//        captureVideoIntent.putExtra (MediaStore.EXTRA_OUTPUT, Uri.fromFile (videoFile));
        Uri videoURI = FileProvider.getUriForFile (this,
                getApplicationContext().getPackageName() + ".provider", videoFile);
        captureVideoIntent.putExtra (MediaStore.EXTRA_OUTPUT, videoURI);
        if (captureVideoIntent.resolveActivity (getPackageManager()) != null) {
            startActivityForResult (captureVideoIntent, IntentIds.CAPTURE_VIDEO_REQUEST);
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent intent)
    {
        if (requestCode == IntentIds.CAPTURE_VIDEO_REQUEST) {
            if (resultCode == RESULT_OK) {
                mPlayVideo.setEnabled (true);
                mUploadVideo.setEnabled (true);
                mUploadVideo.setVisibility (View.VISIBLE);
            }
            else {
                mFilePath = null;
                mFileName = null;

                mPlayVideo.setEnabled (false);
                mUploadVideo.setEnabled (false);
                mUploadVideo.setVisibility (View.INVISIBLE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case Tags.WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST:
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.length > 0) &&
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mFilePath = Utils.createDirectory (mAppFolder);
                }
                else {
                    Toast.makeText (this, R.string.writeExternalStoragePermissionDenied, Toast.LENGTH_SHORT).show();
                }

                return;

            case Tags.READ_EXTERNAL_STORAGE_PERMISSION_REQUEST:
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.length > 0) &&
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mFilePath = Utils.createDirectory (mAppFolder);
                }
                else {
                    Toast.makeText (this, R.string.readExternalStoragePermissionDenied, Toast.LENGTH_SHORT).show();
                }

                return;

            case Tags.CAMERA_PERMISSION_REQUEST:
                if ((grantResults.length > 0) &&
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    recordVideo();
                }
                else {
                    Toast.makeText (this, R.string.cameraPermissionDenied, Toast.LENGTH_SHORT).show();
                }
        }
    }

    private boolean hasPermissions()
    {
        if (ContextCompat.checkSelfPermission (this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions (this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Tags.WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST);
            return false;
        }

        if (ContextCompat.checkSelfPermission (this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions (this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Tags.READ_EXTERNAL_STORAGE_PERMISSION_REQUEST);
            return false;
        }

        if (ContextCompat.checkSelfPermission (this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions (this, new String[]{Manifest.permission.CAMERA},
                    Tags.CAMERA_PERMISSION_REQUEST);
            return false;
        }

        return true;
    }

    private File createEmptyVideoFile (String appFolder) throws IOException
    {
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format (new Date()) + VIDEO_EXTENSION;
        File dstFile = new File (Utils.createDirectory (appFolder), fileName);
        dstFile.createNewFile();
        Log.d (TAG, "File text length " + dstFile.length() + " path " + dstFile.getAbsolutePath());

        return dstFile;
    }

    private File createVideoFile (String appFolder, File originalVideoFile) throws IOException
    {
        String fileName = new SimpleDateFormat ("yyyyMMdd_HHmmss").format (new Date()) + VIDEO_EXTENSION;
        File dstFile = new File (Utils.createDirectory (appFolder), fileName);
        dstFile.createNewFile();

        InputStream in = new FileInputStream (originalVideoFile);
        OutputStream out = new FileOutputStream (dstFile);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write (buf, 0, len);
        }
        in.close();
        out.close();

        Log.d (TAG, "File text length " + dstFile.length() + " path " + dstFile.getAbsolutePath());

        return dstFile;
    }

    public void notifyProgressUpdate (int progress, int dialogTitle, int dialogExpl)
    {
        mOpProgressDialog.updateProgressDialog (progress, dialogTitle, dialogExpl);
    }

    @Override
    public void dismissDialog()
    {
        mOpProgressDialog.dismiss();
    }

    @Override
    public void uploadFinished (boolean success, String toastMessage)
    {
        Toast.makeText (getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
        Intent resIntent = new Intent();
        // if the upload was successful we want to go back to the StageActivities,
        // otherwise we want to stay in the SingleStageActivity
        setResult (success ? RESULT_OK : RESULT_CANCELED, resIntent);
        finish();
    }

    public void sessionExpired()
    {
        Toast.makeText (this, R.string.noServerSession, Toast.LENGTH_SHORT).show();
        startActivity (new Intent(this, LoginActivity.class));
    }
}
