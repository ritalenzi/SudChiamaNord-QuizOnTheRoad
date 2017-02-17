package it.sudchiamanord.quizontheroad.utils;


import android.app.ProgressDialog;
import android.content.Context;

public class BarProgressDialog extends ProgressDialog
{
    private static final String TAG = BarProgressDialog.class.getSimpleName();

    public static final int OPERATION_COMPLETED = 100;
    private Context mContext;

    public BarProgressDialog (Context context)
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
        setProgressStyle (STYLE_HORIZONTAL);
        setProgress (progress);
        setMax (100);
        setTitle (dialogTitle);
        setMessage (mContext.getString (dialogExpl));
        show();
    }
}
