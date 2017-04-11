package it.sudchiamanord.quizontheroad.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.operations.SingleStageOps;
import it.sudchiamanord.quizontheroad.operations.results.PositionResult;
import it.sudchiamanord.quizontheroad.operations.results.SkipClueResult;
import it.sudchiamanord.quizontheroad.services.CheckPositionService;
import it.sudchiamanord.quizontheroad.stage.Status;
import it.sudchiamanord.quizontheroad.stage.Test;
import it.sudchiamanord.quizontheroad.utils.GPSClient;
import it.sudchiamanord.quizontheroad.utils.GenericActivity;
import it.sudchiamanord.quizontheroad.utils.IntentIds;
import it.sudchiamanord.quizontheroad.utils.RingProgressDialog;
import it.sudchiamanord.quizontheroad.utils.Tags;
import it.sudchiamanord.quizontheroad.utils.Utils;

public class SingleStageActivity extends GenericActivity<SingleStageOps>
{
    public static final String TAG = SingleStageActivity.class.getSimpleName();

    private Button mSendPositionBtn;
    private Button mSkipClue;
    private Button mSendAnswerBtn;

    private Test mStageTest;

    private RingProgressDialog mOpProgressDialog;

    private String mSessionKey;
    private String mIdInd;  // Id of the current clue
    private boolean mWaitPositionConfirmed;


    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView (R.layout.activity_single_stage);

        mOpProgressDialog = new RingProgressDialog (SingleStageActivity.this);
        TextView stageNumberTitle = (TextView) findViewById (R.id.stageNumberTitle);
        TextView clueText = (TextView) findViewById (R.id.clueText);
        clueText.setClickable (true);
        clueText.setMovementMethod(LinkMovementMethod.getInstance());

        mSendPositionBtn = (Button) findViewById (R.id.sendPositionBtn);
        mSendAnswerBtn = (Button) findViewById (R.id.sendAnswerBtn);
        mSkipClue = (Button) findViewById (R.id.skipClueBtn);

        Intent intent = getIntent();
        mSessionKey = intent.getStringExtra (Tags.SESSION_KEY);
        mIdInd = getIntent().getStringExtra (Tags.ID_IND);
        int stageNumber = intent.getIntExtra (Tags.STAGE_NUMBER, 0);
        String stageLocationClue = intent.getStringExtra (Tags.STAGE_LOCATION_CLUE);
        String stageMultimediaClue = intent.getStringExtra (Tags.STAGE_MULTIMEDIA_CLUE);
        mStageTest = Test.valueOf (intent.getStringExtra (Tags.STAGE_TEST));
        Status status = Status.getStatusFromServerValue (intent.getIntExtra (Tags.STAGE_STATUS, 0));
        mWaitPositionConfirmed = intent.getBooleanExtra (Tags.STAGE_WAIT_POSITION_CONFIRMED, false);

        stageNumberTitle.setText (getString (R.string.stageNumberTitle) + " " + (stageNumber + 1));
        Log.d (TAG, "STATUS: " + status);

        switch (status) {
            case current:
                mSendPositionBtn.setEnabled (true);
                mSkipClue.setEnabled (true);
                mSendAnswerBtn.setEnabled (false);
                if (mWaitPositionConfirmed) {
                    clueText.setText (Html.fromHtml (stageLocationClue));
                }
                else {
                    StringBuilder sb = new StringBuilder();
                    if ((stageLocationClue != null) && (!stageLocationClue.equals (""))) {
                        sb.append (stageLocationClue);
                        sb.append ("<br><br>");
                    }
                    sb.append (stageMultimediaClue);
                    clueText.setText (Html.fromHtml (sb.toString()));
                }
                break;

            case current_wrong_position:
                mSendPositionBtn.setEnabled (true);
                mSkipClue.setEnabled (true);
                mSendAnswerBtn.setEnabled(false);
                clueText.setText (Html.fromHtml (stageLocationClue + "<br><br>" +
                        this.getString (R.string.wrongPosition)));
                break;

            case current_position_sent:
                mSendPositionBtn.setEnabled (false);
                if (mWaitPositionConfirmed) {
                    mSkipClue.setEnabled (false);
                    mSendAnswerBtn.setEnabled (false);
                    clueText.setText (Html.fromHtml (stageLocationClue + "<br><br>" +
                            this.getString (R.string.checkingPosition)));
                }
                else {
                    mSkipClue.setEnabled (true);
                    mSendAnswerBtn.setEnabled (true);
                    clueText.setText (Html.fromHtml (stageLocationClue));
                }

                startCheckPositionService();
                break;

            case current_position_confirmed:
                mSendPositionBtn.setEnabled (false);
                mSkipClue.setEnabled (true);
                mSendAnswerBtn.setEnabled (true);
                clueText.setText (Html.fromHtml (stageMultimediaClue));
                break;

            case current_position_update_failed:
                mSendPositionBtn.setEnabled (true);
                mSkipClue.setEnabled (true);
                mSendAnswerBtn.setEnabled (false);
                clueText.setText (Html.fromHtml (stageLocationClue + "<br><br>" +
                        this.getString (R.string.positionUpdateFailed)));
                break;
        }

