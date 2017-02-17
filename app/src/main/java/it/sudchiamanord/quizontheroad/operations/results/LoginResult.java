package it.sudchiamanord.quizontheroad.operations.results;

public class LoginResult extends GeneralResult
{
    public LoginResult (String sessionKey, String username, int idUse, String lastname,
                        String firstname, String birth)
    {
        super (true, -1, true);
        mSessionKey = sessionKey;
        mUsername = username;
        mIdUse = idUse;
        mLastname = lastname;
        mFirstname = firstname;
        mBirth = birth;
    }

    public LoginResult (int error)
    {
        super (false, error, false);
        mSessionKey = null;
        mUsername = null;
        mIdUse = 0;
        mLastname = null;
        mFirstname = null;
        mBirth = null;
    }

    public String getSessionKey()
    {
        return mSessionKey;
    }


    private final String mSessionKey;
    private final String mUsername;
    private final int mIdUse;
    private final String mLastname;
    private final String mFirstname;
    private final String mBirth;
}
