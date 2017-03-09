package it.sudchiamanord.quizontheroad.operations.results;

import java.util.List;

import it.sudchiamanord.quizontheroad.stage.Stage;


public class ActualMatchResult extends GeneralResult
{
    public ActualMatchResult (boolean success, int message, boolean correctSession, List<Stage> stages)
    {
        super (success, message, correctSession);
        mStages = stages;
    }

    public List<Stage> getStages()
    {
        return mStages;
    }

    private final List<Stage> mStages;
}
