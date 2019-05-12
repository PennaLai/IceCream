package com.example.icecream.fragment;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;

import com.example.icecream.R;
import com.example.icecream.expanding.Article;


public class ResourceFragment extends Fragment {

  public static ResourceFragment newInstance() {
    return new ResourceFragment();
  }

  /** use to connect to play fragment through main activity*/
  MusicConnector musicConnector;

  /**
   * this interface is use to connect the play fragment, the main activity should
   * implement it.
   * @author Penna
   */
  public interface MusicConnector {
    public void sendNewMusic();

    public void startPlayer();

    public void stopPlayer();

    public void pausePlayer();
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_resource, container, false);
//    Button btgoToPersonalPage = view.findViewById(R.id.bt_goToPersonalDetail);
//    btgoToPersonalPage.setOnClickListener(v -> System.out.println("Fuck You from resource"));
    ExpandingList expandingList = (ExpandingList) view.findViewById(R.id.expanding_list_main);
//
    new Article(expandingList, "知乎", "知乎药丸", 1);
    new Article(expandingList, "头条", "头条真好", 2);

    return view;
  }
}
