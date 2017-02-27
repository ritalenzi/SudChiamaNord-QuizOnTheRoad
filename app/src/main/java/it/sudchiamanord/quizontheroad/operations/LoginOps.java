package it.sudchiamanord.quizontheroad.operations;


import android.app.Activity;
import android.util.Log;

import java.lang.ref.WeakReference;

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.activities.LoginActivity;
import it.sudchiamanord.quizontheroad.operations.mediator.Proxy;
import it.sudchiamanord.quizontheroad.operations.results.LoginResult;
import it.sudchiamanord.quizontheroad.utils.ConfigurableOps;
import it.sudchiamanord.quizontheroad.utils.GenericAsyncTask;
import it.sudchiamanord.quizontheroad.utils.GenericAsyncTaskOps;
import it.sudchiamanord.quizontheroad.utils.RingProgressDialog;


public class LoginOps implements ConfigurableOps, GenericAsyncTaskOps<Object, Integer, LoginResult>
{
    private final String TAG = getClass().getSimpleName();

    private WeakReference<LoginActivity> mActivity;

    private String mUser;
    private String mPassword;
    private String mImei;
    private LoginResult mLoginResult = null;

    private GenericAsyncTask<Object, Integer, LoginResult, LoginOps> mAsyncTask;

    /**
     * Default constructor that's needed by the GenericActivity framework
     */
    public LoginOps()
    {
    }

    @Override
    public void onConfiguration (Activity activity, boolean firstTimeIn)
    {
        final String time = firstTimeIn ? "first time" : "second+ time";
        Log.d (TAG, "onConfiguration() called the " + time + " with activity = " + activity);

        mActivity = new WeakReference<>((LoginActivity) activity);

        if (firstTimeIn) {
            // Nothing to do for now
        }
        else {
            updateResultsDisplay();
        }
    }

    private void updateResultsDisplay()
    {
        if (mLoginResult != null) {
            publishProgress (RingProgressDialog.OPERATION_COMPLETED);
            if (mLoginResult.isSuccessful()) {
                mActivity.get().notifySuccessfulLogin (mLoginResult.getSessionKey());
            }
            else {
                mActivity.get().notifyFailedLogin (mLoginResult.getMessage());
            }
        }
    }

    public void login (String user, String password, String imei, int matchId)
    {
        if (mAsyncTask != null) {
            mAsyncTask.cancel (true);
        }

        mUser = user;
        mPassword = password;
        mImei = imei;

        Object[] params = new Object[4];
        params[0] = user;
        params[1] = password;
        params[2] = imei;
        params[3] = matchId;
        mAsyncTask = new GenericAsyncTask<>(this);
        mAsyncTask.execute (params);
    }

    @Override
    public void publishProgress (int progress)
    {
        mActivity.get().notifyProgressUpdate (progress, R.string.loginDialogTitle,
                R.string.loginDialogExpl);
    }

    @Override
    public LoginResult doInBackground (Object... param)
    {
        Log.i (TAG, "Started login doInBackground");
        return Proxy.doLogin ((String) param[0], (String) param[1], (String) param[2], (Integer) param[3]);
    }

    @Override
    public void onPostExecute (LoginResult loginResult, Object... param)
    {
        mLoginResult = loginResult;
        publishProgress (RingProgressDialog.OPERATION_COMPLETED);
        Log.i (TAG, "Finished login execution");
        updateResultsDisplay();
    }

//    @Override
//    public Void doInBackground (Void... param)
//    {
//        Log.i (TAG, "Started login doInBackground");
//        mLoginResult = Utils.doLogin (mUser, mPassword, mImei);
//
//        return null;
//    }
//
//    @Override
//    public void onPostExecute (Void aVoid, Void param)
//    {
//        publishProgress (PersProgressDialog.OPERATION_COMPLETED);
//        Log.i (TAG, "Finished login execution");
//        updateResultsDisplay();
//    }
}
