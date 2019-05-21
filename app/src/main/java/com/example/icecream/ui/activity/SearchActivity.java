package com.example.icecream.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
//import com.example.newbiechen.ireader.model.bean.packages.SearchBookPackage;
//import com.example.newbiechen.ireader.presenter.SearchPresenter;
//import com.example.newbiechen.ireader.presenter.contract.SearchContract;
//import com.example.newbiechen.ireader.ui.adapter.KeyWordAdapter;
//import com.example.newbiechen.ireader.ui.adapter.SearchBookAdapter;
//import com.example.newbiechen.ireader.ui.base.BaseMVPActivity;
//import com.example.newbiechen.ireader.widget.RefreshLayout;
//import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration;
import com.example.icecream.R;
//import me.gujun.android.taggroup.TagGroup;

/**
 * Created by newbiechen on 17-4-24.
 */

public class SearchActivity extends AppCompatActivity {


  //    @BindView(R.id.search_iv_back)
  private ImageView mIvBack;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    Log.i("TAG", "" + getClass());
    mIvBack = findViewById(R.id.search_iv_back);
    mIvBack.setOnClickListener(
        (v) -> onBackPressed()
    );
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }
}
