package com.example.icecream.ui.activity;

import android.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;


import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.example.icecream.R;

import com.example.icecream.database.entity.Article;
import com.example.icecream.database.entity.RssFeed;
import com.example.icecream.ui.fragment.PlayFragment;
import com.example.icecream.ui.fragment.ReadFragment;
import com.example.icecream.ui.fragment.ResourceFragment;
import com.example.icecream.ui.component.menu.DrawerAdapter;
import com.example.icecream.ui.component.menu.DrawerItem;
import com.example.icecream.ui.component.menu.SimpleItem;
import com.example.icecream.ui.component.menu.SpaceItem;

import com.example.icecream.utils.AppViewModel;
import com.example.icecream.utils.HttpHandler;
import com.example.icecream.utils.ResourceHandler;
import com.example.icecream.utils.UserSettingHandler;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import java.util.Arrays;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * The main activity.
 * Reference: https://blog.csdn.net/u013926110/article/details/46945199
 * @author Penna
 * @version V1.0
 */
public class MainActivity extends AppCompatActivity
    implements View.OnClickListener,
    DrawerAdapter.OnItemSelectedListener,
    ResourceFragment.MusicConnector {

  private static final int POS_DASHBOARD = 0;
  private static final int POS_FEED = 1;
  private static final int POS_STAR = 2;
  private static final int POS_SETTING = 3;
  private static final int POS_LOGOUT = 5;
  private static final String TAG = "MainActivity";

  private Toolbar toolbar;
  private int toolbarMargin;

  private String[] screenTitles;
  private Drawable[] screenIcons;

  private SlidingRootNav slidingRootNav;
  private DrawerAdapter adapter;
  private ViewPager viewPager;

  final Map<Integer, android.support.v4.app.Fragment> data = new TreeMap<>();

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);



    // 定义数据

    data.put(0, ResourceFragment.newInstance());
    data.put(1, ReadFragment.newInstance());


    // 找到ViewPager
    viewPager = (ViewPager) findViewById(R.id.view_pager);

    // 为ViewPager配置Adapter
    viewPager.setAdapter(
        new FragmentStatePagerAdapter(getSupportFragmentManager()) {
          @Override
          public android.support.v4.app.Fragment getItem(int position) {
            return data.get(position);
          }

          @Override
          public int getCount() {
            return data.size();
          }
        });

