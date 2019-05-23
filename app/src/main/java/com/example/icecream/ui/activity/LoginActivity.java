package com.example.icecream.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;

import com.example.icecream.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import com.example.icecream.utils.HttpHandler;
import com.example.icecream.utils.InputStringValidator;

import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;


/**
 * The login activity.
 *
 * @author aaron
 * @author penna
 * @author kemo
 * @version V1.0
 */
public class LoginActivity extends AppCompatActivity {

  private HttpHandler httpHandler;
  private MaterialEditText phoneEdit;
  private MaterialEditText passwordEdit;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    phoneEdit = findViewById(R.id.phone);
    passwordEdit = findViewById(R.id.password);
//    FancyButton login = findViewById(R.id.bt_login);
//    TextView signUp = findViewById(R.id.signup);
//    TextView skip = findViewById(R.id.tv_skip);
//    TextView forget = findViewById(R.id.tv_forget);

    final OkHttpClient client = new OkHttpClient();
    httpHandler = new HttpHandler(client, this.getApplication());
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
//    final String phoneNumber = phoneEditText.toString(); TODO: change it back
    // test
    final String phoneNumber = "18929357397";
    final String password = "123456";
//    final String password = passwordEditText.toString();
    if (checkLoginValid(phoneNumber, password)) {
      new LoginAsyncTask(this).execute(new ParamsPhonePassword(phoneNumber, password));
    }
  }

  /**
   * Invoke the {@link InputStringValidator#validatePhone(String)} to judge whether
   * the password is valid or not.
   *
   * @param phoneNumber The input phoneNumber from the phoneNumber input <em>textview</em>.
   * @return boolean Whether the phoneNumber is valid or not.
   */
  private boolean checkPhoneNumber(final String phoneNumber) {
    InputStringValidator.ValState phoneState = InputStringValidator.validatePhone(phoneNumber);
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
   * Invoke the {@link InputStringValidator#validatePassword(String) validatePassword} to jude whether
   * the password is valid or not.
   *
   * @param password The input password from the password input <em>textview</em>.
   * @return boolean Whether the password is valid or not.
   */
  private boolean checkPassword(final String password) {

    InputStringValidator.ValState passwordState = InputStringValidator.validatePassword(password);
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
   * Jump to the MainPage.
   */
  public void goToMainPage(String phone) {
    Context context = LoginActivity.this;
    Class destinationActivity = MainActivity.class;
    // need to clear current activity stack.
    Intent intent = new Intent(context, destinationActivity)
        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra(Intent.EXTRA_TEXT, phone);
    startActivity(intent);
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
    Editable phone = phoneEdit.getText();
    if (phone != null) {
      startRegisterActivityIntent.putExtra(Intent.EXTRA_TEXT, phone.toString());
    }
    startActivity(startRegisterActivityIntent);
  }

  /**
   * The <em>onClick</em> listener function of Skip text to skip the login procedure.
   *
   * @param view The system stipulated view object.
   */
  public void skipLogin(final View view) {
    Intent intent = new Intent(LoginActivity.this, MainActivity.class)
        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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

  private class ParamsPhonePassword {
    String phone;
    String password;

    ParamsPhonePassword(String phone, String password) {
      this.phone = phone;
      this.password = password;
    }
  }


  private static class LoginAsyncTask extends AsyncTask<ParamsPhonePassword, Void, HttpHandler.ResponseState> {

    private String phoneNumber;
    private String password;
    private WeakReference<LoginActivity> activityReference;

    // only retain a weak reference to the activity
    LoginAsyncTask(LoginActivity context) {
      activityReference = new WeakReference<>(context);
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(ParamsPhonePassword... params) {
      LoginActivity activity = activityReference.get();
      if (activity == null || activity.isFinishing()) {
        return null;
      }
      phoneNumber = params[0].phone;
      password = params[0].password;
      return activity.httpHandler.postLoginState(phoneNumber, password);
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      LoginActivity activity = activityReference.get();
      if (activity == null || activity.isFinishing()) {
        return;
      }
      switch (responseState) {
        case NoSuchUser:
          activity.showToastMessage("用户账号不存在");
          break;
        case WrongPassword:
          activity.showToastMessage("密码错误");
          break;
        case Valid:
          activity.showToastMessage("登录成功");
          activity.goToMainPage(phoneNumber);
          break;
        default:
          activity.showToastMessage("登录失败，请重试");
          break;
      }
    }
  }

}
