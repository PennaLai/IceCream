package com.example.icecream.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.icecream.R;
import com.example.icecream.service.SpeakerService;
import java.util.ArrayList;
import java.util.List;
import me.zhengken.lyricview.LyricView;

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

  /** to update the ui progress thread. */
  Thread progressUpdateThread;

  SeekBar sbProgress;

  /** record play music index right now. */
  private int playIndex = 0;

  private TextView volumeText;

  public static PlayFragment newInstance() {
    return new PlayFragment();
  }

  /**
   * used for callback to update the UI.
   */
  public interface OnPlayerUiListener {
    void updateNewSongUi();
  }

  Handler uiUpdateHandle =
      new Handler() {
        @Override
        public void handleMessage(Message msg) {
          switch (msg.what) {
            case 0:
              double progress = msg.getData().getDouble("progress");
              int max = sbProgress.getMax();
              int position = (int) (max * progress);
              sbProgress.setProgress(position);
              break;
            default:
              break;
          }
        }
      };

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

  private void playBackgroundMusic() {
    if (!speakerService.isPlaying()) {
      speakerService.startMusic();
    } else {
      speakerService.pauseMusic();
    }
  }

  private void playNextMusic() {
    isPlaying = false; // stop update the progress
    speakerService.startNewSong(waitingMusicList.get(playIndex));
    playIndexIncrease();
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
    if (playIndex >= waitingMusicList.size()) {
      playIndex = 0;
    }
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
