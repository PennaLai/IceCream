package com.example.icecream.ui.component.paragraph;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.icecream.R;
import java.util.List;

/**
 * The Adapter of Paragraph.
 */
public class ParagraphAdapter extends BaseAdapter {

  private List<Paragraph> mlist;
  private LayoutInflater inflate;

  public ParagraphAdapter(Context context, List<Paragraph> list){    // 数据链表
    mlist = list;
    inflate = LayoutInflater.from(context);
  }

  @Override
  public int getCount() {
    return mlist.size();
  }

  @Override
  public Object getItem(int position) {
    return mlist.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemViewType(int position) {
    Paragraph paragraph = mlist.get(position);
    return paragraph.getType();
  }

  @Override
  public int getViewTypeCount(){
    return 3;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    Paragraph paragraph = (Paragraph) getItem(position);
    int Type = getItemViewType(position);

    View view;
    TitleViewHolder titleViewHolder = null;

    TimeViewHolder timeViewHolder = null;

    ContentViewHolder contentViewHolder = null;

    if (convertView == null){
      if (Type == 0) {
        view = inflate.inflate(R.layout.item_title, parent,false);
        titleViewHolder = new TitleViewHolder();
        titleViewHolder.paragraphTitle = view.findViewById(R.id.paragraph_title);
        view.setTag(titleViewHolder);
      } else if (Type == 1) {
        view = inflate.inflate(R.layout.item_paragraph, parent,false);
        contentViewHolder = new ContentViewHolder();
        contentViewHolder.paragraphContent = view.findViewById(R.id.paragraph_content);
        view.setTag(contentViewHolder);
      } else {
        view = inflate.inflate(R.layout.item_info, parent,false);
        timeViewHolder = new TimeViewHolder();
        timeViewHolder.paragraphTime = view.findViewById(R.id.paragraph_time);
        view.setTag(timeViewHolder);
      }
    }else{
      view = convertView;
      if (Type == 0)
        titleViewHolder = (TitleViewHolder) convertView.getTag();
      else if (Type == 1)
        contentViewHolder = (ContentViewHolder) convertView.getTag();
      else timeViewHolder = (TimeViewHolder) convertView.getTag();
    }

    if (Type == 0)
      titleViewHolder.paragraphTitle.setText(paragraph.getContent());
    else if (Type == 1)
      contentViewHolder.paragraphContent.setText(paragraph.getContent());
    else
      timeViewHolder.paragraphTime.setText(paragraph.getContent());

    return view;
  }

  class TitleViewHolder {
    TextView paragraphTitle;
  }

  class TimeViewHolder {
    TextView paragraphTime;
  }

  class ContentViewHolder {
    TextView paragraphContent;
  }



}
