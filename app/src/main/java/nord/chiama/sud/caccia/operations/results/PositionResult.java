package nord.chiama.sud.caccia.operations.results;

import nord.chiama.sud.caccia.stage.Status;

public class PositionResult extends GeneralResult
{
    /**
     * Constructor used when the position is updated
     * @param success true if the operation was successful
     * @param message int value corresponding to the message
     * @param correctSession true if the session is still correctly open
     */
    public PositionResult (boolean success, int message, boolean correctSession)
    {
        super (success, message, correctSession);
        mStatus = null;
    }

    /**
     * Constructor used when the position update has already been sent and the app is waiting
     * for the server to confirm its correctness
     * @param status position update status
     * @param correctSession true if the session is still correctly open
     */
    public PositionResult (Status status, int message, boolean correctSession)
    {
        super (true, message, correctSession);
        mStatus = status;
    }

    /**
     * Constructor
     * @param success true if the operation was successful
     * @param status position update status
     * @param message int value corresponding to the message
     * @param correctSession true if the session is still correctly open
     */
    public PositionResult (boolean success, Status status, int message, boolean correctSession)
    {
        super (success, message, correctSession);
        mStatus = status;
    }

    public Status getStatus()
    {
        return mStatus;
    }


    private final Status mStatus;
}
