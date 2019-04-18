package com.example.icecream;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.OkHttpClient;
import utils.HttpHandler;
import utils.User;
import utils.Validator;

public class LoginActivity extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    HttpHandler httpHandler = new HttpHandler(client);
    MaterialEditText usernameEdit;
    MaterialEditText passwordEdit;
    FancyButton login;
    TextView signUp;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEdit = (MaterialEditText) findViewById(R.id.username);
        passwordEdit = (MaterialEditText) findViewById(R.id.password);
        login = (FancyButton) findViewById(R.id.login);
        signUp = (TextView) findViewById(R.id.signup);

    }

    /**
     * @author: Penna
     * @Description: click login event
     */
    public void onLogin(View view) {
        Object usernameEditText = usernameEdit.getText();
        Object passwordEditText = passwordEdit.getText();
        if (usernameEditText == null || passwordEditText == null) {
            Toast.makeText(LoginActivity.this, "login fail", Toast.LENGTH_LONG).show();
            return;
        }
        String username = usernameEditText.toString();
        String password = passwordEditText.toString();
        boolean success = checkLoginValid(username, password);
        if (success) {
            // httpHandler.getLoginResponse(phoneNumber, password);
            goToPersonalDetailPage();
        }
    }

    /**
     * @author: Penna, Kemo
     * @Description: check the usernameEdit and passwordEdit valid
     */
    public boolean checkLoginValid(String username, String password) {
        Validator.ValState userState = Validator.validate(username);
        if (userState != Validator.ValState.Valid) {
            Toast.makeText(LoginActivity.this, "login fail", Toast.LENGTH_LONG).show();
            return false;
        }

        Validator.ValState passwordState = Validator.validate(password);
        if (passwordState != Validator.ValState.Valid) {
            Toast.makeText(LoginActivity.this, "login fail", Toast.LENGTH_LONG).show();
            return false;
        }

        Toast.makeText(LoginActivity.this, "login succeed", Toast.LENGTH_LONG).show();
        return true;
    }

    /**
     * @author: Penna
     * @Description: Jump to the PersonalDetailPage
     */
    public void goToPersonalDetailPage() {
        Context context = LoginActivity.this;
        Class destinationActivity = PersonalDetailActivity.class;
        Intent startPersonalActivityIntent = new Intent(context, destinationActivity);
        startPersonalActivityIntent.putExtra(Intent.EXTRA_TEXT, usernameEdit.getText().toString());
        startActivity(startPersonalActivityIntent);
    }

    /**
     * @author: Penna
     * @Description: onClick sign up function
     */
    public void onSignUp(View view) {
        Context context = LoginActivity.this;
        Class destinationActivity = RegisterActivity.class;
        Intent startRegisterActivityIntent = new Intent(context, destinationActivity);
        startRegisterActivityIntent.putExtra(Intent.EXTRA_TEXT, usernameEdit.getText().toString());
        startActivity(startRegisterActivityIntent);
    }


}
