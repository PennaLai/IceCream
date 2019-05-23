package com.example.icecream.ui.fragment;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.icecream.R;
import com.example.icecream.database.entity.Article;
import com.example.icecream.service.SpeakerService;
import com.example.icecream.ui.activity.LoginActivity;
import com.example.icecream.ui.activity.MainActivity;
import com.wang.avi.AVLoadingIndicatorView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ReadFragment extends Fragment {

  private static final String TAG = PlayFragment.class.getName();

  /** background player service. */
  private SpeakerService speakerService;

  /** all the music that waiting for play. */
  private List<String> waitingMusicList = new ArrayList<>();

  /** article title. */
  private TextView articleTitle;

  /** article publish time. */
  private TextView articleTime;

  /** article content. */
  private TextView articleContent;

  private AVLoadingIndicatorView downloadIndicator;

  private ScrollView articleScrollView;

  /** to control the progress update thread exit. */
  boolean needUpdate;

  /** is there is a song playing now. */
  boolean isPlaying;

  /** to count the song number we has played now. */
  private int playSongCount = 0;

  /** the article we are reading now. */
  private Article article;

  /** to update the ui progress thread. */
  Thread progressUpdateThread;

  private ProgressBar sbProgress;

  private NotificationManager musicBarManage;

  private RemoteViews remoteViews;

  /** Receive notification event. */
  BroadcastReceiver broadcastReceiver;

  private static final String ACTION_PRE = "ACTION_PRE";
  private static final String ACTION_NEXT = "ACTION_NEXT";
  private static final String ACTION_PLAY = "ACTION_PLAY";
  private static final String ACTION_PAUSE = "ACTION_PAUSE";

  /** record play music index right now. */
  private int playIndex = 0;

  private TextView volumeText;

  private ImageView iVBack;

  public static ReadFragment newInstance() {
    return new ReadFragment();
  }

  /** use to update the ui while playing music. */
  private final ReadFragment.MusicHandler uiUpdateHandle = new ReadFragment.MusicHandler(this);

  /** used for callback to update the UI. */
  public interface OnPlayerUiListener {
    void updateNewSongUi();
  }

  /**
   * Handler to update the ui while playing music.
   *
   * @author Penna
   */
  private static class MusicHandler extends Handler {
    private final WeakReference<ReadFragment> readFragmentWeakReference;

    public MusicHandler(ReadFragment fragment) {
      readFragmentWeakReference = new WeakReference<>(fragment);
    }

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      ReadFragment readFragment = readFragmentWeakReference.get();
      if (readFragment != null) {
        switch (msg.what) {
          case 0:
            double progress = msg.getData().getDouble("progress");
            int max = readFragment.sbProgress.getMax();
            int position = (int) (max * progress);
            readFragment.sbProgress.setProgress(position);
            //readFragment.articleScrollView.smoothScrollTo(0, position);
            break;
          default:
            break;
        }
      }
    }
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_read, container, false);
    iVBack = view.findViewById(R.id.read_iv_back);
    iVBack.setOnClickListener(v -> backToResource());
    articleTitle = view.findViewById(R.id.tv_read_title);
    articleTime = view.findViewById(R.id.tv_publish_time);
    articleContent = view.findViewById(R.id.tv_main_content);
    articleScrollView = view.findViewById(R.id.sv_article_view);
    downloadIndicator = view.findViewById(R.id.ld_download);
    ImageView btPlay = view.findViewById(R.id.read_iv_play);
    ImageView btNext = view.findViewById(R.id.read_iv_next);
    sbProgress = view.findViewById(R.id.read_pb_progress);
    btPlay.setOnClickListener(v -> playBackgroundMusic());
    btNext.setOnClickListener(v -> startNextArticle());
    //    sbProgress.setp(new VolumeListener());
    //     bind speaker service
    Activity activity = getActivity();
    if (activity != null) {
      activity.bindService(
          new Intent(getActivity(), SpeakerService.class),
          speakConnection,
          Context.BIND_AUTO_CREATE);
    }
    // start to update progress
    progressUpdateThread = new Thread(new ProgressUpdateThread());
    needUpdate = true;
    progressUpdateThread.start(); // TODO: 之后不应该在这里开始start线程, 不然会造成资源浪费
    // use for test
    waitingMusicList.add("music/I_am_happy.mp3");
    waitingMusicList.add("music/love_song.mp3");
    waitingMusicList.add("music/Reality.mp3");
    downloadIndicator.show();
    return view;
  }

  /** Back to the resource fragment view. */
  private void backToResource() {
    try {
      ((MainActivity) getActivity()).toResourceFragment();
    } catch (NullPointerException e) {
      // do nothing
    }
  }

  /**
   * from the resource fragment -> set new article and start it.
   * @param article
   */
  public void setArticle(Article article) {
    this.article = article;
    startNextArticle();
  }

  /** initialize the notification bar. */
  private void crateNotification() {
    if (speakerService == null) {
      return;
    }
    musicBarManage = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
    remoteViews = new RemoteViews(getActivity().getPackageName(), R.layout.music_notify);

    NotificationCompat.Builder builder = new Builder(getActivity());

    Intent intent = new Intent(getActivity(), MainActivity.class);
    // 点击跳转到主界面
    PendingIntent intent_go =
        PendingIntent.getActivity(getActivity(), 5, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.notice, intent_go);

    // 4个参数context, requestCode, intent, flags
    PendingIntent intent_close =
        PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.widget_close, intent_close);

    // 设置上一曲
    Intent prv = new Intent();
    prv.setAction(ACTION_PRE);
    PendingIntent intent_prev =
        PendingIntent.getBroadcast(getActivity(), 1, prv, PendingIntent.FLAG_UPDATE_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.widget_prev, intent_prev);

    // 下一曲
    Intent next = new Intent();
    next.setAction(ACTION_NEXT);
    PendingIntent intent_next =
        PendingIntent.getBroadcast(getActivity(), 3, next, PendingIntent.FLAG_UPDATE_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.widget_next, intent_next);

    // 设置播放

    if (speakerService.isPlaying()) {
      Intent playorpause = new Intent();
      playorpause.setAction(ACTION_PLAY);
      PendingIntent intent_play =
          PendingIntent.getBroadcast(
              getActivity(), 2, playorpause, PendingIntent.FLAG_UPDATE_CURRENT);
      remoteViews.setOnClickPendingIntent(R.id.widget_play, intent_play);
    } else {
      Intent playorpause = new Intent();
      playorpause.setAction(ACTION_PAUSE);
      PendingIntent intent_play =
          PendingIntent.getBroadcast(
              getActivity(), 6, playorpause, PendingIntent.FLAG_UPDATE_CURRENT);
      remoteViews.setOnClickPendingIntent(R.id.widget_play, intent_play);
    }

    // 设置收藏, 未来做的事情
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

    broadcastReceiver = new ReadFragment.NotificationClickReceiver();
    // register receiver
    IntentFilter filter = new IntentFilter("com.example.ACTION_PLAY");
    // set the custom action
    filter.addAction(ACTION_PRE);
    filter.addAction(ACTION_NEXT);
    filter.addAction(ACTION_PLAY);
    filter.addAction(ACTION_PAUSE);
    // register the receiver
    getActivity().registerReceiver(broadcastReceiver, filter);
  }

  /** Receiver click event from notification bar. */
  public class NotificationClickReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (action.equalsIgnoreCase(ACTION_PRE)) {
        startPreArticle();
      } else if (action.equalsIgnoreCase(ACTION_NEXT)) {
        startNextArticle();
      } else if (action.equalsIgnoreCase(ACTION_PLAY)) {
        playBackgroundMusic();
      } else if (action.equalsIgnoreCase(ACTION_PAUSE)) {
        playBackgroundMusic();
      }
    }
  }

  /** To destroy the music bar notification. */
  private void notificationDestory() {
    if (remoteViews != null) {
      musicBarManage.cancel(1);
    }
  }

  /** Play music or pause music. */
  private void playBackgroundMusic() {
    if (musicBarManage == null) {
      crateNotification();
    }
    if (playSongCount == 0) {
      startNextArticle();
    } else {
      if (!speakerService.isPlaying()) {
        speakerService.startMusic();
        // TODO update button ui
      } else {
        speakerService.pauseMusic();
      }
    }
  }

  /** start New Song. */
  private void startNewArticle() {
    if (article == null) return;
    isPlaying = false; // stop update the progress
    speakerService.startNewSong(waitingMusicList.get(playIndex));
    articleTitle.setText(article.getTitle());
    articleTime.setText(article.getPublishTime());
    articleContent.setText(article.getDescription());
    playSongCount++;
  }

  /** Start the next song. */
  private void startNextArticle() {
    startNewArticle();
    playIndexIncrease();
  }

  /** Back to the last song. */
  private void startPreArticle() {
    startNewArticle();
    playIndexDecrease();
  }

  /** to update ui after click pre or next or play or pause. */
  private void checkUiUpdate() {
    // TODO finish it.
  }

  /**
   * after the service prepare the song resource and start to play it should call back this method
   * to update the new song ui for example max progress, start to update progress thread.
   */
  private void updateNewSongUi() {
    isPlaying = true; // start the progress again
  }

  /**
   * increase the play waiting list to next one, it is a circular list(will go back to the first
   * one).
   */
  private void playIndexIncrease() {
    playIndex++;
    if (playIndex >= waitingMusicList.size()) playIndex = 0;
  }

  /** decrease the play waiting list to last one. */
  private void playIndexDecrease() {
    playIndex--;
    if (playIndex < 0) playIndex = waitingMusicList.size() - 1;
  }
  //
  //  /**
  //   * listener for the progress bar.
  //   */
  //  private class VolumeListener implements SeekBar.OnSeekBarChangeListener {
  //
  //    public void onProgressChanged(SeekBar seekBar, int progress,
  //        boolean fromUser) {
  //      // Log the progress
  //      //set textView's text
  //      volumeText.setText(String.valueOf(progress));
  //    }
  //
  //    public void onStartTrackingTouch(SeekBar seekBar) {}
  //
  //    public void onStopTrackingTouch(SeekBar seekBar) {
  //      int progress = speakerService.getDuration() * seekBar.getProgress() / 100;
  //      speakerService.seeTo(progress);
  //    }
  //
  //  }

  /** Use to connected and disconnected the service. */
  private ServiceConnection speakConnection =
      new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
          speakerService = ((SpeakerService.SpeakerBinder) service).getService();
          speakerService.setOnPlayerUIListener(() -> updateNewSongUi());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
          speakerService.destroyMediaPlayer();
        }
      };

  @Override
  public void onDestroy() {
    super.onDestroy();
    needUpdate = false; // finish the ui update thread.
    notificationDestory();
    Activity activity = getActivity();
    if (activity != null) {
      activity.unbindService(speakConnection);
    }
  }

  /** update the progress ui thread. */
  public class ProgressUpdateThread implements Runnable {

    @Override
    public void run() {
      // used for passing progress to ui
      Bundle data = new Bundle();
      try {
        while (needUpdate) {
          if (isPlaying) { // only when play can we update the progress
            Message msg = new Message();
            msg.what = 0;
            data.putDouble("progress", speakerService.getProgress());
            msg.setData(data);
            uiUpdateHandle.sendMessage(msg);
          }
        }
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Log.e(TAG, "run: ", e);
      }
    }
  }
}
