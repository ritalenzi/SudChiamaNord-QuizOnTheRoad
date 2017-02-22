package it.sudchiamanord.quizontheroad.operations;

import android.app.Activity;

import it.sudchiamanord.quizontheroad.operations.results.ActiveMatchesResult;
import it.sudchiamanord.quizontheroad.utils.ConfigurableOps;
import it.sudchiamanord.quizontheroad.utils.GenericAsyncTaskOps;

public class ActiveMatchesOps implements ConfigurableOps, GenericAsyncTaskOps<String, Integer, ActiveMatchesResult>
{
    /**
     * Default constructor that's needed by the GenericActivity framework
     */
    public ActiveMatchesOps()
    {
    }

    @Override
    public void onConfiguration (Activity activity, boolean firstTimeIn)
    {

    }

    @Override
    public void publishProgress(int progress) {

    }

    @Override
    public ActiveMatchesResult doInBackground(String... param) {
        return null;
    }

    @Override
    public void onPostExecute(ActiveMatchesResult activeMatchesResult, String... param) {

    }
}
