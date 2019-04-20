package com.example.icecream;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * The personal detail activity.
 *
 * @author penna
 * @version V1.0
 */

public class PersonalDetailActivity extends AppCompatActivity {

  private String userName;
  private TextView userNameTextView;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_personal_detail);
    userNameTextView = findViewById(R.id.username);
    getUserInformation();
  }

  /**
   * This method is to get the user information from user page.
   */
  public void getUserInformation() {
    Intent intentFromLogin = getIntent();
    if (intentFromLogin.hasExtra(Intent.EXTRA_TEXT)) {
      userName = intentFromLogin.getStringExtra(Intent.EXTRA_TEXT);
      updateUserInformation();
    }
  }

  /**
   * This method is to update the user information on the screen.
   */
  public void updateUserInformation() {
    userNameTextView.setText(userName);
  }


}
