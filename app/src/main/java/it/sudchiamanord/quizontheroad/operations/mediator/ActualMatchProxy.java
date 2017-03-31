package it.sudchiamanord.quizontheroad.operations.mediator;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.operations.mediator.requests.Request;
import it.sudchiamanord.quizontheroad.operations.mediator.responses.Indizi;
import it.sudchiamanord.quizontheroad.operations.mediator.responses.Tec;
import it.sudchiamanord.quizontheroad.operations.results.ActualMatchResult;
import it.sudchiamanord.quizontheroad.stage.Stage;
import it.sudchiamanord.quizontheroad.stage.Status;
import it.sudchiamanord.quizontheroad.stage.Test;
import it.sudchiamanord.quizontheroad.utils.Consts;
import it.sudchiamanord.quizontheroad.utils.Utils;

class ActualMatchProxy
{
    private final String TAG = ActualMatchProxy.class.getSimpleName();

    private HttpURLConnection httpConn;

    ActualMatchProxy(String requestURL) throws IOException
    {
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoOutput (true);
        httpConn.setRequestMethod ("POST");
        httpConn.setConnectTimeout (10000);
        httpConn.setRequestProperty ("Content-Type", "application/json");
    }

    void request (String sessionKey) throws IOException
    {
        Gson gson = new Gson();
        Request request = new Request();
        request.setAction (Consts.Actions.getActualMatch);
        request.setSessionKey (sessionKey);
        String dataRequest = gson.toJson (request);
        try {
            OutputStreamWriter wr = new OutputStreamWriter(httpConn.getOutputStream());
            wr.write (dataRequest);
            wr.flush();
        }
        catch (IOException e) {
            Log.e (TAG, "Problem in opening the connection", e);
            throw new IOException(e);
        }
    }

    ActualMatchResult getResponse() throws IOException
    {
        int responseCode = httpConn.getResponseCode();
        Log.d (TAG, "Response Code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream is = new BufferedInputStream (httpConn.getInputStream());
            String response = Utils.convertStreamToString (is);
            Log.d (TAG, "Actual Match Response: " + response);   // TODO: remove

            is.close();
            httpConn.disconnect();

            try {
                JSONObject jsonResponse = new JSONObject(response);
                String num = jsonResponse.getString ("num");
                switch (num) {
                    case "s006":
                        JSONObject jstec = jsonResponse.getJSONObject ("tec");
                        Gson gson = new Gson();
                        Tec tec = gson.fromJson (jstec.toString(), Tec.class);
                        return new ActualMatchResult (true, R.string.stagesRetrievalSuccess, true,
                                extractStages (tec));

                    case "e011":
                        return new ActualMatchResult (false, R.string.matchNotActive, true, null);

                    case "e002":
                    case "e004":
                    case "e005":
                        return new ActualMatchResult (false, R.string.stagesRetrievalError, false, null);

                    default:
                        throw new JSONException("Wrong num value " + num);
                }
            }
            catch (JSONException e) {
                Log.e (TAG, "Problem in parsing the actual match response", e);
                throw new IOException(e);
            }
        }

        throw new IOException("Received response code " + responseCode + " instead of " +
                HttpURLConnection.HTTP_OK);
    }

    private List<Stage> extractStages (Tec tec)
    {
        List<Stage> stages = new ArrayList<>();

        int currClueId = tec.getIdain();
        List<Indizi> clues = tec.getIndizi();
        for (Indizi clue : clues) {
            Stage stage = new Stage();
            stage.setNumber (clue.getOrdin());
            stage.setServerId (String.valueOf (clue.getIdind()));

            int status = clue.getStato();
            stage.setStatus (Status.getStatusFromServerValue (status));

            stage.setLocationClue (clue.getLutxt());
            stage.setMultimediaClue (clue.getIntxt());

            int test = clue.getTipor();
            stage.setTest (Test.getTest (test));

            stage.setWaitPositionConfirmed (clue.getSkipa() == 0);

            stages.add (stage);
        }

        return stages;
    }
}
