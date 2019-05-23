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
import com.example.icecream.database.entity.Article;
import com.example.icecream.database.entity.RssFeed;
import com.example.icecream.ui.activity.MainActivity;
import com.example.icecream.ui.activity.SearchActivity;
import com.example.icecream.ui.component.recycleveiw.ArticlesAdapter;
import com.example.icecream.utils.AppViewModel;
import com.example.icecream.utils.HttpHandler;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import java.lang.ref.WeakReference;
import java.util.List;
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
    void onArticleSelect(Article article);
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


    // 下拉刷新和底部加载初始化和监听函数
    RefreshLayout refreshLayout = (RefreshLayout)view.findViewById(R.id.refreshLayout);
//    refreshLayout.setEnableLoadMore(false); // 禁用上拉加载
    refreshLayout.setOnRefreshListener(
        refresh -> {
          refreshResource();
          refresh.finishRefresh(2000 /*,false*/); // 传入false表示刷新失败
        });
    refreshLayout.setOnLoadMoreListener(refresh -> {
      refresh.finishLoadMore(2000/*,false*/);//传入false表示加载失败
    });
    refreshResource();
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
   * To refresh resource.
   */
  private void refreshResource() {
    getAllRssFeeds();
    subscribe("18929357397", "https://36kr.com/feed");
    getPersonalRssFeeds("18929357397");
    //    unsubscribe("18929357397", "https://36kr.com/feed");
    getPersonalArticles("18929357397");

  }





  /**
   * This is where we receive our callback from
   * {@link ArticlesAdapter.ListItemClickListener}
   * <p>
   * This callback is invoked when you click on an item in the list.
   *
   *
   */
  @Override
  public void onListItemClick(Article article) {
    musicConnectorCallback.onArticleSelect(article);
  }

  private void onBackPressed() {
    if (getActivity() != null) {
      getActivity().onBackPressed();
    }
  }

  private void getAllRssFeeds() {
    new UpdateAllFeedsAsyncTask(this).execute();
  }

  private void getPersonalRssFeeds(String phoneNumber) {
    new UpdatePersonalFeedsAsyncTask(this).execute(phoneNumber);
  }

  private void getPersonalArticles(String phoneNumber) {
    new UpdateArticlesAsyncTask(this).execute(phoneNumber);
  }

  private void subscribe(String phoneNumber, String url) {
    new SubscribeAsyncTask(this).execute(phoneNumber, url);
  }

  private void unsubscribe(String phoneNumber, String url) {
    new UnsubscribeAsyncTask(this).execute(phoneNumber, url);
  }

  private static class UpdateAllFeedsAsyncTask extends AsyncTask<Void, Void, HttpHandler.ResponseState> {

    private WeakReference<ResourceFragment> reference;

    // only retain a weak reference to the activity
    UpdateAllFeedsAsyncTask(ResourceFragment context) {
      reference = new WeakReference<>(context);
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(Void... params) {
      ResourceFragment fragment = reference.get();
      if (fragment == null) {
        return null;
      }
      return fragment.httpHandler.getUpdateAllFeedsState();
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
          Log.i(TAG, "inserting all rss feeds");
          List<RssFeed> list = fragment.httpHandler.getAllRssFeeds();
          fragment.viewModel.insertAllRssFeeds(list.toArray(new RssFeed[0]));
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

  private static class UpdatePersonalFeedsAsyncTask extends AsyncTask<String, Void, HttpHandler.ResponseState> {

    private WeakReference<ResourceFragment> reference;

    // only retain a weak reference to the activity
    UpdatePersonalFeedsAsyncTask(ResourceFragment context) {
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
          Log.i(TAG, "get articles");
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
          fragment.viewModel.subscribe(phone, rssFeedUrl);
          Log.i(TAG, "subscribe succeed");
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
          fragment.viewModel.unsubscribe(phone, rssFeedUrl);
          Log.i(TAG, "unsubscribe succeed");
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
