package it.sudchiamanord.quizontheroad.operations.results;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActiveMatchesResult extends GeneralResult
{
    public ActiveMatchesResult()
    {
        super (true, -1, true);
        mMatches = new ArrayList<>();
    }

    public ActiveMatchesResult (int error)
    {
        super (false, error, false);
        mMatches = new ArrayList<>();
    }

    public void addMatch (String id, String name)
    {
        mMatches.add (new Match (id, name));
    }

    public List<Match> getMatches()
    {
        return Collections.unmodifiableList (mMatches);
    }

    private final List<Match> mMatches;

    public class Match
    {
        public Match (String id, String name)
        {
            this.id = id;
            this.name = name;
        }

        public final String id;
        public final String name;
    }
}
