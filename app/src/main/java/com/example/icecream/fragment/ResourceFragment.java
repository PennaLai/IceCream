package com.example.icecream.fragment;

// import android.app.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.icecream.R;

// import static android.content.ContentValues.TAG;

public class ResourceFragment extends Fragment {

  // TODO: connect to the mainactivity so that we can communicate from resourcefragment to playfragment

  public static ResourceFragment newInstance() {
    return new ResourceFragment();
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_resource, container, false);
    Button btgoToPersonalPage = view.findViewById(R.id.bt_goToPersonalDetail);
    btgoToPersonalPage.setOnClickListener(v -> System.out.println("Fuck You from resource"));
    return view;
  }
}
