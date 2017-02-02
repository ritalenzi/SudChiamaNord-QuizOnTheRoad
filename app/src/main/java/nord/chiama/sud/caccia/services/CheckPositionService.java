package nord.chiama.sud.caccia.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import nord.chiama.sud.caccia.utils.Tags;
import nord.chiama.sud.caccia.operations.mediator.Proxy;
import nord.chiama.sud.caccia.stage.Status;
import nord.chiama.sud.caccia.operations.results.PositionResult;

public class CheckPositionService extends IntentService
{
    private static final String TAG = CheckPositionService.class.getSimpleName();

    public CheckPositionService()
    {
        super ("CheckPositionService");
    }

    @Override
    protected void onHandleIntent (Intent intent)
    {
        if (intent == null) {
            return;
        }

        final String sessionKey = intent.getStringExtra (Tags.SESSION_KEY);
        final String serverClueId = intent.getStringExtra (Tags.ID_IND);

        new Thread (new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    Thread.sleep (5000);    // sleeping a bit before starting the thread
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                while (true) {
                    PositionResult positionResult = Proxy.checkPositionUpdate (sessionKey, serverClueId);
                    if (positionResult == null) {
                        continue;
                    }

                    if (!positionResult.isSuccessful()) {
                        if ((positionResult.getStatus() != null) && (positionResult.getStatus().equals (Status.wrong))) {
                            Log.d (TAG, "The server responded with \"Wrong position\". This clue might have been skipped");
                            return;
                        }
                        Intent failedIntent = new Intent ("nord.chiama.sud.caccia.USER_ACTION");
                        failedIntent.putExtra (Tags.STAGE_POSITION_UPDATE_STATUS,
                                Status.current_position_update_failed.name());
                        failedIntent.putExtra (Tags.SESSION_KEY, sessionKey);
                        failedIntent.putExtra(Tags.ID_IND, serverClueId);
                        sendBroadcast (failedIntent);
                        return;
                    }

                    Log.d (TAG, "Status: " + positionResult.getStatus());
                    if (positionResult.getStatus() == null) {
                        Log.d (TAG, "Found status = null, the clue might have been skipped - exiting.");
                        return;
                    }

                    switch (positionResult.getStatus()) {
                        case current_position_confirmed:
                            Log.d (TAG, "Position confirmed");
                            Intent successIntent = new Intent ("nord.chiama.sud.caccia.USER_ACTION");
                            successIntent.putExtra (Tags.STAGE_POSITION_UPDATE_STATUS,
                                    Status.current_position_confirmed.name());
                            successIntent.putExtra (Tags.SESSION_KEY, sessionKey);
                            successIntent.putExtra (Tags.ID_IND, serverClueId);
                            sendBroadcast (successIntent);
                            return;

                        case current_wrong_position:
                            Log.d (TAG, "Wrong position");
                            Intent failedIntent = new Intent ("nord.chiama.sud.caccia.USER_ACTION");
                            failedIntent.putExtra (Tags.STAGE_POSITION_UPDATE_STATUS,
                                    Status.current_wrong_position.name());
                            failedIntent.putExtra (Tags.SESSION_KEY, sessionKey);
                            failedIntent.putExtra (Tags.ID_IND, serverClueId);
                            sendBroadcast (failedIntent);
                            return;

                        case current_position_sent:
                            // Still waiting for the position to be confirmed
                            break;

                        default:
                            Log.d (TAG, "Wrong answer - Exiting service");
                            return;
                    }

                    try {
                        Thread.sleep (5000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
//        while (true) {
//            PositionResult positionResult = Proxy.checkPositionUpdate(sessionKey, serverClueId);
//            if (positionResult == null) {
//                continue;
//            }
//
//            if (!positionResult.isSuccessful()) {
//                if ((positionResult.getStatus() != null) && (positionResult.getStatus().equals (Status.wrong))) {
//                    Log.d (TAG, "The server responded with \"Wrong position\". This clue might have been skipped");
//                    return;
//                }
//                Intent failedIntent = new Intent ("nord.chiama.sud.caccia.USER_ACTION");
//                failedIntent.putExtra (Tags.STAGE_POSITION_UPDATE_STATUS,
//                        Status.current_position_update_failed.name());
//                failedIntent.putExtra (Tags.SESSION_KEY, sessionKey);
//                failedIntent.putExtra(Tags.ID_IND, serverClueId);
//                sendBroadcast (failedIntent);
//                return;
//            }
//
//            Log.d (TAG, "Status: " + positionResult.getStatus());
//            if (positionResult.getStatus() == null) {
//                Log.d (TAG, "Found status = null, the clue might have been skipped - exiting.");
//                return;
//            }
//
//            switch (positionResult.getStatus()) {
//                case current_position_confirmed:
//                    Log.d (TAG, "Position confirmed");
//                    Intent successIntent = new Intent ("nord.chiama.sud.caccia.USER_ACTION");
//                    successIntent.putExtra (Tags.STAGE_POSITION_UPDATE_STATUS,
//                            Status.current_position_confirmed.name());
//                    successIntent.putExtra (Tags.SESSION_KEY, sessionKey);
//                    successIntent.putExtra (Tags.ID_IND, serverClueId);
//                    sendBroadcast (successIntent);
//                    return;
//
//                case current_wrong_position:
//                    Log.d (TAG, "Wrong position");
//                    Intent failedIntent = new Intent ("nord.chiama.sud.caccia.USER_ACTION");
//                    failedIntent.putExtra (Tags.STAGE_POSITION_UPDATE_STATUS,
//                            Status.current_wrong_position.name());
//                    failedIntent.putExtra (Tags.SESSION_KEY, sessionKey);
//                    failedIntent.putExtra (Tags.ID_IND, serverClueId);
//                    sendBroadcast (failedIntent);
//                    return;
//
//                case current_position_sent:
//                    // Still waiting for the position to be confirmed
//                    break;
//
//                default:
//                    Log.d (TAG, "Wrong answer - Exiting service");
//                    return;
//            }
//
//            try {
//                Thread.sleep (5000);
//            }
//            catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
