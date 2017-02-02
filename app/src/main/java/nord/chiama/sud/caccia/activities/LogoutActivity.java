package nord.chiama.sud.caccia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import nord.chiama.sud.caccia.R;
import nord.chiama.sud.caccia.utils.RingProgressDialog;
import nord.chiama.sud.caccia.utils.Tags;
import nord.chiama.sud.caccia.operations.LogoutOps;
import nord.chiama.sud.caccia.services.PositionSenderService;
import nord.chiama.sud.caccia.utils.GenericActivity;

public class LogoutActivity extends GenericActivity<LogoutOps>
{
    private RingProgressDialog mOpProgressDialog;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        setTheme (android.R.style.Theme_Holo_Light_NoActionBar);
        setContentView (R.layout.activity_logout);

        mOpProgressDialog = new RingProgressDialog (LogoutActivity.this);

        super.onCreate (savedInstanceState, LogoutOps.class);

        String sessionKey = getIntent().getStringExtra (Tags.SESSION_KEY);
        getOps().logout (sessionKey);
    }

    public void notifyProgressUpdate (int progress, int dialogTitle, int dialogExpl)
    {
        mOpProgressDialog.updateProgressDialog (progress, dialogTitle, dialogExpl);
    }

    public void notifySuccessfulLogout (int message)
    {
        displayResult (message);
        finish();
    }

    public void notifyFailedLogout (int error)
    {
        displayResult (error);
        finish();
    }

    private void displayResult (int message)
    {
        Toast.makeText (this, message, Toast.LENGTH_SHORT).show();
        PositionSenderService.terminate();
        startActivity (new Intent (this, LoginActivity.class));
    }
}
