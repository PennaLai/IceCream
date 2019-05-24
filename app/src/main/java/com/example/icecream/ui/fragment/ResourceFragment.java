package com.example.icecream.ui.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.icecream.R;
import com.example.icecream.database.entity.Article;
import com.example.icecream.ui.activity.LoginActivity;
import com.example.icecream.ui.activity.MainActivity;
import com.example.icecream.ui.activity.SearchActivity;
import com.example.icecream.ui.component.recycleveiw.ArticlesAdapter;
import com.example.icecream.ui.component.recycleveiw.ArticlesAdapter.ListItemClickListener;
import com.example.icecream.utils.AppViewModel;
import com.example.icecream.utils.HttpHandler;
import com.example.icecream.utils.ResourceHandler;
import com.example.icecream.utils.UserSettingHandler;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.MaterialSearchBar.OnSearchActionListener;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.scwang.smartrefresh.layout.api.RefreshLayout;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * This is the resource(main) fragment to display the articles.
 *
 * @author Aaron
 */
public class ResourceFragment extends Fragment implements ListItemClickListener,
    OnSearchActionListener {

  private static final int NUM_LIST_ITEMS = 0;

  private ArticlesAdapter mAdapter;
  private RecyclerView mArticleList;
  private Context mainAppContext;
  private Toast mToast;


  private List<String> lastSearches = new ArrayList<>();
  private MaterialSearchBar mSearchBar;

  private static String placeHolder = "Articles";

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

  @Override
  public void onSearchStateChanged(boolean enabled) {

  }

  @Override
  public void onSearchConfirmed(CharSequence text) {
    String strToSearch = text.toString();
    // TODO: search(strToSearch)
//    Log.i("search", strToSearch);
    Toast.makeText(getContext(), strToSearch, Toast.LENGTH_LONG).show();
  }

  @Override
  public void onButtonClicked(int buttonCode) {

  }


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



    mSearchBar = (MaterialSearchBar) view.findViewById(R.id.search_searchBar);
    mSearchBar.setHint(getResources().getText(R.string.search_hint));
    mSearchBar.setSpeechMode(false);
    mSearchBar.setPlaceHolder(placeHolder.subSequence(0, placeHolder.length()));
    mSearchBar.setPlaceHolderColor(Color.rgb(0,0,0));
    mSearchBar.setOnSearchActionListener(this);
    // TODO: lastSearches = loadSearchSuggestionFromDisk();
    mSearchBar.setLastSuggestions(lastSearches);
    // TODO: onTextChange
    mSearchBar.addTextChangeListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d("LOG_TAG", getClass().getSimpleName() + " text changed " + mSearchBar.getText());
      }

      @Override
      public void afterTextChanged(Editable editable) {
      }
    });

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

    resourceHandler = ResourceHandler.getInstance(httpHandler, viewModel, getActivity().getApplication());
    // 用户设置读取
    userSettingHandler = UserSettingHandler.getInstance(getActivity().getApplication());

    String phone = userSettingHandler.getLoginPhone();

    // observe articles from subscribed feeds
    if (phone != null) { // TODO: 加一个判断有没有订阅
      viewModel.getPersonalArticles().observe(this, articles -> mAdapter.setArticles(articles));
    }else {
      Toast.makeText(getContext(), "你还没有订阅哦，先随便看看吧", Toast.LENGTH_LONG).show();
      viewModel.getCommonArticles().observe(this, articles -> mAdapter.setArticles(articles));
    }


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
    resourceHandler.updateAllRssFeeds();
    String phoneNumber = userSettingHandler.getLoginPhone();
    if (phoneNumber != null)
    resourceHandler.updatePersonalResources(phoneNumber);
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
