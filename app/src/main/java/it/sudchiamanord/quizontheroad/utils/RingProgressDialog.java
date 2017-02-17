package it.sudchiamanord.quizontheroad.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class RingProgressDialog extends ProgressDialog
{
    private static final String TAG = RingProgressDialog.class.getSimpleName();

    public static final int OPERATION_COMPLETED = 100;
    private Context mContext;

    public RingProgressDialog(Context context)
    {
        super (context);
        mContext = context;
    }

    public void updateProgressDialog (int progress, int dialogTitle, int dialogExpl)
    {
        if (progress == OPERATION_COMPLETED) {
            dismiss();
            return;
        }

        setCanceledOnTouchOutside (false);
        setProgress (progress);
        setTitle (dialogTitle);
        setMessage (mContext.getString (dialogExpl));
        show();
    }
}
