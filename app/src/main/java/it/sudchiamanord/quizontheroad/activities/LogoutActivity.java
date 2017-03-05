package it.sudchiamanord.quizontheroad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.operations.LogoutOps;
import it.sudchiamanord.quizontheroad.utils.GenericActivity;
import it.sudchiamanord.quizontheroad.utils.RingProgressDialog;
import it.sudchiamanord.quizontheroad.utils.Tags;


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
        startActivity (new Intent(this, LoginActivity.class));
    }
}
