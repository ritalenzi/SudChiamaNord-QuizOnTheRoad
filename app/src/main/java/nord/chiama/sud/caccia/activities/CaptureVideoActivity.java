package nord.chiama.sud.caccia.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.util.UUID;

import nord.chiama.sud.caccia.R;

public class CaptureVideoActivity extends Activity implements SurfaceHolder.Callback
{
    private MediaRecorder mRecorder;

    private SurfaceHolder mHolder;

    private boolean mRecording = false;

    private String mOutFilename;

    public static final String TAG_VIDEO_DATA_FILE = "VIDEO_DATA_FILE";

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);

        requestWindowFeature (Window.FEATURE_NO_TITLE);
        getWindow().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mRecorder = new MediaRecorder();
        initRecorder();
        setContentView (R.layout.activity_capture_video);

        SurfaceView cameraView = (SurfaceView) findViewById (R.id.CameraView);
        mHolder = cameraView.getHolder();
        mHolder.addCallback (this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mHolder.setType (SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        cameraView.setClickable (true);
        cameraView.setOnClickListener (new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                if (mRecording) {
                    mRecorder.stop();
                    mRecording = false;

                    initRecorder();
                    prepareRecorder();
                }
                else {
                    mRecording = true;
                    mRecorder.start();
                }
            }
        });
    }

    private void initRecorder()
    {
        File storageDir = Environment.getExternalStorageDirectory();
        storageDir.mkdirs();
        mOutFilename = storageDir + "/" + UUID.randomUUID().toString() + "_data.mp4";

        mRecorder.setAudioSource (MediaRecorder.AudioSource.DEFAULT);
        mRecorder.setVideoSource (MediaRecorder.VideoSource.DEFAULT);

        CamcorderProfile cpHigh = CamcorderProfile.get (CamcorderProfile.QUALITY_HIGH);
        mRecorder.setProfile (cpHigh);
        mRecorder.setOutputFile (mOutFilename);
        mRecorder.setMaxFileSize (50000000); // Approximately 50 megabytes
    }

    private void prepareRecorder()
    {
        mRecorder.setPreviewDisplay (mHolder.getSurface());

        try {
            mRecorder.prepare();
        }
        catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }


    @Override
    public void surfaceCreated (SurfaceHolder holder)
    {
        prepareRecorder();
    }

    @Override
    public void surfaceChanged (SurfaceHolder holder, int format, int width, int height)
    {
    }

    @Override
    public void surfaceDestroyed (SurfaceHolder holder)
    {
        if (mRecording) {
            mRecorder.stop();
            mRecording = false;
        }
        mRecorder.release();

        Intent intent = new Intent (this, LoginActivity.class);

        if (mOutFilename == null) {
            setResult (RESULT_CANCELED, intent);
        }
        else {
            File outFile = new File (mOutFilename);
            long length = outFile.length();
            length = length/1024;
            length = length/1024;
            if (length > 50) {
                Toast.makeText (this, "The video size is more than 50MB. Impossible to upload it",
                        Toast.LENGTH_SHORT).show();
                setResult (RESULT_CANCELED, intent);
            }
            else {
//                MediaPlayer mp = MediaPlayer.create (this, Uri.parse (mOutFilename));
//                int duration = mp.getDuration() == -1 ? 0 : mp.getDuration();
//                mp.release();
//                intent.putExtra (TAG_VIDEO_DURATION, duration);
                setResult (RESULT_OK, intent);
            }
        }


        finish();
    }
}
