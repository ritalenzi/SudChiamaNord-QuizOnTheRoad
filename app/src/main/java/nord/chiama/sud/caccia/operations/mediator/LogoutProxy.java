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
import nord.chiama.sud.caccia.operations.results.LogoutResult;
import nord.chiama.sud.caccia.operations.mediator.requests.Request;
import nord.chiama.sud.caccia.utils.Utils;

public class LogoutProxy
{
    private final String TAG = LogoutProxy.class.getSimpleName();

    private HttpURLConnection httpConn;

    LogoutProxy(String requestURL) throws IOException
    {
        URL url = new URL (requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoOutput (true);
        httpConn.setRequestMethod ("POST");
        httpConn.setConnectTimeout (10000);
        httpConn.setRequestProperty ("Content-Type", "application/json");
    }

    void logout (String sessionKey) throws IOException
    {
        Gson gson = new Gson();
        Request request = new Request();
        request.setAction ("logout");
        request.setSessionKey (sessionKey);
        String dataRequest = gson.toJson (request);
        try {
            OutputStreamWriter wr = new OutputStreamWriter (httpConn.getOutputStream());
            wr.write (dataRequest);
            wr.flush();
        }
        catch (IOException e) {
            Log.e(TAG, "Problem in opening the connection", e);
            throw new IOException (e);
        }
    }

    LogoutResult getResponse() throws IOException
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
                    case "s002":
                        return new LogoutResult (true, R.string.successfulLogout, true);

                    case "e002":
                    case "e004":
                    case "e005":
                        return new LogoutResult (false, R.string.failedLogout, false);

                    default:
                        throw new JSONException ("Wrong num value " + num);
                }
            }
            catch (JSONException e) {
                Log.e (TAG, "Problem in parsing the logout response", e);
                throw new IOException (e);
            }
        }

        throw new IOException ("Received response code " + responseCode + " instead of " +
                HttpURLConnection.HTTP_OK);
    }
}
