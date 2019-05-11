package com.example.icecream.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.icecream.R;
import com.example.icecream.SpeakerService;

public class PlayFragment extends Fragment {

  private SpeakerService speakerService;

  public static PlayFragment newInstance() {
    return new PlayFragment();
  }

  @Override
  public View onCreateView(
    LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_play, container, false);
    Button btPlay = view.findViewById(R.id.bt_play);
    btPlay.setOnClickListener(v -> playBackgroundMusic());
    // bind speaker service
    getActivity().bindService(new Intent(getActivity(), SpeakerService.class),
        speakConnection, Context.BIND_AUTO_CREATE);

    return view;
  }

  private void playBackgroundMusic() {
    speakerService.testForPlay();
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