        super.onCreate (savedInstanceState, SingleStageOps.class);
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
            case Tags.READ_LOCATION_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.length > 0) &&
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mSendPositionBtn.setEnabled (false);
                    getOps().sendPosition (mSessionKey, mIdInd);
                }
                else {
                    Toast.makeText (this, R.string.readLocationPermissionDenied, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void sendPosition (View view)
    {
        if ((mSessionKey == null) || (mIdInd == null)) {
            startActivity (new Intent(this, LoginActivity.class));
            finish();
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setMessage (R.string.sendPositionConfirmationDialog)
                .setCancelable (false)
                .setPositiveButton (R.string.yesOption, new DialogInterface.OnClickListener()
                {
                    public void onClick (final DialogInterface dialog, final int id)
                    {
                        // TODO: put this in all the activities
                        if (!GPSClient.isGPSEnabled (SingleStageActivity.this)) {
                            GPSClient.showEnableDialog (SingleStageActivity.this);
                        }
                        else {
                            if (ContextCompat.checkSelfPermission (SingleStageActivity.this,
                                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                                    PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions (SingleStageActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        Tags.READ_LOCATION_PERMISSION_REQUEST);
                                return;
                            }

                            mSendPositionBtn.setEnabled (false);
                            getOps().sendPosition (mSessionKey, mIdInd);
                        }
                    }
                })
                .setNegativeButton (R.string.noOption, new DialogInterface.OnClickListener() {
                    public void onClick (final DialogInterface dialog, final int id)
                    {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void notifyProgressUpdate (int progress, int dialogTitle, int dialogExpl)
    {
        mOpProgressDialog.updateProgressDialog (progress, dialogTitle, dialogExpl);
    }

    public void positionUpdateFinished (PositionResult positionResult)
    {
        if (positionResult == null) {
            positionUpdateFailed (R.string.generalServerError);
            return;
        }

        if (!positionResult.isSessionCorrect()) {
            sessionExpired();
            return;
        }

        if (positionResult.isSuccessful()) {
            Toast.makeText (this, R.string.positionCorrectlyUpdated, Toast.LENGTH_SHORT).show();
            mSendPositionBtn.setEnabled (false);
            if (!mWaitPositionConfirmed) {
                mSendAnswerBtn.setEnabled (true);
            }
            else {
                Toast.makeText (this, R.string.checkingPosition, Toast.LENGTH_SHORT).show();
                startCheckPositionService();
            }
        }
        else {
            positionUpdateFailed (positionResult.getMessage());
        }

        finish();
    }

    public void positionUpdateFailed (int errorString)
    {
        Toast.makeText (this, errorString, Toast.LENGTH_SHORT).show();
        mSendPositionBtn.setEnabled (true);
    }

    private void startCheckPositionService()
    {
        if (Utils.isServiceRunning (this, CheckPositionService.class)) {
            return;
        }

        Intent serviceIntent = new Intent (this, CheckPositionService.class);
        serviceIntent.putExtra (Tags.SESSION_KEY, mSessionKey);
        serviceIntent.putExtra (Tags.ID_IND, mIdInd);
        startService (serviceIntent);

//        CheckPositionTask checkPositionTask = CheckPositionTask.getInstance();
//        if (checkPositionTask.isRunning (mSessionKey, mIdInd)) {
//            Log.d (TAG, "The CheckPositionTask is already running with the correct session key and clue id");
//            return;
//        }
//
//        checkPositionTask.updateSessionKey (mSessionKey);
//        checkPositionTask.updateServerClueId (mIdInd);
//        checkPositionTask.setContext (getApplicationContext());
//        checkPositionTask.start();
    }

    public void skipClue (View view)
    {
        if ((mSessionKey == null) || (mIdInd == null)) {
            startActivity (new Intent(this, LoginActivity.class));
            finish();
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setMessage (R.string.skipClueConfirmationDialog)
                .setCancelable (false)
                .setPositiveButton (R.string.yesOption, new DialogInterface.OnClickListener()
                {
                    public void onClick (final DialogInterface dialog, final int id)
                    {
                        getOps().skipClue (mSessionKey, mIdInd);
                    }
                })
                .setNegativeButton (R.string.noOption, new DialogInterface.OnClickListener() {
                    public void onClick (final DialogInterface dialog, final int id)
                    {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void skipClueFinished (SkipClueResult skipClueResult)
    {
        if (skipClueResult == null) {
            Toast.makeText (this, R.string.generalServerError, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!skipClueResult.isSessionCorrect()) {
            sessionExpired();
            return;
        }

        if (skipClueResult.isSuccessful()) {
            Toast.makeText (this, R.string.skipClueSucceded, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText (this, skipClueResult.getMessage(), Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void sessionExpired()
    {
        Toast.makeText (this, R.string.noServerSession, Toast.LENGTH_SHORT).show();
        startActivity (new Intent(this, LoginActivity.class));
    }

    public void sendMultimedia (View view)
    {
        if ((mSessionKey == null) || (mIdInd == null)) {
            startActivity (new Intent(this, LoginActivity.class));
            finish();
        }

        switch (mStageTest) {
            case photo:
                Intent cameraIntent = new Intent (getApplicationContext(), PhotoSendingActivity.class);
                //cameraIntent.putExtra (Tags.APP_FOLDER, getString (R.string.app_folder));
                cameraIntent.putExtra (Tags.SESSION_KEY, mSessionKey);
                cameraIntent.putExtra (Tags.ID_IND, mIdInd);
                startActivityForResult (cameraIntent, IntentIds.REQUEST_CAMERA_INTENT);
                break;

            case audio:
                Intent audioIntent = new Intent (getApplicationContext(), AudioRecordingActivity.class);
                //audioIntent.putExtra (Tags.APP_FOLDER, getString (R.string.app_folder));
                audioIntent.putExtra (Tags.SESSION_KEY, mSessionKey);
                audioIntent.putExtra (Tags.ID_IND, mIdInd);
                startActivityForResult (audioIntent, IntentIds.REQUEST_AUDIO_INTENT);
                break;

            case text:
                Intent textIntent = new Intent (getApplicationContext(), TextSendingActivity.class);
                //textIntent.putExtra (Tags.APP_FOLDER, getString (R.string.app_folder));
                textIntent.putExtra (Tags.SESSION_KEY, mSessionKey);
                textIntent.putExtra (Tags.ID_IND, mIdInd);
                startActivityForResult (textIntent, IntentIds.REQUEST_TEXT_INTENT);
                break;

            case video:
                Intent videoIntent = new Intent (getApplicationContext(), VideoRecordingActivity.class);
                //videoIntent.putExtra (Tags.APP_FOLDER, getString (R.string.app_folder));
                videoIntent.putExtra (Tags.SESSION_KEY, mSessionKey);
                videoIntent.putExtra (Tags.ID_IND, mIdInd);
                startActivityForResult (videoIntent, IntentIds.REQUEST_VIDEO_INTENT);
                break;
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent intent)
    {
        // if the upload was successful we want to go back to the StageActivities,
        // otherwise we want to stay in the SingleStageActivity
        if (resultCode == RESULT_OK) {
            Log.i (TAG, "The upload was successful");
            finish();
        }
    }

//    public void checkPosition (View view)
//    {
//        startCheckPositionService();
//    }
}
