package com.example.icecream;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
//import com.example.newbiechen.ireader.model.bean.packages.SearchBookPackage;
//import com.example.newbiechen.ireader.presenter.SearchPresenter;
//import com.example.newbiechen.ireader.presenter.contract.SearchContract;
//import com.example.newbiechen.ireader.ui.adapter.KeyWordAdapter;
//import com.example.newbiechen.ireader.ui.adapter.SearchBookAdapter;
//import com.example.newbiechen.ireader.ui.base.BaseMVPActivity;
//import com.example.newbiechen.ireader.widget.RefreshLayout;
//import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration;
import java.util.List;
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
        Log.i("TAG", ""+getClass());
        mIvBack = findViewById(R.id.search_iv_back);
        mIvBack.setOnClickListener(
            (v) -> onBackPressed()
        );
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
