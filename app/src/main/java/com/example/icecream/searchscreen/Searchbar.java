package com.example.icecream.searchscreen;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import com.example.icecream.R;
import com.example.icecream.search.TransformingToolbar;

/**
 * A Toolbar with an EditText used for searching
 *
 * @author aaron
 */
public class Searchbar extends TransformingToolbar {

  private EditText editText;

  /**
   * create the search bar.
   * @param context Context
   * @param attrs attribute set
   */
  public Searchbar(Context context, AttributeSet attrs) {
    super(context, attrs);
    setBackgroundColor(context.getResources().getColor(android.R.color.white));
    setNavigationIcon(R.drawable.ic_action_back);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    inflate(getContext(), R.layout.merge_search, this);
    editText = (EditText) findViewById(R.id.toolbar_search_edittext);
  }

  @Override
  public void showContent() {
    super.showContent();
    editText.requestFocus();
  }

  public void clearText() {
    editText.setText(null);
  }
}
