package it.sudchiamanord.quizontheroad.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.operations.LoginOps;
import it.sudchiamanord.quizontheroad.utils.GenericActivity;
import it.sudchiamanord.quizontheroad.utils.RingProgressDialog;
import it.sudchiamanord.quizontheroad.utils.Tags;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends GenericActivity<LoginOps>
{

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    private EditText mUser;
    private EditText mPassword;
    private CheckBox mShowPw;
    private Button mLoginBtn;

    private String mSessionKey;
//    private int mNStages = -1;

    private RingProgressDialog mOpProgressDialog;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        setContentView (R.layout.activity_login);

        mOpProgressDialog = new RingProgressDialog (LoginActivity.this);

        mUser = (EditText) findViewById (R.id.loginUser);
        mPassword = (EditText) findViewById (R.id.loginPassword);
        mShowPw = (CheckBox) findViewById (R.id.loginShowPassword);

        mShowPw.setOnClickListener (new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                checkPasswordFormat();
            }
        });

        mLoginBtn = (Button) findViewById (R.id.loginButton);

        Intent intent = getIntent();
        Map<String, String> activeMatches = (HashMap<String, String>) intent.getSerializableExtra (Tags.ACTIVE_MATCHES);
        for (String id : activeMatches.keySet()) {
            Log.i (TAG, id + ": " + activeMatches.get (id));
        }

        super.onCreate (savedInstanceState, LoginOps.class);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        getMenuInflater().inflate (R.menu.info_action, menu);

        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        Intent intent;
        switch (item.getItemId()) {

            case R.id.action_info:
                intent = new Intent (this, InfoActivity.class);
                startActivity (intent);
                break;
        }

        return true;
    }

    private void checkPasswordFormat()
    {
        int start = mPassword.getSelectionStart();
        int end = mPassword.getSelectionEnd();
        if (mShowPw.isChecked()) {
            mPassword.setTransformationMethod (null);
        }
        else {
            mPassword.setTransformationMethod (new PasswordTransformationMethod());
        }
        mPassword.setSelection (start, end);
    }

    public void login (View view)
    {
        hideKeyboard (this, mUser.getWindowToken());
        hideKeyboard (this, mPassword.getWindowToken());

        String user = mUser.getText().toString();
        String pw = mPassword.getText().toString();
        if ((user.isEmpty()) || (pw.isEmpty())) {
            Toast.makeText (this, getString (R.string.nullUserAndPwMsg), Toast.LENGTH_SHORT).show();
            return;
        }

        TelephonyManager tm = (TelephonyManager) getSystemService (Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        if (imei == null) {
            Toast.makeText (this, getString (R.string.nullIMEIMsg), Toast.LENGTH_SHORT).show();
            return;
        }

        getOps().login (user, pw, imei);
    }

    public void hideKeyboard (Activity activity, IBinder windowToken)
    {
        InputMethodManager mgr = (InputMethodManager) activity.getSystemService (
                Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow (windowToken, 0);
    }

    private void startStagesActivity()
    {
//        Intent intent = new Intent (this, StagesActivity.class);
//        intent.putExtra (Tags.SESSION_KEY, mSessionKey);
//        startActivity (intent);
    }

    public void notifyProgressUpdate (int progress, int dialogTitle, int dialogExpl)
    {
        mOpProgressDialog.updateProgressDialog (progress, dialogTitle, dialogExpl);
    }

    public void notifySuccessfulLogin (String sessionKey)
    {
        mSessionKey = sessionKey;
//        mNStages = nStages;

        if (sessionKey == null) {
            String toastMsg = LoginActivity.this.getString (R.string.loginFailed);
            Toast.makeText (this, toastMsg, Toast.LENGTH_SHORT).show();
            return;
        }

//        int rowDeleted = getContentResolver().delete (StageEntry.CONTENT_URI, null, null);
//        Log.i (TAG, "Deleted " + rowDeleted + " from the database");
//        ContentValues[] cvArray = new ContentValues[5];
//        String idCurrentClue = null;
//        for (int i=0; i<5; i++) {
//            ContentValues cv = new ContentValues();
//            cv.put (StageEntry.COLUMN_NUMBER, i+1);
//            if (i<2) {
//                cv.put (StageEntry.COLUMN_STATUS, StageStatus.passed.name());
//                cv.put (StageEntry.COLUMN_CLUE, "This is the clue for the passed step\nGOOD LUCK!!");
//                cv.put (StageEntry.COLUMN_TEST, StageTest.text.name());
//            }
//            else if (i==2) {
//                cv.put (StageEntry.COLUMN_STATUS, StageStatus.current.name());
//                cv.put (StageEntry.COLUMN_CLUE, "This is the clue for the third step\nGOOD LUCK!!");
//                cv.put (StageEntry.COLUMN_TEST, StageTest.text.name());
//                idCurrentClue = String.valueOf (2);
//            }
//            else {
//                cv.put (StageEntry.COLUMN_STATUS, StageStatus.locked.name());
//                cv.put (StageEntry.COLUMN_CLUE, "This is the clue for the locked step\nGOOD LUCK!!");
//                cv.put (StageEntry.COLUMN_TEST, StageTest.audio.name());
//            }
//            cv.put (StageEntry.COLUMN_CORRECT_POSITION, 0);
//
//            cvArray[i] = cv;
//        }
//        getContentResolver().bulkInsert (StageEntry.CONTENT_URI, cvArray);

//        startStagesActivity (idCurrentClue);
        startStagesActivity();
    }

    public void notifyFailedLogin (int error)
    {
        String toastMsg = getString (R.string.loginFailed) + ": " + getString (error);
        Toast.makeText (this, toastMsg, Toast.LENGTH_SHORT).show();
    }
}

