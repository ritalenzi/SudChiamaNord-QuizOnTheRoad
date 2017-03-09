package it.sudchiamanord.quizontheroad.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import it.sudchiamanord.quizontheroad.R;

public class GPSClient implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    private final String TAG = getClass().getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private boolean isGPSClientReady = false;

    public GPSClient(Context context)
    {
        mGoogleApiClient = new GoogleApiClient.Builder (context)
                .addConnectionCallbacks (this)
                .addOnConnectionFailedListener (this)
                .addApi (LocationServices.API)
                .build();
    }

    public boolean isConnected()
    {
        return mGoogleApiClient.isConnected();
    }

    public void connect()
    {
        mGoogleApiClient.connect();
    }

    public boolean isReady()
    {
        return isGPSClientReady;
    }

    public Location getLocation()
    {
        return LocationServices.FusedLocationApi.getLastLocation (mGoogleApiClient);
    }

    public void disconnect()
    {
        mGoogleApiClient.disconnect();
    }

    /**
     * GPS interface methods
     */

    @Override
    public void onConnected (Bundle bundle)
    {
        Log.d (TAG, "Successfully connected to the GPS Google API");
        isGPSClientReady = true;
    }

    @Override
    public void onConnectionSuspended (int i)
    {
        Log.w (TAG, "Connection with the GPS Google API suspended");
        isGPSClientReady = false;
    }

    @Override
    public void onConnectionFailed (ConnectionResult connectionResult)
    {
        Log.w (TAG, "Connection with the GPS Google API failed");
        isGPSClientReady = false;
    }

    public static boolean isGPSEnabled (Context context)
    {
        final LocationManager manager = (LocationManager) context.getSystemService (
                Context.LOCATION_SERVICE);

        return manager.isProviderEnabled (LocationManager.GPS_PROVIDER);
    }

    public static void showEnableDialog (final Context context)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder (context);
        builder.setMessage (R.string.gpsDisabled)
                .setCancelable (false)
                .setPositiveButton (R.string.yesOption, new DialogInterface.OnClickListener()
                {
                    public void onClick (final DialogInterface dialog, final int id)
                    {
                        context.startActivity (new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton (R.string.noOption, new DialogInterface.OnClickListener()
                {
                    public void onClick (final DialogInterface dialog, final int id)
                    {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
