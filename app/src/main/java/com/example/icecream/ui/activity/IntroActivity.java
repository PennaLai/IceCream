package com.example.icecream.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.example.icecream.R;
import com.example.icecream.utils.AppViewModel;
import com.example.icecream.utils.HttpHandler;
import com.example.icecream.utils.ResourceHandler;
import com.example.icecream.utils.UserSettingHandler;
import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AhoyOnboarderActivity {

  /** A List to store all the introduction pages */
  List<AhoyOnboarderCard> pages = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initIntroCards();
    setOnboardPages(pages);
    setColorBackground(R.color.light_blue);
    setFinishButtonTitle("开始探索吧");

    loadData();
  }

  /**
   * Initialize the introduction pages.
   */
  private void initIntroCards() {
    AhoyOnboarderCard card1 = new AhoyOnboarderCard("个人定制",
        "一键订阅，众多话题任由你来选择", R.drawable.one);
    AhoyOnboarderCard card2 = new AhoyOnboarderCard("实时资讯",
        "轻松下滑，足不出户便知天下之事", R.drawable.two);

    AhoyOnboarderCard card3 = new AhoyOnboarderCard("语音播放",
        "后台播放，时时刻刻可享阅读乐趣", R.drawable.three);
    pages.add(card1);
    pages.add(card2);
    pages.add(card3);
  }

  @Override
  public void onFinishButtonPressed() {
    go();
  }

  /**
   * Load data for the main thread.
   */
  private void loadData() {
    AppViewModel viewModel = ViewModelProviders.of(this).get(AppViewModel.class);
    HttpHandler httpHandler = HttpHandler.getInstance(getApplication());
    ResourceHandler resourceHandler = ResourceHandler.getInstance(httpHandler, viewModel,getApplication());
    UserSettingHandler userSettingHandler = UserSettingHandler.getInstance(getApplication());
    String phoneNumber = userSettingHandler.getLoginPhone();
    if (phoneNumber != null) {
      resourceHandler.loadRssFeeds(phoneNumber);
      resourceHandler.loadArticles(phoneNumber);
    }
  }


  /**
   * Verify login state and switch to the corresponding activity.
   */
  private void go() {
    UserSettingHandler userSettingHandler = UserSettingHandler.getInstance(getApplication());
    String phoneNumber = userSettingHandler.getLoginPhone();
    if (phoneNumber != null) {
      Intent intent = new Intent(IntroActivity.this, MainActivity.class)
          .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
    } else {
      Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
      startActivity(intent);
    }
  }
}
