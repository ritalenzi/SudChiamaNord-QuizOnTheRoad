package it.sudchiamanord.quizontheroad.operations.results;

public class UploadResult extends GeneralResult
{
    public UploadResult(boolean success, int message, boolean correctSession, UploadStatus status)
    {
        super (success, message, correctSession);
        mStatus = status;
    }

    public UploadStatus getStatus()
    {
        return mStatus;
    }

    private final UploadStatus mStatus;

    public enum UploadStatus
    {
        file_too_big,
        finished    // either successful or not
    }
}
