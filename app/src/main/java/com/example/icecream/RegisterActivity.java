package com.example.icecream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.github.glomadrian.codeinputlib.CodeInput;
import com.mob.MobSDK;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import mehdi.sakout.fancybuttons.FancyButton;
import utils.Validation;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    // component
    private EditText etUserName;
    private EditText etPassword;
    private EditText etPhoneNumber;
    private FancyButton btSendVerificationCode;
    private CodeInput pinCode;
    private FancyButton btNextStep;
    private FancyButton btSignUp;
    private TextView goToLogin;

    private boolean hasTryVerify = false;
    private boolean verificationSuccess = false;
    private String phoneNumber;
    private String verificationCode;
    private boolean timer_running;

    boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etUserName = (EditText) findViewById(R.id.usernameRegister);
        etPassword = (EditText) findViewById(R.id.passwordRegister);
        etPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
        pinCode = (CodeInput) findViewById(R.id.pinCode);
        goToLogin = (TextView) findViewById(R.id.bt_goToLogin);
        btSendVerificationCode = (FancyButton) findViewById(R.id.btn_getVerificationCode);
        btNextStep = (FancyButton) findViewById(R.id.btn_checkVerificationCode);
        btSignUp = (FancyButton) findViewById(R.id.bt_signUp);
        btSignUp.setOnClickListener(this);
        goToLogin.setOnClickListener(this);
        btSendVerificationCode.setOnClickListener(this);
        btNextStep.setOnClickListener(this);


        initSMSSDK();

    }


    public void initSMSSDK(){
        MobSDK.init(this);
        // the event handler for the SMSSDK
        EventHandler eh=new EventHandler(){

            @Override
            public void afterEvent(int event, int result, Object data) {
                // afterEvent会在子线程被调用，因此如果后续有UI相关操作，需要将数据发送到UI线程
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                new Handler(Looper.getMainLooper(), new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        int event = msg.arg1;
                        int result = msg.arg2;
                        Object data = msg.obj;
                        if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            if (result == SMSSDK.RESULT_COMPLETE) {
                                // TODO 处理成功得到验证码的结果
                                Toast.makeText(RegisterActivity.this, "发送成功",  Toast.LENGTH_LONG).show();
                                // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                            } else {
                                // TODO 处理没有得到验证码错误的结果
                                ((Throwable) data).printStackTrace();
                            }
                        } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            if (result == SMSSDK.RESULT_COMPLETE) {
                                verificationSuccess = true;
                                Toast.makeText(RegisterActivity.this, "验证成功",  Toast.LENGTH_LONG).show();
                            } else {
                                verificationSuccess = false;
                                Toast.makeText(RegisterActivity.this, "验证失败",  Toast.LENGTH_LONG).show();
                                ((Throwable) data).printStackTrace();
                            }
                        }
                        return false;
                    }
                }).sendMessage(msg);

            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }


    /**
     * @ author: Airine
     * @ Description: start the verify pin code timer, to set the text per second.
     */
    private void start_verify_timer(){
        CountDownTimer downTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer_running = true;
                btSendVerificationCode.setText((millisUntilFinished/1000)+"s");
            }

            @Override
            public void onFinish() {
                timer_running = false;
                btSendVerificationCode.setText("Get Pin");
                btSendVerificationCode.setClickable(true);
                btSendVerificationCode.setBackgroundColor(Color.parseColor("#30363E"));
            }
        };
        if (!timer_running)
            downTimer.start();
    }

    /**
     * @ author: Penna
     * @ Description: onClick function, to check what component the user click
     * and do the corresponding operation.
     */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_getVerificationCode:
                onClickGetVerificationCode();
                break;
            case R.id.btn_checkVerificationCode:
                if(!hasTryVerify) {
                    verifyCode();
                    hasTryVerify = true;
                }
                break;
            case R.id.bt_signUp:
                submitRegister();
            case R.id.bt_goToLogin:
                goToLoginPage();
                break;
        }
    }


    public void verifyCode(){
        Character[] chars = pinCode.getCode();
        verificationCode = "";
        for (Character c : chars)
            verificationCode += c.toString();
        SMSSDK.submitVerificationCode("86", phoneNumber, verificationCode);
    }

    /**
     * @ author: Penna
     * @ Description: to check the valid of phone number and get verification code
     */
    public void onClickGetVerificationCode(){
        phoneNumber = etPhoneNumber.getText().toString();
        Validation.ValState phoneNumberState = Validation.CheckPhoneNumberValidate(phoneNumber);
        if(phoneNumberState!= Validation.ValState.Valid){
            Toast.makeText(RegisterActivity.this, "Phone number not valid",  Toast.LENGTH_LONG).show();
            return;
        }
        SMSSDK.getVerificationCode("86", phoneNumber);
        btSendVerificationCode.setClickable(false);
        btSendVerificationCode.setBackgroundColor(Color.parseColor("#898989"));
        start_verify_timer();
    }

    /**
     * @ author: Penna
     * @ Description: submit all information and register
     */
    private void submitRegister(){
        String userName = etUserName.getText().toString();
        String password = etPassword.getText().toString();

        Validation.ValState userNameState = Validation.CheckUserNameValidate(userName);
        Validation.ValState passwordState = Validation.CheckPasswordValidate(password);

        switch (userNameState){
            case Valid:
                break;
            case TooShort:
                Toast.makeText(RegisterActivity.this, "UserName too short",  Toast.LENGTH_LONG).show();
                return;
            case TooLong:
                Toast.makeText(RegisterActivity.this, "UserName too long",  Toast.LENGTH_LONG).show();
                return;
            case InvalidCharacters:
                Toast.makeText(RegisterActivity.this, "UserName has invalid character",  Toast.LENGTH_LONG).show();
                return;
            case Empty:
                Toast.makeText(RegisterActivity.this, "UserName should not be empty",  Toast.LENGTH_LONG).show();
                return;
        }

        switch (passwordState){
            case Valid:
                break;
            case InvalidCharacters:
                Toast.makeText(RegisterActivity.this, "Password has invalid character",  Toast.LENGTH_LONG).show();
                return;
            case TooShort:
                Toast.makeText(RegisterActivity.this, "Password too short",  Toast.LENGTH_LONG).show();
                return;
            case TooLong:
                Toast.makeText(RegisterActivity.this, "Password too long",  Toast.LENGTH_LONG).show();
                return;
            case Empty:
                Toast.makeText(RegisterActivity.this, "Password should not be empty",  Toast.LENGTH_LONG).show();
                return;
        }

        if(!hasTryVerify){
            verifyCode();
            hasTryVerify = true;
        }

        if (!verificationSuccess){
            Toast.makeText(RegisterActivity.this, "验证不通过",  Toast.LENGTH_LONG).show();
            return;
        }

        //TODO: try to request to server to check whether the  username existed, if not, register it and return true
        Toast.makeText(RegisterActivity.this, "注册成功",  Toast.LENGTH_LONG).show();

    }

    /**
     * @ author: Penna
     * @ Description: go goToLogin to the login page
     * @ TODO: There is a bug that if we already have a login page, this method just create a new page, we just want to go goToLogin to previous one
     */
    public void goToLoginPage(){
        Context context = RegisterActivity.this;
        Class destinationActivity = LoginActivity.class;
        Intent startRegisterActivityIntent = new Intent(context, destinationActivity);
        startActivity(startRegisterActivityIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();  // Destroy the callback interface
    }

}
