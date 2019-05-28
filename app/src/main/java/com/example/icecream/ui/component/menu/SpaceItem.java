package com.example.icecream.ui.component.menu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * This class is used to padding.
 *
 * @author aaron
 */
public class SpaceItem extends DrawerItem<SpaceItem.ViewHolder> {

  /*
   * The padding size.
   */
  private int spaceDp;

  public SpaceItem(int spaceDp) {
    this.spaceDp = spaceDp;
  }

  @Override
  public ViewHolder createViewHolder(ViewGroup parent) {
    Context c = parent.getContext();
    View view = new View(c);
    int height = (int) (c.getResources().getDisplayMetrics().density * spaceDp);
    view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
    return new ViewHolder(view);
  }

  @Override
  public void bindViewHolder(ViewHolder holder) {}

  @Override
  public boolean isSelectable() {
    return false;
  }

  static class ViewHolder extends DrawerAdapter.ViewHolder {

    public ViewHolder(View itemView) {
      super(itemView);
    }
  }
}
