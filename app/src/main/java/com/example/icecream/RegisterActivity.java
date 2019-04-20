package com.example.icecream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.glomadrian.codeinputlib.CodeInput;
import com.mob.MobSDK;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.OkHttpClient;
import utils.HttpHandler;
import utils.Validator;

import static utils.HttpHandler.State.Valid;

/**
 * The register activity.
 *
 * @author aaron
 * @author penna
 * @version V1.0
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    // component
    private EditText etUserName;
    private EditText etPassword;
    private EditText etPhoneNumber;
    private FancyButton btSendAuthCode;
    private CodeInput pinCode;
    private FancyButton btNextStep;
    private FancyButton btSignUp;
    private TextView goToLogin;

    private boolean verificationSuccess;
    private String phoneNumber;
    private String authCode;
    private boolean timerRunning;

    private final OkHttpClient client = new OkHttpClient();
    private final HttpHandler httpHandler = new HttpHandler(client);

    public RegisterActivity() {
        super();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etUserName = findViewById(R.id.usernameRegister);
        etPassword = findViewById(R.id.passwordRegister);
        etPhoneNumber = findViewById(R.id.phoneNumber);
        pinCode = findViewById(R.id.pinCode);
        goToLogin = findViewById(R.id.bt_goToLogin);
        btSendAuthCode = findViewById(R.id.btn_getVerificationCode);
        btNextStep = findViewById(R.id.btn_checkVerificationCode);
        btSignUp = findViewById(R.id.bt_signUp);
        btSignUp.setOnClickListener(this);
        goToLogin.setOnClickListener(this);
        btSendAuthCode.setOnClickListener(this);
        btNextStep.setOnClickListener(this);

        initSMSSDK();

    }


    /**
     * This method is to init the sdk handler.
     */
    private void initSMSSDK() {
        MobSDK.init(this);
        // the event handler for the SMSSDK
        final EventHandler eventHandler = new EventHandler() {

            @Override
            public void afterEvent(final int event, final int result, final Object data) {
                // afterEvent会在子线程被调用，因此如果后续有UI相关操作，需要将数据发送到UI线程
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                new Handler(Looper.getMainLooper(), msg1 -> {
                    int event1 = msg1.arg1;
                    int result1 = msg1.arg2;
                    Object data1 = msg1.obj;
                    if (event1 == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        if (result1 == SMSSDK.RESULT_COMPLETE) {
                            Toast.makeText(RegisterActivity.this, "发送成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "发送失败，请重试", Toast.LENGTH_LONG).show();
                            ((Throwable) data1).printStackTrace();
                        }
                    } else if (event1 == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        if (result1 == SMSSDK.RESULT_COMPLETE) {
                            verificationSuccess = true;
                            Toast.makeText(RegisterActivity.this, "验证成功", Toast.LENGTH_LONG).show();
                        } else {
                            verificationSuccess = false;
                            Toast.makeText(RegisterActivity.this, "验证失败", Toast.LENGTH_LONG).show();
                            ((Throwable) data1).printStackTrace();
                        }
                    }
                    return false;
                }).sendMessage(msg);

            }
        };
        SMSSDK.registerEventHandler(eventHandler);
    }


    /**
     * Start the verify pin code timer, to set the text of the
     * <em>btn_checkVerificationCode</em>
     * button per second.
     */
    private void startVerifyTimer() {
        long millisInFuture = 60 * 1000;
        long countDownInterval = 1000;
        final CountDownTimer downTimer = new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerRunning = true;
                btSendAuthCode.setText((millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                btSendAuthCode.setText("Get Pin");
                btSendAuthCode.setClickable(true);
                btSendAuthCode.setBackgroundColor(Color.parseColor("#30363E"));
            }
        };
        if (!timerRunning) {
            downTimer.start();
        }
    }

    /**
     * This <em>onClick</em> method is to check what component the user click
     * and do the corresponding operation.
     *
     * @param view The system stipulated view object.
     */
    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.btn_getVerificationCode:
                onClickGetVerificationCode();
                break;
            case R.id.btn_checkVerificationCode:
                verifyCode();
                break;
            case R.id.bt_signUp:
                submitRegister();
                break;
            case R.id.bt_goToLogin:
                goToLoginPage();
                break;
            default:
                break;
        }
    }


    /**
     * Check the valid of phone and try to verify code.
     */
    private void verifyCode() {
        final Character[] chars = pinCode.getCode();
        StringBuilder stringBuilder = new StringBuilder();
        for (Character c : chars) {
            stringBuilder.append(c.toString());
        }
        authCode = stringBuilder.toString();
        SMSSDK.submitVerificationCode("86", phoneNumber, authCode);
    }

    /**
     * This method is to check the valid of phone number and get verification code.
     */
    private void onClickGetVerificationCode() {
        phoneNumber = etPhoneNumber.getText().toString();
        Validator.ValState phoneNumberState = Validator.validatePhoneNumber(phoneNumber);
        if (phoneNumberState == Validator.ValState.Empty) {
            Toast.makeText(this,
                    "手机号不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (phoneNumberState != Validator.ValState.Valid) {
            Toast.makeText(this,
                    "手机号不合法", Toast.LENGTH_LONG).show();
            return;
        }
        /* check if duplicate */
        switch (httpHandler.getPhoneResponseState(phoneNumber)) {
            case DuplicatePhoneNumber:
                Toast.makeText(this,
                        "这个手机号已经被注册过了", Toast.LENGTH_LONG).show();
                return;
            case Valid:
                Toast.makeText(this,
                        "验证码已发送", Toast.LENGTH_LONG).show();
                break;
            default:
                return;
        }

        SMSSDK.getVerificationCode("86", phoneNumber);
        btSendAuthCode.setClickable(false);
        btSendAuthCode.setBackgroundColor(Color.parseColor("#898989"));
        startVerifyTimer();
    }

    /**
     * This method is to submit all information and the register request.
     */
    private void submitRegister() {
        final String userName = etUserName.getText().toString();

        Validator.ValState userNameState = Validator.validateUsername(userName);
        switch (userNameState) {
            case Valid:
                break;
            case TooShort:
                Toast.makeText(this, "用户名过短", Toast.LENGTH_LONG).show();
                return;
            case TooLong:
                Toast.makeText(this, "用户名过长", Toast.LENGTH_LONG).show();
                return;
            case InvalidCharacters:
                Toast.makeText(this, "用户名含有非法字符", Toast.LENGTH_LONG).show();
                return;
            case Empty:
                Toast.makeText(this, "用户名不能为空", Toast.LENGTH_LONG).show();
                return;
            default:
                return;
        }

        final String password = etPassword.getText().toString();

        Validator.ValState passwordState = Validator.validatePassword(password);
        switch (passwordState) {
            case Valid:
                break;
            case InvalidCharacters:
                Toast.makeText(this, "密码含有非法字符", Toast.LENGTH_LONG).show();
                return;
            case TooShort:
                Toast.makeText(this, "密码太短", Toast.LENGTH_LONG).show();
                return;
            case TooLong:
                Toast.makeText(this, "密码太长", Toast.LENGTH_LONG).show();
                return;
            case Empty:
                Toast.makeText(this, "密码不能为空", Toast.LENGTH_LONG).show();
                return;
            default:
                return;
        }

        if (!verificationSuccess) {
            Toast.makeText(this, "手机号验证未通过", Toast.LENGTH_LONG).show();
            return;
        }

        if (httpHandler.getRegisterResponseState(phoneNumber, userName, password) != Valid) {
            Toast.makeText(this, "出现未知问题，请稍后重试", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, "注册成功", Toast.LENGTH_LONG).show();

    }

    /**
     * This method is to go to the login page.
     * TODO: There is a bug that if we already have a login page, this method just create a new page, we just want to go goToLogin to previous one
     */
    private void goToLoginPage() {
        Context context = this;
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
