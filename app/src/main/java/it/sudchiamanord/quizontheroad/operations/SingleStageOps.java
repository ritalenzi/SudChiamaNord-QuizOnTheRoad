package it.sudchiamanord.quizontheroad.operations;

import android.app.Activity;
import android.location.Location;
import android.util.Log;

import java.lang.ref.WeakReference;

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.activities.SingleStageActivity;
import it.sudchiamanord.quizontheroad.operations.mediator.Proxy;
import it.sudchiamanord.quizontheroad.operations.results.PositionResult;
import it.sudchiamanord.quizontheroad.operations.results.SkipClueResult;
import it.sudchiamanord.quizontheroad.utils.ConfigurableOps;
import it.sudchiamanord.quizontheroad.utils.GPSClient;
import it.sudchiamanord.quizontheroad.utils.GenericAsyncTask;
import it.sudchiamanord.quizontheroad.utils.GenericAsyncTaskOps;
import it.sudchiamanord.quizontheroad.utils.RingProgressDialog;

public class SingleStageOps implements ConfigurableOps, GenericAsyncTaskOps<Object, Integer, Object>//,
//        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private final String TAG = getClass().getSimpleName();

    private WeakReference<SingleStageActivity> mActivity;
//    private GoogleApiClient mGoogleApiClient;
//    private boolean isGPSClientReady = false;
    private GPSClient mGPSClient;
    private boolean mIsPositionSent = false;
    private PositionResult mPositionResult;
    private boolean mIsSkipClueSent = false;
    private SkipClueResult mSkipClueResult;
