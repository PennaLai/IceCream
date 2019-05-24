package com.example.icecream.ui.component.menu;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The DrawerAdapter to control the Drawer.
 * Refer from yarolegovich
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

  /** The List for DrawerItems */
  private List<DrawerItem> items;

  /** The Map to store the type of DrawerItems */
  private Map<Class<? extends DrawerItem>, Integer> viewTypes;


  private SparseArray<DrawerItem> holderFactories;

  private OnItemSelectedListener listener;


  public DrawerAdapter(List<DrawerItem> items) {
    this.items = items;
    this.viewTypes = new HashMap<>();
    this.holderFactories = new SparseArray<>();

    processViewTypes();
  }

  @Override
  @NonNull
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ViewHolder holder = holderFactories.get(viewType).createViewHolder(parent);
    holder.adapter = this;
    return holder;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    items.get(position).bindViewHolder(holder);
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  @Override
  public int getItemViewType(int position) {
    Class<? extends DrawerItem> clz = items.get(position).getClass();
    return viewTypes.get(clz);
  }

  /** process the ViewType of DrawItems */
  private void processViewTypes() {
    int type = 0;
    for (DrawerItem item : items) {
      if (!viewTypes.containsKey(item.getClass())) {
        viewTypes.put(item.getClass(), type);
        holderFactories.put(type, item);
        type++;
      }
    }
  }


  /**
   * Update the DrawItem in given position by passing icon and text.
   * @param position : The position of the DrawItem
   * @param icon : The new icon of the DrawItem
   * @param text : The new text of the DrawItem
   */
  public void updateItem(int position, Drawable icon, String text){
    SimpleItem updateItem = (SimpleItem) items.get(position);
    updateItem.setIcon(icon);
    updateItem.setTitle(text);
    notifyItemChanged(position);
  }

  public void setSelected(int position) {
    DrawerItem newChecked = items.get(position);
    if (!newChecked.isSelectable()) {
      return;
    }

    for (int i = 0; i < items.size(); i++) {
      DrawerItem item = items.get(i);
      if (item.isChecked()) {
        item.setChecked(false);
        notifyItemChanged(i);
        break;
      }
    }

    newChecked.setChecked(true);
    notifyItemChanged(position);

    if (listener != null) {
      listener.onItemSelected(position);
    }
  }

  /**
   * Set the OnItemSelectedListener for the Drawer
   * @param listener : The OnItemSelectedListener for the Drawer.
   */
  public void setListener(OnItemSelectedListener listener) {
    this.listener = listener;
  }

  abstract static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private DrawerAdapter adapter;

    public ViewHolder(View itemView) {
      super(itemView);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      adapter.setSelected(getAdapterPosition());
    }
  }

  public interface OnItemSelectedListener {
    void onItemSelected(int position);
  }
}
