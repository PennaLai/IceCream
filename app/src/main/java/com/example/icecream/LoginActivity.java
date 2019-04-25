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
import utils.Validator;

/**
 * The login activity.
 *
 * @author aaron
 * @author penna
 * @author kemo
 * @version V1.0
 */
public class LoginActivity extends AppCompatActivity {

  private final OkHttpClient client = new OkHttpClient();
  private final HttpHandler httpHandler = new HttpHandler(client);
  private MaterialEditText phoneEdit;
  private MaterialEditText passwordEdit;
  private FancyButton login;
  private TextView signUp;
  private TextView skip;
  private TextView forget;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    // thread problem for request
    final StrictMode.ThreadPolicy policy =
        new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);

    phoneEdit = findViewById(R.id.phone);
    passwordEdit = findViewById(R.id.password);
    login = findViewById(R.id.bt_login);
    signUp = findViewById(R.id.signup);
    skip = findViewById(R.id.tv_skip);
    forget = findViewById(R.id.tv_forget);

  }

  /**
   * The <em>onClick</em> listener function for the login button.
   *
   * @param view The system stipulated view object.
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
      switch (httpHandler.postLoginResponseState(phoneNumber, password)) {
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

  /**
   * Invoke the {@link Validator#validatePhoneNumber(String)} to jude whether
   * the password is valid or not.
   *
   * @param phoneNumber The input phoneNumber from the phoneNumber input <em>textview</em>.
   * @return boolean Whether the phoneNumber is valid or not.
   */
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

  /**
   * Invoke the {@link Validator#validatePassword(String) validatePassword} to jude whether
   * the password is valid or not.
   *
   * @param password The input password from the password input <em>textview</em>.
   * @return boolean Whether the password is valid or not.
   */
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
   * This method is to jude the login request is valid or not.
   *
   * @param phoneNumber The phone number user entered.
   * @param password    The password user entered.
   * @return boolean Whether the login request is valid.
   */
  public boolean checkLoginValid(final String phoneNumber, final String password) {
    return checkPhoneNumber(phoneNumber) && checkPassword(password);
  }


  /**
   * The method is to show message in a toast from the bottom of the screen,
   * we may change the UI framework later for toast.
   *
   * @param message The message to show.
   */
  public void showToastMessage(final String message) {
    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
  }

  /**
   * Jump to the PersonalDetailPage.
   */
  public void goToPersonalDetailPage() {
    Context context = LoginActivity.this;
    Class destinationActivity = PersonalDetailActivity.class;
    Intent startPersonalActivityIntent = new Intent(context, destinationActivity);
    startPersonalActivityIntent.putExtra(Intent.EXTRA_TEXT, phoneEdit.getText().toString());
    startActivity(startPersonalActivityIntent);
  }

  /**
   * The <em>onClick</em> listener function of sign up text.
   *
   * @param view The system stipulated view object.
   */
  public void onSignUp(final View view) {
    Context context = LoginActivity.this;
    Class destinationActivity = RegisterActivity.class;
    Intent startRegisterActivityIntent = new Intent(context, destinationActivity);
    startRegisterActivityIntent.putExtra(Intent.EXTRA_TEXT, phoneEdit.getText().toString());
    startActivity(startRegisterActivityIntent);
  }

  /**
   * The <em>onClick</em> listener function of Skip text to skip the login procedure.
   *
   * @param view The system stipulated view object.
   */
  public void skipLogin(final View view) {
    Intent intent = new Intent(LoginActivity.this, PersonalDetailActivity.class);
    startActivity(intent);
  }

  /**
   * The <em>onClick</em> listener function of Skip text to jump to find password page.
   *
   * @param view The system stipulated view object.
   */
  public void goToForgetPasswordPage(final View view) {
    Context context = LoginActivity.this;
    Class destinationActivity = ForgetPasswordActivity.class;
    Intent startRegisterActivityIntent = new Intent(context, destinationActivity);
    startActivity(startRegisterActivityIntent);
  }


}
