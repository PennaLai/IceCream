package com.example.icecream;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import java.io.IOException;

/**
 * Use service to play our resource in the background, for save the resource prepare time in
 * the media player, we use on prepared listener to set the other thread to prepare it but not
 * the ui thread.
 * Reference website: https://juejin.im/post/5bdab2495188257f863d19fb
 *                    https://blog.csdn.net/qq_37077360/article/details/80570684
 *                    https://www.jianshu.com/p/f65e35fc089d
 *                    https://blog.csdn.net/u014365133/article/details/53330776
 * @author Penna.
 */
public class SpeakerService extends Service implements OnPreparedListener, OnCompletionListener {
  private MediaPlayer speakerPlayer = new MediaPlayer();
  private SpeakerBinder speakerBinder = new SpeakerBinder();

  public SpeakerService() {}

  /**
   * create this service.
   */
  @Override
  public void onCreate() {
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
    return speakerBinder;
  }

  @Override
  public boolean onUnbind(Intent intent) {
    //TODO: 回收资源
    return super.onUnbind(intent);
  }

  /**
   * override method. when start this service and receive command.
   * @param intent
   * @param flags
   * @param startId
   * @return
   */
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    speakerPlayer.prepareAsync();
    return super.onStartCommand(intent, flags, startId);
  }

  /**
   * after prepared, call back and start to play music.
   * @param mp media player
   */
  @Override
  public void onPrepared(MediaPlayer mp) {
    mp.start();
  }

  /**
   * after one song finish, call back this function to do something, like looping.
   * @param mp media player
   */
  @Override
  public void onCompletion(MediaPlayer mp) {

  }

  /**
   * This class is a binder class that help activity to control the music player service.
   * @author Penna
   */
  public class SpeakerBinder extends Binder {
    public SpeakerService getService() {
      return SpeakerService.this;
    }
  }

  /**
   * when destroy this service, we should release all resource.
   */
  @Override
  public void onDestroy() {
    destroyMediaPlayer();
    super.onDestroy();
  }

  private void initMediaPlayer() {
    speakerPlayer = new MediaPlayer();
    speakerPlayer.setOnPreparedListener(this);
    speakerPlayer.setOnCompletionListener(this);
  }


  private boolean isPlaying() {
    return speakerPlayer.isPlaying();
  }

  /**
   * play the music.
   */
  public void startMusic() {
    if(!isPlaying()) {
      speakerPlayer.start();
    }
  }


  /**
   * pause the music.
   */
  public void pauseMusic() {
    if(isPlaying()) {
      speakerPlayer.pause();
    }
  }

  /**
   * to stop the previous song and start the new player resource.
   * @param url music url.
   */
  public void setNewResource(String url) {
    if (isPlaying()) {
      speakerPlayer.stop();
      try {
        speakerPlayer.setDataSource(url);
        speakerPlayer.prepareAsync();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


  public void testForPlay() {
    AssetFileDescriptor fd = null;
    try {
      fd = getAssets().openFd("music/Reality.mp3");
      speakerPlayer.setDataSource(fd);
      speakerPlayer.prepareAsync();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get the file duration time.
   * @return current music progress.
   */
  public int getDuration() {
    return speakerPlayer.getDuration();
  }

  /**
   * Set the current music file progress.
   * @param mesc time with millisecond
   */
  public void seeTo(int mesc) {
    speakerPlayer.seekTo(mesc);
  }

  /**
   * to release the media player resource.
   */
  public void destroyMediaPlayer() {
    if (speakerPlayer != null) {
      speakerPlayer.stop();
      speakerPlayer.release();
      speakerPlayer = null;
    }
  }


}
