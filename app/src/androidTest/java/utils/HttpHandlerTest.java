package utils;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.OkHttpClient;

import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class HttpHandlerTest {

    @Test
    public void getLoginResponseStateWrongPasswordTest() {
        OkHttpClient client = new OkHttpClient();
        HttpHandler httpHandler = new HttpHandler(client);
        HttpHandler.State state = httpHandler.getLoginResponseState("15602432271", "dhkjahdjka");
        assertEquals(state, HttpHandler.State.WrongPassword);
    }

    @Test
    public void getLoginResponseStateNoSuchUserTest() {
        OkHttpClient client = new OkHttpClient();
        HttpHandler httpHandler = new HttpHandler(client);
        HttpHandler.State state = httpHandler.getLoginResponseState("15602432279", "dhkjahdjka");
        assertEquals(state, HttpHandler.State.NoSuchUser);
    }

    @Test
    public void getLoginResponseStateValidTest() {
        OkHttpClient client = new OkHttpClient();
        HttpHandler httpHandler = new HttpHandler(client);
        HttpHandler.State state = httpHandler.getLoginResponseState("15602432271", "zjsjsjdjdd");
        assertEquals(state, HttpHandler.State.Valid);
    }

    @Test
    public void getRegisterResponseStateDupTest(){
        OkHttpClient client = new OkHttpClient();
        HttpHandler httpHandler = new HttpHandler(client);
        HttpHandler.State state = httpHandler.getRegisterResponseState("15602432271", "zjsjdfdjdd", "shdskjashdk");
        assertEquals(state, HttpHandler.State.DuplicatePhoneNumber);
    }

    @Test
    public void getRegisterResponseStateValidTest(){
        OkHttpClient client = new OkHttpClient();
        HttpHandler httpHandler = new HttpHandler(client);
        HttpHandler.State state = httpHandler.getRegisterResponseState("15602432274", "zjsjdfdjdd", "shdskjashdk");
        assertEquals(state, HttpHandler.State.Valid);
    }
}