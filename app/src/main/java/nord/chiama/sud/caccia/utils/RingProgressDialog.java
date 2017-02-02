package nord.chiama.sud.caccia.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

public class RingProgressDialog extends ProgressDialog
{
    private static final String TAG = BarProgressDialog.class.getSimpleName();

    private int mOpProgress;
    public static final int OPERATION_COMPLETED = 100;
    private Context mContext;

    public RingProgressDialog (Context context)
    {
        super (context);
        mContext = context;
    }

    public void updateProgressDialog (int progress, int dialogTitle, int dialogExpl)
    {
        mOpProgress = progress;
        if (mOpProgress == OPERATION_COMPLETED) {
            dismiss();
            return;
        }

        setCanceledOnTouchOutside (false);
        setProgress (mOpProgress);
        setTitle (dialogTitle);
        setMessage (mContext.getString (dialogExpl));
        show();
    }
}
