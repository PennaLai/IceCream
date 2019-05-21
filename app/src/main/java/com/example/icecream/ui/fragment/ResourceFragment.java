package com.example.icecream.ui.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.icecream.R;
import com.example.icecream.ui.activity.MainActivity;
import com.example.icecream.ui.activity.SearchActivity;
import com.example.icecream.ui.component.recycleveiw.ArticlesAdapter;
import com.example.icecream.utils.AppViewModel;
import com.example.icecream.utils.HttpHandler;

import java.lang.ref.WeakReference;
import java.util.Objects;

import okhttp3.OkHttpClient;

import static android.content.ContentValues.TAG;


/**
 * This is the resource(main) fragment to display the articles.
 *
 * @author Aaron
 */
public class ResourceFragment extends Fragment implements ArticlesAdapter.ListItemClickListener {

  private static final int NUM_LIST_ITEMS = 100;

  private ArticlesAdapter mAdapter;
  private RecyclerView mArticleList;
  private Context mainAppContext;
  private Toast mToast;


  /**
   * Create a instance of ResourceFragment.
   *
   * @return com.example.icecream.ui.fragment.ResourceFragment
   */
  public static ResourceFragment newInstance() {
    return new ResourceFragment();
  }

  /**
   * use to connect to play fragment through main activity
   */
  private MusicConnector musicConnectorCallback;

  private AppViewModel viewModel;

  private HttpHandler httpHandler;


  /**
   * this interface is use to connect the play fragment, the main activity should
   * implement it.
   *
   * @author Penna
   */
  public interface MusicConnector {

    /**
     * if the article is select, Resource fragment -> Main activity -> Player fragment.
     */
    void onArticleSelect();
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_resource, container, false);

    /*
     * Set up tool bar for search.
     */
    Toolbar toolbar = view.findViewById(R.id.toolbar);
    if (getActivity() != null) {
      ((MainActivity) getActivity()).setUpToolbar(toolbar);
    }
    ImageView imageView = view.findViewById(R.id.action_search);
    imageView.setOnClickListener(v -> goToSearch());


    /*
     * Generate article list using recycleView.
     */
    mArticleList = view.findViewById(R.id.rv_source);

    try {
      mainAppContext = Objects.requireNonNull(getActivity()).getApplicationContext();
      LinearLayoutManager layoutManager = new LinearLayoutManager(mainAppContext);
      mArticleList.setLayoutManager(layoutManager);
    } catch (java.lang.NullPointerException npe) {
      npe.printStackTrace();
    }

    //TODO: get the articles size and set it into NUM_LIST_ITEMS

    mAdapter = new ArticlesAdapter(NUM_LIST_ITEMS, this);
    mArticleList.setAdapter(mAdapter);

    // http
    httpHandler = new HttpHandler(new OkHttpClient(), getActivity().getApplication());

    // view model
    viewModel = ViewModelProviders.of(this).get(AppViewModel.class);

