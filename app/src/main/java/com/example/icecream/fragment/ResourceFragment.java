package com.example.icecream.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.icecream.MainActivity;
import com.example.icecream.R;
import com.example.icecream.recycleveiw.RecycleAdapter;
import com.example.icecream.search.SimpleToolbar;

import java.util.Objects;


/**
 * This is the resource(main) fragment to display the articles.
 * @author Aaron
 */
public class ResourceFragment extends Fragment implements RecycleAdapter.ListItemClickListener {

  private static final int NUM_LIST_ITEMS = 100;

  private RecycleAdapter mAdapter;
  private RecyclerView mArticleList;
  private Context mainAppContext;

  private Toast mToast;

  /**
   * Create a instance of ResourceFragment.
   * @return com.example.icecream.fragment.ResourceFragment
   */
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
    void sendNewMusic();

    void startPlayer();

    void stopPlayer();

    void pausePlayer();
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_resource, container, false);

    /*
     * Set up tool bar for search.
     */
    SimpleToolbar toolbar = view.findViewById(R.id.toolbar);
    ((MainActivity)getActivity()).setUpToolbar(toolbar);

    /*
     * Generate article list using reclycleView.
     */
    mArticleList = view.findViewById(R.id.rv_source);

    try {
      mainAppContext = Objects.requireNonNull(getActivity()).getApplicationContext();
      LinearLayoutManager layoutManager = new LinearLayoutManager(mainAppContext);
      mArticleList.setLayoutManager(layoutManager);
    } catch (java.lang.NullPointerException npe){
      npe.printStackTrace();
    }

    mArticleList.setHasFixedSize(true);

    mAdapter = new RecycleAdapter(NUM_LIST_ITEMS, this);
    mArticleList.setAdapter(mAdapter);

    return view;
  }

  // COMPLETED (10) Override ListItemClickListener's onListItemClick method
  /**
   * This is where we receive our callback from
   * {@link RecycleAdapter.ListItemClickListener}
   *
   * This callback is invoked when you click on an item in the list.
   *
   * @param clickedItemIndex Index in the list of the item that was clicked.
   */
  @Override
  public void onListItemClick(int clickedItemIndex) {
    // COMPLETED (11) In the beginning of the method, cancel the Toast if it isn't null
    /*
     * Even if a Toast isn't showing, it's okay to cancel it. Doing so
     * ensures that our new Toast will show immediately, rather than
     * being delayed while other pending Toasts are shown.
     *
     * Comment out these three lines, run the app, and click on a bunch of
     * different items if you're not sure what I'm talking about.
     */
    if (mToast != null) {
      mToast.cancel();
    }

    // COMPLETED (12) Show a Toast when an item is clicked, displaying that item number that was clicked
    /*
     * Create a Toast and store it in our Toast field.
     * The Toast that shows up will have a message similar to the following:
     *
     *                     Item #42 clicked.
     */
    String toastMessage = "Item #" + clickedItemIndex + " clicked.";
    mToast = Toast.makeText(mainAppContext, toastMessage, Toast.LENGTH_LONG);

    mToast.show();
  }

}
