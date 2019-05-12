package com.example.icecream.fragment;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

//import com.diegodobelo.expandingview.ExpandingItem;
//import com.diegodobelo.expandingview.ExpandingList;
import com.example.icecream.expanding.expandingview.ExpandingItem;
import com.example.icecream.expanding.expandingview.ExpandingList;

import com.example.icecream.R;


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
    ExpandingItem item = expandingList.createNewItem(R.layout.expanding_layout);
    ExpandingItem item1= expandingList.createNewItem(R.layout.expanding_layout);
    /*ExpandingItem extends from View, so you can call
    findViewById to get any View inside the layout*/
    TextView textView = (TextView) item.findViewById(R.id.title);
    textView.setText("It works!!");
    textView = item1.findViewById(R.id.title);
    textView.setText("It works!!");

    //This will create 5 items
    item.createSubItems(5);

//get a sub item View
    View subItemZero = item.getSubItemView(0);
    ((TextView) subItemZero.findViewById(R.id.sub_title)).setText("Cool");

    View subItemOne = item.getSubItemView(1);
    ((TextView) subItemOne.findViewById(R.id.sub_title)).setText("Awesome");
    item.setIndicatorColorRes(R.color.blue);
    item1.setIndicatorIconRes(R.drawable.ic_play);
    return view;
  }
}
