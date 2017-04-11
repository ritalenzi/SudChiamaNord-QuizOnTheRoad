package it.sudchiamanord.quizontheroad.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.operations.SendDataOps;
import it.sudchiamanord.quizontheroad.stage.Test;
import it.sudchiamanord.quizontheroad.utils.BarProgressDialog;
import it.sudchiamanord.quizontheroad.utils.Consts;
import it.sudchiamanord.quizontheroad.utils.IntentIds;
import it.sudchiamanord.quizontheroad.utils.Tags;
import it.sudchiamanord.quizontheroad.utils.Utils;

public class PhotoSendingActivity extends SendingActivity
{
    private static final String TAG = "PhotoSendingActivity";

    //private String mAppFolder;
    private String photoName;
    private String photoAbsolutePath;
    private String mSessionKey;
    private String mIdInd;  // Id of the current clue

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);

//        mAppFolder = getIntent().getStringExtra (Tags.APP_FOLDER);
        mSessionKey = getIntent().getStringExtra (Tags.SESSION_KEY);
        mIdInd = getIntent().getStringExtra (Tags.ID_IND);

        mOpProgressDialog = new BarProgressDialog (PhotoSendingActivity.this);

        super.onCreate (savedInstanceState, SendDataOps.class);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity (getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile (Consts.appFolder);
            }
            catch (IOException ex) {
                Log.e (TAG, "Error: ", ex);
                Toast.makeText (getApplicationContext(), "Problem in saving data on the SD card",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (photoFile != null) {
                //photoName = photoFile.getName().substring (0, photoFile.getName().lastIndexOf ("."));
                photoName = photoFile.getName();
                photoAbsolutePath = photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, IntentIds.REQUEST_TAKE_PHOTO);
            }
        }
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
    protected void onActivityResult (int requestCode, int resultCode, Intent intent)
    {
        if (requestCode == IntentIds.REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                final AlertDialog.Builder builder = new AlertDialog.Builder (PhotoSendingActivity.this);
                builder.setMessage (R.string.confirmPhotoUpload)
                        .setCancelable (false)
                        .setPositiveButton (R.string.yesOption, new DialogInterface.OnClickListener()
                        {
                            public void onClick (final DialogInterface dialog, final int id)
                            {
                                compressImage();
                                getOps().sendData (mSessionKey, photoName, photoAbsolutePath, mIdInd,
                                        Test.photo);
                            }
                        })
                        .setNegativeButton(R.string.noOption, new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                                finish();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private File createImageFile (String appFolder) throws IOException
    {
        String imageFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return File.createTempFile (imageFileName, ".jpg", new File (Utils.createDirectory (
                appFolder)));
    }

    private void compressImage()
    {
        try {
            FileInputStream inputStream = new FileInputStream(photoAbsolutePath);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            int totalBytes = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                totalBytes += bytesRead;
            }
            inputStream.close();
            Log.d (TAG, "Total bytes for original image: " + totalBytes);
        }
        catch (IOException e) {
            Log.e (TAG, "Impossible to compute the image size", e);
            return;
        }

        Bitmap bmpPic = BitmapFactory.decodeFile (photoAbsolutePath);
        if ((bmpPic.getWidth() >= 2000) && (bmpPic.getHeight() >= 2000)) {
            BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
            bmpOptions.inSampleSize = 1;
            while ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
                bmpOptions.inSampleSize++;
                bmpPic = BitmapFactory.decodeFile (photoAbsolutePath, bmpOptions);
            }
            Log.d (TAG, "Resize: " + bmpOptions.inSampleSize);
        }

        ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
        bmpPic.compress (Bitmap.CompressFormat.JPEG, 50, bmpStream);
        byte[] bmpPicByteArray = bmpStream.toByteArray();
        Log.d (TAG, "Compressed image size: " + bmpPicByteArray.length);

        String compressedImage = photoAbsolutePath.replace (".jpg", "_compressed.jpg");
        try {
            FileOutputStream bmpFile = new FileOutputStream(compressedImage);
            bmpPic.compress (Bitmap.CompressFormat.JPEG, 50, bmpFile);
            bmpFile.flush();
            bmpFile.close();
        }
        catch (IOException e) {
            Log.d (TAG, "Impossible to save the compressed image", e);
            return;
        }
        photoAbsolutePath = compressedImage;
        photoName = photoName.replace (".jpg", "_compressed.jpg");
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
