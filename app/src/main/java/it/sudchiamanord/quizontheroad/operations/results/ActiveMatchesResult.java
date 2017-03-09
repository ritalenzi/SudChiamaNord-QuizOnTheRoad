package it.sudchiamanord.quizontheroad.operations.results;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void addMatch (int id, String name)
    {
        mMatches.add (new Match (id, name));
    }

    public List<Match> getMatches()
    {
        if (mMatches == null) {
            return Collections.unmodifiableList (new ArrayList<Match>());
        }

        return Collections.unmodifiableList (mMatches);
    }

    private final List<Match> mMatches;

    public class Match
    {
        Match (int id, String name)
        {
            this.id = id;
            this.name = name;
        }

        public final Integer id;
        public final String name;
    }
}
