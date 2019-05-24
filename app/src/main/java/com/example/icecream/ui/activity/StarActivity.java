package com.example.icecream.ui.activity;

import static com.mob.MobSDK.getContext;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.icecream.R;
import com.example.icecream.database.entity.Article;
import com.example.icecream.ui.component.recycleveiw.ArticlesAdapter;
import com.example.icecream.ui.component.recycleveiw.ArticlesAdapter.ListItemClickListener;
import com.example.icecream.ui.fragment.ResourceFragment.MusicConnector;
import com.example.icecream.utils.AppViewModel;
import com.example.icecream.utils.HttpHandler;
import com.example.icecream.utils.ResourceHandler;
import com.example.icecream.utils.UserSettingHandler;
import java.util.Objects;

public class StarActivity extends AppCompatActivity implements ListItemClickListener {

  private ArticlesAdapter mAdapter;
  private RecyclerView mArticleList;
  private Context mainAppContext;

  private TextView header;

  private String phone;

  private HttpHandler httpHandler;
  private ResourceHandler resourceHandler;
  private AppViewModel viewModel;

//  private MusicConnector musicConnectorCallback;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_star);

    header = findViewById(R.id.star_header);

//    musicConnectorCallback = (MainActivity) MainActivity.getContext();

    loadUserInfo();
    loadArticles();

  }

  private void loadUserInfo(){
    viewModel = ViewModelProviders.of(this).get(AppViewModel.class);
    httpHandler = HttpHandler.getInstance(this.getApplication());
    resourceHandler = ResourceHandler.getInstance(httpHandler, viewModel);
    UserSettingHandler userSettingHandler = UserSettingHandler.getInstance(this.getApplication());
    phone = userSettingHandler.getLoginPhone();
  }

  private void loadArticles(){
    mArticleList = findViewById(R.id.rv_source);

    try {
      mainAppContext = StarActivity.this;
      LinearLayoutManager layoutManager = new LinearLayoutManager(mainAppContext);
      mArticleList.setLayoutManager(layoutManager);
    } catch (java.lang.NullPointerException npe) {
      npe.printStackTrace();
    }

    mAdapter = new ArticlesAdapter(0, this);
    mArticleList.setAdapter(mAdapter);

    if (phone!=null) {
      viewModel.getStarArticles().observe(this, mAdapter::setArticles);
//      viewModel.getPersonalArticles().observe(this, mAdapter::setArticles);
    } else {
      Toast.makeText(getContext(), "你还没有登录哦，先随便看看吧", Toast.LENGTH_LONG).show();
      viewModel.getCommonArticles().observe(this, mAdapter::setArticles);
    }
  }

  @Override
  public void onListItemClick(Article article) {
//    musicConnectorCallback.onArticleSelect(article);
    onBackPressed();
  }
}
