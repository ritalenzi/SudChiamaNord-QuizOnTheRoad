package it.sudchiamanord.quizontheroad.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.stage.Test;
import it.sudchiamanord.quizontheroad.utils.BarProgressDialog;
import it.sudchiamanord.quizontheroad.utils.Consts;
import it.sudchiamanord.quizontheroad.utils.Tags;
import it.sudchiamanord.quizontheroad.utils.Utils;


public class TextSendingActivity extends SendingActivity
{
    private static final String TAG = TextSendingActivity.class.getSimpleName();

//    private String mAppFolder;
    private static String mFilePath = null;
    private static String mFileName = null;
    private String mSessionKey;
    private String mIdInd;  // Id of the current clue

    private EditText mMessage = null;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);

//        mAppFolder = getIntent().getStringExtra (Tags.APP_FOLDER);
        mSessionKey = getIntent().getStringExtra (Tags.SESSION_KEY);
        mIdInd = getIntent().getStringExtra (Tags.ID_IND);

        setContentView (R.layout.activity_text_sending);

        mOpProgressDialog = new BarProgressDialog (TextSendingActivity.this);

        mMessage = (EditText) findViewById (R.id.messageEditText);

        Button uploadBtn = (Button) findViewById (R.id.uploadTextButton);
        uploadBtn.setOnClickListener (new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                if ((mMessage.getText() == null) || (mMessage.getText().toString().isEmpty())) {
                    Toast.makeText (getApplicationContext(), R.string.emptyText, Toast.LENGTH_LONG).show();
                    return;
                }

                if (!hasPermissions()) {
                    Toast.makeText (getApplicationContext(),
                            R.string.writeExternalStoragePermissionDenied, Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    File f = createTextFile (Consts.appFolder, mMessage.getText().toString());
                    mFilePath = f.getAbsolutePath();
                    mFileName = f.getName();
                }
                catch (IOException e) {
                    Toast.makeText (getApplicationContext(), R.string.savingTextFailed,
                            Toast.LENGTH_LONG).show();
                    Log.e (TAG, "Problem:", e);
                    return;
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder (TextSendingActivity.this);
                builder.setMessage (R.string.confirmTextUpload)
                        .setCancelable (false)
                        .setPositiveButton (R.string.yesOption, new DialogInterface.OnClickListener()
                        {
                            public void onClick (final DialogInterface dialog, final int id)
                            {
                                getOps().sendData (mSessionKey, mFileName, mFilePath, mIdInd,
                                        Test.text);
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
                    mFilePath = Utils.createDirectory (Consts.appFolder);
                }
                else {
                    Toast.makeText (this, R.string.writeExternalStoragePermissionDenied, Toast.LENGTH_SHORT).show();
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

        return true;
    }

    private File createTextFile (String appFolder, String text) throws IOException
    {
        String fileName = new SimpleDateFormat ("yyyyMMdd_HHmmss").format (new Date()) + ".txt";
        File file = new File (Utils.createDirectory (appFolder), fileName);
        file.createNewFile();
        OutputStreamWriter os = new OutputStreamWriter (new FileOutputStream(file));
        os.write (text);
        os.close();

        Log.d (TAG, "File text length " + file.length() + " path " + file.getAbsolutePath());

        return file;
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
