package com.example.icecream;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.icecream.utils.HttpHandler;

/**
 * The personal detail activity.
 *
 * @author penna
 * @version V1.0
 */

public class PersonalDetailActivity extends AppCompatActivity implements View.OnClickListener {

  private String userName;
  private TextView userNameTextView;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_personal_detail);
    userNameTextView = findViewById(R.id.username);
    Button btGoToLoginPage = findViewById(R.id.bt_goToLogin);
    btGoToLoginPage.setOnClickListener(this);
    loadUserInformation();
  }

  /**
   * This method is to load the user information.
   */
  public void loadUserInformation() {
    //TODO load user information from com.example.icecream.database.
  }

  /**
   * This method is to update the user information on the screen.
   */
  public void updateUserInformation() {
    userNameTextView.setText(userName);
  }

  /**
   * This method is to go to login page to login the account.
   */
  public void goToLoginPage() {
    SharedPreferences sharedPreferences = this.getSharedPreferences(HttpHandler.TOKEN_FILE_KEY, Context.MODE_PRIVATE);
    String token = sharedPreferences.getString(HttpHandler.TOKEN_KEY, "");
    System.out.println("Token: " + token);

    Intent intent = new Intent(PersonalDetailActivity.this, LoginActivity.class);
    startActivity(intent);
  }

  /**
   * handle the onClick event for widget.
   * @param v view
   */
  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.bt_goToLogin:
        goToLoginPage();
        break;
      default:
        break;
    }
  }
}
