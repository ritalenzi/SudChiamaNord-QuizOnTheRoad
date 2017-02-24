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

    public void addMatch (String id, String name)
    {
        mMatches.add (new Match (id, name));
    }

    public Map<String, String> getMatchesAsMap()
    {
        Map<String, String> map = new HashMap<>();
        for (Match match : mMatches) {
            map.put (match.id, match.name);
        }

        return map;
    }

    private final List<Match> mMatches;

    private class Match
    {
        Match (String id, String name)
        {
            this.id = id;
            this.name = name;
        }

        public final String id;
        public final String name;
    }
}
