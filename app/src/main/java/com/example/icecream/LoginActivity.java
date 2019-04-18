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
import utils.Validation;

public class LoginActivity extends AppCompatActivity {
    private MaterialEditText phoneEdit;
    private MaterialEditText passwordEdit;
    private FancyButton login;
    private TextView signUp;
    private TextView skip;
    private TextView forget;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phoneEdit = (MaterialEditText) findViewById(R.id.phone);
        passwordEdit = (MaterialEditText) findViewById(R.id.password);
        login = (FancyButton) findViewById(R.id.t);
        signUp = (TextView) findViewById(R.id.signup);
        skip = (TextView) findViewById(R.id.tv_skip);
        forget = (TextView) findViewById(R.id.tv_forget);
        user = new User();

    }

    /**
     * @ author: Penna
     * @ Description: click login event
     */
    public void onLogin(View view) {
        boolean success = checkLoginValid();
        if (success) {
            // TODO try to connect server to login
            if(connectToSever()) {
                goToPersonalDetailPage();
            }else{
                ToastMessage("Login Failed, Try again");
            }
        }
    }


    public boolean connectToSever(){
        return true;
    }


    /**
     * @ author: Penna, Kemo
     * @ Description: check the phoneEdit and passwordEdit valid
     */
    public boolean checkLoginValid() {
        Object phoneEditText = phoneEdit.getText();
        Object passwordEditText = passwordEdit.getText();
        assert phoneEditText != null;
        assert passwordEditText != null;
        String username = phoneEditText.toString();
        String password = passwordEditText.toString();

        Validation.ValState phoneState = Validation.CheckPhoneNumberValidate(username);
        switch (phoneState){
            case Empty:
                ToastMessage("Phone number should not be empty");
                return false;
            case TooLong:
                ToastMessage("Phone number too long");
                return false;
            case TooShort:
                ToastMessage("Phone number too short");
                return false;
            case InvalidCharacters:
                ToastMessage("Phone number invalid");
                return false;
            case Valid:
                break;
        }

        Validation.ValState passwordState = Validation.CheckPasswordValidate(password);
        switch (passwordState){
            case Empty:
                ToastMessage("Password should not be empty");
                return false;
            case TooLong:
                ToastMessage("Password too long");
                return false;
            case TooShort:
                ToastMessage("Password too short");
                return false;
            case InvalidCharacters:
                ToastMessage("Password invalid");
                return false;
            case Valid:
                break;
        }

        return true;
    }


    /**
     * @ author: Penna
     * @ Description: we may change the UI framework later for toast
     */
    public void ToastMessage(String message){
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * @ author: Penna
     * @ Description: Jump to the PersonalDetailPage
     * @ Todo:
     */
    public void goToPersonalDetailPage() {
        Context context = LoginActivity.this;
        Class destinationActivity = PersonalDetailActivity.class;
        Intent startPersonalActivityIntent = new Intent(context, destinationActivity);
        startPersonalActivityIntent.putExtra(Intent.EXTRA_TEXT, phoneEdit.getText().toString());
        startActivity(startPersonalActivityIntent);
    }

    /**
     * @ author: Penna
     * @ Description: onClick sign up function
     */
    public void onSignUp(View view) {
        Context context = LoginActivity.this;
        Class destinationActivity = RegisterActivity.class;
        Intent startRegisterActivityIntent = new Intent(context, destinationActivity);
        startRegisterActivityIntent.putExtra(Intent.EXTRA_TEXT, phoneEdit.getText().toString());
        startActivity(startRegisterActivityIntent);
    }

    /**
     * @ author: Penna
     * @ Description: Skip login
     */
    public void onSkip(View view){
        Context context = LoginActivity.this;
        Class destinationActivity = PersonalDetailActivity.class;
        Intent startRegisterActivityIntent = new Intent(context, destinationActivity);
        startActivity(startRegisterActivityIntent);
    }

    /**
     * @ author: Penna
     * @ Description: Jump to find password page
     */
    public void onForget(View view){
        Context context = LoginActivity.this;
        Class destinationActivity = ForgetPasswordActivity.class;
        Intent startRegisterActivityIntent = new Intent(context, destinationActivity);
        startActivity(startRegisterActivityIntent);
    }


}
