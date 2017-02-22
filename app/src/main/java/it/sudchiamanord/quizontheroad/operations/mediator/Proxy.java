package it.sudchiamanord.quizontheroad.operations.mediator;

import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.operations.results.ActiveMatchesResult;
import it.sudchiamanord.quizontheroad.operations.results.LoginResult;

public class Proxy
{
    private static final String TAG = Proxy.class.getSimpleName();

    private static final String MANAGER_CONN_URL = "http://www.sudchiamanord.com/quizontheroad/app/manager.php";
    private static final String UPLOAD_CONN_URL = "http://www.sudchiamanord.com/appcaccia/upload.php";

    private static final int MAX_UPLOAD_SIZE = 25000000;

    public static ActiveMatchesResult doActiveMatchesRequest (boolean setDevel)
    {
        try {
            ActiveMatchesProxy amProxyObj = new ActiveMatchesProxy (MANAGER_CONN_URL);
            amProxyObj.request (setDevel);
            return amProxyObj.getResult();
        }
        catch (IOException e) {
            Log.e (TAG, "Problem in opening the connection", e);
        }

        return new ActiveMatchesResult (R.string.wrongActiveMatchesResponse);
    }

    public static LoginResult doLogin (String user, String password, String imei)
    {
        try {
            LoginProxy loginProxyObj = new LoginProxy (MANAGER_CONN_URL);
            loginProxyObj.login (user, password, imei);
            return loginProxyObj.getLoginResult();
        }
        catch (IOException e) {
            Log.e (TAG, "Problem in opening the connection", e);
        }

        return new LoginResult (R.string.loginFailed);
    }

//    public static UploadResult doFileUpload (String sessionKey, Uri filePath, String filename,
//                                             String idInd, UploadCallback callback)
//    {
//        try {
//            // Checking the video size
//            FileInputStream inputStream = new FileInputStream(filePath.toString());
//            byte[] buffer = new byte[4096];
//            int bytesRead = -1;
//            int totalBytes = 0;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                totalBytes += bytesRead;
//            }
//            inputStream.close();
//            Log.d (TAG, "Total bytes: " + totalBytes);
//            if (totalBytes > MAX_UPLOAD_SIZE) {
//                return new UploadResult (false, R.string.fileTooBig, true,
//                        UploadResult.UploadStatus.file_too_big);
//            }
//
//
//            MultipartProxy multipart = new MultipartProxy (MANAGER_CONN_URL, "UTF-8");
//
//            multipart.addFormField ("sessionKey", sessionKey);
//            multipart.addFormField ("action", "setRespClue");
//            multipart.addFormField ("idain", idInd);
//
//            multipart.addFilePart ("uploadedfile", new File(filePath.toString()), callback,
//                    totalBytes);
//
//            multipart.finish();
//            return multipart.getResponse();
//        }
//        catch (IOException e) {
//            Log.e (TAG, "error: " + e.getMessage(), e);
//        }
//
//        return null;
//    }

//    public static ActualMatchResult getActualMatch (String sessionKey)
//    {
//        try {
//            ActualMatchProxy amUtility = new ActualMatchProxy(MANAGER_CONN_URL);
////            Log.d (TAG, "Session key: " + sessionKey);
//            amUtility.request(sessionKey);
//
//            return amUtility.getResponse();
//        }
//        catch (IOException e) {
//            Log.e (TAG, "Problem in getting the actual match situation", e);
//        }
//
//        return null;
//    }

//    public static PositionResult sendPeriodicPositionUpdate (String sessionKey, double latitude,
//                                                             double longitude)
//    {
//        try {
//            PositionProxy positionProxy = new PositionProxy (MANAGER_CONN_URL);
//            positionProxy.setActualPosition (sessionKey, latitude, longitude);
//
//            return positionProxy.getResponse();
//        }
//        catch (IOException e) {
//            Log.e (TAG, "Problem in sending the current position", e);
//        }
//
//        return null;
//    }

//    public static PositionResult sendCluePositionUpdate (String sessionKey, String serverClueId,
//                                                         double latitude, double longitude)
//    {
//        try {
//            PositionProxy positionProxy = new PositionProxy (MANAGER_CONN_URL);
//            positionProxy.setCluePosition (sessionKey, Integer.parseInt(serverClueId), latitude,
//                    longitude);
//
//            return positionProxy.getResponse();
//        }
//        catch (IOException e) {
//            Log.e (TAG, "Problem in sending the current position", e);
//        }
//
//        return null;
//    }

//    public static PositionResult checkPositionUpdate (String sessionKey, String serverClueId)
//    {
//        try {
//            PositionProxy positionProxy = new PositionProxy (MANAGER_CONN_URL);
//            positionProxy.checkPosition (sessionKey, Integer.parseInt (serverClueId));
//
//            return positionProxy.getResponse();
//        }
//        catch (NumberFormatException e) {
//            Log.e (TAG, "Impossible to convert the serverClueId to int", e);
//        }
//        catch (IOException e) {
//            Log.e (TAG, "Problem in sending the current position", e);
//        }
//
//        return null;
//    }

//    public static SkipClueResult skipClue (String sessionKey, String serverClueId)
//    {
//        try {
//            SkipClueProxy skipClueProxy = new SkipClueProxy(MANAGER_CONN_URL);
//            skipClueProxy.skipClue(sessionKey, Integer.parseInt(serverClueId));
//
//            return skipClueProxy.getResponse();
//        }
//        catch (NumberFormatException e) {
//            Log.e (TAG, "Impossible to convert the serverClueId to int", e);
//        }
//        catch (IOException e) {
//            Log.e (TAG, "Problem in skipping the clue", e);
//        }
//
//        return null;
//    }

//    public static LogoutResult doLogout (String sessionKey)
//    {
//        try {
//            LogoutProxy logoutProxy = new LogoutProxy(MANAGER_CONN_URL);
//            logoutProxy.logout (sessionKey);
//
//            return logoutProxy.getResponse();
//        }
//        catch (IOException e) {
//            Log.e (TAG, "Problem in logging out", e);
//        }
//
//        return null;
//    }
}
