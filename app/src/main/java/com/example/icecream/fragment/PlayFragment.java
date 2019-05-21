package com.example.icecream.fragment;

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
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;


import butterknife.BindView;

import com.example.icecream.MainActivity;

import com.example.icecream.R;
import com.example.icecream.service.SpeakerService;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
//import me.zhengken.lyricview.LyricView;

/**
 * This is the ui fragment to handler the player event.
 * Reference: https://blog.csdn.net/zhao123h/article/details/33393909
 * @author Penna
 */
public class PlayFragment extends Fragment {

  private static final String TAG = PlayFragment.class.getName();
  /** background player service.*/
  private SpeakerService speakerService;

  /** all the music that waiting for play. */
  private List<String> waitingMusicList = new ArrayList<>();

  /** to control the progress update thread exit. */
  boolean needUpdate;

  /** is there is a song playing now. */
  boolean isPlaying;

  /** to count the song number we has palyed now*/
  private int playSongCount = 0;

  /** to update the ui progress thread. */
  Thread progressUpdateThread;

  SeekBar sbProgress;

  private NotificationManager musicBarManage;

  private RemoteViews remoteViews;

  BroadcastReceiver broadcastReceiver;

  private static final String ACTION_PRE = "ACTION_PRE";
  private static final String ACTION_NEXT = "ACTION_NEXT";
  private static final String ACTION_PLAY = "ACTION_PLAY";
  private static final String ACTION_PAUSE = "ACTION_PAUSE";

  /** record play music index right now. */
  private int playIndex = 0;

  private TextView volumeText;



  public static PlayFragment newInstance() {
    return new PlayFragment();
  }

  /** use to update the ui while playing music. */
  private final MusicHandler uiUpdateHandle = new MusicHandler(this);

  /**
   * used for callback to update the UI.
   */
  public interface OnPlayerUiListener {
    void updateNewSongUi();
  }

  /**
   * Handler to update the ui while playing music.
   * @author Penna
   */
  private static class MusicHandler extends Handler {
    private final WeakReference<PlayFragment> playFragmentWeakReference;

