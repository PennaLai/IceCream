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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.example.icecream.R;
import com.example.icecream.database.entity.Article;
import com.example.icecream.service.SpeakerService;
import com.example.icecream.ui.activity.LoginActivity;
import com.example.icecream.ui.activity.MainActivity;
import com.example.icecream.ui.component.paragraph.Paragraph;
import com.example.icecream.ui.component.paragraph.ParagraphAdapter;
import com.example.icecream.utils.Para;
import com.wang.avi.AVLoadingIndicatorView;
import com.xw.repo.BubbleSeekBar;
import com.xw.repo.BubbleSeekBar.OnProgressChangedListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReadFragment extends Fragment {

  private static final String TAG = PlayFragment.class.getName();

  /** background player service. */
  private SpeakerService speakerService;

  /** all the music that waiting for play. */
  private List<String> waitingMusicList = new ArrayList<>();

  private List<Paragraph> paragraphList = new ArrayList<>();

  private ListView paragraphs;

  private AVLoadingIndicatorView downloadIndicator;

  /** to control the progress update thread exit. */
  boolean needUpdate;

  /** is there is a song playing now. */
  boolean isPlaying;

  /** to count the song number we has played now. */
  private int playSongCount = 0;

  /** the article we are reading now. */
  private Article article;

  /** para num*/
  private int paraNum;

  /** all para information. */
  private Para para;

  /** to update the ui progress thread. */
  Thread progressUpdateThread;

//  private ProgressBar sbProgress;
  private BubbleSeekBar sbProgress;

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

    private MusicHandler(ReadFragment fragment) {
      readFragmentWeakReference = new WeakReference<>(fragment);
    }

    /**
     * update the seek bar and list view scroll
     * @param msg
     */
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      ReadFragment readFragment = readFragmentWeakReference.get();
      if (readFragment != null) {
        switch (msg.what) {
          case 0:
            double progress = msg.getData().getDouble("progress");
            int max = 100;
            int position = (int) (max * progress);
            readFragment.sbProgress.setProgress(position);
            double timeNow = progress * readFragment.speakerService.getDuration();
            int i = 0;
            for (; i < readFragment.paraNum-1; i++) {
              if (readFragment.para.getParas()[i].getStartTime()
                  > timeNow) break;
            }
            readFragment.paragraphs.smoothScrollToPositionFromTop(i, 0, 500);
            break;
          default:
            break;
        }
      }
    }
  }

  private void initParagraphs() {
    paraNum = 17;
    para = new Para(17);
    paragraphList.add(new Paragraph(getResources().getString(R.string.title),0));
    paragraphList.add(new Paragraph(getResources().getString(R.string.time),2));
    // TODO: Initial the paragraphs
    for (int i = 0; i < paraNum; i++) {
      paragraphList.add(new Paragraph(para.getParas()[i].getContent(),1));
    }
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_read, container, false);
    iVBack = view.findViewById(R.id.read_iv_back);
    iVBack.setOnClickListener(v -> backToResource());
    downloadIndicator = view.findViewById(R.id.ld_download);
    ImageView btPlay = view.findViewById(R.id.read_iv_play);
    ImageView btNext = view.findViewById(R.id.read_iv_next);
    sbProgress = view.findViewById(R.id.read_pb_progress);
    btPlay.setOnClickListener(v -> playBackgroundMusic());
    btNext.setOnClickListener(v -> startNextArticle());


    initParagraphs();
    sbProgress.getConfigBuilder()
        .max(100)
        .sectionCount(paraNum)
        .build();
    sbProgress.setOnProgressChangedListener(
        new OnProgressChangedListener() {
          @Override
          public void onProgressChanged(
              BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
            if (fromUser) {
              double timeNow = ((double) progress / 100) * speakerService.getDuration();
              Log.i(TAG, "onProgressChanged: timeNow " + timeNow);
              int i = 0;
              for (; i < paraNum-1; i++) {
                if (para.getParas()[i].getStartTime() >= timeNow) break;
              }
              Log.i(TAG, "onProgressChanged: index" +i);
              scrollToParagraph(i + 2);
            }
          }

          @Override
          public void getProgressOnActionUp(
              BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {}

          @Override
          public void getProgressOnFinally(
              BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {}
        });
    // TODO: reuse the seekbar
//        sbProgress.setProgress(new VolumeListener());
//         bind speaker service


    ParagraphAdapter paragraphAdapter = new ParagraphAdapter(getContext(), paragraphList);
    paragraphs = (ListView) view.findViewById(R.id.lv_article_view);
    paragraphs.setAdapter(paragraphAdapter);
    paragraphs.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        scrollToParagraph(position);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
    paragraphs.setOnItemLongClickListener((parent, view12, position, id) -> {
      scrollToParagraph(position);
      return false;
    });
    paragraphs.setOnItemClickListener((parent, view1, position, id) -> {
        scrollToParagraph(position);
    });

    paragraphs.setOnScrollListener(new OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
          // OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;// 手指接触状态
          case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
            isPlaying = false;
            break;
          // crollState =SCROLL_STATE_IDLE(0) ：表示屏幕已停止。屏幕停止滚动时为0。
          case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
            // TODO: stop for 2 seconds.
            isPlaying = true;
            break;
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {

      }
    });
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
    waitingMusicList.add("music/result.mp3");
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

  private void scrollToParagraph(int position) {
    paragraphs.smoothScrollToPositionFromTop(position, 0, 500);
    if (speakerService != null) {
      if (speakerService.isPlaying())
        speakerService.seeTo(para.getParas()[position - 2].getStartTime());
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
    if (article == null) {
      Toast.makeText(this.getContext(), "当前并没有播放资源", Toast.LENGTH_LONG).show();
      return;
    }
    isPlaying = false; // stop update the progress
    speakerService.startNewSong(waitingMusicList.get(playIndex));
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
//    /**
//     * listener for the progress bar.
//     */
//    private class VolumeListener implements BubbleSeekBar {
//
//      public void onProgressChanged(SeekBar seekBar, int progress,
//          boolean fromUser) {
//        // Log the progress
//        //set textView's text
//        volumeText.setText(String.valueOf(progress));
//      }
//
//      public void onStartTrackingTouch(SeekBar seekBar) {}
//
//      public void onStopTrackingTouch(SeekBar seekBar) {
//        int progress = speakerService.getDuration() * seekBar.getProgress() / 100;
//        speakerService.seeTo(progress);
//      }
//
//    }

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
            Thread.sleep(1000);
          }
        }
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Log.e(TAG, "run: ", e);
      }
    }
  }
}
