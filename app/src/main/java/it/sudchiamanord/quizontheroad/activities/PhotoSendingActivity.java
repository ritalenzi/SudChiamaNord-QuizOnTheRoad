package it.sudchiamanord.quizontheroad.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

    private Button mTakePhoto;
    private Button mOpenPhoto;
    private ImageView mPhotoPreview;
    private Button mUploadPhoto;

    private Uri mPicUri;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);

//        mAppFolder = getIntent().getStringExtra (Tags.APP_FOLDER);
        mSessionKey = getIntent().getStringExtra (Tags.SESSION_KEY);
        mIdInd = getIntent().getStringExtra (Tags.ID_IND);

        mOpProgressDialog = new BarProgressDialog (PhotoSendingActivity.this);

        super.onCreate (savedInstanceState, SendDataOps.class);

        setContentView (R.layout.activity_photo);

        mPhotoPreview = (ImageView) findViewById (R.id.photoPreview);

        mTakePhoto = (Button) findViewById (R.id.takePhoto);
        mTakePhoto.setOnClickListener (new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                takePicture();
            }
        });

        mOpenPhoto = (Button) findViewById (R.id.openPhoto);
        mOpenPhoto.setOnClickListener (new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                openPicture();
            }
        });

        mUploadPhoto = (Button) findViewById (R.id.uploadPhoto);
        mUploadPhoto.setEnabled (false);
        mUploadPhoto.setVisibility (View.INVISIBLE);
        mUploadPhoto.setOnClickListener (new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                if ((photoName == null) || (photoAbsolutePath == null)) {
                    Toast.makeText (getApplicationContext(), R.string.noVideoRecorded,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

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
                        .setNegativeButton (R.string.noOption, new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                                finish();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }
        });
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
    public void onRequestPermissionsResult (int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case Tags.WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST:
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.length > 0) &&
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    photoAbsolutePath = Utils.createDirectory (Consts.appFolder);
                    takePicture();
                }
                else {
                    Toast.makeText (this, R.string.writeExternalStoragePermissionDenied, Toast.LENGTH_SHORT).show();
                }

                return;

            case Tags.READ_EXTERNAL_STORAGE_PERMISSION_REQUEST:
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.length > 0) &&
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    photoAbsolutePath = Utils.createDirectory (Consts.appFolder);
                    openPicture();
                }
                else {
                    Toast.makeText (this, R.string.readExternalStoragePermissionDenied, Toast.LENGTH_SHORT).show();
                }

                return;

            case Tags.CAMERA_PERMISSION_REQUEST:
                if ((grantResults.length > 0) &&
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    takePicture();
                }
                else {
                    Toast.makeText (this, R.string.cameraPermissionDenied, Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        super.onSaveInstanceState (outState);
        Log.e (TAG, "calling onSaveInstanceState() - picUri: " + mPicUri);

        outState.putParcelable (Tags.RESOURCE_URI, mPicUri);
    }

    @Override
    protected void onRestoreInstanceState (@NonNull Bundle savedInstanceState)
    {
        super.onRestoreInstanceState (savedInstanceState);
        Log.e (TAG, "calling onRestoreInstanceState()");

        mPicUri = savedInstanceState.getParcelable (Tags.RESOURCE_URI);
        Log.e (TAG, "picUri: " + mPicUri);

    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent intent)
    {
        if (resultCode != RESULT_OK) {
            return;
        }

        //Uri uri = intent.getData();
        Uri uri;
        if ((intent == null) || (intent.getData() == null)) {
            uri = mPicUri;
        }
        else {
            uri = intent.getData();
        }
        Log.i (TAG, uri.toString());    // TODO: REMOVE
        Log.i (TAG, uri.getPath());     // TODO: REMOVE

        File tempPhotoFile;

        switch (requestCode) {
            case IntentIds.CAPTURE_PHOTO_REQUEST:
            case IntentIds.OPEN_PHOTO_REQUEST:
                Cursor cursor = getContentResolver().query (uri, null, null, null, null, null);
                try {
                    if ((cursor != null) && (cursor.moveToFirst())) {
                        String displayName = cursor.getString (
                                cursor.getColumnIndex (OpenableColumns.DISPLAY_NAME));
                        Log.i (TAG, "Display Name: " + displayName);

                        tempPhotoFile = Utils.saveTempFile (displayName, this, uri);

                        if (!tempPhotoFile.exists()) {
                            Log.e (TAG, "The photo file does not exists");
                            return;
                        }
                        photoAbsolutePath = tempPhotoFile.getAbsolutePath();
                        photoName = tempPhotoFile.getName();

                        mUploadPhoto.setEnabled (true);
                        mUploadPhoto.setVisibility (View.VISIBLE);

                        Bitmap bMap = BitmapFactory.decodeFile (tempPhotoFile.getAbsolutePath());
                        //Bitmap bMap = MediaStore.Images.Media.getBitmap (this.getContentResolver(), uri);
                        mPhotoPreview.setImageBitmap (bMap);
                    }
                    else {
                        photoAbsolutePath = null;
                        photoName = null;
                        mPhotoPreview.setImageDrawable (null);

                        mUploadPhoto.setEnabled (false);
                        mUploadPhoto.setVisibility (View.INVISIBLE);
                    }
                }
                finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                break;
        }
    }

    private void takePicture()
    {
        if (ContextCompat.checkSelfPermission (this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions (this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Tags.WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST);
            return;
        }

        if (ContextCompat.checkSelfPermission (this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions (this, new String[]{Manifest.permission.CAMERA},
                    Tags.CAMERA_PERMISSION_REQUEST);
            return;
        }

        Intent takePictureIntent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile;
        try {
            photoFile = createImageFile (Consts.appFolder);
        }
        catch (IOException ex) {
            Log.e (TAG, "Error: ", ex);
            Toast.makeText (getApplicationContext(), R.string.sdcardError,
                    Toast.LENGTH_SHORT).show();
            return;
        }
//            photoName = photoFile.getName();
//            photoAbsolutePath = photoFile.getAbsolutePath();
        mPicUri = FileProvider.getUriForFile (this,
                getApplicationContext().getPackageName() + ".provider", photoFile);
        takePictureIntent.putExtra (MediaStore.EXTRA_OUTPUT, mPicUri);
        Log.i (TAG, "Photo URI: " + mPicUri);
        if (takePictureIntent.resolveActivity (getPackageManager()) != null) {
            startActivityForResult (takePictureIntent, IntentIds.CAPTURE_PHOTO_REQUEST);
        }
        else {
            Log.e (TAG, "Null \"Action Image Capture\" activity");
        }
    }

    private void openPicture()
    {
        if (ContextCompat.checkSelfPermission (this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions (this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Tags.READ_EXTERNAL_STORAGE_PERMISSION_REQUEST);
            return;
        }

        Intent intent = new Intent (Intent.ACTION_GET_CONTENT);
        intent.setType ("image/*");
        startActivityForResult (intent, IntentIds.OPEN_PHOTO_REQUEST);
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
