package com.example.icecream.ui.component.paragraph;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.icecream.R;
import java.util.List;

public class ParagraphAdapter extends BaseAdapter {

//  private int resourceId;
  private Context mcontext;
  private List<Paragraph> mlist;
  private LayoutInflater inflate;

  // 构造函数
  public ParagraphAdapter(Context context, List<Paragraph> list){    // 数据链表
//    resourceId = textViewResourceId;
    mcontext = context;
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

  // 重写getView
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    // 获取当前项的Fruit实例
    Paragraph paragraph = (Paragraph) getItem(position);
    int Type = getItemViewType(position);

    View view;
    TitleViewHolder titleViewHolder = null;

    TimeViewHolder timeViewHolder = null;

    ContentViewHolder contentViewHolder = null;

    if (convertView == null){
      if (Type == 0) {
//        view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        view = inflate.inflate(R.layout.item_title, parent,false);
        titleViewHolder = new TitleViewHolder();
        titleViewHolder.paragraphTitle = (TextView) view.findViewById(R.id.paragraph_title);
        view.setTag(titleViewHolder);
      } else if (Type == 1) {
        view = inflate.inflate(R.layout.item_paragraph, parent,false);
        contentViewHolder = new ContentViewHolder();
        contentViewHolder.paragraphContent = (TextView) view.findViewById(R.id.paragraph_content);
        view.setTag(contentViewHolder);
      } else {
        view = inflate.inflate(R.layout.item_info, parent,false);
        timeViewHolder = new TimeViewHolder();
        timeViewHolder.paragraphTime = (TextView) view.findViewById(R.id.paragraph_time);
        view.setTag(timeViewHolder);
      }

//      // inflate出子项布局，实例化其中的图片控件和文本控件
//      view = LayoutInflater.from(getContext()).inflate(resourceId, null);
//
//      viewHolder = new ViewHolder();
//
//      // 缓存图片控件和文本控件的实例
//      view.setTag(viewHolder);
    }else{
      view = convertView;
      if (Type == 0)
        titleViewHolder = (TitleViewHolder) convertView.getTag();
      else if (Type == 1)
        contentViewHolder = (ContentViewHolder) convertView.getTag();
      else timeViewHolder = (TimeViewHolder) convertView.getTag();
    }
//      // 取出缓存
//      viewHolder = (ViewHolder) view.getTag();
    if (Type == 0)
      titleViewHolder.paragraphTitle.setText(paragraph.getContent());
    else if (Type == 1)
      contentViewHolder.paragraphContent.setText(paragraph.getContent());
    else
      timeViewHolder.paragraphTime.setText(paragraph.getContent());

    return view;
  }

  // 内部类
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
