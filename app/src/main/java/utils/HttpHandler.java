package utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class HttpHandler {

    private final OkHttpClient okHttpClient;
    private static final String PROTOCOL = "http";
    private static final String HOST = "39.108.73.166";
    private static final String PORT = "8080";
    private static final String PRE_URL = PROTOCOL + "://" + HOST + ":" + PORT + "/";

    private static final String LOGIN_URL = PRE_URL + "login";
    private static final String REGISTER_URL = PRE_URL + "register";

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public HttpHandler(final OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public enum State {
        DuplicatePhoneNumber,
        NoSuchUser,
        WrongPassword,
        Valid
    }

    /**
     * @param phoneNumber The phone number string that used for login
     * @param password    The password string that used for login
     * @return The validation result state
     * @author Kemo
     * @description login
     */
    public State getLoginResponseState(final String phoneNumber, final String password) {
        String url = LOGIN_URL + String.format(
                "?phone=%s&password=%s",
                phoneNumber,
                password
        );
        return parseResponseJson(getHttpResponseString(url));
    }

    /**
     * @param phoneNumber
     * @param username
     * @param password
     * @return
     * @author Kemo
     * @description register
     */
    public State getRegisterResponseState(final String phoneNumber, final String username, final String password) {
        String url = REGISTER_URL + String.format(
                "?phone=%s&username=%s&password=%s",
                phoneNumber,
                username,
                password
        );
        return parseResponseJson(getHttpResponseString(url));
    }

    private String getHttpResponseString(final String url) {
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.e(TAG, "Got response from server");
                assert response.body() != null;
                return response.body().string();
            }
        } catch (IOException e) {
            Log.e(TAG, "error in response of get request");
            e.printStackTrace();
        }
        return null;
    }

    private String postHttpResponseString(final String url, final String json) {
        OkHttpClient httpClient = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
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

    private State parseResponseJson(final String jsonData) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
            switch (jsonObject.getString("state")) {
                case "DuplicatePhoneNumber":
                    return State.DuplicatePhoneNumber;
                case "NoSuchUser":
                    return State.NoSuchUser;
                case "WrongPassword":
                    return State.WrongPassword;
                case "Valid":
                    return State.Valid;
                default:
                    return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