//    Toolbar toolbar = findViewById(R.id.toolbar);
//    setSupportActionBar(toolbar);

    slidingRootNav =
        new SlidingRootNavBuilder(this)
            .withToolbarMenuToggle(toolbar)
            .withMenuOpened(false)
            .withContentClickableWhenMenuOpened(false)
            .withSavedState(savedInstanceState)
            .withMenuLayout(R.layout.menu_left_drawer)
            .inject();

    screenIcons = loadScreenIcons();
    screenTitles = loadScreenTitles();

    adapter =
        new DrawerAdapter(
            Arrays.asList(
                createItemFor(POS_DASHBOARD).setChecked(true),
                createItemFor(POS_FEED),
                createItemFor(POS_STAR),
                createItemFor(POS_SETTING),
                new SpaceItem(48),
                createItemFor(POS_LOGOUT)));
    adapter.setListener(this);

    RecyclerView draw_list = findViewById(R.id.draw_list);

    draw_list.setNestedScrollingEnabled(false);
    draw_list.setLayoutManager(new LinearLayoutManager(this));
    draw_list.setAdapter(adapter);

    adapter.setSelected(POS_DASHBOARD);
    // load data into view model
    AppViewModel viewModel = ViewModelProviders.of(this).get(AppViewModel.class);
    HttpHandler httpHandler = HttpHandler.getInstance(getApplication());
    ResourceHandler resourceHandler = ResourceHandler.getInstance(httpHandler, viewModel);
    UserSettingHandler userSettingHandler = UserSettingHandler.getInstance(getApplication());
    String phoneNumber = userSettingHandler.getLoginPhone();
    resourceHandler.loadRssFeeds(phoneNumber);
    resourceHandler.loadArticles(phoneNumber);


  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
  }


  /**
   * This method is invoked from resource fragment to set up the customized Toolbar.
   *
   * @param tb : The instance of SimpleToolbar
   */

  public void setUpToolbar(Toolbar tb) {
//    toolbar = tb;
//    setSupportActionBar(toolbar);
//
//    toolbarMargin = getResources().getDimensionPixelSize(R.dimen.toolbarMargin);
//    toolbar.setOnClickListener(v -> showKeyboard());
//    super.setUpToolbar(toolbar);
    toolbar = tb;
//    toolbar.setLogo(R.mipmap.logo);
//    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
////    getSupportActionBar().setTitle("");
  }

  @Override
  public void onItemSelected(int position) {
    if (position == POS_LOGOUT) {
//      login();
      UserSettingHandler.autoLoginFlag = false;
      goToActivity(LoginActivity.class);
      adapter.setSelected(POS_DASHBOARD);
//      finish();
    } else if (position == POS_FEED) {
//      feed();
      goToActivity(SubscribeActivity.class);
      adapter.setSelected(POS_DASHBOARD);
    }
    slidingRootNav.closeMenu();
//    Fragment selectedScreen = CenteredTextFragment.createFor(screenTitles[position]);
//    showFragment(selectedScreen);

    Log.i("Draw", "" + position);
  }

  private void goToActivity(Class destActivity){
    final Context context = this;
    final Intent registerIntent = new Intent(context, destActivity);
    startActivity(registerIntent);
  }

//  private void login() {
//    final Context context = this;
//    final Class destActivity = LoginActivity.class;
//    final Intent registerIntent = new Intent(context, destActivity);
//    startActivity(registerIntent);
//  }
//
//  private void feed() {
//    final Context context = this;
//    final Class destActivity = SubscribeActivity.class;
//    final Intent registerIntent = new Intent(context, destActivity);
//    startActivity(registerIntent);  }

  /**
   * This method is to switch fragment according to the selected draw item.
   *
   * @param fragment : The destined fragment.
   */
  private void showFragment(Fragment fragment) {
    getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
  }

  /**
   * This method creates the draw items.
   *
   * @param position : create item by index to initialize.
   * @return com.example.icecream.ui.component.menu.DrawerItem
   */
  private DrawerItem createItemFor(int position) {
    return new SimpleItem(screenIcons[position], screenTitles[position])
        .withIconTint(color(R.color.textColorSecondary))
        .withTextTint(color(R.color.textColorPrimary))
        .withSelectedIconTint(color(R.color.colorAccent))
        .withSelectedTextTint(color(R.color.colorAccent));
  }

  private String[] loadScreenTitles() {
    return getResources().getStringArray(R.array.ld_activityScreenTitles);
  }

  private Drawable[] loadScreenIcons() {
    TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
    Drawable[] icons = new Drawable[ta.length()];
    for (int i = 0; i < ta.length(); i++) {
      int id = ta.getResourceId(i, 0);
      if (id != 0) {
        icons[i] = ContextCompat.getDrawable(this, id);
      }
    }
    ta.recycle();
    return icons;
  }

  @ColorInt
  private int color(@ColorRes int res) {
    return ContextCompat.getColor(this, res);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      // do nothing
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  /**
   * Select the article and go the reading page.
   * @param article the article
   */
  @Override
  public void onArticleSelect(Article article) {
    ReadFragment readFragment = ((ReadFragment)data.get(1));
    readFragment.setArticle(article);
    toReadFragment();
  }

  /**
   * Switch view to Resource Fragment.
   */
  public void toResourceFragment() {
    viewPager.setCurrentItem(0, true);
  }

  /**
   * Switch view to Read Fragment.
   */
  public void toReadFragment() {
    viewPager.setCurrentItem(1, true);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }



}
