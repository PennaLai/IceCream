package com.example.icecream.ui.fragment;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
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
import com.example.icecream.ui.activity.MainActivity;
import com.example.icecream.ui.component.paragraph.Paragraph;
import com.example.icecream.ui.component.paragraph.ParagraphAdapter;
import com.example.icecream.utils.AppViewModel;
import com.example.icecream.utils.HttpHandler;
import com.example.icecream.utils.Para;
import com.example.icecream.utils.ResourceHandler;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.wang.avi.AVLoadingIndicatorView;
import com.xw.repo.BubbleSeekBar;
import com.xw.repo.BubbleSeekBar.OnProgressChangedListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import jp.co.recruit_lifestyle.android.widget.PlayPauseButton;

/**
 * The read fragment. The fragment displays and controls everything in reading service.
 *
 * @author Aaron Penna
 * @version V1.0
 */
public class ReadFragment extends Fragment {


  /** background player service. */
  private SpeakerService speakerService;

  /** all the music that waiting for play. */
  private List<String> waitingMusicList = new ArrayList<>();

  /** all the paragraphs of the given. */
  private List<Paragraph> paragraphList = new ArrayList<>();

  private ListView paragraphs;

  private AVLoadingIndicatorView downloadIndicator;

  /** to control the progress update thread exit. */
  boolean needUpdate;

  /** is there is a song playing now. */
  boolean isPlaying;

  /** to count the song number we has played now. */
  private int playSongCount = 0;

  /** the check whether the download is succeed. */
  private boolean downloadSucceed;

  /** whether is this article favorite. */
  private boolean favorite = true;

  /** view model. */
  private AppViewModel viewModel;

  private HttpHandler httpHandler;

  /** the article we are reading now. */
  private Article article;

  /** para num. */
  private int paraNum = 0;

  /** all para information. */
  private Para para;

  /** to update the ui progress thread. */
  Thread progressUpdateThread;

  /** to seek the article position. */
  private BubbleSeekBar sbProgress;

  /** to manage the notification. */
  private NotificationManager musicBarManage;

  /** remote view for notification. */
  private RemoteViews remoteViews;

  private PlayPauseButton playPauseButton;

  /** Receive notification event. */
  BroadcastReceiver broadcastReceiver;

  /** for action for the player button. */
  private static final String ACTION_PRE = "ACTION_PRE";

  private static final String ACTION_NEXT = "ACTION_NEXT";
  private static final String ACTION_PLAY = "ACTION_PLAY";
  private static final String ACTION_PAUSE = "ACTION_PAUSE";

  /** record play music index right now. */
  private int playIndex = 0;

  public static ReadFragment newInstance() {
    return new ReadFragment();
  }

  /** used for callback to update the UI. */
  public interface OnPlayerUiListener {
    void updateNewSongUi();
  }

  /** use to update the ui while playing music. */
  private final ReadFragment.MusicHandler uiUpdateHandle = new ReadFragment.MusicHandler(this);

  /**
   * Handler to update the ui while playing music.
   *
   * @author Penna.
   */
  private static class MusicHandler extends Handler {
    private final WeakReference<ReadFragment> readFragmentWeakReference;

    private MusicHandler(ReadFragment fragment) {
      readFragmentWeakReference = new WeakReference<>(fragment);
    }

    /**
     * update the seek bar and list view scroll.
     *
     * @param msg message.
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
            for (; i < readFragment.paraNum - 1; i++) {
              if (readFragment.para.getParas()[i].getStartTime() > timeNow) {
                break;
              }
            }
            readFragment.paragraphs.smoothScrollToPositionFromTop(i, 0, 500);
            break;
          default:
            break;
        }
      }
    }
  }

  /** to show the article that can play. */
  private void initParagraphs() {
    paraNum = 17;
    para = new Para(17);
    paragraphList.add(new Paragraph(getResources().getString(R.string.title), 0));
    paragraphList.add(new Paragraph(getResources().getString(R.string.time), 2));
    for (int i = 0; i < paraNum; i++) {
      paragraphList.add(new Paragraph(para.getParas()[i].getContent(), 1));
    }
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_read, container, false);

    ShineButton shineButton;
    /* initial the favorite button */
    shineButton = view.findViewById(R.id.read_sb_favorite);
    shineButton.init(getActivity());
    // TODO: initial the shineButton to checked state if already favorited
    shineButton.setChecked(favorite);
    shineButton.setOnCheckStateChangeListener(
        (view14, checked) -> favorite = checked);

    downloadIndicator = view.findViewById(R.id.ld_download);
    downloadIndicator.hide();
    ImageView btNext = view.findViewById(R.id.read_iv_next);
    btNext.setOnClickListener(v -> startNextArticle());
    playPauseButton = view.findViewById(R.id.read_play_pause_button);
    playPauseButton.setOnClickListener(v -> {
      playBackgroundMusic();
    });

    viewModel = ViewModelProviders.of(this).get(AppViewModel.class);
    // observe the download state.
    viewModel.getDownloadComplete().observe(this, isSucceed -> downloadSucceed = isSucceed);

    initParagraphs();

