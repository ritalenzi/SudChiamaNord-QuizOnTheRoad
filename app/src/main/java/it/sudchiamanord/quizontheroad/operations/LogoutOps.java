package it.sudchiamanord.quizontheroad.operations;


import android.app.Activity;
import android.util.Log;

import java.lang.ref.WeakReference;

import it.sudchiamanord.quizontheroad.activities.LogoutActivity;
import it.sudchiamanord.quizontheroad.operations.results.LogoutResult;
import it.sudchiamanord.quizontheroad.utils.ConfigurableOps;
import it.sudchiamanord.quizontheroad.utils.GenericAsyncTask;
import it.sudchiamanord.quizontheroad.utils.GenericAsyncTaskOps;
import it.sudchiamanord.quizontheroad.utils.RingProgressDialog;


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
