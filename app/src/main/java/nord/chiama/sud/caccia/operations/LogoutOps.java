package nord.chiama.sud.caccia.operations;


import android.app.Activity;
import android.util.Log;

import java.lang.ref.WeakReference;

import nord.chiama.sud.caccia.R;
import nord.chiama.sud.caccia.activities.LogoutActivity;
import nord.chiama.sud.caccia.operations.mediator.Proxy;
import nord.chiama.sud.caccia.utils.ConfigurableOps;
import nord.chiama.sud.caccia.utils.GenericAsyncTask;
import nord.chiama.sud.caccia.utils.GenericAsyncTaskOps;
import nord.chiama.sud.caccia.operations.results.LogoutResult;
import nord.chiama.sud.caccia.utils.RingProgressDialog;

public class LogoutOps implements ConfigurableOps, GenericAsyncTaskOps<String, Integer, LogoutResult>
{
    private final String TAG = getClass().getSimpleName();

    private WeakReference<LogoutActivity> mActivity;
    private LogoutResult mLogoutResult = null;

    private GenericAsyncTask<String, Integer, LogoutResult, LogoutOps> mAsyncTask;

    @Override
    public void onConfiguration (Activity activity, boolean firstTimeIn)
    {
        final String time = firstTimeIn ? "first time" : "second+ time";
        Log.d (TAG, "onConfiguration() called the " + time + " with activity = " + activity);

        mActivity = new WeakReference<>((LogoutActivity) activity);

        if (firstTimeIn) {
            // Nothing to do for now
        }
        else {
            updateResult();
        }
    }

    private void updateResult()
    {
        if (mLogoutResult != null) {
            publishProgress (RingProgressDialog.OPERATION_COMPLETED);
            if (mLogoutResult.isSuccessful()) {
                mActivity.get().notifySuccessfulLogout (mLogoutResult.getMessage());
            }
            else {
                mActivity.get().notifyFailedLogout (mLogoutResult.getMessage());
            }
        }
    }

    public void logout (String sessionKey)
    {
        if (mAsyncTask != null) {
            mAsyncTask.cancel (true);
        }

        String[] params = new String[1];
        params[0] = sessionKey;
        mAsyncTask = new GenericAsyncTask<>(this);
        mAsyncTask.execute (params);
    }

    @Override
    public void publishProgress (int progress)
    {
        mActivity.get().notifyProgressUpdate (progress, R.string.logoutDialogTitle,
                R.string.logoutDialogExpl);
    }

    @Override
    public LogoutResult doInBackground (String... param)
    {
        Log.i (TAG, "Started logout doInBackground");
        return Proxy.doLogout(param[0]);
    }

    @Override
    public void onPostExecute (LogoutResult logoutResult, String... param)
    {
        mLogoutResult = logoutResult;
        publishProgress (RingProgressDialog.OPERATION_COMPLETED);
        Log.i (TAG, "Finished logout execution");
        updateResult();
    }
}
