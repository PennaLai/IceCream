package com.example.icecream;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

/**
 * @author Penna.
 */
public class SpeakerService extends Service {
  private MediaPlayer speakerPlayer;

  public SpeakerService() {}

  /**
   * create this service
   */
  @Override
  public void onCreate() {
    super.onCreate();
    speakerPlayer = new MediaPlayer();
  }

  /**
   * bind this service from activity.
   *
   * @param intent an intent
   * @return my binder for control music player
   */
  @Override
  public IBinder onBind(Intent intent) {
    return new MyBinder();
  }

  /**
   * This class is a binder class that help activity to control the music player service
   * @author Penna
   */
  public class MyBinder extends Binder {

    public boolean isPlaying() {
      return speakerPlayer.isPlaying();
    }

    /**
     * play the music
     */
    public void play() {
      if(!isPlaying()) {
        speakerPlayer.start();
      }
    }

    /**
     * pause the music.
     */
    public void pause() {
      if(isPlaying()) {
        speakerPlayer.pause();
      }
    }

    /**
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

  }
}