//    private GenericAsyncTask<Object, Integer, PositionResult, SingleStageOps> mAsyncTask;
    private GenericAsyncTask<Object, Integer, Object, SingleStageOps> mAsyncTask;

    /**
     * Default constructor that's needed by the GenericActivity framework
     */
    public SingleStageOps()
    {
    }

    @Override
    public void onConfiguration (Activity activity, boolean firstTimeIn)
    {
        final String time = firstTimeIn ? "first time" : "second+ time";
        Log.d (TAG, "onConfiguration() called the " + time + " with activity = " + activity);

        mActivity = new WeakReference<>((SingleStageActivity) activity);

        if (firstTimeIn) {
//            mGoogleApiClient = new GoogleApiClient.Builder (mActivity.get())
//                    .addConnectionCallbacks (this)
//                    .addOnConnectionFailedListener (this)
//                    .addApi (LocationServices.API)
//                    .build();
            mGPSClient = new GPSClient (mActivity.get());
        }

//        if (!mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.connect();
//        }
        if (!mGPSClient.isConnected()) {
            mGPSClient.connect();
        }
    }

    public void sendPosition (String sessionKey, String serverClueId)
    {
//        if (!isGPSClientReady) {
        if (!mGPSClient.isReady()) {
            mActivity.get().positionUpdateFailed (R.string.gpsFailed);
        }
        else {
//            Location location = LocationServices.FusedLocationApi.getLastLocation (mGoogleApiClient);
            Location location = mGPSClient.getLocation();
            if (location != null) {
                Log.d (TAG, "Latitude: " + location.getLatitude());
                Log.d (TAG, "Longitude: " + location.getLongitude());

                if (mAsyncTask != null) {
                    mAsyncTask.cancel (true);
                }

                mActivity.get().notifyProgressUpdate (0, R.string.posUpdateDialogTitle,
                        R.string.posUpdateDialogExpl);
                Object[] params = new Object[5];
                params[0] = Operation.send_position;
                params[1] = sessionKey;
                params[2] = serverClueId;
                params[3] = location.getLatitude();
                params[4] = location.getLongitude();
                mAsyncTask = new GenericAsyncTask<>(this);
                mAsyncTask.execute (params);
            }
            else {
                mActivity.get().positionUpdateFailed (R.string.gpsFailed);
            }
        }

//        mGoogleApiClient.disconnect();
        mGPSClient.disconnect();
    }

    public void skipClue (String sessionKey, String serverClueId)
    {
        if (mAsyncTask != null) {
            mAsyncTask.cancel (true);
        }

        mActivity.get().notifyProgressUpdate (0, R.string.skipClueDialogTitle,
                R.string.skipClueDialogExpl);
        Object[] params = new Object[3];
        params[0] = Operation.skip_clue;
        params[1] = sessionKey;
        params[2] = serverClueId;
        mAsyncTask = new GenericAsyncTask<>(this);
        mAsyncTask.execute (params);
    }

    private void notifyForPositionUpdate()
    {
        if (!mIsPositionSent) {
            return;
        }

        mActivity.get().positionUpdateFinished (mPositionResult);
    }

    private void notifyForSkipClue()
    {
        if (!mIsSkipClueSent) {
            return;
        }

        mActivity.get().skipClueFinished (mSkipClueResult);
    }

    @Override
    public void publishProgress (int progress)
    {
    }

    @Override
    public Object doInBackground (Object... params)
    {
        Operation operation = (Operation) params[0];
        switch (operation) {

            case send_position:
                Log.d(TAG, "About to send the position");
                return Proxy.sendCluePositionUpdate ((String) params[1], (String) params[2],
                        (Double) params[3], (Double) params[4]);

            case skip_clue:
                Log.d (TAG, "About to skip clue");
                return Proxy.skipClue ((String) params[1], (String) params[2]);
        }

        //TODO: (MAYBE) update database by setting that the position is correct (in case it is)
        return null;
    }

    @Override
    public void onPostExecute (Object result, Object... params)
    {
        Operation operation = (Operation) params[0];
        switch (operation) {

            case send_position:
                mPositionResult = (PositionResult) result;
                mIsPositionSent = true;
                mActivity.get().notifyProgressUpdate (RingProgressDialog.OPERATION_COMPLETED,
                        R.string.posUpdateDialogTitle, R.string.posUpdateDialogExpl);
//                publishProgress (RingProgressDialog.OPERATION_COMPLETED);
                Log.i (TAG, "Finished position update execution");
                notifyForPositionUpdate();
                break;

            case skip_clue:
                mSkipClueResult = (SkipClueResult) result;
                mIsSkipClueSent = true;
                mActivity.get().notifyProgressUpdate (RingProgressDialog.OPERATION_COMPLETED,
                        R.string.skipClueDialogTitle, R.string.skipClueDialogExpl);
//                publishProgress (RingProgressDialog.OPERATION_COMPLETED);
                Log.i (TAG, "Finished communication of skipping position");
                notifyForSkipClue();
                break;
        }
    }

//    @Override
//    public PositionResult doInBackground (Object... params)
//    {
//        return Utils.sendPositionUpdate ((String) params[0], (Double) params[1], (Double) params[2]);
//        //TODO: (MAYBE) update database by setting that the position is correct (in case it is)
//    }
//
//    @Override
//    public void onPostExecute (PositionResult result, Object params)
//    {
//        mPositionResult = result;
//        mIsPositionSent = true;
//        publishProgress (PersProgressDialog.OPERATION_COMPLETED);
//        Log.i (TAG, "Finished position update execution");
//        notifyActivity();
//    }

//    /**
//     * GPS interface methods
//     */
//
//    @Override
//    public void onConnected (Bundle bundle)
//    {
//        Log.d (TAG, "Successfully connected to the GPS Google API");
//        isGPSClientReady = true;
//    }
//
//    @Override
//    public void onConnectionSuspended (int i)
//    {
//        Log.w (TAG, "Connection with the GPS Google API suspended");
//        isGPSClientReady = false;
//    }
//
//    @Override
//    public void onConnectionFailed (ConnectionResult connectionResult)
//    {
//        Log.w (TAG, "Connection with the GPS Google API failed");
//        isGPSClientReady = false;
//    }

    private enum Operation
    {
        send_position,
        skip_clue
    }
}
