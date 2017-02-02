package nord.chiama.sud.caccia.utils;


import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

public class BarProgressDialog extends ProgressDialog
{
    private static final String TAG = BarProgressDialog.class.getSimpleName();

    private int mOpProgress;
    public static final int OPERATION_COMPLETED = 100;
    private Context mContext;

    public BarProgressDialog (Context context)
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
        setProgressStyle (STYLE_HORIZONTAL);
        setProgress (mOpProgress);
        setMax (100);
        setTitle (dialogTitle);
        setMessage (mContext.getString (dialogExpl));
        show();
    }
}
