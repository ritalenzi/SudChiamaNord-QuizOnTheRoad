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

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.operations.mediator.requests.Details;
import it.sudchiamanord.quizontheroad.operations.mediator.requests.Request;
import it.sudchiamanord.quizontheroad.operations.results.SkipClueResult;
import it.sudchiamanord.quizontheroad.utils.Consts;
import it.sudchiamanord.quizontheroad.utils.Utils;

class SkipClueProxy
{
    private final String TAG = SkipClueProxy.class.getSimpleName();

    private HttpURLConnection httpConn;

    SkipClueProxy(String requestURL) throws IOException
    {
        URL url = new URL (requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoOutput (true);
        httpConn.setRequestMethod ("POST");
        httpConn.setConnectTimeout (10000);
        httpConn.setRequestProperty ("Content-Type", "application/json");
    }

    void skipClue (String sessionKey, int serverClueId) throws IOException
    {
        Gson gson = new Gson();
        Details details = new Details();
        details.setIdain (serverClueId);
        Request request = new Request();
        request.setAction (Consts.Actions.skipClue);
        request.setSessionKey (sessionKey);
        request.setDetails (details);
        String dataRequest = gson.toJson (request);
        try {
            OutputStreamWriter wr = new OutputStreamWriter(httpConn.getOutputStream());
            wr.write (dataRequest);
            wr.flush();
        }
        catch (IOException e) {
            Log.e(TAG, "Problem in opening the connection", e);
            throw new IOException(e);
        }
    }

    SkipClueResult getResponse() throws IOException
    {
        int responseCode = httpConn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream is = new BufferedInputStream (httpConn.getInputStream());
            String response = Utils.convertStreamToString (is);
            Log.d (TAG, "Response: " + response);   // TODO: remove

            is.close();
            httpConn.disconnect();

            try {
                JSONObject jsonResponse = new JSONObject(response);
                String num = jsonResponse.getString ("num");
                switch (num) {
                    case "s009":
                        return new SkipClueResult (true, R.string.skipClueSucceded, true);

                    case "e014":
                        return new SkipClueResult (false, R.string.skipClueFailed, true);

                    case "e002":
                    case "e004":
                    case "e005":
                        return new SkipClueResult (false, R.string.noServerSession, false);

                    default:
                        throw new JSONException("Wrong num value " + num);
                }
            }
            catch (JSONException e) {
                Log.e (TAG, "Problem in parsing the login response", e);
                throw new IOException(e);
            }
        }

        throw new IOException("Received response code " + responseCode + " instead of " +
                HttpURLConnection.HTTP_OK);
    }
}
