package nord.chiama.sud.caccia.operations.mediator;


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

import nord.chiama.sud.caccia.R;
import nord.chiama.sud.caccia.operations.results.PositionResult;
import nord.chiama.sud.caccia.operations.mediator.requests.Details;
import nord.chiama.sud.caccia.operations.mediator.requests.Request;
import nord.chiama.sud.caccia.stage.Status;
import nord.chiama.sud.caccia.utils.Utils;

class PositionProxy
{
    private final String TAG = PositionProxy.class.getSimpleName();

    private HttpURLConnection httpConn;

    PositionProxy(String requestURL) throws IOException
    {
        URL url = new URL (requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoOutput (true);
        httpConn.setRequestMethod ("POST");
        httpConn.setConnectTimeout (10000);
        httpConn.setRequestProperty ("Content-Type", "application/json");
    }

    void setActualPosition (String sessionKey, double latitude, double longitude) throws IOException
    {
        Gson gson = new Gson();
        Details details = new Details();
        details.setLatit (String.valueOf (latitude));
        details.setLongi (String.valueOf (longitude));
        Request request = new Request();
        request.setAction ("setActualPosition");
        request.setSessionKey (sessionKey);
        request.setDetails (details);
        String dataRequest = gson.toJson (request);
        try {
            OutputStreamWriter wr = new OutputStreamWriter (httpConn.getOutputStream());
            wr.write (dataRequest);
            wr.flush();
        }
        catch (IOException e) {
            Log.e (TAG, "Problem in opening the connection", e);
            throw new IOException (e);
        }
    }

    void setCluePosition (String sessionKey, int serverClueId, double latitude, double longitude)
            throws IOException
    {
        Gson gson = new Gson();
        Details details = new Details();
        details.setRlati (String.valueOf (latitude));
        details.setRlong(String.valueOf(longitude));
        details.setIdain (serverClueId);
        Request request = new Request();
        request.setAction ("setCluePosition");
        request.setSessionKey (sessionKey);
        request.setDetails (details);
        String dataRequest = gson.toJson (request);
        try {
            OutputStreamWriter wr = new OutputStreamWriter (httpConn.getOutputStream());
            wr.write (dataRequest);
            wr.flush();
        }
        catch (IOException e) {
            Log.e (TAG, "Problem in opening the connection", e);
            throw new IOException (e);
        }
    }

    void checkPosition (String sessionKey, int serverClueId) throws IOException
    {
        Gson gson = new Gson();
        Details details = new Details();
        details.setIdain (serverClueId);
        Request request = new Request();
        request.setAction ("checkResponsePosition");
        request.setSessionKey (sessionKey);
        request.setDetails (details);
        String dataRequest = gson.toJson (request);
        try {
            OutputStreamWriter wr = new OutputStreamWriter (httpConn.getOutputStream());
            wr.write (dataRequest);
            wr.flush();
        }
        catch (IOException e) {
            Log.e (TAG, "Problem in opening the connection", e);
            throw new IOException (e);
        }
    }

    PositionResult getResponse() throws IOException
    {
        int responseCode = httpConn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream is = new BufferedInputStream (httpConn.getInputStream());
            String response = Utils.convertStreamToString(is);
            Log.d (TAG, "Response: " + response);   // TODO: remove

            is.close();
            httpConn.disconnect();

            try {
                JSONObject jsonResponse = new JSONObject (response);
                String num = jsonResponse.getString ("num");
                switch (num) {
                    case "s005":
                        return new PositionResult (true, R.string.positionCorrectlyRegistered, true);

                    case "s007":
                        return new PositionResult (true, R.string.positionCorrectlyUpdated, true);

                    case "s012":
                        return new PositionResult (false,
                                R.string.positionUpdateFailed, true);

                    case "s052":
                        return new PositionResult (Status.current_position_sent,
                                R.string.checkingPosition, true);

                    case "s050":
                        return new PositionResult (Status.current_position_confirmed,
                                R.string.correctPosition, true);

                    case "s051":
                        return new PositionResult (Status.current_wrong_position,
                                R.string.wrongPosition, true);

                    case "e002":
                    case "e004":
                    case "e005":
                        return new PositionResult (false, R.string.noServerSession, false);

                    case "e014":
                        return new PositionResult (false, Status.wrong,
                                R.string.wrongAnswer, true);

                    default:
                        throw new JSONException ("Wrong num value " + num);
                }
            }
            catch (JSONException e) {
                Log.e (TAG, "Problem in parsing the position response", e);
                throw new IOException (e);
            }
        }

        throw new IOException ("Received response code " + responseCode + " instead of " +
                HttpURLConnection.HTTP_OK);
    }
}
