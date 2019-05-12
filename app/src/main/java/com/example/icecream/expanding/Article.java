package com.example.icecream.expanding;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.example.icecream.R;

public class Article {

    private ExpandingItem item;
    private String title;
    private String abstr;
    private int source;

    public Article(ExpandingList mExpandingList, String title, String abstr, int source) {
        this.title = title;
        this.source = source;
        this.abstr = abstr;
        item = mExpandingList.createNewItem(R.layout.expanding_layout);
        if (item!=null){

            switch (source){
                case 1: // 知乎
                    item.setIndicatorColorRes(R.color.zhihu);
                    item.setIndicatorIconRes(R.drawable.zhihu);
                    break;
                case 2: // 头条
                    item.setIndicatorColorRes(R.color.toutiao);
                    item.setIndicatorIconRes(R.drawable.toutiao);
                    break;
                default:
                    item.setIndicatorColorRes(R.color.indicator);
                    item.setIndicatorIconRes(R.drawable.logo);
                    break;
            }

            ((TextView) item.findViewById(R.id.article_title)).setText(title);
            ((TextView) item.findViewById(R.id.article_abstr)).setText(abstr);

            item.createSubItems(2);
            View star = item.getSubItemView(0);
            ((TextView) star.findViewById(R.id.sub_title)).setText("Add to favorite.");
            ((ImageView) star.findViewById(R.id.sub_button)).setImageResource(R.drawable.ic_star);

            View warn = item.getSubItemView(1);
            ((TextView) warn.findViewById(R.id.sub_title)).setText("Report the airticle.");
            ((ImageView) warn.findViewById(R.id.sub_button)).setImageResource(R.drawable.ic_warn);
        }
    }

    public void setItem(ExpandingItem item) {
        this.item = item;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public ExpandingItem getItem() {
        return item;
    }

    public String getTitle() {
        return title;
    }

    public int getSource() {
        return source;
    }

    public String getAbstr() {
        return abstr;
    }
}
