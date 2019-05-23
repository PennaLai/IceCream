package com.example.icecream.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
  private Chip chip_kr;

  private boolean [] feeds = new boolean[FEED];
  private boolean select_all = false;

  private Chip[] chips = new Chip[FEED];

  private Chip all;

  private ImageView back;

  private Button confirm;

  private void initChips(){
     boolean[] selectInfo = {false, false, false, false, false};
    // TODO: complete function below to get the user feed selection info.
    // selectInfo = getSelectInfo();
    // feeds = selectInfo;
//    for (int i = 0; i < FEED; i++) {
//      setChipSelected(chips[i], selectInfo[i]);
//    }

  }

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.acitivity_subscribe);
    chip_kr = findViewById(R.id.kr_chip);
//    chips[KR] = findViewById(R.id.kr_chip);
    chips[IFANR] = findViewById(R.id.ifanr_chip);
    chips[FENG] = findViewById(R.id.feng_chip);
    chips[GEEKPARK] = findViewById(R.id.geekpark_chip);
    chips[ZHIHU] = findViewById(R.id.zhihu_chip);
    all = findViewById(R.id.all_chip);
    back = findViewById(R.id.sub_iv_back);
    confirm = findViewById(R.id.sub_btn_confirm);
//    setChipSelected(all, true);
    initChips();

    /* select all(init) failed, able to debug.*/
//    all.setOnChipClickListener(v -> {
//      for (int i = 0; i < FEED; i++) {
//        feeds[i] = !select_all;
//        setChipSelected(chips[i], !select_all);
//      }
//      select_all = !select_all;
//      setChipSelected(all, select_all);
//      all.setChipText("全不选");
//      Log.i("???", "!!!"+select_all);
//    });

    all.setOnSelectClickListener((v, selected)->{
      if (selected) {
        all.setChipText("全选");
//        for (int i = 0; i < FEED ; i ++){
//          chip_kr.changeBackgroundColor(R.color.selected);
//          chip_kr.setSelectable(true);
        chip_kr.setClosable(false);
//        }
      } else {
        all.setChipText("全不选");
//        for (int i = 0; i < FEED ; i ++){
//        chip_kr.changeBackgroundColor(R.color.unselected);
//        chip_kr.setSelectable(false);
        chip_kr.setClosable(true);
//        }
      }
    });

    back.setOnClickListener(v -> {
      onBackPressed();
    });

    confirm.setOnClickListener(v -> {
      // TODO
    });
  }

  private void selectAll(){

  }

  private void setChipSelected(Chip chip, boolean destinationState){
//    chip.setSelectable(destinationState);
//    if (destinationState) {
//      chip.setSelectable(destinationState);
//      chip.setStrokeColor(chips[2].getSelectedBackgroundColor());
//      Log.i("COLOR", chips[2].getSelectedBackgroundColor()+"");
//      chip.setTextColor(chip.getSelectedTextColor());
//    } else {
//      chip.setStrokeColor(chips[2].getBackgroundColor());
//      Log.i("COLOR", chips[2].getBackgroundColor()+"");
//      chip.setTextColor(chip.getTextColor());
//    }
  }

}
