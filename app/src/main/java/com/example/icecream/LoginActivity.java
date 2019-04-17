package com.example.icecream;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import mehdi.sakout.fancybuttons.FancyButton;
import utils.User;

public class LoginActivity extends AppCompatActivity {
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
        login = (FancyButton) findViewById(R.id.t);
        signUp = (TextView) findViewById(R.id.signup);

    }

    /**
     * @author: Penna
     * @Description: click login event
     */
    public void onLogin(View view){
        boolean success = checkLoginValid();
        if(success){
            // TODO try to connect server to login
            goToPersonalDetailPage();
        }
    }

    /**
     * @author: Penna
     * @Description: check the usernameEdit and passwordEdit valid
     */
    public boolean checkLoginValid(){
        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        if (username.equals("") || password.equals("")){
            Toast.makeText(LoginActivity.this, "username or password should not be empty",  Toast.LENGTH_LONG).show();
            return false;
        }else if(true){
            // TODO check more restriction that username and password should satisfy
            return false;
        }
            Toast.makeText(LoginActivity.this, "login successful",  Toast.LENGTH_LONG).show();
        return true;
    }

    /**
     * @author: Penna
     * @Description: Jump to the PersonalDetailPage
     */
    public void goToPersonalDetailPage(){
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
    public void onSignUp(View view){
        Context context = LoginActivity.this;
        Class destinationActivity = RegisterActivity.class;
        Intent startRegisterActivityIntent = new Intent(context, destinationActivity);
        startRegisterActivityIntent.putExtra(Intent.EXTRA_TEXT, usernameEdit.getText().toString());
        startActivity(startRegisterActivityIntent);
    }



}
