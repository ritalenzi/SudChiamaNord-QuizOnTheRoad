package nord.chiama.sud.caccia.operations.mediator;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import nord.chiama.sud.caccia.R;
import nord.chiama.sud.caccia.operations.callbacks.UploadCallback;
import nord.chiama.sud.caccia.operations.results.UploadResult;
import nord.chiama.sud.caccia.utils.Utils;

/**
 * This utility class provides an abstraction layer for sending multipart HTTP
 * POST requests to a web server.
 * @author www.codejava.net
 */
class MultipartProxy
{
    private final String TAG = getClass().getSimpleName();

    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     * @param requestURL
     * @param charset
     * @throws IOException
     */
    MultipartProxy(String requestURL, String charset) throws IOException
    {
        this.charset = charset;

        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";

        URL url = new URL (requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches (false);
        httpConn.setDoOutput (true); // indicates POST method
        httpConn.setDoInput (true);
        httpConn.setRequestProperty ("Content-Type", "multipart/form-data; boundary=" + boundary);
        httpConn.setRequestProperty ("User-Agent", "CodeJava Agent");
        httpConn.setRequestProperty ("Test", "Bonjour");
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter (new OutputStreamWriter (outputStream, charset), true);
    }

    /**
     * Adds a form field to the request
     * @param name field name
     * @param value field value
     */
    void addFormField (String name, String value)
    {
        writer.append ("--" + boundary).append(LINE_FEED);
        writer.append ("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
        writer.append ("Content-Type: text/plain; charset=" + charset).append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append (value).append (LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a upload file section to the request
     * @param fieldName name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    void addFilePart (String fieldName, File uploadFile, UploadCallback callback, int totalBytes)
            throws IOException
    {
        String fileName = uploadFile.getName();
        writer.append ("--" + boundary).append (LINE_FEED);
        writer.append ("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"")
                .append (LINE_FEED);
        writer.append ("Content-Type: " + URLConnection.guessContentTypeFromName (fileName))
                .append(LINE_FEED);
        writer.append ("Content-Transfer-Encoding: binary").append (LINE_FEED);
        writer.append (LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream (uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        int totalBytesRead = 0;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write (buffer, 0, bytesRead);
            totalBytesRead += bytesRead;
            int progress = totalBytesRead * 100 / totalBytes;
            callback.updateProgress (progress);
        }

        outputStream.flush();
        inputStream.close();

        writer.append (LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a header field to the request.
     * @param name - name of the header field
     * @param value - value of the header field
     */
    void addHeaderField (String name, String value)
    {
        writer.append (name + ": " + value).append (LINE_FEED);
        writer.flush();
    }

    /**
     * Completes the request and receives response from the server
     * @throws IOException
     */
    void finish() throws IOException
    {
        List<String> response = new ArrayList<>();

        writer.append (LINE_FEED).flush();
        writer.append ("--");
        writer.append (boundary);
        writer.append ("--");
        writer.append (LINE_FEED);
        writer.close();
    }

    UploadResult getResponse() throws IOException
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
                    case "s010":
                        return new UploadResult (true, R.string.uploadSuccessful, true,
                                UploadResult.UploadStatus.finished);

                    case "e002":
                    case "e004":
                    case "e005":
                        return new UploadResult (false, R.string.noServerSession,
                                false, UploadResult.UploadStatus.finished);

                    case "e016":
                        return new UploadResult (false, R.string.uploadFailed, true,
                                UploadResult.UploadStatus.finished);

                    default:
                        throw new JSONException ("Wrong num value " + num);
                }
            }
            catch (JSONException e) {
                Log.e (TAG, "Problem in parsing the upload file response", e);
                throw new IOException (e);
            }
        }

        throw new IOException ("Received response code " + responseCode + " instead of " +
                HttpURLConnection.HTTP_OK);
    }

}
