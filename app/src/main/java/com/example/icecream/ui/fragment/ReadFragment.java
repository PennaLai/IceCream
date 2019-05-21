package com.example.icecream.ui.fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.icecream.R;

public class ReadFragment extends Fragment{

  private static final String TAG = PlayFragment.class.getName();

  public static ReadFragment newInstance() {
    return new ReadFragment();
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_play, container, false);

    return view;
  }

}
