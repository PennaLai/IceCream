package com.example.icecream;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment.SavedState;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViews.RemoteView;
import com.example.icecream.fragment.CenteredTextFragment;
import com.example.icecream.fragment.PlayFragment;
import com.example.icecream.fragment.ResourceFragment;
import com.example.icecream.menu.DrawerAdapter;
import com.example.icecream.menu.DrawerItem;
import com.example.icecream.menu.SimpleItem;
import com.example.icecream.menu.SpaceItem;
import com.example.icecream.search.BoilerplateActivity;
import com.example.icecream.search.SimpleToolbar;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * The main activity.
 *
 * Reference: https://blog.csdn.net/u013926110/article/details/46945199
 * @author Penna
 * @version V1.0
 */
public class MainActivity extends BoilerplateActivity
    implements View.OnClickListener,
        DrawerAdapter.OnItemSelectedListener,
        ResourceFragment.MusicConnector {
  // TODO: bind the speaker service here but not play fragment.

  private static final int POS_DASHBOARD = 0;
  private static final int POS_ACCOUNT = 1;
  private static final int POS_MESSAGES = 2;
  private static final int POS_CART = 3;
  private static final int POS_LOGOUT = 5;
  private static final String TAG = "MainActivity";

  private SimpleToolbar toolbar;
  private int toolbarMargin;

  private String[] screenTitles;
  private Drawable[] screenIcons;

  private SlidingRootNav slidingRootNav;
  private DrawerAdapter adapter;
  private ViewPager viewPager;

  private NotificationManager musicBarManage;
  private Notification notify;
  private RemoteViews remoteViews;

  BroadcastReceiver broadcastReceiver;

  private static final String ACTION_PRE = "ACTION_PRE";
  private static final String ACTION_NEXT = "ACTION_NEXT";

  /** connection between UI and Repository. */
//    private ViewModel mViewMode;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // 定义数据
    final Map<Integer, android.support.v4.app.Fragment> data = new TreeMap<>();
    data.put(0, ResourceFragment.newInstance());
    data.put(1, PlayFragment.newInstance());

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

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

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
                createItemFor(POS_ACCOUNT),
                createItemFor(POS_MESSAGES),
                createItemFor(POS_CART),
                new SpaceItem(48),
                createItemFor(POS_LOGOUT)));
    adapter.setListener(this);

    RecyclerView draw_list = findViewById(R.id.draw_list);

    draw_list.setNestedScrollingEnabled(false);
    draw_list.setLayoutManager(new LinearLayoutManager(this));
    draw_list.setAdapter(adapter);

    adapter.setSelected(POS_DASHBOARD);

    // register notification and its receiver
    initNotification();

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    notificationDestory();
  }

  /**
   * initialize the notification bar.
   */
  private void initNotification() {

    musicBarManage = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    remoteViews = new RemoteViews(getPackageName(),R.layout.music_notify);

    NotificationCompat.Builder builder = new Builder(this);

    Intent intent = new Intent(MainActivity.this, MainActivity.class);
    // 点击跳转到主界面
    PendingIntent intent_go = PendingIntent.getActivity(this, 5, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.notice, intent_go);

    // 4个参数context, requestCode, intent, flags
    PendingIntent intent_close = PendingIntent.getActivity(this, 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.widget_close, intent_close);

    // 设置上一曲
    Intent prv = new Intent();
    prv.setAction(ACTION_PRE);
    PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, prv,
        0);
    remoteViews.setOnClickPendingIntent(R.id.widget_prev, intent_prev);

    // 下一曲
    Intent next = new Intent();
    next.setAction(ACTION_NEXT);
    PendingIntent intent_next = PendingIntent.getBroadcast(this, 3, next,
        0);
    remoteViews.setOnClickPendingIntent(R.id.widget_next, intent_next);

//    // 设置播放
//    Intent playorpause = new Intent();
//    playorpause.setAction("PLAY");
//    PendingIntent intent_play = PendingIntent.getBroadcast(this, 2,
//        playorpause, PendingIntent.FLAG_UPDATE_CURRENT);
//    remoteViews.setOnClickPendingIntent(R.id.widget_play, intent_play);

