package it.sudchiamanord.quizontheroad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.operations.ActiveMatchesOps;
import it.sudchiamanord.quizontheroad.utils.GenericActivity;
import it.sudchiamanord.quizontheroad.utils.RingProgressDialog;
import it.sudchiamanord.quizontheroad.utils.Tags;

public class MainActivity extends GenericActivity<ActiveMatchesOps>
{
    private RingProgressDialog mOpProgressDialog;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        setContentView (R.layout.activity_main);

        mOpProgressDialog = new RingProgressDialog (MainActivity.this);

        FloatingActionButton fab = (FloatingActionButton) findViewById (R.id.fab);
        fab.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                CheckBox devel = (CheckBox) findViewById (R.id.devel_check_box);
                getOps().request (devel.isChecked());
            }
        });

        super.onCreate (savedInstanceState, ActiveMatchesOps.class);
    }

    public void notifyProgressUpdate (int progress, int dialogTitle, int dialogExpl)
    {
        mOpProgressDialog.updateProgressDialog (progress, dialogTitle, dialogExpl);
    }

    public void notifySuccess (Map<String, String> activeMatches)
    {
//        for (String id : activeMatches.keySet()) {
//            Log.i (TAG, id + ": " + activeMatches.get (id));
//        }
        Intent intent = new Intent (this, LoginActivity.class);
        intent.putExtra (Tags.ACTIVE_MATCHES, (HashMap) activeMatches);
        startActivity (intent);
    }

    public void notifyFail (int error)
    {
        String toastMsg = getString (R.string.activeMatchesReqFailed) + ": " + getString (error);
        Toast.makeText (this, toastMsg, Toast.LENGTH_SHORT).show();
    }
}
