package it.sudchiamanord.quizontheroad.operations.results;

public class LogoutResult extends GeneralResult
{
    public LogoutResult(boolean success, int message, boolean correctSession)
    {
        super (success, message, correctSession);
    }
}
