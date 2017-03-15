package it.sudchiamanord.quizontheroad.operations;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import java.lang.ref.WeakReference;

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.activities.SendingActivity;
import it.sudchiamanord.quizontheroad.operations.callbacks.UploadCallback;
import it.sudchiamanord.quizontheroad.operations.mediator.Proxy;
import it.sudchiamanord.quizontheroad.operations.results.UploadResult;
import it.sudchiamanord.quizontheroad.stage.Test;
import it.sudchiamanord.quizontheroad.utils.BarProgressDialog;
import it.sudchiamanord.quizontheroad.utils.ConfigurableOps;
import it.sudchiamanord.quizontheroad.utils.GenericAsyncTask;
import it.sudchiamanord.quizontheroad.utils.GenericAsyncTaskOps;
import it.sudchiamanord.quizontheroad.utils.MultimediaActivitiesMap;


public class SendDataOps implements ConfigurableOps, GenericAsyncTaskOps<Void, Integer, UploadResult>,
        UploadCallback
{
    private final String TAG = getClass().getSimpleName();

    private WeakReference<SendingActivity> mActivity;
    private GenericAsyncTask<Void, Integer, UploadResult, SendDataOps> mAsyncTask;
    private String mSessionKey;
    private String mPathname;
    private String mFilename;
    private String mIdInd;
    private Uri mFileUri;
    private MultimediaActivitiesMap.DialogsInfo mDialogsInfo;
    private boolean mIsUploadFinished = false;
    //private boolean mIsUploadSuccessful = false;
    private UploadResult mUploadResult;

    /**
     * Default constructor that's needed by the GenericActivity framework
     */
    public SendDataOps()
    {
    }

    @Override
    public void onConfiguration (Activity activity, boolean firstTimeIn)
    {
        final String time = firstTimeIn ? "first time" : "second+ time";
        Log.d (TAG, "onConfiguration() called the " + time + " with activity = " + activity);

        mActivity = new WeakReference<>((SendingActivity) activity);

        if (firstTimeIn) {
            // Nothing to do
        }
        else {
            if (mIsUploadFinished) {
                notifyFinished();
            }
        }
    }

    public void sendData (String sessionKey, String filename, String pathName, String idInd,
                          Test test)
    {
        if ((sessionKey == null) || (pathName == null) || (filename == null) || (idInd == null) ||
                (test == null)) {
            Log.e (TAG, "Null parameters");
            return;
        }

        mSessionKey = sessionKey;
        mPathname = pathName;
        mFilename = filename;
        mFileUri = Uri.parse (mPathname);
        mIdInd = idInd;
        mDialogsInfo = MultimediaActivitiesMap.getDialogsInfo (test);
        if (mDialogsInfo == null) {
            Log.e (TAG, "Wrong StageTest " + test);
            return;
        }

        if (mAsyncTask != null) {
            mAsyncTask.cancel (true);
        }
        mAsyncTask = new GenericAsyncTask<>(this);
        mAsyncTask.execute();
    }

    private void notifyFinished()
    {
        if (mUploadResult != null) {
            publishProgress (BarProgressDialog.OPERATION_COMPLETED);

            if (!mUploadResult.isSessionCorrect()) {
                mActivity.get().sessionExpired();
                return;
            }

            if (mUploadResult.isSuccessful()) {
                mActivity.get().uploadFinished (true, mActivity.get().getString (mDialogsInfo.mSuccessToast));
            }
            else {
                if (mUploadResult.getStatus().equals (UploadResult.UploadStatus.file_too_big)) {
                    mActivity.get().uploadFinished (false, mActivity.get().getString (
                            mDialogsInfo.mFailToast) + " - " + mActivity.get().getString (
                            R.string.fileTooBig));
                }
                else {
                    mActivity.get().uploadFinished (false, mActivity.get().getString (
                            mDialogsInfo.mFailToast));
                }
            }
            return;
        }

        if (mIsUploadFinished) {
            // The background operation is finished but the upload result is null - notify upload failed
            mActivity.get().uploadFinished (false, mActivity.get().getString (
                    mDialogsInfo.mFailToast));
        }
    }

    @Override
    public void publishProgress (int progress)
    {
        mActivity.get().notifyProgressUpdate (progress, mDialogsInfo.mProgressDialogTitle,
                mDialogsInfo.mProgressDialogMsg);
    }

    @Override
    public UploadResult doInBackground (Void... param)
    {
        Log.i (TAG, "Started doInBackground");
        return Proxy.doFileUpload (mSessionKey, mFileUri, mFilename, mIdInd, this);
    }

    @Override
    public void onPostExecute (UploadResult result, Void... param)
    {
        mIsUploadFinished = true;
        mUploadResult = result;
//        publishProgress (BarProgressDialog.OPERATION_COMPLETED);
        mActivity.get().dismissDialog();
        Log.i (TAG, "Finished stage ops execution");

        notifyFinished();
    }

    @Override
    public void updateProgress (final int progress)
    {
        mActivity.get().runOnUiThread (new Runnable()
        {
            @Override
            public void run()
            {
                publishProgress (progress);
            }
        });
    }
}
