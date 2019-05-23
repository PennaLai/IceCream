package com.example.icecream.ui.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.icecream.R;
import com.example.icecream.database.entity.Article;
import com.example.icecream.ui.activity.MainActivity;
import com.example.icecream.ui.activity.SearchActivity;
import com.example.icecream.ui.component.recycleveiw.ArticlesAdapter;
import com.example.icecream.utils.AppViewModel;
import com.example.icecream.utils.HttpHandler;
import com.example.icecream.utils.MyLock;
import com.example.icecream.utils.ResourceHandler;
import com.example.icecream.utils.UserSettingHandler;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;


import java.util.Objects;


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

  private UserSettingHandler userSettingHandler;


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

  ResourceHandler resourceHandler;


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
    HttpHandler httpHandler = HttpHandler.getInstance(getActivity().getApplication());

    // view model
    AppViewModel viewModel = ViewModelProviders.of(this).get(AppViewModel.class);

    // observe articles from subscribed feeds
    viewModel.getPersonalArticles().observe(this, articles -> mAdapter.setArticles(articles));

    resourceHandler = ResourceHandler.getInstance(httpHandler, viewModel);
    // load local feeds and articles

//    com.scwang.smartrefresh.header.BezierCircleHeader header = view.findViewById(R.id.refreshHeader);
//    header.setAccentColor(getResources().getColor(R.color.light_pink)); // 强调颜色
//    header.setPrimaryColor(getResources().getColor(R.color.green)); // 主题颜色
    // 下拉刷新和底部加载初始化和监听函数
    RefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
    refreshLayout.setEnableLoadMore(false); // 禁用上拉加载
    refreshLayout.setOnRefreshListener(
        refresh -> {
          refreshResource();
          refresh.finishRefresh(2000 /*,false*/); // 传入false表示刷新失败
        });
    refreshLayout.setOnLoadMoreListener(refresh -> {
      refresh.finishLoadMore(2000/*,false*/);//传入false表示加载失败
    });

    // 用户设置读取
    userSettingHandler = UserSettingHandler.getInstance(getActivity().getApplication());

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
    String phoneNumber = userSettingHandler.getLoginPhone();
    resourceHandler.updatePersonalRssFeeds(phoneNumber);  // do not delete
    resourceHandler.updatePersonalArticles(phoneNumber);
  }


  /**
   * This is where we receive our callback from
   * {@link ArticlesAdapter.ListItemClickListener}
   * <p>
   * This callback is invoked when you click on an item in the list.
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

}
