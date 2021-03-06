package com.example.icecream.ui.component.menu;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.icecream.R;

/**
 * This class is the item in the draw.
 * @author aaron
 */
public class SimpleItem extends DrawerItem<SimpleItem.ViewHolder> {

  /*
   * The selected type icon id.
   */
  private int selectedItemIconTint;

  /*
   * The selected type text id.
   */
  private int selectedItemTextTint;

  /*
   * The normal type icon id.
   */
  private int normalItemIconTint;

  /*
   * The normal type icon id.
   */
  private int normalItemTextTint;

  /*
   * The icon.
   */
  private Drawable icon;

  public void setIcon(Drawable icon) {
    this.icon = icon;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  /*
   * The text.
   */
  private String title;

  /**
   * The Constructor for SimpleItem.
   * @param icon : icon.
   * @param title : text.
   */
  public SimpleItem(Drawable icon, String title) {
    this.icon = icon;
    this.title = title;
  }

  @Override
  public ViewHolder createViewHolder(ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View v = inflater.inflate(R.layout.item_option, parent, false);
    return new ViewHolder(v);
  }

  @Override
  public void bindViewHolder(ViewHolder holder) {
    holder.title.setText(title);
    holder.icon.setImageDrawable(icon);

    holder.title.setTextColor(isChecked ? selectedItemTextTint : normalItemTextTint);
    holder.icon.setColorFilter(isChecked ? selectedItemIconTint : normalItemIconTint);
  }

  /**
   * Set the selected type icon for a SimpleItem instance
   * @param selectedItemIconTint : the selected icon id.
   * @return com.example.icecream.ui.component.menu.SimpleItem the instance.
   */
  public SimpleItem withSelectedIconTint(int selectedItemIconTint) {
    this.selectedItemIconTint = selectedItemIconTint;
    return this;
  }

  /**
   * Set the selected type text for a SimpleItem instance
   * @param selectedItemTextTint : the selected text id.
   * @return com.example.icecream.ui.component.menu.SimpleItem the instance.
   */
  public SimpleItem withSelectedTextTint(int selectedItemTextTint) {
    this.selectedItemTextTint = selectedItemTextTint;
    return this;
  }


  /**
   * Set the normal type icon for a SimpleItem instance
   * @param normalItemIconTint : the normal icon id.
   * @return com.example.icecream.ui.component.menu.SimpleItem the instance.
   */
  public SimpleItem withIconTint(int normalItemIconTint) {
    this.normalItemIconTint = normalItemIconTint;
    return this;
  }

  /**
   * Set the normal text for a SimpleItem instance
   * @param normalItemTextTint : the normal text id.
   * @return com.example.icecream.ui.component.menu.SimpleItem the instance.
   */
  public SimpleItem withTextTint(int normalItemTextTint) {
    this.normalItemTextTint = normalItemTextTint;
    return this;
  }

  static class ViewHolder extends DrawerAdapter.ViewHolder {

    private ImageView icon;
    private TextView title;

    public ViewHolder(View itemView) {
      super(itemView);
      icon = (ImageView) itemView.findViewById(R.id.icon);
      title = (TextView) itemView.findViewById(R.id.title);
    }
  }
}
