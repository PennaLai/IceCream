package com.example.icecream.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.example.icecream.ui.fragment.ReadFragment.OnPlayerUiListener;
import java.io.IOException;

/**
 * Use service to play our resource in the background, for save the resource prepare time in the
 * media player, we use on prepared listener to set the other thread to prepare it but not the ui
 * thread. Reference website: https://juejin.im/post/5bdab2495188257f863d19fb
 * https://blog.csdn.net/qq_37077360/article/details/80570684 https://www.jianshu.com/p/f65e35fc089d
 * https://blog.csdn.net/u014365133/article/details/53330776
 *
 * @author Penna.
 */
public class SpeakerService extends Service implements OnPreparedListener, OnCompletionListener {

  private static final String TAG = SpeakerService.class.getName();

  /** speak player to play resource. */
  private MediaPlayer speakerPlayer;

  /** binder between fragment and service. */
  private SpeakerBinder speakerBinder = new SpeakerBinder();

  /** used to change the ui layer of player fragment. */
  private OnPlayerUiListener onPlayerUiListener;

  /** create this service. */
  @Override
  public void onCreate() {
    initMediaPlayer();
    super.onCreate();
  }

  /**
   * bind this service from activity.
   *
   * @param intent an intent
   * @return my binder for control music player
   */
  @Override
  public IBinder onBind(Intent intent) {
    destroyMediaPlayer();
    initMediaPlayer();
    return speakerBinder;
  }

  /**
   * Unbind the connector and allocate the resource.
   *
   * @param intent the intent.
   * @return true
   */
  @Override
  public boolean onUnbind(Intent intent) {
    destroyMediaPlayer();
    return super.onUnbind(intent);
  }

  /**
   * after prepared, call back and start to play music.
   *
   * @param mp media player
   */
  @Override
  public void onPrepared(MediaPlayer mp) {
    mp.start();
    onPlayerUiListener.updateNewSongUi(); // update the ui in play fragment
  }

  /**
   * after one song finish, call back this function to do something, like looping.
   *
   * @param mp media player
   */
  @Override
  public void onCompletion(MediaPlayer mp) {
    // 暂时设定为循环播放
    mp.start();
    mp.setLooping(true);
  }

  /**
   * This class is a binder class that help activity to control the music player service.
   *
   * @author Penna
   */
  public class SpeakerBinder extends Binder {
    public SpeakerService getService() {
      return SpeakerService.this;
    }
  }

  /** when destroy this service, we should release all resource. */
  @Override
  public void onDestroy() {
    destroyMediaPlayer();
    super.onDestroy();
  }

  /**
   * register the ui listener.
   *
   * @param onPlayerUiListener used to update the ui.
   */
  public void setOnPlayerUiListener(OnPlayerUiListener onPlayerUiListener) {
    this.onPlayerUiListener = onPlayerUiListener;
  }

  /** initialize the media player. */
  private void initMediaPlayer() {
    speakerPlayer = new MediaPlayer();
    speakerPlayer.setOnPreparedListener(this);
    speakerPlayer.setOnCompletionListener(this);
  }

  /**
   * to know the media player is playing or not.
   *
   * @return is playing.
   */
  public boolean isPlaying() {
    if (speakerPlayer == null) {
      return false;
    }
    return speakerPlayer.isPlaying();
  }

  /** play the music. */
  public void startMusic() {
    if (!isPlaying()) {
      speakerPlayer.start();
    }
  }

  /** pause the music. */
  public void pauseMusic() {
    if (isPlaying()) {
      speakerPlayer.pause();
    }
  }

  /**
   * to stop the previous song and start the new player resource.
   *
   * @param url music url.
   */
  public void startNewSong(String url) {
    if (isPlaying()) {
      speakerPlayer.stop();
    }
    AssetFileDescriptor fd;
    try {
      Log.i(TAG, "startNewSong: " + url);
      speakerPlayer.reset();
      fd = getAssets().openFd(url);
      speakerPlayer.setDataSource(fd);
      speakerPlayer.prepareAsync();
    } catch (IOException e) {
      Log.e(TAG, "startNewSong: ", e);
    }
  }

  /**
   * Get the file duration time.
   *
   * @return current music progress.
   */
  public int getDuration() {
    return speakerPlayer.getDuration();
  }

  /**
   * get the percentage progress.
   *
   * @return percentage progress of current song
   */
  public Double getProgress() {
    int position = speakerPlayer.getCurrentPosition();
    int time = getDuration();
    return (double) position / (double) time;
  }

  /**
   * Set the current music file progress.
   *
   * @param mesc time with millisecond
   */
  public void seeTo(int mesc) {
    speakerPlayer.seekTo(mesc);
  }

  /** to release the media player resource. */
  public void destroyMediaPlayer() {
    if (speakerPlayer != null) {
      speakerPlayer.stop();
      speakerPlayer.release();
      speakerPlayer = null;
    }
  }
}
