package it.sudchiamanord.quizontheroad.services;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import it.sudchiamanord.quizontheroad.operations.mediator.Proxy;
import it.sudchiamanord.quizontheroad.operations.results.PositionResult;
import it.sudchiamanord.quizontheroad.utils.Tags;

public class CheckPositionTask
{
    private static final String TAG = CheckPositionTask.class.getSimpleName();

    private boolean mTerminate = false;
    private static CheckPositionTask INSTANCE;
    private String mSessionKey;
    private String mServerClueId;
    private Context mContext;
    private AsyncTask<Void, Void, Void> mAsyncTask;

    private CheckPositionTask()
    {
    }

    public static CheckPositionTask getInstance()
    {
        if (INSTANCE == null) {
            INSTANCE = new CheckPositionTask();
        }

        return INSTANCE;
    }

    public void updateSessionKey (String sessionKey)
    {
        stop();
        mSessionKey = sessionKey;
    }

    public void updateServerClueId (String serverClueId)
    {
        stop();
        mServerClueId = serverClueId;
    }

    public void setContext (Context context)
    {
        stop();
        mContext = context;
    }

    public boolean isRunning (String sessionKey, String serverClueId)
    {
        if (mAsyncTask == null) {
            return false;
        }

        if (mTerminate) {
            return false;
        }

        if (mSessionKey == null) {
            Log.d (TAG, "Null session key");
            return false;
        }

        if (mServerClueId == null) {
            Log.d (TAG, "Null server clue id");
            return false;
        }

        if (!mSessionKey.equals (sessionKey)) {
            Log.d (TAG, "The task is running with the wrong session key - stopping it");
            stop();
            return false;
        }

        if (!mServerClueId.equals (serverClueId)) {
            Log.d (TAG, "The task is running with the wrong server clue id - stopping it");
            stop();
            return false;
        }


        return true;
    }

    public void start()
    {
        stop();
        mTerminate = false;

        mAsyncTask = new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground (Void... voids)
            {
                if ((mSessionKey == null) || (mServerClueId == null) || (mContext == null)) {
                    Log.e (TAG, "Null task parameters");
                    mTerminate = true;
                    return null;
                }

                while (!mTerminate) {
                    PositionResult positionResult = Proxy.checkPositionUpdate (mSessionKey,
                            mServerClueId);
                    if (positionResult == null) {
                        continue;
                    }

                    if (!positionResult.isSuccessful()) {
                        if ((positionResult.getStatus() != null) &&
                                (positionResult.getStatus().equals (
                                        it.sudchiamanord.quizontheroad.stage.Status.wrong))) {
                            Log.d (TAG, "The server responded with \"Wrong position\". " +
                                    "This clue might have been skipped");
                            break;
                        }
                        Intent failedIntent = new Intent("nord.chiama.sud.caccia.USER_ACTION");
                        failedIntent.putExtra (Tags.STAGE_POSITION_UPDATE_STATUS,
                                it.sudchiamanord.quizontheroad.stage.Status.current_position_update_failed.name());
                        failedIntent.putExtra (Tags.SESSION_KEY, mSessionKey);
                        failedIntent.putExtra (Tags.ID_IND, mServerClueId);
                        mContext.sendBroadcast (failedIntent);
                        break;
                    }

                    Log.d (TAG, "Status: " + positionResult.getStatus());
                    if (positionResult.getStatus() == null) {
                        Log.d (TAG, "Found status = null, the clue might have been skipped - exiting.");
                        break;
                    }

                    switch (positionResult.getStatus()) {
                        case current_position_confirmed:
                            Log.d(TAG, "Position confirmed");
                            Intent successIntent = new Intent("nord.chiama.sud.caccia.USER_ACTION");
                            successIntent.putExtra (Tags.STAGE_POSITION_UPDATE_STATUS,
                                    it.sudchiamanord.quizontheroad.stage.Status.current_position_confirmed.name());
                            successIntent.putExtra (Tags.SESSION_KEY, mSessionKey);
                            successIntent.putExtra(Tags.ID_IND, mServerClueId);
                            mContext.sendBroadcast (successIntent);
                            break;

                        case current_wrong_position:
                            Log.d(TAG, "Wrong position");
                            Intent failedIntent = new Intent("nord.chiama.sud.caccia.USER_ACTION");
                            failedIntent.putExtra (Tags.STAGE_POSITION_UPDATE_STATUS,
                                    it.sudchiamanord.quizontheroad.stage.Status.current_wrong_position.name());
                            failedIntent.putExtra (Tags.SESSION_KEY, mSessionKey);
                            failedIntent.putExtra (Tags.ID_IND, mServerClueId);
                            mContext.sendBroadcast (failedIntent);
                            break;

                        case current_position_sent:
                            // Still waiting for the position to be confirmed
                            break;

                        default:
                            Log.d (TAG, "Wrong answer - Exiting service");
                            break;
                    }

                    try {
                        Thread.sleep (10000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                mTerminate = true;
                return null;
            }
        };
        mAsyncTask.execute();
    }

    public void stop()
    {
        mTerminate = true;

        if (mAsyncTask != null) {
            mAsyncTask.cancel (true);
        }
    }
}
