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

/**
 * The register activity.
 *
 * @author aaron
 * @author penna
 * @version V1.0
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

  // component
  /**
   * edit text for username.
   */
  private EditText etUserName;
  /**
   * edit text for password.
   */
  private EditText etPassword;
  /**
   * edit text for phone number.
   */
  private EditText etPhoneNumber;
  /**
   * button for sending auth code.
   */
  private FancyButton btSendAuthCode;
  /**
   * code input for pin code.
   */
  private CodeInput pinCode;
  /**
   * Used for checking if verification code has been verified.
   */
  private boolean verified;
  /**
   * The phone number that user input.
   */
  private String phoneNumber;
  /**
   * timer running state.
   */
  private boolean timerRunning;
  /**
   * http handler for handling request and response.
   */
  private HttpHandler httpHandler;

  @Override
  protected void onCreate(final Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.activity_register);
    etUserName = findViewById(R.id.usernameRegister);
    etPassword = findViewById(R.id.passwordRegister);
    etPhoneNumber = findViewById(R.id.phoneNumber);
    pinCode = findViewById(R.id.pinCode);
    btSendAuthCode = findViewById(R.id.btn_getVerificationCode);
    final TextView goToLogin = findViewById(R.id.bt_goToLogin);
    final FancyButton btNextStep = findViewById(R.id.btn_checkVerificationCode);
    final FancyButton btSignUp = findViewById(R.id.bt_signUp);
    btSignUp.setOnClickListener(this);
    goToLogin.setOnClickListener(this);
    btSendAuthCode.setOnClickListener(this);
    btNextStep.setOnClickListener(this);

    final OkHttpClient client = new OkHttpClient();
    httpHandler = new HttpHandler(client);

    initSmsSdk();
  }


  /**
   * This method is to init the sdk handler.
   */
  private void initSmsSdk() {
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
              verified = true;
              Toast.makeText(RegisterActivity.this, "验证成功", Toast.LENGTH_LONG).show();
            } else {
              verified = false;
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
    final CountDownTimer downTimer = new CountDownTimer(60 * 1000, 1000) {
      @Override
      public void onTick(final long mills) {
        timerRunning = true;
        btSendAuthCode.setText((mills / 1000) + "s");
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
    final StringBuilder stringBuilder = new StringBuilder();
    for (final Character c : chars) {
      stringBuilder.append(c.toString());
    }
    final String authCode = stringBuilder.toString();
    SMSSDK.submitVerificationCode("86", phoneNumber, authCode);
  }

  /**
   * This method is to check the valid of phone number and get verification code.
   */
  private void onClickGetVerificationCode() {
    phoneNumber = etPhoneNumber.getText().toString();
    final Validator.ValState phoneNumberState = Validator.validatePhoneNumber(phoneNumber);

    if (phoneNumberState == Validator.ValState.Valid) {
      /* check if duplicate */
      boolean isValidPhone = false;
      switch (httpHandler.getPhoneResponseState(phoneNumber)) {
        case DuplicatePhoneNumber:
          Toast.makeText(this,
              "这个手机号已经被注册过了", Toast.LENGTH_LONG).show();
          break;
        case Valid:
          isValidPhone = true;
          break;
        default:
          break;
      }
      if (isValidPhone) {
        SMSSDK.getVerificationCode("86", phoneNumber);
        btSendAuthCode.setClickable(false);
        btSendAuthCode.setBackgroundColor(Color.parseColor("#898989"));
        startVerifyTimer();
      }
    } else {
      if (phoneNumberState == Validator.ValState.Empty) {
        Toast.makeText(this,
            "手机号不能为空", Toast.LENGTH_LONG).show();
      } else {
        Toast.makeText(this,
            "手机号不合法", Toast.LENGTH_LONG).show();
      }
    }
  }

  /**
   * Verify the username.
   *
   * @param username input username
   * @return true if username is valid
   */
  private boolean verifyUsername(final String username) {
    boolean result = false;
    final Validator.ValState userNameState = Validator.validateUsername(username);
    switch (userNameState) {
      case Valid:
        result = true;
        break;
      case TooShort:
        Toast.makeText(this, "用户名过短", Toast.LENGTH_LONG).show();
        break;
      case TooLong:
        Toast.makeText(this, "用户名过长", Toast.LENGTH_LONG).show();
        break;
      case InvalidCharacters:
        Toast.makeText(this, "用户名含有非法字符", Toast.LENGTH_LONG).show();
        break;
      case Empty:
        Toast.makeText(this, "用户名不能为空", Toast.LENGTH_LONG).show();
        break;
      default:
        break;
    }
    return result;
  }

  /**
   * Verify the password.
   *
   * @param password input password
   * @return true if password is valid
   */
  private boolean verifyPassword(final String password) {
    final Validator.ValState passwordState = Validator.validatePassword(password);
    boolean result = false;
    switch (passwordState) {
      case Valid:
        result = true;
        break;
      case InvalidCharacters:
        Toast.makeText(this, "密码含有非法字符", Toast.LENGTH_LONG).show();
        break;
      case TooShort:
        Toast.makeText(this, "密码太短", Toast.LENGTH_LONG).show();
        break;
      case TooLong:
        Toast.makeText(this, "密码太长", Toast.LENGTH_LONG).show();
        break;
      case Empty:
        Toast.makeText(this, "密码不能为空", Toast.LENGTH_LONG).show();
        break;
      default:
        break;
    }
    return result;
  }

  /**
   * This method is to submit all information and the register request.
   */
  private void submitRegister() {
    final String userName = etUserName.getText().toString();
    final String password = etPassword.getText().toString();

    if (verifyUsername(userName) && verifyPassword(password)) {
      if (verified) {
        if (httpHandler.postRegisterResponseState(phoneNumber, userName, password)
            == HttpHandler.State.Valid) {
          Toast.makeText(this, "注册成功", Toast.LENGTH_LONG).show();
        }
        Toast.makeText(this, "出现未知问题，请稍后重试", Toast.LENGTH_LONG).show();
      } else {
        Toast.makeText(this, "手机号验证未通过", Toast.LENGTH_LONG).show();
      }
    }
  }

  /**
   * This method is to go to the login page.
   * TODO: Avoid creating new page for login
   */
  private void goToLoginPage() {
    final Context context = this;
    final Class destActivity = LoginActivity.class;
    final Intent registerIntent = new Intent(context, destActivity);
    startActivity(registerIntent);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    SMSSDK.unregisterAllEventHandler();  // Destroy the callback interface
  }

}
