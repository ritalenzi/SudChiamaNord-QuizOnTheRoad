package nord.chiama.sud.caccia.operations;


import android.app.Activity;
import android.util.Log;

import java.lang.ref.WeakReference;

import nord.chiama.sud.caccia.activities.LoginActivity;
import nord.chiama.sud.caccia.operations.mediator.Proxy;
import nord.chiama.sud.caccia.operations.results.LoginResult;
import nord.chiama.sud.caccia.R;
import nord.chiama.sud.caccia.utils.ConfigurableOps;
import nord.chiama.sud.caccia.utils.GenericAsyncTask;
import nord.chiama.sud.caccia.utils.GenericAsyncTaskOps;
import nord.chiama.sud.caccia.utils.RingProgressDialog;

public class LoginOps implements ConfigurableOps, GenericAsyncTaskOps<String, Integer, LoginResult>
{
    private final String TAG = getClass().getSimpleName();

    private WeakReference<LoginActivity> mActivity;

    private String mUser;
    private String mPassword;
    private String mImei;
    private LoginResult mLoginResult = null;

    private GenericAsyncTask<String, Integer, LoginResult, LoginOps> mAsyncTask;

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

    public void login (String user, String password, String imei)
    {
        if (mAsyncTask != null) {
            mAsyncTask.cancel (true);
        }

        mUser = user;
        mPassword = password;
        mImei = imei;

        String[] params = new String[3];
        params[0] = user;
        params[1] = password;
        params[2] = imei;
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
    public LoginResult doInBackground (String... param)
    {
        Log.i (TAG, "Started login doInBackground");
        return Proxy.doLogin(param[0], param[1], param[2]);
    }

    @Override
    public void onPostExecute (LoginResult loginResult, String... param)
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
