package com.example.icecream.ui.fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.icecream.R;
import com.example.icecream.ui.activity.MainActivity;

public class ReadFragment extends Fragment{

  private static final String TAG = PlayFragment.class.getName();

  private ImageView iVBack;

  public static ReadFragment newInstance() {
    return new ReadFragment();
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_read, container, false);
    iVBack = view.findViewById(R.id.read_iv_back);
    iVBack.setOnClickListener(v -> backToResource());
    return view;
  }

  private void backToResource(){
    try {
      ((MainActivity) getActivity()).toResourceFragment();
    } catch (NullPointerException e){
      // do nothing
    }
  }

}
