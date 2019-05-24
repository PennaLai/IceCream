package com.example.icecream.ui.activity;

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
import android.view.Menu;
import android.view.View;

import android.widget.Toast;
import com.example.icecream.R;

import com.example.icecream.database.entity.Article;
import com.example.icecream.ui.fragment.ReadFragment;
import com.example.icecream.ui.fragment.ResourceFragment;
import com.example.icecream.ui.component.menu.DrawerAdapter;
import com.example.icecream.ui.component.menu.DrawerItem;
import com.example.icecream.ui.component.menu.SimpleItem;
import com.example.icecream.ui.component.menu.SpaceItem;

import com.example.icecream.utils.UserSettingHandler;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

import java.util.Map;
import java.util.TreeMap;


/**
 * The main activity.
 * Reference: https://blog.csdn.net/u013926110/article/details/46945199
 *
 * @author Penna
 * @version V1.0
 */
public class MainActivity extends AppCompatActivity
    implements View.OnClickListener,
    DrawerAdapter.OnItemSelectedListener,
    ResourceFragment.MusicConnector {

  public static MainActivity context;

  /** The positions for Drawer Item. */
  private static final int POS_DASHBOARD = 0;
  private static final int POS_FEED = 1;
  private static final int POS_STAR = 2;
  private static final int POS_SETTING = 3;
  private static final int POS_LOGOUT = 5;

  /** The toolbar for the Activity */
  private Toolbar toolbar;

  /** The titles for the drawer items */
  private String[] screenTitles;

  /** The icons for the drawer items */
  private Drawable[] screenIcons;

  /** The function interface for the SlidingRootNavLayout we used */
  private SlidingRootNav slidingRootNav;

  /** The Adapter for the Drawer */
  private DrawerAdapter adapter;

  /** The ViewPager instance to hold sliding ResourceFragment and ReadFragment */
  private ViewPager viewPager;

  /** The Map to hold the sliding Fragments */
  final Map<Integer, android.support.v4.app.Fragment> data = new TreeMap<>();

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    context = this;
    // 定义数据
    data.put(0, ResourceFragment.newInstance());
    data.put(1, ReadFragment.newInstance());

    // 找到ViewPager
    viewPager = findViewById(R.id.view_pager);

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

    UserSettingHandler userSettingHandler = UserSettingHandler.getInstance(getApplication());

    if (userSettingHandler.getLoginPhone() != null) {
      adapter.updateItem(POS_LOGOUT, getResources().getDrawable(R.drawable.icons8_export),
          getResources().getText(R.string.logout).toString());
    }

    adapter.setSelected(POS_DASHBOARD);
  }

  /**
   * This method is invoked from resource fragment to set up the customized Toolbar.
   *
   * @param tb : The instance of Toolbar.
   */
  public void setUpToolbar(Toolbar tb) {
    toolbar = tb;
  }

  @Override
  public void onItemSelected(int position) {
    if (position == POS_LOGOUT) {
      UserSettingHandler userSettingHandler = UserSettingHandler.getInstance(getApplication());
      if (userSettingHandler.getLoginPhone() != null) {
        userSettingHandler.setLoginPhone(null);
      }
      goToActivity(LoginActivity.class);
      adapter.setSelected(POS_DASHBOARD);
      slidingRootNav.closeMenu();
    } else if (position == POS_FEED) {
      goToActivity(SubscribeActivity.class);
      adapter.setSelected(POS_DASHBOARD);
      slidingRootNav.closeMenu();
    } else if (position == POS_STAR) {
//      String repairing = "抱歉，这个功能正在装修中";
//      Toast.makeText(this, repairing, Toast.LENGTH_LONG).show();
      goToActivity(StarActivity.class);
      adapter.setSelected(POS_DASHBOARD);
      slidingRootNav.closeMenu();
    } else if (position == POS_SETTING) {
      goToActivity(SettingActivity.class);
      adapter.setSelected(POS_DASHBOARD);
      slidingRootNav.closeMenu();
    }
  }

  /**
   * This method would change the current activity to a destinated activity.
   *
   * @param destActivity : the destinated activity.
   */
  private void goToActivity(Class destActivity) {
    final Context context = this;
    final Intent registerIntent = new Intent(context, destActivity);
    startActivity(registerIntent);
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

  /**
   * This method load titles of drawer items from xml file.
   *
   * @return java.lang.String[] the String array of titles
   */
  private String[] loadScreenTitles() {
    return getResources().getStringArray(R.array.ld_activityScreenTitles);
  }

  /**
   * This method load icons of drawer items from xml file.
   *
   * @return android.graphics.drawable.Drawable[] the Drawable array of icons
   */
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
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  /**
   * Select the article and go the reading page.
   *
   * @param article the article
   */
  @Override
  public void onArticleSelect(Article article) {
    ReadFragment readFragment = ((ReadFragment) data.get(1));
    toReadFragment();
    readFragment.startDownloadArticle(article);
  }

  @Override
  public void onProfileClicked() {
    slidingRootNav.openMenu();
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
//    super.onBackPressed();
    toResourceFragment();
  }

  public static MainActivity getContext() {
    return context;
  }
}
