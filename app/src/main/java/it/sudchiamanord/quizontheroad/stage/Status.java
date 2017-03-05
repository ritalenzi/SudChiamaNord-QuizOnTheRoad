package it.sudchiamanord.quizontheroad.stage;


import it.sudchiamanord.quizontheroad.R;

/**
 * Lists all the possible status of a stage
 */
public enum Status
{
    locked (-1, R.string.stageLocked),
    current (0, R.string.stageCurrent),
    current_position_update_failed (-2, R.string.stageCurrentPositionUpdateFailed), // this does not come from the server but from the CheckPositionService
    current_position_sent (1, R.string.stageCurrentPositionSent),
    current_wrong_position (2, R.string.stageCurrentWrongPosition),
    current_position_confirmed (3, R.string.stageCurrentPositionConfirmed),
    skipped_position (4, R.string.stageSkipped),
    passed (5, R.string.stagePassed),
    wrong (-3, R.string.stageWrong);    // this does not come from the server but from the CheckPositionService

    Status (int serverValue, int appValue)
    {
        this.serverValue = serverValue;
        this.appValue = appValue;
    }

    public static Status getStatusFromServerValue (int serverValue)
    {
        for (Status st : values()) {
            if (st.serverValue == serverValue) {
                return st;
            }
        }

        return locked;
    }

    public int getServerValue()
    {
        return serverValue;
    }

    public int getAppValue()
    {
        return appValue;
    }

    private final int serverValue;
    private final int appValue;
}
