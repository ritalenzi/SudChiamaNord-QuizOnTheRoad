package it.sudchiamanord.quizontheroad.utils;

import java.util.HashMap;
import java.util.Map;

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.stage.Test;


public class MultimediaActivitiesMap
{
    private MultimediaActivitiesMap()
    {
        mDialogsMap.put (Test.photo, new DialogsInfo (R.string.photoUploadSuccess,
                R.string.photoUploadFailed, R.string.photoDialogTitle, R.string.photoDialogExpl));
        mDialogsMap.put (Test.audio, new DialogsInfo (R.string.audioUploadSuccess,
                R.string.audioUploadFailed, R.string.audioDialogTitle, R.string.audioDialogExpl));
        mDialogsMap.put (Test.text, new DialogsInfo (R.string.textUploadSuccess,
                R.string.textUploadFailed, R.string.textDialogTitle, R.string.textDialogExpl));
        mDialogsMap.put (Test.video, new DialogsInfo (R.string.videoUploadSuccess,
                R.string.videoUploadFailed, R.string.videoDialogTitle, R.string.videoDialogExpl));
    }

    public synchronized static DialogsInfo getDialogsInfo (Test type)
    {
        if (INSTANCE == null) {
            INSTANCE = new MultimediaActivitiesMap();
        }

        return mDialogsMap.get (type);
    }

    private static MultimediaActivitiesMap INSTANCE = null;
    private static final Map<Test, DialogsInfo> mDialogsMap = new HashMap<>();

    public class DialogsInfo
    {
        DialogsInfo (int successToast, int failToast, int processDialogTitle, int progressDialogMsg)
        {
            mSuccessToast = successToast;
            mFailToast = failToast;
            mProgressDialogTitle = processDialogTitle;
            mProgressDialogMsg = progressDialogMsg;
        }

        public final int mSuccessToast;
        public final int mFailToast;
        public final int mProgressDialogTitle;
        public final int mProgressDialogMsg;
    }
}
