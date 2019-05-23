package com.example.icecream.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.shape.MaterialShapeDrawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.icecream.R;
import com.robertlevonyan.views.chip.Chip;
import com.robertlevonyan.views.chip.OnSelectClickListener;

public class SubscribeActivity extends AppCompatActivity{

  static final int FEED = 5;
  static final int KR = 0;
  static final int IFANR = 1;
  static final int FENG = 2;
  static final int GEEKPARK = 3;
  static final int ZHIHU = 4;
//  private Chip chip_kr;

  private boolean [] feeds = new boolean[FEED];
  private boolean select_all = false;

  private FrameLayout[] chips = new FrameLayout[FEED];
  private TextView[] tvs = new TextView[FEED];

  private Chip all;

  private ImageView back;

  private Button confirm;

  private void initChips(){
    //     TODO: complete function below to get the user feed selection info.
    feeds = new boolean[]{false, false, true, false, true};
    for (int i = 0; i < FEED; i++) {
      setChipSelected(i, feeds[i]);
    }

  }

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.acitivity_subscribe);
    chips[KR] = findViewById(R.id.kr_chip);
    tvs[KR] = findViewById(R.id.kr_tv);
    chips[IFANR] = findViewById(R.id.ifanr_chip);
    tvs[IFANR] = findViewById(R.id.ifanr_tv);
    chips[FENG] = findViewById(R.id.feng_chip);
    tvs[FENG] = findViewById(R.id.feng_tv);
    chips[GEEKPARK] = findViewById(R.id.geekpark_chip);
    tvs[GEEKPARK] = findViewById(R.id.geekpark_tv);
    chips[ZHIHU] = findViewById(R.id.zhihu_chip);
    tvs[ZHIHU] = findViewById(R.id.zhihu_tv);
    all = findViewById(R.id.all_chip);
    back = findViewById(R.id.sub_iv_back);
    confirm = findViewById(R.id.sub_btn_confirm);
    initChips();

    for (int i = 0; i < FEED; i++) {
      int tempt = i;
      chips[i].setOnClickListener(v -> {
        feeds[tempt] = ! feeds[tempt];
        setChipSelected(tempt, feeds[tempt]);
      });
    }

    all.setOnSelectClickListener((v, selected)->{
      if (selected) {
        all.setChipText("全选");
        for (int i = 0; i < FEED; i++) {
          setChipSelected(i, true);
          feeds[i] = true;
        }
      } else {
        all.setChipText("全不选");

        for (int i = 0; i < FEED; i++) {
          setChipSelected(i, false);
          feeds[i] = false;
        }
      }
    });

    back.setOnClickListener(v -> {
      onBackPressed();
    });

    confirm.setOnClickListener(v -> {
      // TODO
      onBackPressed();
    });
  }

  private void setChipSelected(int i, boolean destinationState){
    if (destinationState) {
      chips[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.chip_selected));
      tvs[i].setTextColor(getResources().getColor(R.color.colorChipTextClicked));
    } else {
      chips[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.chip_unselected));
      tvs[i].setTextColor(getResources().getColor(R.color.colorChipText));
    }
  }

}
