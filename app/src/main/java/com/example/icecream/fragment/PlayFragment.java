package com.example.icecream.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.icecream.R;

public class PlayFragment extends Fragment {

    public static PlayFragment newInstance() {
        return new PlayFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState ) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        Button btPlay = view.findViewById(R.id.bt_play);
        btPlay.setOnClickListener(v -> System.out.println("Fuck You from play"));
        return view;
    }
}
