package nord.chiama.sud.caccia.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class ServerConnection
{
    private static final String TAG = "ServerConnection";

    private static final String MANAGER_CONN_URL = "http://www.sudchiamanord.com/appcaccia/manager.php";
    private static final String UPLOAD_CONN_URL = "http://www.sudchiamanord.com/appcaccia/upload.php";

    private HttpURLConnection httpConn;

    public ServerConnection() throws ServerConnectionException
    {
        init();
    }

    private void init() throws ServerConnectionException
    {
        if (httpConn != null) {
            reset();
        }

        try {
            URL url = new URL (MANAGER_CONN_URL);
            httpConn = (HttpURLConnection) url.openConnection();
        }
        catch (MalformedURLException e) {
            Log.e (TAG, "Problem in creating the url", e);
            throw new ServerConnectionException();
        }
        catch (IOException e) {
            Log.e (TAG, "Problem in opening the connection", e);
            throw new ServerConnectionException();
        }
    }

    private void reset()
    {
        if (httpConn != null) {
            httpConn.disconnect();
            httpConn = null;
        }
    }

    private void setCookies()
    {
        // Get Cookies form response header and load them to cookieManager
        CookieManager msCookieManager = new java.net.CookieManager();
        Map<String, List<String>> headerFields = httpConn.getHeaderFields();
        List<String> cookiesHeader = headerFields.get ("Set-Cookie");
        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                msCookieManager.getCookieStore().add (null, HttpCookie.parse (cookie).get(0));
            }
        }

        // Get Cookies form cookieManager and load them to connection
        if (msCookieManager.getCookieStore().getCookies().size() > 0)
        {
            //While joining the Cookies, use ',' or ';' as needed. Most of the server are using ';'
            httpConn.setRequestProperty ("Cookie", TextUtils.join(";",
                    msCookieManager.getCookieStore().getCookies()));
        }
    }
}
