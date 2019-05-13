package com.example.icecream;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

//import com.example.icecream.database.entity.User;
import com.example.icecream.fragment.CenteredTextFragment;
import com.example.icecream.fragment.PlayFragment;
import com.example.icecream.fragment.ResourceFragment;
import com.example.icecream.menu.DrawerAdapter;
import com.example.icecream.menu.DrawerItem;
import com.example.icecream.menu.SimpleItem;
import com.example.icecream.menu.SpaceItem;
//import com.example.icecream.utils.ViewModel;
import com.example.icecream.search.BoilerplateActivity;
import com.example.icecream.search.SimpleToolbar;
import com.example.icecream.searchscreen.SearchActivity;
import com.example.icecream.transition.FadeInTransition;
import com.example.icecream.transition.FadeOutTransition;
import com.example.icecream.transition.SimpleTransitionListener;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

//import android.support.v4.app.Fragment;

/**
 * The main activity.
 *
 * @version V1.0
 */

public class MainActivity extends BoilerplateActivity implements View.OnClickListener, DrawerAdapter.OnItemSelectedListener, ResourceFragment.MusicConnector{
  //TODO: bind the speaker service here but not playfragment.

  private static final int POS_DASHBOARD = 0;
  private static final int POS_ACCOUNT = 1;
  private static final int POS_MESSAGES = 2;
  private static final int POS_CART = 3;
  private static final int POS_LOGOUT = 5;

  private SimpleToolbar toolbar;
  private int toolbarMargin;

  private String[] screenTitles;
  private Drawable[] screenIcons;

  private SlidingRootNav slidingRootNav;

  /** connection between UI and Repository*/
//  private ViewModel mViewMode;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Get view model
//    mViewMode = ViewModelProviders.of(this).get(ViewModel.class);
//    // listen to live data onChange
//    mViewMode.getAllUsers().observe(this, new Observer<List<User>>() {
//      @Override
//      public void onChanged(@Nullable List<User> users) {
//        // update the cached of user in the adapter
//      }
//    });

//    toolbar = (SimpleToolbar) findViewById(R.id.toolbar);

    //定义数据
    final Map<Integer, android.support.v4.app.Fragment> data = new TreeMap<>();
    data.put(0, ResourceFragment.newInstance());
    data.put(1, PlayFragment.newInstance());

    //找到ViewPager
    ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

    //为ViewPager配置Adapter
    viewPager.setAdapter(new FragmentStatePagerAdapter(
            getSupportFragmentManager()) {
      @Override
      public android.support.v4.app.Fragment getItem(int position) {
        return data.get(position);
      }

      @Override
      public int getCount() {
        return data.size();
      }
    });

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    slidingRootNav = new SlidingRootNavBuilder(this)
            .withToolbarMenuToggle(toolbar)
            .withMenuOpened(false)
            .withContentClickableWhenMenuOpened(false)
            .withSavedState(savedInstanceState)
            .withMenuLayout(R.layout.menu_left_drawer)
            .inject();

    screenIcons = loadScreenIcons();
    screenTitles = loadScreenTitles();

    DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
            createItemFor(POS_DASHBOARD).setChecked(true),
            createItemFor(POS_ACCOUNT),
            createItemFor(POS_MESSAGES),
            createItemFor(POS_CART),
            new SpaceItem(48),
            createItemFor(POS_LOGOUT)));
    adapter.setListener(this);

    RecyclerView list = findViewById(R.id.list);
    list.setNestedScrollingEnabled(false);
    list.setLayoutManager(new LinearLayoutManager(this));
    list.setAdapter(adapter);

    adapter.setSelected(POS_DASHBOARD);
  }

  public void setUpToolbar(SimpleToolbar tb){
    toolbar = tb;
    setSupportActionBar(toolbar);

    toolbarMargin = getResources().getDimensionPixelSize(R.dimen.toolbarMargin);
    toolbar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // Prepare the keyboard as soon as the user touches the Toolbar
        // This will make the transition look faster
        showKeyboard();
//        transitionToSearch();
      }
    });
  }

  @Override
  public void onItemSelected(int position) {
    if (position == POS_LOGOUT) {
      finish();
    }
    slidingRootNav.closeMenu();
    Fragment selectedScreen = CenteredTextFragment.createFor(screenTitles[position]);
    showFragment(selectedScreen);
  }

  private void showFragment(Fragment fragment) {
    getFragmentManager().beginTransaction()
            .replace(R.id.container, fragment)
            .commit();
  }

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
      case R.id.bt_goToPersonalDetail:
        goToPersonalDetail();
        break;
    }
  }

  private void goToPersonalDetail() {
    Intent intent = new Intent(MainActivity.this, PersonalDetailActivity.class);
    startActivity(intent);
  }

  @Override
  public void sendNewMusic() {
    // connect to player fragment
  }

  @Override
  public void startPlayer() {

  }

  @Override
  public void stopPlayer() {

  }

  @Override
  public void pausePlayer() {

  }
//
//
//  private void transitionToSearch() {
//    // create a transition that navigates to search when complete
//    Transition transition = FadeOutTransition.withAction(navigateToSearchWhenDone());
//
//    // let the TransitionManager do the heavy work for us!
//    // all we have to do is change the attributes of the toolbar and the TransitionManager animates the changes
//    // in this case I am removing the bounds of the toolbar (to hide the blue padding on the screen) and
//    // I am hiding the contents of the Toolbar (Navigation icon, Title and Option Items)
//    TransitionManager.beginDelayedTransition(toolbar, transition);
//    FrameLayout.LayoutParams frameLP = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
//    frameLP.setMargins(0, 0, 0, 0);
//    toolbar.setLayoutParams(frameLP);
//    toolbar.hideContent();
//  }
//
//  private Transition.TransitionListener navigateToSearchWhenDone() {
//    return new SimpleTransitionListener() {
//      @Override
//      public void onTransitionEnd(Transition transition) {
//        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
//        startActivity(intent);
//
//        // we are handing the enter transitions ourselves
//        // this line overrides that
//        overridePendingTransition(0, 0);
//
//        // by this point of execution we have animated the 'expansion' of the Toolbar and hidden its contents.
//        // We are half way there. Continue to the SearchActivity to finish the animation
//      }
//    };
//  }

//  @Override
//  protected void onResume() {
//    super.onResume();
//
//    // when you are back from the SearchActivity animate the 'shrinking' of the Toolbar and
//    // fade its contents back in
//    fadeToolbarIn();
//
//    // in case we are not coming here from the SearchActivity the Toolbar would have been already visible
//    // so the above method has no effect
//  }
//
//  private void fadeToolbarIn() {
//    TransitionManager.beginDelayedTransition(toolbar, FadeInTransition.createTransition());
//    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
//    layoutParams.setMargins(toolbarMargin, toolbarMargin, toolbarMargin, toolbarMargin);
//    toolbar.showContent();
//    toolbar.setLayoutParams(layoutParams);
//  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.main_action_share) {
      shareDemo();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

}