    sbProgress = view.findViewById(R.id.read_pb_progress);
    sbProgress.getConfigBuilder().max(100).sectionCount(paraNum).build();
    sbProgress.setOnProgressChangedListener(
        new OnProgressChangedListener() {
          @Override
          public void onProgressChanged(
              BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
            if (fromUser) {
              double timeNow = ((double) progress / 100) * speakerService.getDuration();
              int i = 0;
              for (; i < paraNum - 1; i++) {
                if (para.getParas()[i].getStartTime() >= timeNow) {
                  break;
                }
              }
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

    ParagraphAdapter paragraphAdapter = new ParagraphAdapter(getContext(), paragraphList);
    paragraphs = view.findViewById(R.id.lv_article_view);
    paragraphs.setAdapter(paragraphAdapter);
    paragraphs.setOnItemSelectedListener(
        new OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            scrollToParagraph(position);
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {}
        });

    paragraphs.setOnItemLongClickListener(
        (parent, view12, position, id) -> {
          scrollToParagraph(position);
          return false;
        });

    paragraphs.setOnItemClickListener(
        (parent, view1, position, id) -> scrollToParagraph(position));

    paragraphs.setOnScrollListener(
        new OnScrollListener() {
          @Override
          public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
              case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                isPlaying = false;
                break;
              case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                // TODO: stop for 2 seconds.
                isPlaying = true;
                break;
              default:
                break;
            }
          }

          @Override
          public void onScroll(
              AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
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
    return view;
  }

  /**
   * use position to scroll to the paragraph.
   * @param position
   */
  private void scrollToParagraph(int position) {
    paragraphs.smoothScrollToPositionFromTop(position, 0, 500);
    if (speakerService != null) {
      if (speakerService.isPlaying())
        speakerService.seeTo(para.getParas()[position - 2].getStartTime());
    }
  }

  /**
   * from the resource fragment -> set new article and start it.
   *
   * @param article te article that resource fragment send.
   */
  public void startDownloadArticle(Article article) {

//    ResourceHandler resourceHandler = ResourceHandler.getInstance(httpHandler, viewModel);
//    resourceHandler.downloadSpeech(article.getId());
//    downloadIndicator.smoothToShow();
    // start download.
    //    UpdateSpeechAsyncTask asyncTask = new UpdateSpeechAsyncTask();
//    asyncTask.doInBackground(article.getId());
    this.article = article;
    startNextArticle();
  }

  public void setArticle() {
    downloadIndicator.smoothToHide();
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

    PendingIntent intentGo =
        PendingIntent.getActivity(getActivity(), 5, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.notice, intentGo);

    Intent prv = new Intent();
    prv.setAction(ACTION_PRE);
    PendingIntent intentPrev =
        PendingIntent.getBroadcast(getActivity(), 1, prv, PendingIntent.FLAG_UPDATE_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.widget_prev, intentPrev);
    Log.i("Notify", "Success4");

    Intent next = new Intent();
    next.setAction(ACTION_NEXT);
    PendingIntent intentNext =
        PendingIntent.getBroadcast(getActivity(), 3, next, PendingIntent.FLAG_UPDATE_CURRENT);
    remoteViews.setOnClickPendingIntent(R.id.widget_next, intentNext);
    Log.i("Notify", "Success5");

    if (speakerService.isPlaying()) {
      Intent playorpause = new Intent();
      playorpause.setAction(ACTION_PLAY);
      PendingIntent intentPlay =
          PendingIntent.getBroadcast(
              getActivity(), 2, playorpause, PendingIntent.FLAG_UPDATE_CURRENT);
      remoteViews.setOnClickPendingIntent(R.id.widget_play, intentPlay);
    } else {
      Intent playorpause = new Intent();
      playorpause.setAction(ACTION_PAUSE);
      PendingIntent intentPlay =
          PendingIntent.getBroadcast(
              getActivity(), 6, playorpause, PendingIntent.FLAG_UPDATE_CURRENT);
      remoteViews.setOnClickPendingIntent(R.id.widget_play, intentPlay);
    }
    Log.i("Notify", "Success6");

    builder.setSmallIcon(R.drawable.logo); // 设置顶部图标
    Log.i("Notify", "Success7");
    Notification notify = builder.build();
    notify.contentView = remoteViews; // 设置下拉图标
    notify.bigContentView = remoteViews; // 防止显示不完全,需要添加apisupport
    notify.flags = Notification.FLAG_ONGOING_EVENT;
    notify.icon = R.drawable.logo;
    Log.i("Notify", "Success8");
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
    waitingMusicList.add("result.mp3");
    getActivity().registerReceiver(broadcastReceiver, filter);
    Log.i("Notify", "Success9");
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
      playPauseButton.setPlayed(true);
    } else {
      if (!speakerService.isPlaying()) {
        speakerService.startMusic();
        playPauseButton.setPlayed(true);
      } else {
        speakerService.pauseMusic();
        playPauseButton.setPlayed(false);
      }
    }
  }

  /** start New Song. */
  private void startNewArticle() {
//    if (article == null) {
//      Toast.makeText(this.getContext(), "当前并没有播放资源", Toast.LENGTH_LONG).show();
//      return;
//    }
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
    if (playIndex >= waitingMusicList.size()) {
      playIndex = 0;
    }
  }

  /** decrease the play waiting list to last one. */
  private void playIndexDecrease() {
    playIndex--;
    if (playIndex < 0) {
      playIndex = waitingMusicList.size() - 1;
    }
  }

  /** Use to connected and disconnected the service. */
  private ServiceConnection speakConnection =
      new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
          speakerService = ((SpeakerService.SpeakerBinder) service).getService();
          speakerService.setOnPlayerUiListener(() -> updateNewSongUi());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
          speakerService.destroyMediaPlayer();
        }
      };


  /**
   * Destroy the activity and service.
   */
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
      }
    }
  }


  private class UpdateSpeechAsyncTask extends AsyncTask<Long, Void, String> {
    private Long id;

    @Override
    protected String doInBackground(Long... params) {
      id = params[0];
      httpHandler.getUpdateSpeech(id);
      return httpHandler.getUpdateSpeechInfo(id);
    }

    @Override
    protected void onPostExecute(String result) {
      viewModel.updateArticleParagraph(id, result);
      setArticle();
    }
  }
}
