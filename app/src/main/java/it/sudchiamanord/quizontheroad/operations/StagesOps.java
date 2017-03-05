package it.sudchiamanord.quizontheroad.operations;

import android.app.Activity;
import android.util.Log;

import java.lang.ref.WeakReference;

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.activities.StagesActivity;
import it.sudchiamanord.quizontheroad.operations.mediator.Proxy;
import it.sudchiamanord.quizontheroad.operations.results.ActualMatchResult;
import it.sudchiamanord.quizontheroad.utils.ConfigurableOps;
import it.sudchiamanord.quizontheroad.utils.GenericAsyncTask;
import it.sudchiamanord.quizontheroad.utils.GenericAsyncTaskOps;
import it.sudchiamanord.quizontheroad.utils.RingProgressDialog;


public class StagesOps implements ConfigurableOps, GenericAsyncTaskOps<String, Integer, ActualMatchResult>
{
    private final String TAG = getClass().getSimpleName();

    private WeakReference<StagesActivity> mActivity;
    private GenericAsyncTask<String, Integer, ActualMatchResult, StagesOps> mAsyncTask;
    private ActualMatchResult mResult;
    private boolean mIsRequestFinished = false;

    /**
     * Default constructor that's needed by the GenericActivity framework
     */
    public StagesOps()
    {
    }

    @Override
    public void onConfiguration (Activity activity, boolean firstTimeIn)
    {
        final String time = firstTimeIn ? "first time" : "second+ time";
        Log.d (TAG, "onConfiguration() called the " + time + " with activity = " + activity);

        mActivity = new WeakReference<>((StagesActivity) activity);

        if (firstTimeIn) {
            // Nothing to do here
        }
        else {
            updateResultsDisplay();
        }
    }

    public void requestStagesInfo (String sessionKey)
    {
        if (mAsyncTask != null) {
            mAsyncTask.cancel (true);
        }
        mAsyncTask = new GenericAsyncTask<>(this);
        String[] params = new String[1];
        params[0] = sessionKey;
        mAsyncTask.execute (params);
    }

    private void updateResultsDisplay()
    {
        if (mResult != null) {
            publishProgress (RingProgressDialog.OPERATION_COMPLETED);
            mActivity.get().displayResults (mResult, null);
            return;
        }

        if (mIsRequestFinished) {
            // The request was completed but the ActualMatchResult is still null
            mActivity.get().displayResults (mResult, mActivity.get().getString (
                    R.string.stagesRetrievalError));
        }
    }

    @Override
    public void publishProgress (int progress)
    {
        mActivity.get().notifyProgressUpdate (progress, R.string.stagesDialogTitle,
                R.string.stagesDialogExpl);
    }

    @Override
    public ActualMatchResult doInBackground (String... param)
    {
        Log.i (TAG, "Started stages ops doInBackground");
//        final String ORDER_BY = StageEntry.COLUMN_NUMBER;
//        Cursor cursor = mActivity.get().getContentResolver().query (StageEntry.CONTENT_URI, null,
//                null, null, ORDER_BY);
//        if (!cursor.moveToFirst()) {
//            return null;
//        }
//        mResults = new ArrayList<>();
//        do {
//            Stage stage = new Stage();
//            stage.setNumber (cursor.getInt (cursor.getColumnIndex (StageEntry.COLUMN_NUMBER)));
//            stage.setLocation (cursor.getString (cursor.getColumnIndex (StageEntry.COLUMN_LOCATION)));
//            stage.setStatus (cursor.getString (cursor.getColumnIndex (StageEntry.COLUMN_STATUS)));
//            stage.setClue (cursor.getString (cursor.getColumnIndex (StageEntry.COLUMN_CLUE)));
//            stage.setTest (cursor.getString (cursor.getColumnIndex (StageEntry.COLUMN_TEST)));
//            stage.setCorrectPosition (cursor.getInt (cursor.getColumnIndex (
//                    StageEntry.COLUMN_CORRECT_POSITION)) == 1);
//            mResults.add (stage);
//        }
//        while (cursor.moveToNext());
//        cursor.close();
//
//        return null;

        return Proxy.getActualMatch (param[0]);
    }

    @Override
    public void onPostExecute (ActualMatchResult result, String... param)
    {
        mResult = result;
        mIsRequestFinished = true;
        publishProgress (RingProgressDialog.OPERATION_COMPLETED);
        Log.i (TAG, "Finished stage ops execution");
        int errorMessage = (mResult == null ? R.string.stagesRetrievalError : mResult.getMessage());
        mActivity.get().displayResults (mResult, mActivity.get().getString (errorMessage));
    }
}
