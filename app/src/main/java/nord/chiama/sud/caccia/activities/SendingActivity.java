package nord.chiama.sud.caccia.activities;


import android.os.Bundle;

import nord.chiama.sud.caccia.operations.SendDataOps;
import nord.chiama.sud.caccia.utils.BarProgressDialog;
import nord.chiama.sud.caccia.utils.GenericActivity;

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
