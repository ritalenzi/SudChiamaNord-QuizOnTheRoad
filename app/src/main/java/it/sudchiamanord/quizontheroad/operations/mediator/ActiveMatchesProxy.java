package it.sudchiamanord.quizontheroad.operations.mediator;


import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.operations.mediator.requests.Details;
import it.sudchiamanord.quizontheroad.operations.mediator.requests.Request;
import it.sudchiamanord.quizontheroad.operations.mediator.responses.Tec;
import it.sudchiamanord.quizontheroad.operations.results.ActiveMatchesResult;
import it.sudchiamanord.quizontheroad.utils.Consts;
import it.sudchiamanord.quizontheroad.utils.Utils;

class ActiveMatchesProxy
{
    private final String TAG = ActiveMatchesProxy.class.getSimpleName();

    private HttpURLConnection httpConn;

    ActiveMatchesProxy (String requestURL) throws IOException
    {
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoOutput (true);
        httpConn.setRequestMethod ("POST");
        httpConn.setConnectTimeout (10000);
        httpConn.setRequestProperty ("Content-Type", "application/json");
    }

    void request (boolean setDevel) throws IOException
    {
        Gson gson = new Gson();
        Details details = new Details();
        if (setDevel) {
            details.setDevel ("1");
        }
        Request request = new Request();
        request.setAction (Consts.Actions.activeMatches);
        request.setSessionKey ("");
        request.setDetails (details);
        String dataRequest = gson.toJson (request);
        try {
            OutputStreamWriter wr = new OutputStreamWriter (httpConn.getOutputStream());
            wr.write (dataRequest);
            wr.flush();
        }
        catch (IOException e) {
            Log.e (TAG, "Problem in opening the connection", e);
            throw new IOException(e);
        }
    }


    ActiveMatchesResult getResult() throws IOException
    {
        int responseCode = httpConn.getResponseCode();
        Log.d (TAG, "Response Code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream is = new BufferedInputStream (httpConn.getInputStream());
            String response = Utils.convertStreamToString (is);

            is.close();
            httpConn.disconnect();

            try {
                JSONObject jsonResponse = new JSONObject(response);
                String num = jsonResponse.getString ("num");
                switch (num) {
                    case "s000":
                        JSONArray jstecs = jsonResponse.getJSONArray ("tec");
                        Gson gson = new Gson();
                        ActiveMatchesResult result = new ActiveMatchesResult();
                        for (int i=0; i<jstecs.length(); i++) {
                            JSONObject jstec = jstecs.getJSONObject (i);
                            Tec tec = gson.fromJson (jstec.toString(), Tec.class);
                            result.addMatch (tec.getIdpar(), tec.getNomep());
                        }

                        return result;

                    case "e007":
                        return new ActiveMatchesResult (R.string.wrongActiveMatchesResponse);

                    default:
                        throw new JSONException ("Wrong num value " + num);
                }
            }
            catch (JSONException e) {
                Log.e (TAG, "Problem in parsing the active matches response", e);
                throw new IOException(e);
            }
        }

        throw new IOException ("Received response code " + responseCode + " instead of " +
                HttpURLConnection.HTTP_OK);
    }
}
