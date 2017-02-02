package nord.chiama.sud.caccia.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.UUID;

import nord.chiama.sud.caccia.R;
import nord.chiama.sud.caccia.utils.BarProgressDialog;
import nord.chiama.sud.caccia.utils.Tags;
import nord.chiama.sud.caccia.utils.Utils;
import nord.chiama.sud.caccia.stage.Test;

public class AudioRecordingActivity extends SendingActivity
{
    private static final String TAG = "AudioRecordingActivity";
    
    private ToggleButton mRecordButton = null;
    private boolean mStartRecording = true;
    private MediaRecorder mRecorder = null;
//    private boolean mReadyForUpload = false;

    private SeekBar mSeekBar = null;
    private Button mPlayButton = null;
    private Button mStopButton = null;
    boolean mStartPlaying = true;
    private MediaPlayer mPlayer = null;

    private Button mUploadAudio = null;

    private static String mFilePath = null;
    private static String mFileName = null;
    private String mSessionKey;
    private String mIdInd;  // Id of the current clue

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);

        String appFolder = getIntent().getStringExtra (Tags.APP_FOLDER);
        mFilePath = Utils.createDirectory(appFolder);
        mSessionKey = getIntent().getStringExtra (Tags.SESSION_KEY);
        mIdInd = getIntent().getStringExtra (Tags.ID_IND);

        setContentView (R.layout.activity_audio_recording);

        mOpProgressDialog = new BarProgressDialog (AudioRecordingActivity.this);

        mRecordButton = (ToggleButton) findViewById (R.id.startRecordingBtn);
//        mRecordButton.setText (R.string.recordAudioButtonStart);
        mRecordButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                onRecord (mStartRecording);
                mStartRecording = !mStartRecording;

            }
        });
//        mRecordButton.setOnClickListener (new View.OnClickListener()
//        {
//            public void onClick (View v)
//            {
//                onRecord (mStartRecording);
//                if (mStartRecording) {
//                    mRecordButton.setText (R.string.recordAudioButtonStop);
//                }
//                else {
//                    mRecordButton.setText (R.string.recordAudioButtonStart);
//                }
//                mStartRecording = !mStartRecording;
//            }
//        });

        mSeekBar = (SeekBar) findViewById (R.id.playRecordingSeekBar);
        mSeekBar.setOnSeekBarChangeListener (new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser)
            {
                if ((mPlayer != null) && (fromUser)) {
                    mPlayer.seekTo (progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch (SeekBar seekBar)
            {
            }

            @Override
            public void onStopTrackingTouch (SeekBar seekBar)
            {
                mPlayButton.setEnabled (true);
                mStopButton.setEnabled (false);
            }
        });

        mPlayButton = (Button) findViewById (R.id.playButton);
        mPlayButton.setOnClickListener (new View.OnClickListener()
        {
            public void onClick (View v)
            {
                onPlay (mStartPlaying);
                mStartPlaying = !mStartPlaying;
                mPlayButton.setEnabled (false);
                mStopButton.setEnabled (true);
            }
        });
        mPlayButton.setEnabled (false);

        mStopButton = (Button) findViewById (R.id.stopButton);
        mStopButton.setOnClickListener (new View.OnClickListener()
        {
            public void onClick (View v)
            {
                onPlay (mStartPlaying);
                mStartPlaying = !mStartPlaying;
                mPlayButton.setEnabled (true);
                mStopButton.setEnabled (false);
            }
        });
        mStopButton.setEnabled (false);

        mUploadAudio = (Button) findViewById (R.id.uploadAudioButton);
        mUploadAudio.setEnabled (false);
        mUploadAudio.setVisibility (View.INVISIBLE);
        mUploadAudio.setOnClickListener (new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder (AudioRecordingActivity.this);
                builder.setMessage (R.string.confirmAudioUpload)
                        .setCancelable (false)
                        .setPositiveButton (R.string.yesOption, new DialogInterface.OnClickListener()
                        {
                            public void onClick (final DialogInterface dialog, final int id)
                            {
                                getOps().sendData (mSessionKey, mFileName, buildCompleteName (
                                        mFilePath, mFileName), mIdInd, Test.audio);
                            }
                        })
                        .setNegativeButton (R.string.noOption, new DialogInterface.OnClickListener()
                        {
                            public void onClick (final DialogInterface dialog, final int id)
                            {
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
                intent = new Intent (this, LogoutActivity.class);
                intent.putExtra (Tags.SESSION_KEY, mSessionKey);
                startActivity (intent);
                break;

            case R.id.action_info:
                intent = new Intent (this, InfoActivity.class);
                startActivity (intent);
                break;
        }

        return true;
    }

    private String buildCompleteName (String path, String name)
    {
        return path + "/" + name;
    }

    private void onRecord (boolean start)
    {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay (boolean start)
    {
        if (start) {
            startPlaying();
        }
        else {
            stopPlaying();
        }
    }

    private void startPlaying()
    {
        if (mFileName == null) {
            return;
        }

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource (buildCompleteName (mFilePath, mFileName));
            mPlayer.prepare();
            mSeekBar.setMax (mPlayer.getDuration() / 1000);
            mPlayer.start();
        }
        catch (IOException e) {
            Log.e (TAG, "prepare() failed", e);
        }

        final Handler mHandler = new Handler();
        AudioRecordingActivity.this.runOnUiThread (new Runnable()
        {
            @Override
            public void run()
            {
                if(mPlayer != null){
                    int mCurrentPosition = mPlayer.getCurrentPosition() / 1000;
                    mSeekBar.setProgress (mCurrentPosition);
                }
                mHandler.postDelayed (this, 1000);
            }
        });
    }

    private void stopPlaying()
    {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording()
    {
//        mReadyForUpload = false;
        mUploadAudio.setEnabled (false);
        mPlayButton.setEnabled (false);
        mStopButton.setEnabled (false);
        mUploadAudio.setVisibility (View.INVISIBLE);

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat (MediaRecorder.OutputFormat.THREE_GPP);

        mFileName = UUID.randomUUID().toString() + ".3gp";
        mRecorder.setOutputFile (buildCompleteName (mFilePath, mFileName));

        mRecorder.setAudioEncoder (MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e (TAG, "prepare() failed", e);
        }

        mRecorder.start();
    }

    private void stopRecording()
    {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

//        mReadyForUpload = false;
        mUploadAudio.setEnabled (true);
        mPlayButton.setEnabled (true);
        mUploadAudio.setVisibility (View.VISIBLE);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
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
        startActivity (new Intent (this, LoginActivity.class));
    }
}
