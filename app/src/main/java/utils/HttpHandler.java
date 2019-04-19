package utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class HttpHandler {

    private final OkHttpClient okHttpClient;
    private final static String PROTOCOL = "http";
    private final static String HOST = "39.108.73.166";
    private final static String PORT = "8080";
    private final static String PRE_URL = PROTOCOL + "://" + HOST + ":" + PORT + "/";

    private final static String LOGIN_URL = PRE_URL + "login";
    private final static String REGISTER_URL = PRE_URL + "register";

    public HttpHandler(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public enum State {
        DuplicatePhoneNumber,
        NoSuchUser,
        WrongPassword,
        Valid
    }

    /**
     * @param phoneNumber
     * @param password
     * @return
     * @description login
     */
    public State getLoginResponseState(String phoneNumber, String password) {
        String url = LOGIN_URL + String.format(
                "?phone=%s&password=%s", phoneNumber, password);
        return parseResponseJson(getHttpResponseString(url));
    }

    /**
     * @param phoneNumber
     * @param username
     * @param password
     * @return
     * @description register
     */
    public State getRegisterResponseState(String phoneNumber, String username, String password) {
        String url = REGISTER_URL + String.format(
                "?phone=%s&username=%s&password=%s", phoneNumber, username, password);
        return parseResponseJson(getHttpResponseString(url));
    }

    private String getHttpResponseString(String url) {
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.body() == null) {
                Log.e(TAG, "response body is null");
                return null;
            }
            return response.body().string();
        } catch (IOException e) {
            Log.e(TAG, "error in response of get request");
            e.printStackTrace();
        }
        return null;
    }

    private String postHttpResponseString(String url, String name, String value) {
        OkHttpClient httpClient = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add(name, value)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        try {
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.e(TAG, "Got response from server");
                assert response.body() != null;
                return response.body().string();
            }

        } catch (IOException e) {
            Log.e(TAG, "error in getting response post request");
        }
        return null;
    }

    private State parseResponseJson(String jsonData) {
        JSONObject Jobject;
        try {
            Jobject = new JSONObject(jsonData);
            switch (Jobject.getString("state")) {
                case "DuplicatePhoneNumber":
                    return State.DuplicatePhoneNumber;
                case "NoSuchUser":
                    return State.NoSuchUser;
                case "WrongPassword":
                    return State.WrongPassword;
                case "Valid":
                    return State.Valid;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
