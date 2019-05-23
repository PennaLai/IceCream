package com.example.icecream.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.icecream.R;
import com.robertlevonyan.views.chip.Chip;

public class SubscribeActivity extends AppCompatActivity {

  class SubscribeItem {

    /** item name. */
    String name;

    /** has subscribe or not. */
    boolean hasSubscribe;

    /** subscribe url. */
    String subScribeUrl;

    /** the ui chip. */
    FrameLayout chip;

    /** I do not know. */
    TextView textView;

    private SubscribeItem(String name, boolean hasSubscribe, String subScribeUrl,
        FrameLayout chip, TextView textView) {
      this.name = name;
      this.subScribeUrl = subScribeUrl;
      this.chip = chip;
      this.textView = textView;
      this.chip.setOnClickListener(v -> {
        clickSubScribe();
      });
      this.setSubscribe(hasSubscribe);
    }

    private void clickSubScribe() {
      this.hasSubscribe = !this.hasSubscribe;
      updateUi();
    }

    private void setSubscribe(boolean dest) {
      this.hasSubscribe = dest;
      updateUi();
    }

    private void updateUi() {
      if (this.hasSubscribe) {
        this.chip.setBackgroundDrawable(getResources().getDrawable(R.drawable.chip_selected));
        this.textView.setTextColor(getResources().getColor(R.color.colorChipTextClicked));
      } else {
        this.chip.setBackgroundDrawable(getResources().getDrawable(R.drawable.chip_unselected));
        this.textView.setTextColor(getResources().getColor(R.color.colorChipText));
      }
    }
  }

  /** the subscribe number. */
  static final int SUBSCRIBE_NUM = 5;

  /** all the subscribe element. */
  private SubscribeItem[] chips = new SubscribeItem[SUBSCRIBE_NUM];

  /** select all chip. */
  private Chip all;

  private ImageView back;

  private Button confirm;


  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.acitivity_subscribe);

    chips[0] = new SubscribeItem("36氪", false, "https://36kr.com/feed",
        findViewById(R.id.kr_chip), findViewById(R.id.kr_tv));
    chips[1] = new SubscribeItem("爱范儿", true, "https://www.ifanr.com/feed",
        findViewById(R.id.ifanr_chip), findViewById(R.id.ifanr_tv));
    chips[2] = new SubscribeItem("威锋网", false, "https://www.feng.com/rss.xml",
        findViewById(R.id.feng_chip), findViewById(R.id.feng_tv));
    chips[3] = new SubscribeItem("极客公园", true, "https://www.geekpark.net/rss",
        findViewById(R.id.geekpark_chip), findViewById(R.id.geekpark_tv));
    chips[4] = new SubscribeItem("知乎", false, "https://www.zhihu.com/rss",
        findViewById(R.id.zhihu_chip), findViewById(R.id.zhihu_tv));

    all = findViewById(R.id.all_chip);
    back = findViewById(R.id.sub_iv_back);
    confirm = findViewById(R.id.sub_btn_confirm);


    all.setOnSelectClickListener((v, selected)-> {
      if (selected) {
        all.setChipText("全选");
        for (int i = 0; i < SUBSCRIBE_NUM; i++) {
          chips[i].setSubscribe(true);
        }
      } else {
        all.setChipText("全不选");
        for (int i = 0; i < SUBSCRIBE_NUM; i++) {
          chips[i].setSubscribe(false);
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



}