//    if (!Myapp.isPlay) {
//      Intent playorpause = new Intent();
//      playorpause.setAction(Constants.ACTION_PLAY);
//      PendingIntent intent_play = PendingIntent.getBroadcast(this, 6,
//          playorpause, PendingIntent.FLAG_UPDATE_CURRENT);
//      remoteViews.setOnClickPendingIntent(R.id.widget_play, intent_play);
//    }

//    // 设置收藏
//    PendingIntent intent_fav = PendingIntent.getBroadcast(this, 4, intent,
//        PendingIntent.FLAG_UPDATE_CURRENT);
//    remoteViews.setOnClickPendingIntent(R.id.widget_fav, intent_fav);
//

    builder.setSmallIcon(R.drawable.logo); // 设置顶部图标

    Notification notify = builder.build();
    notify.contentView = remoteViews; // 设置下拉图标
    notify.bigContentView = remoteViews; // 防止显示不完全,需要添加apisupport
    notify.flags = Notification.FLAG_ONGOING_EVENT;
    notify.icon = R.drawable.logo;

    musicBarManage.notify(1, notify); // id 代表通知的id，可以在后续通过id关闭

    broadcastReceiver = new NotificationClickReceiver();
    // register receiver
    IntentFilter filter = new IntentFilter("com.example.ACTION_PLAY");
    // set the custom action
    filter.addAction(ACTION_PRE);
    filter.addAction(ACTION_NEXT);
    // register the receiver
    registerReceiver(broadcastReceiver, filter);

  }

  public class NotificationClickReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (action.equalsIgnoreCase(ACTION_PRE)) {
        Log.i(TAG, "onReceive: PRE!");
      } else if (action.equalsIgnoreCase(ACTION_NEXT)) {
        Log.i(TAG, "onReceive: NEXT!");
      }

    }
  }

  /**
   * To destroy the music bar notification.
   */
  private void notificationDestory() {
    if (remoteViews != null) {
      musicBarManage.cancel(1);
    }
  }

//  private static class NotificationHandler extends Handler {
//    private final WeakReference<MainActivity> mainActivityWeakReference;
//
//    public NotificationHandler(
//        WeakReference<MainActivity> mainActivityWeakReference) {
//      this.mainActivityWeakReference = mainActivityWeakReference;
//    }
//
//    @Override
//    public void handleMessage(Message msg) {
//      super.handleMessage(msg);
//    }
//  }

  /**
   * This method is invoked from resource fragment to set up the customized Toolbar.
   * @param tb : The instance of SimpleToolbar
   */
  public void setUpToolbar(SimpleToolbar tb) {
    toolbar = tb;
    setSupportActionBar(toolbar);

    toolbarMargin = getResources().getDimensionPixelSize(R.dimen.toolbarMargin);
    toolbar.setOnClickListener(v -> showKeyboard());
  }

  @Override
  public void onItemSelected(int position) {
    if (position == POS_LOGOUT) {
      login();
      adapter.setSelected(POS_DASHBOARD);
//      finish();
    }
    slidingRootNav.closeMenu();
//    Fragment selectedScreen = CenteredTextFragment.createFor(screenTitles[position]);
//    showFragment(selectedScreen);

    Log.i("Draw", ""+position);
  }

  private void login() {
    final Context context = this;
    final Class destActivity = LoginActivity.class;
    final Intent registerIntent = new Intent(context, destActivity);
    startActivity(registerIntent);
  }

  /**
   * This method is to switch fragment according to the selected draw item.
   * @param fragment : The destined fragment.
   */
  private void showFragment(Fragment fragment) {
    getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
  }

  /**
   * This method creates the draw items.
   * @param position : create item by index to initialize.
   * @return com.example.icecream.menu.DrawerItem
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

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.main_action_share) {
      shareDemo();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onArticleSelect() {
    Log.i(TAG, "onArticleSelect: Go fuck your self");
    viewPager.setCurrentItem(1, true);
  }

}
