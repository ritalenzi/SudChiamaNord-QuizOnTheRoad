package it.sudchiamanord.quizontheroad.operations;

import android.app.Activity;
import android.util.Log;

import java.lang.ref.WeakReference;

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.activities.MainActivity;
import it.sudchiamanord.quizontheroad.operations.mediator.Proxy;
import it.sudchiamanord.quizontheroad.operations.results.ActiveMatchesResult;
import it.sudchiamanord.quizontheroad.utils.ConfigurableOps;
import it.sudchiamanord.quizontheroad.utils.GenericAsyncTask;
import it.sudchiamanord.quizontheroad.utils.GenericAsyncTaskOps;
import it.sudchiamanord.quizontheroad.utils.RingProgressDialog;

public class ActiveMatchesOps implements ConfigurableOps, GenericAsyncTaskOps<Boolean, Integer, ActiveMatchesResult>
{
    private final String TAG = getClass().getSimpleName();

    private WeakReference<MainActivity> mActivity;
    private GenericAsyncTask<Boolean, Integer, ActiveMatchesResult, ActiveMatchesOps> mAsyncTask;
    private ActiveMatchesResult mAMResult = null;

    /**
     * Default constructor that's needed by the GenericActivity framework
     */
    public ActiveMatchesOps()
    {
    }

    @Override
    public void onConfiguration (Activity activity, boolean firstTimeIn)
    {
        final String time = firstTimeIn ? "first time" : "second+ time";
        Log.d (TAG, "onConfiguration() called the " + time + " with activity = " + activity);

        mActivity = new WeakReference<>((MainActivity) activity);

        if (firstTimeIn) {
        }
        else {
            updateResultsDisplay();
        }
    }

    private void updateResultsDisplay()
    {
        if (mAMResult != null) {
            publishProgress (RingProgressDialog.OPERATION_COMPLETED);
            if (mAMResult.isSuccessful()) {
                mActivity.get().notifySuccess (mAMResult.getMatches());
            }
            else {
                mActivity.get().notifyFail (mAMResult.getMessage());
            }
        }
    }

    public void request (boolean devel)
    {
        Log.i (TAG, "Devel: " + devel);
        if (mAsyncTask != null) {
            mAsyncTask.cancel (true);
        }

        Boolean[] params = new Boolean[1];
        params[0] = devel;
        mAsyncTask = new GenericAsyncTask<>(this);
        mAsyncTask.execute (params);
    }

    @Override
    public void publishProgress (int progress)
    {
        mActivity.get().notifyProgressUpdate (progress, R.string.activeMatchesReqDialogTitle,
                R.string.activeMatchesReqDialogExpl);
    }

    @Override
    public ActiveMatchesResult doInBackground (Boolean... param)
    {
        Log.i (TAG, "Started active matches request doInBackground");
        return Proxy.doActiveMatchesRequest (param[0]);
    }

    @Override
    public void onPostExecute (ActiveMatchesResult activeMatchesResult, Boolean... param)
    {
        mAMResult = activeMatchesResult;
        publishProgress (RingProgressDialog.OPERATION_COMPLETED);
        Log.i (TAG, "Finished active matches request execution");
        updateResultsDisplay();
    }
}
