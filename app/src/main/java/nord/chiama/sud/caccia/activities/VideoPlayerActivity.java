package nord.chiama.sud.caccia.activities;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.VideoView;

import nord.chiama.sud.caccia.R;
import nord.chiama.sud.caccia.utils.Tags;

public class VideoPlayerActivity extends Activity
{
    private VideoView mVideoView;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);

        setContentView (R.layout.activity_video_player);

        mVideoView = (VideoView) findViewById (R.id.videoView);

        String filePath = getIntent().getExtras().getString (Tags.RESOURCE_ABSOLUTE_PATH);
        if (filePath == null) {
            Toast.makeText(getApplicationContext(), R.string.noVideoRecorded,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mVideoView.setVideoURI (Uri.parse (filePath));
        mVideoView.start();
    }
}
