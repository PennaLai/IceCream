package com.example.icecream;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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

    private final OkHttpClient client = new OkHttpClient();
    private final HttpHandler httpHandler = new HttpHandler(client);
    private MaterialEditText phoneEdit;
    private MaterialEditText passwordEdit;
    private FancyButton login;
    private TextView signUp;
    private TextView skip;
    private TextView forget;
    private User user;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // thread problem for request
        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        phoneEdit = findViewById(R.id.phone);
        passwordEdit = findViewById(R.id.password);
        login = findViewById(R.id.bt_login);
        signUp = findViewById(R.id.signup);
        skip = findViewById(R.id.tv_skip);
        forget = findViewById(R.id.tv_forget);
        user = new User();

    }

    /**
     * @author Penna, Kemo
     * @Description click login event
     */
    public void onLogin(final View view) {
        final Object phoneEditText = phoneEdit.getText();
        final Object passwordEditText = passwordEdit.getText();
        if (phoneEditText == null || passwordEditText == null) {
            Toast.makeText(LoginActivity.this, "不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        final String phoneNumber = phoneEditText.toString();
        final String password = passwordEditText.toString();
        if (checkLoginValid(phoneNumber, password)) {
            switch (httpHandler.getLoginResponseState(phoneNumber, password)) {
                case NoSuchUser:
                    showToastMessage("用户账号不存在");
                    break;
                case WrongPassword:
                    showToastMessage("密码错误");
                    break;
                case Valid:
                    showToastMessage("登录成功");
                    goToPersonalDetailPage();
                    break;
                default:
                    showToastMessage("登录失败，请重试");
                    break;
            }
        }
    }

    private boolean checkPhoneNumber(final String phoneNumber) {
        Validator.ValState phoneState = Validator.validatePhoneNumber(phoneNumber);
        switch (phoneState) {
            case Empty:
                showToastMessage("手机号不能为空");
                return false;
            case TooLong:
                showToastMessage("手机号太长了");
                return false;
            case TooShort:
                showToastMessage("手机号太短了");
                return false;
            case InvalidCharacters:
                showToastMessage("手机号格式不正确");
                return false;
            case Valid:
                return true;
            default:
                return false;
        }
    }

    private boolean checkPassword(final String password) {
        Validator.ValState passwordState = Validator.validatePassword(password);
        switch (passwordState) {
            case Empty:
                showToastMessage("密码不能为空");
                return false;
            case TooLong:
                showToastMessage("密码太长");
                return false;
            case TooShort:
                showToastMessage("密码太短");
                return false;
            case InvalidCharacters:
                showToastMessage("密码含有非法字符");
                return false;
            case Valid:
                return true;
            default:
                return false;
        }
    }

    /**
     * @author: Penna, Kemo
     * @Description:
     */
    public boolean checkLoginValid(final String phoneNumber, final String password) {
        return checkPhoneNumber(phoneNumber) && checkPassword(password);
    }


    /**
     * @ author: Penna
     * @ Description: we may change the UI framework later for toast
     */
    public void showToastMessage(final String message) {
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
     * @author: Penna
     * @Description: onClick sign up function
     */
    public void onSignUp(final View view) {
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
    public void onSkip(final View view) {
        Context context = LoginActivity.this;
        Class destinationActivity = PersonalDetailActivity.class;
        Intent startRegisterActivityIntent = new Intent(context, destinationActivity);
        startActivity(startRegisterActivityIntent);
    }

    /**
     * @ author: Penna
     * @ Description: Jump to find password page
     */
    public void onForget(final View view) {
        Context context = LoginActivity.this;
        Class destinationActivity = ForgetPasswordActivity.class;
        Intent startRegisterActivityIntent = new Intent(context, destinationActivity);
        startActivity(startRegisterActivityIntent);
    }


}
