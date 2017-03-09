package it.sudchiamanord.quizontheroad.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import it.sudchiamanord.quizontheroad.operations.mediator.Proxy;
import it.sudchiamanord.quizontheroad.operations.results.PositionResult;
import it.sudchiamanord.quizontheroad.utils.GPSClient;
import it.sudchiamanord.quizontheroad.utils.Tags;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread
 */
public class PositionSenderService extends Service
{
    private final String TAG = PositionSenderService.class.getSimpleName();
    private Looper mServiceLooper;
    private String mSessionKey;
    private GPSClient mGPSClient;
    private ServiceHandler mServiceHandler;
    private static boolean mTerminate = true;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler
    {
        public ServiceHandler (Looper looper)
        {
            super (looper);
        }

        @Override
        public void handleMessage (Message msg)
        {
            Log.d (TAG, "Received message to start thread in ServiceHandler");
            while (!mTerminate) {
                if (!mGPSClient.isReady()) {
                    Log.w (TAG, "The gps client is not ready yet");
                    try {
                        Thread.sleep (5000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                Location location = mGPSClient.getLocation();
                if (location == null) {
                    Log.e (TAG, "The location is null");
                    try {
                        Thread.sleep (5000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                PositionResult positionResult = Proxy.sendPeriodicPositionUpdate (mSessionKey,
                        location.getLatitude(), location.getLongitude());
                if ((positionResult == null) || (!positionResult.isSuccessful())) {
                    Log.e (TAG, "Impossible to send the position");
                }
                else {
                    Log.d (TAG, "Periodic position sent");
                }
                try {
                    Thread.sleep (60000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            Log.d (TAG, "Outside loop in ServiceHandler");
            stopSelf (msg.arg1);
        }
    }

    @Override
    public void onCreate()
    {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler (mServiceLooper);
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId)
    {
        if (!GPSClient.isGPSEnabled (this)) {
            Log.w (TAG, "The GPS is off");
            return START_REDELIVER_INTENT;
        }

        mTerminate = false;

        mSessionKey = intent.getStringExtra (Tags.SESSION_KEY);
        mGPSClient = new GPSClient (getApplicationContext());
        mGPSClient.connect();
        Log.d (TAG, "Service started");

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage (msg);

        // If we get killed, after returning from here, do not restart
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind (Intent intent)
    {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy()
    {
        Log.d (TAG, "onDestroy() called");
    }

    public static void terminate()
    {
        mTerminate = true;
    }

    public static boolean isRunning()
    {
        return !mTerminate;
    }
}