    // observe articles from subscribed feeds
    viewModel.getPersonalArticles().observe(this, articles -> mAdapter.setArticles(articles));
    return view;
  }

  public void goToSearch() {
    Class<?> activityCls = SearchActivity.class;

    Intent intent = new Intent(mainAppContext, activityCls);
    startActivity(intent);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    musicConnectorCallback = (MainActivity) context;
  }


  /**
   * This is where we receive our callback from
   * {@link ArticlesAdapter.ListItemClickListener}
   * <p>
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
    // tell the main activity to change the screen to the player fragment
    musicConnectorCallback.onArticleSelect();

  }

  private void onBackPressed() {
    if (getActivity() != null) {
      getActivity().onBackPressed();
    }
  }


  private void getRssFeeds(String phoneNumber) {
    new UpdateRssFeedsAsyncTask(this).execute(phoneNumber);
  }


  private static class UpdateRssFeedsAsyncTask extends AsyncTask<String, Void, HttpHandler.ResponseState> {

    private WeakReference<ResourceFragment> reference;

    // only retain a weak reference to the activity
    UpdateRssFeedsAsyncTask(ResourceFragment context) {
      reference = new WeakReference<>(context);
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(String... params) {
      ResourceFragment fragment = reference.get();
      if (fragment == null) {
        return null;
      }
      return fragment.httpHandler.getUpdateRSSFeedsState(params[0]);
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      ResourceFragment fragment = reference.get();
      if (fragment == null) {
        return;
      }
      switch (responseState) {
        case Valid:
          // get those feeds successfully
          Log.i(TAG, "get rss feeds");
          fragment.viewModel.setPersonalRssFeeds(fragment.httpHandler.getPersonalRssFeeds());
          break;
        case InvalidToken:
          // TODO back to login
//          activity.login();
          break;
        case NoSuchUser:
          // TODO back to login
//          activity.login();
          break;
        default:
          break;
      }
    }
  }

  private static class UpdateArticlesAsyncTask extends AsyncTask<String, Void, HttpHandler.ResponseState> {

    private WeakReference<ResourceFragment> reference;

    // only retain a weak reference to the activity
    UpdateArticlesAsyncTask(ResourceFragment context) {
      reference = new WeakReference<>(context);
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(String... params) {
      ResourceFragment fragment = reference.get();
      if (fragment == null) {
        return null;
      }
      return fragment.httpHandler.getUpdateArticlesState(params[0]);
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      ResourceFragment fragment = reference.get();
      if (fragment == null) {
        return;
      }
      switch (responseState) {
        case Valid:
          // get those articles successfully
          Log.i("dd", "get articles");
          fragment.viewModel.setPersonalArticles(fragment.httpHandler.getPersonalArticles());
          break;
        case InvalidToken:
          // TODO back to login
//          activity.login();
          break;
        case NoSuchUser:
          // TODO back to login
//          activity.login();
          break;
        default:
          break;
      }
    }
  }


  private static class SubscribeAsyncTask extends AsyncTask<String, Void, HttpHandler.ResponseState> {

    private WeakReference<ResourceFragment> reference;
    private String phone;
    private String rssFeedUrl;

    // only retain a weak reference to the activity
    SubscribeAsyncTask(ResourceFragment context) {
      reference = new WeakReference<>(context);
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(String... params) {
      ResourceFragment fragment = reference.get();
      if (fragment == null
          || fragment.getActivity() == null
          || fragment.getActivity().isFinishing()) {
        return null;
      }
      phone = params[0];
      rssFeedUrl = params[1];
      return fragment.httpHandler.getSubscribeFeedState(phone, rssFeedUrl);
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      ResourceFragment fragment = reference.get();
      if (fragment == null) {
        return;
      }
      switch (responseState) {
        case Valid:
          Log.i(TAG, "subscribe succeed");
          fragment.viewModel.subscribe(phone, rssFeedUrl);
          break;
        case InvalidToken:
          // TODO back to login
//          activity.login();
          break;
        case NoSuchUser:
          // TODO back to login
//          activity.login();
          break;
        default:
          break;
      }
    }
  }

  private static class UnsubscribeAsyncTask extends AsyncTask<String, Void, HttpHandler.ResponseState> {

    private WeakReference<ResourceFragment> reference;
    private String phone;
    private String rssFeedUrl;

    // only retain a weak reference to the activity
    UnsubscribeAsyncTask(ResourceFragment context) {
      reference = new WeakReference<>(context);
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(String... params) {
      ResourceFragment fragment = reference.get();
      if (fragment == null) {
        return null;
      }
      phone = params[0];
      rssFeedUrl = params[1];
      return fragment.httpHandler.getUnsubscribeFeedState(phone, rssFeedUrl);
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      ResourceFragment fragment = reference.get();
      if (fragment == null) {
        return;
      }
      switch (responseState) {
        case Valid:
          Log.i(TAG, "unsubscribe succeed");
          fragment.viewModel.unsubscribe(phone, rssFeedUrl);
          break;
        case InvalidToken:
          // TODO back to login
//          activity.login();
          break;
        case NoSuchUser:
          // TODO back to login
//          activity.login();
          break;
        default:
          break;
      }
    }
  }
}
