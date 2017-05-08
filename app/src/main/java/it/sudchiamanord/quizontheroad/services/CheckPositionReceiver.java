package it.sudchiamanord.quizontheroad.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.activities.StagesActivity;
import it.sudchiamanord.quizontheroad.stage.Status;
import it.sudchiamanord.quizontheroad.utils.Tags;

public class CheckPositionReceiver extends BroadcastReceiver
{
    private final int MY_NOTIFICATION_ID = 11151990;

    private static final String TAG = CheckPositionReceiver.class.getSimpleName();

    public CheckPositionReceiver()
    {
    }

    @Override
    public void onReceive (Context context, Intent intent)
    {
        Log.e (TAG, "Received broadcast");
        String sessionKey = intent.getStringExtra (Tags.SESSION_KEY);
        String serverClueId = intent.getStringExtra (Tags.ID_IND);
        String positionUpdateStatus = intent.getStringExtra (Tags.STAGE_POSITION_UPDATE_STATUS);
        Status status = Status.valueOf (positionUpdateStatus);

        Intent stagesActivityIntent = new Intent (context, StagesActivity.class);
        stagesActivityIntent.putExtra (Tags.SESSION_KEY, sessionKey);

//        ActivityManager activityManager = (ActivityManager) context.getSystemService (Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks (Integer.MAX_VALUE);
//
//        if (services.get (0).topActivity.getPackageName().equalsIgnoreCase (context.getPackageName())) {
//            Log.d (TAG, "ACTIVITY IS ALREADY RUNNING!!");
//            // TODO: fix the fact that by doing this the back button does not work properly
//            stagesActivityIntent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity (stagesActivityIntent);
//        }
//        else {
            // building notification
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, stagesActivityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            RemoteViews mContentView = new RemoteViews(context.getPackageName(),
                    R.layout.check_position_notification);

            String notificationText = null;
            switch (status) {
                case current_position_update_failed:
                    notificationText = context.getString (R.string.positionUpdateFailed);
                    break;

                case current_position_confirmed:
                    notificationText = context.getString (R.string.correctPosition);
                    break;

                case current_wrong_position:
                    notificationText = context.getString (R.string.wrongPosition);
                    break;
            }
            mContentView.setTextViewText (R.id.notification_text, notificationText);
            Notification.Builder notificationBuilder = new Notification.Builder (context)
                    .setAutoCancel (true)
                    .setContent (mContentView)
                    .setContentTitle ("Position Update Result")
                    .setContentIntent (pendingIntent)
                    .setSmallIcon (R.drawable.notif_small)
                    .setSound (RingtoneManager.getDefaultUri (RingtoneManager.TYPE_NOTIFICATION));
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
            Notification notification;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                notification = notificationBuilder.getNotification();
            }
            else {
                notification = notificationBuilder.build();
            }
            mNotificationManager.notify (MY_NOTIFICATION_ID, notification);
//        }
    }
}
