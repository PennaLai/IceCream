package com.example.icecream.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.icecream.R;
import com.robertlevonyan.views.chip.Chip;

public class SubscribeActivity extends AppCompatActivity{

  static final int KR = 0;
  static final int IFANR = 1;
  static final int FENG = 2;
  static final int GEEKPARK = 3;
  static final int ZHIHU = 4;

  Chip[] chips = new Chip[5];

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.acitivity_subscribe);
    chips[KR] = findViewById(R.id.kr_chip);
    chips[IFANR] = findViewById(R.id.ifanr_chip);
    chips[FENG] = findViewById(R.id.feng_chip);
    chips[GEEKPARK] = findViewById(R.id.geekpark_chip);
    chips[ZHIHU] = findViewById(R.id.zhihu_chip);

  }

}
