package com.example.icecream;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * The forgot password activity.
 *
 * @version V1.0
 */

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener{
  private Button btgoToLogin;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_forget_password);
    btgoToLogin = findViewById(R.id.bt_goToLogin);
    btgoToLogin.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.bt_goToLogin:
        goToLoginPage();
        break;
    }
  }

  private void goToLoginPage() {
    Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
    startActivity(intent);
  }
}
