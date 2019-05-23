package com.example.icecream.ui.activity;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.icecream.R;
import com.example.icecream.database.entity.RssFeed;
import com.example.icecream.utils.AppViewModel;
import com.example.icecream.utils.HttpHandler;
import com.example.icecream.utils.ResourceHandler;
import com.example.icecream.utils.UserSettingHandler;
import com.robertlevonyan.views.chip.Chip;
import java.util.List;

public class SubscribeActivity extends AppCompatActivity {

  /**
   * the select item to store the subscribe information.
   */
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

    /** initial to not select state. */
    private SubscribeItem(String name, String subScribeUrl,
        FrameLayout chip, TextView textView) {
      this.name = name;
      this.subScribeUrl = subScribeUrl;
      this.chip = chip;
      this.textView = textView;
      this.chip.setOnClickListener(v -> clickSubScribe());
      this.setSubscribe(false);
    }

    /** when click the item, it should change the state and update ui. */
    private void clickSubScribe() {
      this.hasSubscribe = !this.hasSubscribe;
      updateUi();
    }

    /** direct set the state and update ui. */
    private void setSubscribe(boolean dest) {
      this.hasSubscribe = dest;
      updateUi();
    }

    /** update ui depend the state.*/
    private void updateUi() {
      if (this.hasSubscribe) {
        this.chip.setBackgroundDrawable(getResources().getDrawable(R.drawable.chip_selected));
        this.textView.setTextColor(getResources().getColor(R.color.colorChipTextClicked));
      } else {
        this.chip.setBackgroundDrawable(getResources().getDrawable(R.drawable.chip_unselected));
        this.textView.setTextColor(getResources().getColor(R.color.colorChipText));
      }
    }

    /** return state. */
    private boolean isHasSubscribe() {
      return hasSubscribe;
    }

    /** return the url. */
    private String getSubScribeUrl() {
      return subScribeUrl;
    }

  }

  /** the subscribe number. */
  static final int SUBSCRIBE_NUM = 5;

  /** all the subscribe element. */
  private SubscribeItem[] chips = new SubscribeItem[SUBSCRIBE_NUM];

  /** select all chip. */
  private Chip all;

  /** store the all subscribe list. */
  private List<RssFeed> allSubscribes;


  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.acitivity_subscribe);
    AppViewModel viewModel = ViewModelProviders.of(this).get(AppViewModel.class);

    chips[0] = new SubscribeItem("36氪", "https://36kr.com/feed",
        findViewById(R.id.kr_chip), findViewById(R.id.kr_tv));
    chips[1] = new SubscribeItem("爱范儿", "https://www.ifanr.com/feed",
        findViewById(R.id.ifanr_chip), findViewById(R.id.ifanr_tv));
    chips[2] = new SubscribeItem("威锋网", "https://www.feng.com/rss.xml",
        findViewById(R.id.feng_chip), findViewById(R.id.feng_tv));
    chips[3] = new SubscribeItem("极客公园", "https://www.geekpark.net/rss",
        findViewById(R.id.geekpark_chip), findViewById(R.id.geekpark_tv));
    chips[4] = new SubscribeItem("知乎",  "https://www.zhihu.com/rss",
        findViewById(R.id.zhihu_chip), findViewById(R.id.zhihu_tv));

    viewModel
        .getPersonalRssFeeds()
        .observe(
            this,
            rssFeeds -> {
              allSubscribes = rssFeeds;
              for (RssFeed rssFeed : allSubscribes) {
                if (rssFeed.getId() >= 0 && rssFeed.getId() < SUBSCRIBE_NUM) {
                  chips[1].setSubscribe(true);  // TODO : 更改黄最新接口
                }
              }
            });

    all = findViewById(R.id.all_chip);
    ImageView back = findViewById(R.id.sub_iv_back);
    Button confirm = findViewById(R.id.sub_btn_confirm);

    all.setOnSelectClickListener((v, selected) -> {
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

    back.setOnClickListener(v -> onBackPressed());

    confirm.setOnClickListener(v -> {
      confirmSubscribe();
      onBackPressed();
    });
  }

  /**
   * send the request to the server to subscribe the selected item.
   */
  private void confirmSubscribe() {
    HttpHandler httpHandler = HttpHandler.getInstance(getApplication());
    AppViewModel viewModel = ViewModelProviders.of(this).get(AppViewModel.class);
    ResourceHandler resourceHandler = ResourceHandler.getInstance(httpHandler, viewModel);
    UserSettingHandler userSettingHandler = UserSettingHandler.getInstance(getApplication());
    String phoneNumber;
    phoneNumber = userSettingHandler.getLoginPhone();
    if (phoneNumber == null) {
      Toast.makeText(SubscribeActivity.this, "你还没有登录哦", Toast.LENGTH_LONG).show();
      return;
    }
    for (SubscribeItem element : chips) {
        if (element.isHasSubscribe()) {
          resourceHandler.subscribe(phoneNumber,
              element.getSubScribeUrl());
        }
    }
  }

}
