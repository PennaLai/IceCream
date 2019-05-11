package com.example.icecream.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.icecream.R;
import com.example.icecream.SpeakerService;

public class PlayFragment extends Fragment {

  /** background player service*/
  private SpeakerService speakerService;

  private TextView volume_text;

  public static PlayFragment newInstance() {
    return new PlayFragment();
  }

  @Override
  public View onCreateView(
    LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_play, container, false);
    volume_text = view.findViewById(R.id.tv_volume);
    Button btPlay = view.findViewById(R.id.bt_play);
    Button btNext = view.findViewById(R.id.bt_next);
    SeekBar sbVolume = view.findViewById(R.id.seekBar_volume);
    btPlay.setOnClickListener(v -> playBackgroundMusic());
    btNext.setOnClickListener(v -> playNextMusic());
    sbVolume.setOnSeekBarChangeListener(new volumeListener());
    // bind speaker service
    getActivity().bindService(new Intent(getActivity(), SpeakerService.class),
        speakConnection, Context.BIND_AUTO_CREATE);

    return view;
  }

  private void playBackgroundMusic() {
    speakerService.testForPlay();
  }

  private void playNextMusic() {

  }

  private class volumeListener implements SeekBar.OnSeekBarChangeListener {

    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
      // Log the progress
      Log.i("DEBUG", "Progress is: "+progress);
      //set textView's text
      volume_text.setText(""+progress);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {}

    public void onStopTrackingTouch(SeekBar seekBar) {}

  }

  private ServiceConnection speakConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      speakerService = ((SpeakerService.SpeakerBinder) service).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
  };


}