    public MusicHandler(PlayFragment fragment) {
      playFragmentWeakReference = new WeakReference<>(fragment);
    }

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      PlayFragment playFragment = playFragmentWeakReference.get();
      if (playFragment != null) {
        switch (msg.what) {
          case 0:
            double progress = msg.getData().getDouble("progress");
            int max = playFragment.sbProgress.getMax();
            int position = (int) (max * progress);
            playFragment.sbProgress.setProgress(position);
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
    View view = inflater.inflate(R.layout.fragment_play, container, false);

    volumeText = view.findViewById(R.id.music_title);
    ImageView btPlay = view.findViewById(R.id.btn_play_pause);
    ImageView btNext = view.findViewById(R.id.btn_next);
    sbProgress = view.findViewById(R.id.music_seek_bar);
    btPlay.setOnClickListener(v -> playBackgroundMusic());
    btNext.setOnClickListener(v -> playNextMusic());
    sbProgress.setOnSeekBarChangeListener(new VolumeListener());
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
    progressUpdateThread.start();  // TODO: 之后不应该在这里开始start线程, 不然会造成资源浪费,
    // use for test
    waitingMusicList.add("music/I_am_happy.mp3");
    waitingMusicList.add("music/love_song.mp3");
    waitingMusicList.add("music/Reality.mp3");
    return view;
  }

  /**
   * initialize the notification bar.
   */
  private void crateNotification() {
    if (speakerService == null) {
      return;
    }
    musicBarManage = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
    remoteViews = new RemoteViews(getActivity().getPackageName(),R.layout.music_notify);

    NotificationCompat.Builder builder = new Builder(getActivity());

    Intent intent = new Intent(getActivity(), MainActivity.class);
    // 点击跳转到主界面
    PendingIntent intent_go = PendingIntent.getActivity(getActivity(), 5, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.notice, intent_go);

    // 4个参数context, requestCode, intent, flags
    PendingIntent intent_close = PendingIntent.getActivity(getActivity(), 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.widget_close, intent_close);

    // 设置上一曲
    Intent prv = new Intent();
    prv.setAction(ACTION_PRE);
    PendingIntent intent_prev = PendingIntent.getBroadcast(getActivity(), 1, prv,
        PendingIntent.FLAG_UPDATE_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.widget_prev, intent_prev);

    // 下一曲
    Intent next = new Intent();
    next.setAction(ACTION_NEXT);
    PendingIntent intent_next = PendingIntent.getBroadcast(getActivity(), 3, next,
        PendingIntent.FLAG_UPDATE_CURRENT);
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
          PendingIntent.getBroadcast(getActivity(), 6, playorpause, PendingIntent.FLAG_UPDATE_CURRENT);
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

    broadcastReceiver = new NotificationClickReceiver();
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


  /**
   * Receiver click event from notification bar.
   */
  public class NotificationClickReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (action.equalsIgnoreCase(ACTION_PRE)) {
        playPreMusic();
      } else if (action.equalsIgnoreCase(ACTION_NEXT)) {
        playNextMusic();
      } else if (action.equalsIgnoreCase(ACTION_PLAY)) {
        playBackgroundMusic();
      } else if (action.equalsIgnoreCase(ACTION_PAUSE)) {
        playBackgroundMusic();
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

  /**
   * Play music or pause music.
   */
  private void playBackgroundMusic() {
    if (musicBarManage == null) {
      crateNotification();
    }
    if (playSongCount == 0) {
      playNextMusic();
    } else {
      if (!speakerService.isPlaying()) {
        speakerService.startMusic();
      } else {
        speakerService.pauseMusic();
      }
    }
  }

  /**
   * Start the next song.
   */
  private void playNextMusic() {
    isPlaying = false; // stop update the progress to avoid null point.
    speakerService.startNewSong(waitingMusicList.get(playIndex));
    playSongCount++;
    playIndexIncrease();
  }

  /**
   * Back to the last song.
   */
  private void playPreMusic() {
    isPlaying = false; // stop update the progress
    speakerService.startNewSong(waitingMusicList.get(playIndex));
    playSongCount++;
    playIndexDecrease();
  }

  /**
   * to update ui after click pre or next or play or pause.
   */
  private void checkUiUpdate() {
    //TODO finish it.
  }


  /**
   * after the service prepare the song resource and start to play
   * it should call back this method to update the new song ui for example
   * max progress, start to update progress thread.
   */
  private void updateNewSongUi() {
    isPlaying = true; // start the progress again
  }

  /**
   * increase the play waiting list to next one,
   * it is a circular list(will go back to the first one).
   */
  private void playIndexIncrease() {
    playIndex++;
    if (playIndex >= waitingMusicList.size())
      playIndex = 0;
  }

  /**
   * decrease the play waiting list to last one.
   */
  private void playIndexDecrease() {
    playIndex--;
    if (playIndex < 0)
      playIndex = waitingMusicList.size()-1;
  }

  /**
   * listener for the progress bar.
   */
  private class VolumeListener implements SeekBar.OnSeekBarChangeListener {

    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
      // Log the progress
      //set textView's text
      volumeText.setText(String.valueOf(progress));
    }

    public void onStartTrackingTouch(SeekBar seekBar) {}

    public void onStopTrackingTouch(SeekBar seekBar) {
      int progress = speakerService.getDuration() * seekBar.getProgress() / 100;
      speakerService.seeTo(progress);
    }

  }

  /**
   * Use to connected and disconnected the service.
   */
  private ServiceConnection speakConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      speakerService = ((SpeakerService.SpeakerBinder) service).getService();
      speakerService.setOnPlayerUIListener(() -> updateNewSongUi());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
  };


  /**
   * update the progress ui thread.
   */
  public class ProgressUpdateThread implements Runnable {

    @Override
    public void run() {
      //used for passing progress to ui
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
