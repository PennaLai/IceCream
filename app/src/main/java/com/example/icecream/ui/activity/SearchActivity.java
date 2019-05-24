package com.example.icecream.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.icecream.R;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.MaterialSearchBar.OnSearchActionListener;
import java.util.List;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
//import me.gujun.android.taggroup.TagGroup;

/**
 * Created by newbiechen on 17-4-24.
 */

public class SearchActivity extends AppCompatActivity
    implements OnNavigationItemSelectedListener, OnSearchActionListener, OnMenuItemClickListener {

  private List<String> lastSearches;
  private MaterialSearchBar searchBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search_new);

    searchBar = (MaterialSearchBar) findViewById(R.id.search_searchBar);
    searchBar.setHint("Custom hint");
    searchBar.setSpeechMode(false);
    //enable searchbar callbacks
    searchBar.setOnSearchActionListener(this);
    //restore last queries from disk
//    lastSearches = loadSearchSuggestionFromDisk();
//    searchBar.setLastSuggestions(list);
    //Inflate menu and setup OnMenuItemClickListener
    searchBar.inflateMenu(R.menu.menu_main);
    searchBar.getMenu().setOnMenuItemClickListener(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    //save last queries to disk
//    saveSearchSuggestionToDisk(searchBar.getLastSuggestions());
  }

  @Override
  public void onSearchStateChanged(boolean enabled) {
    String s = enabled ? "enabled" : "disabled";
    Toast.makeText(SearchActivity.this, "Search " + s, Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onSearchConfirmed(CharSequence text) {
    startSearch(text.toString(), true, null, true);
  }

  @Override
  public void onButtonClicked(int buttonCode) {
    switch (buttonCode){
      case MaterialSearchBar.BUTTON_NAVIGATION:
//        drawer.openDrawer(Gravity.LEFT);
        break;
      case MaterialSearchBar.BUTTON_SPEECH:
//        openVoiceRecognizer();
        break;
      case MaterialSearchBar.BUTTON_BACK:
        onBackPressed();
        break;
    }
  }
  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
    return false;
  }

  @Override
  public boolean onMenuItemClick(MenuItem menuItem) {
    return false;
  }
}
