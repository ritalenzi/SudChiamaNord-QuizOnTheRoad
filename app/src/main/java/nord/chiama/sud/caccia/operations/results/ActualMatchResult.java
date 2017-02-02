package nord.chiama.sud.caccia.operations.results;

import java.util.List;

import nord.chiama.sud.caccia.stage.Stage;

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
