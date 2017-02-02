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
import nord.chiama.sud.caccia.operations.results.LoginResult;
import nord.chiama.sud.caccia.operations.mediator.requests.Details;
import nord.chiama.sud.caccia.operations.mediator.requests.Request;
import nord.chiama.sud.caccia.operations.mediator.responses.Tec;
import nord.chiama.sud.caccia.utils.Utils;

class LoginProxy
{
    private final String TAG = LoginProxy.class.getSimpleName();

    private HttpURLConnection httpConn;

    LoginProxy(String requestURL) throws IOException
    {
        URL url = new URL (requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoOutput (true);
        httpConn.setRequestMethod ("POST");
        httpConn.setConnectTimeout (10000);
        httpConn.setRequestProperty ("Content-Type", "application/json");
    }

    void login (String user, String password, String imei) throws IOException
    {
        Gson gson = new Gson();
        Details details = new Details();
        details.setUsern (user);
        details.setPaswd (password);
        details.setCimei (imei);
        Request request = new Request();
        request.setAction ("login");
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
            throw new IOException (e);
        }
    }

    LoginResult getLoginResult() throws IOException
    {
        int responseCode = httpConn.getResponseCode();
        Log.d (TAG, "Response Code: " + responseCode);

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
                    case "s001":
                        JSONObject jstec = jsonResponse.getJSONObject ("tec");
                        Gson gson = new Gson();
                        Tec tec = gson.fromJson(jstec.toString(), Tec.class);
                        String sessionKey = tec.getSessionKey();
                        String username = tec.getUsern();
                        int idUse = tec.getIduse();
                        String lastname = tec.getCogno();
                        String firstname = tec.getNomeu();
                        String birth = tec.getDatan();
                        return new LoginResult (sessionKey, username, idUse, lastname, firstname, birth);

                    case "e007":
                        return new LoginResult (R.string.wrongPwMsg);

                    case "e008":
                        return new LoginResult (R.string.wrongUserMsg);

                    case "e010":
                        return new LoginResult (R.string.wrongIMEIMsg);

                    default:
                        throw new JSONException ("Wrong num value " + num);
                }
            }
            catch (JSONException e) {
                Log.e (TAG, "Problem in parsing the login response", e);
                throw new IOException (e);
            }
        }

        throw new IOException ("Received response code " + responseCode + " instead of " + HttpURLConnection.HTTP_OK);
    }
}
