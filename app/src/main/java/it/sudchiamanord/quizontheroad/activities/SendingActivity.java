package it.sudchiamanord.quizontheroad.activities;


import android.os.Bundle;

import it.sudchiamanord.quizontheroad.operations.SendDataOps;
import it.sudchiamanord.quizontheroad.utils.BarProgressDialog;
import it.sudchiamanord.quizontheroad.utils.GenericActivity;


public abstract class SendingActivity extends GenericActivity<SendDataOps>
{
    protected BarProgressDialog mOpProgressDialog;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState, SendDataOps.class);
    }

    public abstract void notifyProgressUpdate (int progress, int dialogTitle, int dialogExpl);

    public abstract void dismissDialog();

    public abstract void uploadFinished (boolean success, String toastMessage);

    public abstract void sessionExpired();
}
