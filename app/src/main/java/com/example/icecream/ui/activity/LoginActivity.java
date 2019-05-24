package com.example.icecream.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;

import com.example.icecream.R;
import com.example.icecream.utils.AppViewModel;
import com.example.icecream.utils.ResourceHandler;
import com.example.icecream.utils.UserSettingHandler;
import com.rengwuxian.materialedittext.MaterialEditText;

import com.example.icecream.utils.HttpHandler;
import com.example.icecream.utils.InputStringValidator;

import java.lang.ref.WeakReference;


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
  ResourceHandler resourceHandler;
  private UserSettingHandler userSettingHandler;
  private MaterialEditText phoneEdit;
  private MaterialEditText passwordEdit;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    // if the user has already login and the token is not expired, no need to login
    userSettingHandler = UserSettingHandler.getInstance(getApplication());
    String phoneNumber = userSettingHandler.getLoginPhone();
    if (phoneNumber != null) {
      goToMainPage();
    }
    phoneEdit = findViewById(R.id.phone);
    passwordEdit = findViewById(R.id.password);
    httpHandler = HttpHandler.getInstance(getApplication());
    // view model
    AppViewModel viewModel = ViewModelProviders.of(this).get(AppViewModel.class);

    resourceHandler = ResourceHandler.getInstance(httpHandler, viewModel, getApplication());

//    if (UserSettingHandler.autoLoginFlag) {
//      autoLogin();
//    }
  }

  private void autoLogin() {
    new AutoLoginAsyncTask(this).execute(userSettingHandler.getLoginPhone());
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
      new LoginAsyncTask(this).execute(phoneNumber, password);
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
  public void goToMainPage() {
    resourceHandler.updateCommonArticles(); // 提前加载随便看看
    String phone = userSettingHandler.getLoginPhone();
    if (phone != null) {
      resourceHandler.updateAllRssFeeds();
      resourceHandler.updatePersonalResources(phone);
    }
    Context context = LoginActivity.this;
    Class destinationActivity = MainActivity.class;
    // need to clear current activity stack.
    Intent intent = new Intent(context, destinationActivity)
        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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

  private static class LoginAsyncTask extends AsyncTask<String, Void, HttpHandler.ResponseState> {

    private String phoneNumber;
    private String password;
    private WeakReference<LoginActivity> activityReference;

    // only retain a weak reference to the activity
    LoginAsyncTask(LoginActivity context) {
      activityReference = new WeakReference<>(context);
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(String... params) {
      LoginActivity activity = activityReference.get();
      if (activity == null) {
        return null;
      }
      phoneNumber = params[0];
      password = params[1];
      return activity.httpHandler.postLoginState(phoneNumber, password);
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      LoginActivity activity = activityReference.get();
      if (activity == null) {
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
          // store phone
          activity.userSettingHandler.setLoginPhone(phoneNumber);
          activity.goToMainPage();
          break;
        default:
          activity.showToastMessage("登录失败，请稍后重试");
          break;
      }
    }
  }

  private static class AutoLoginAsyncTask extends AsyncTask<String, Void, HttpHandler.ResponseState> {
    private WeakReference<LoginActivity> activityReference;
    private String phone;

    // only retain a weak reference to the activity
    AutoLoginAsyncTask(LoginActivity context) {
      activityReference = new WeakReference<>(context);
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(String... params) {
      LoginActivity activity = activityReference.get();
      if (activity == null) {
        return null;
      }
      phone = params[0];
      return activity.httpHandler.getCheckToken(phone);
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      LoginActivity activity = activityReference.get();
      System.out.println("login: " + phone + "state: " + responseState);
      if (activity != null && responseState == HttpHandler.ResponseState.Valid) {
        activity.goToMainPage();
      }
    }
  }
}
