package com.example.icecream.ui.activity;

//import agency.tango.materialintroscreen.MessageButtonBehaviour;
//import agency.tango.materialintroscreen.SlideFragmentBuilder;
//import agency.tango.materialintroscreen.animations.IViewTranslation;
import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.FloatRange;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

  List<AhoyOnboarderCard> pages = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initIntroCards();
    setOnboardPages(pages);
    setColorBackground(R.color.light_blue);

    loadData();
  }

  private void initIntroCards() {
    AhoyOnboarderCard card1 = new AhoyOnboarderCard("实时资讯",
        "轻松下滑就能从你的订阅源中获取最新咨询", R.drawable.logo);
    AhoyOnboarderCard card2 = new AhoyOnboarderCard("后台播放",
        "反正就能后台播放", R.drawable.profile);
    pages.add(card1);
    pages.add(card2);
  }

  @Override
  public void onFinishButtonPressed() {
    go();
  }

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
