package it.sudchiamanord.quizontheroad.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.operations.StagesOps;
import it.sudchiamanord.quizontheroad.operations.results.ActualMatchResult;
import it.sudchiamanord.quizontheroad.services.PositionSenderService;
import it.sudchiamanord.quizontheroad.stage.Stage;
import it.sudchiamanord.quizontheroad.utils.GenericActivity;
import it.sudchiamanord.quizontheroad.utils.RingProgressDialog;
import it.sudchiamanord.quizontheroad.utils.StagesArrayAdapter;
import it.sudchiamanord.quizontheroad.utils.Tags;

public class StagesActivity extends GenericActivity<StagesOps>
{
    private static final String TAG = StagesActivity.class.getSimpleName();

    private RingProgressDialog mOpProgressDialog;

    private ListView mListView;
    private StagesArrayAdapter mAdapter;
    private String mSessionKey;
//    private String mIdInd;  // Id of the current clue

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView (R.layout.activity_stages_list);

        mOpProgressDialog = new RingProgressDialog (StagesActivity.this);

        mSessionKey = getIntent().getStringExtra (Tags.SESSION_KEY);
//        mIdInd = getIntent().getStringExtra (Tags.ID_IND);

        mListView = (ListView) findViewById (R.id.stagesInfoList);
        mAdapter = new StagesArrayAdapter (this);
        mListView.setAdapter (mAdapter);
        mListView.setOnItemClickListener (new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id)
            {
                Stage stage = (Stage) parent.getItemAtPosition (position);
                switch (stage.getStatus()) {
                    case passed:
                        Toast.makeText (getApplicationContext(), R.string.stagePassedToast,
                                Toast.LENGTH_SHORT).show();
                        break;

                    case current:
                    case current_wrong_position:
                    case current_position_sent:
                    case current_position_confirmed:
                    case current_position_update_failed:
                        Intent intent = new Intent(getApplicationContext(), SingleStageActivity.class);
                        intent.putExtra (Tags.SESSION_KEY, mSessionKey);
//                        intent.putExtra (Tags.ID_IND, mIdInd);
                        intent.putExtra (Tags.ID_IND, stage.getServerId());
                        intent.putExtra (Tags.STAGE_NUMBER, stage.getNumber());
                        intent.putExtra (Tags.STAGE_LOCATION_CLUE, stage.getLocationClue());
                        intent.putExtra (Tags.STAGE_MULTIMEDIA_CLUE, stage.getMultimediaClue());
                        intent.putExtra (Tags.STAGE_TEST, stage.getTest().name());
                        intent.putExtra (Tags.STAGE_STATUS, stage.getStatus().getServerValue());
//                        intent.putExtra (Tags.STAGE_CORRECT_POSITION, stage.isPositionCorrect());
                        intent.putExtra (Tags.STAGE_WAIT_POSITION_CONFIRMED, stage.waitForPositionConfirmed());
                        startActivity (intent);
                        break;

                    case locked:
                        Toast.makeText (getApplicationContext(), R.string.stageLockedToast,
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        super.onCreate(savedInstanceState, StagesOps.class);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (mSessionKey == null) {
            Toast.makeText (this, R.string.noServerSession,
                    Toast.LENGTH_SHORT).show();
//            finish();
            // TODO: add stop PositionSenderService in all the activities if session id == null
            PositionSenderService.terminate();
            startActivity (new Intent (this, LoginActivity.class));
            finish();
        }

        if (!PositionSenderService.isRunning()) {
            if (ContextCompat.checkSelfPermission (this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions (this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Tags.READ_LOCATION_REQUEST);
                return;
            }

            Intent intent = new Intent (this, PositionSenderService.class);
            intent.putExtra (Tags.SESSION_KEY, mSessionKey);
            startService (intent);
        }
        else {
            Log.d (TAG, "The PositionSenderService is already running");
        }

        getOps().requestStagesInfo (mSessionKey);
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case Tags.READ_LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Intent intent = new Intent (this, PositionSenderService.class);
                    intent.putExtra (Tags.SESSION_KEY, mSessionKey);
                    startService (intent);

                    getOps().requestStagesInfo (mSessionKey);
                }
                else {
                    Toast.makeText (this, R.string.readLocationPermissionDenied, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        getMenuInflater().inflate (R.menu.info_action, menu);
        getMenuInflater().inflate (R.menu.settings_actions, menu);

        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        Intent intent;
        switch (item.getItemId()) {

            case R.id.action_logout:
                intent = new Intent(this, LogoutActivity.class);
                intent.putExtra (Tags.SESSION_KEY, mSessionKey);
                startActivity (intent);
                break;

            case R.id.action_info:
                intent = new Intent(this, InfoActivity.class);
                startActivity (intent);
                break;
        }

        return true;
    }

    public void notifyProgressUpdate (int progress, int dialogTitle, int dialogExpl)
    {
        mOpProgressDialog.updateProgressDialog (progress, dialogTitle, dialogExpl);
    }

    public void displayResults (ActualMatchResult result, String errorMessage)
    {
        if ((result == null) || (!result.isSessionCorrect()) || (result.getStages() == null) ||
                (result.getStages().size() == 0)) {
            Toast.makeText (getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
            startActivity (new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        mAdapter.clear();
        mAdapter.addAll (result.getStages());
        mAdapter.notifyDataSetChanged();
    }
}
