package com.example.icecream;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * The personal detail activity.
 *
 * @author penna
 * @version V1.0
 */

public class PersonalDetailActivity extends AppCompatActivity implements View.OnClickListener {

  private String userName;
  private TextView userNameTextView;
  private Button btgoToLoginPage;
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_personal_detail);
    userNameTextView = findViewById(R.id.username);
    btgoToLoginPage = findViewById(R.id.bt_goToLogin);
    btgoToLoginPage.setOnClickListener(this);
    loadUserInformation();
  }

  /**
   * This method is to load the user information
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
   * This method is to go to login page to login the account
   */
  public void goToLoginPage() {
    Intent intent = new Intent(PersonalDetailActivity.this, LoginActivity.class);
    startActivity(intent);
  }

  /**
   * handle the onClick event for widget
   * @param v
   */
  @Override
  public void onClick(View v) {
    switch(v.getId()) {
      case R.id.bt_goToLogin:
        goToLoginPage();
        break;
    }
  }
}
