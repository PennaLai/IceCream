package com.example.icecream;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * The main activity.
 *
 * @version V1.0
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private Button btgoToPersonalPage;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    btgoToPersonalPage = findViewById(R.id.bt_goToPersonalDetail);
    btgoToPersonalPage.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.bt_goToPersonalDetail:
        goToPersonalDetail();
        break;
    }
  }

  private void goToPersonalDetail() {
    Intent intent = new Intent(MainActivity.this, PersonalDetailActivity.class);
    startActivity(intent);
  }

}
