package com.example.icecream.ui.component.recycleveiw;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.icecream.R;
import com.example.icecream.database.entity.Article;
import com.example.icecream.ui.component.recycleveiw.ArticlesAdapter.ArticlesViewHolder;

import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesViewHolder> {

  private static final String TAG = ArticlesAdapter.class.getSimpleName();

  /*
   * An on-click handler that we've defined to make it easy for an Activity to interface with
   * our RecyclerView
   */
  private final ListItemClickListener mOnClickListener;

  private static int viewHolderCount;

  private int mNumberItems;

  /**
   * Cache copy of article.
   */
  private List<Article> mArticles;

//  private List<String> mFeedNames;

  /**
   * The interface that receives onClick messages.
   */
  public interface ListItemClickListener {
    void onListItemClick(Article article);
  }

  /**
   * Constructor for GreenAdapter that accepts a number of items to display and the specification
   * for the ListItemClickListener.
   *
   * @param numberOfItems Number of items to display in list
   * @param listener      Listener for list item clicks
   */
  public ArticlesAdapter(int numberOfItems, ListItemClickListener listener) {
    mNumberItems = numberOfItems;
    mOnClickListener = listener;
    viewHolderCount = 0;
  }

  /**
   * This gets called when each new ViewHolder is created. This happens when the RecyclerView is
   * laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
   *
   * @param viewGroup The ViewGroup that these ViewHolders are contained within.
   * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
   *                  can use this viewType integer to provide a different layout. See {@link
   *                  android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)} for more details.
   * @return A new ArticlesViewHolder that holds the View for each list item
   */
  @NonNull
  @Override
  public ArticlesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    Context context = viewGroup.getContext();
    int layoutIdForListItem = R.layout.article_list_item;
    LayoutInflater inflater = LayoutInflater.from(context);

    View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
    ArticlesViewHolder viewHolder = new ArticlesViewHolder(view);

    viewHolder.viewHolderIndex.setText("ViewHolder index: " + viewHolderCount);

    viewHolderCount++;
    Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: " + viewHolderCount);
    return viewHolder;
  }

  /**
   * set the database article cache and notify the data set change.
   *
   * @param articles database articles
   */
  public void setArticles(List<Article> articles) {
    mNumberItems = articles.size();
    mArticles = articles;
    notifyDataSetChanged();
  }

  /**
   * return current articles numbers.
   *
   * @return articles count
   */
  public int getArticlesCount() {
    if (mArticles == null)
      return 0;
    else return mArticles.size();
  }


  /**
   * OnBindViewHolder is called by the RecyclerView to display the data at the specified position.
   * In this method, we update the contents of the ViewHolder to display the correct indices in the
   * list for this particular position, using the "position" argument that is conveniently passed
   * into us.
   *
   * @param holder   The ViewHolder which should be updated to represent the contents of the item at
   *                 the given position in the data set.
   * @param position The position of the item within the adapter's data set.
   */
  @Override
  public void onBindViewHolder(@NonNull ArticlesViewHolder holder, int position) {
    if (mArticles != null && position < mArticles.size()) {
      Article current = mArticles.get(position);
      if (current != null) {
        holder.bindContent(current);
      }
    }
  }

  /**
   * This method simply returns the number of items to display. It is used behind the scenes to help
   * layout our Views and for animations.
   *
   * @return The number of items available
   */
  @Override
  public int getItemCount() {
    return mNumberItems;
  }


  /**
   * Cache of the children views for a list item.
   */
  class ArticlesViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    /**
     * article title.
     */
    TextView title;

    /**
     * articles author name.
     */
    TextView author;

    /**
     * the articles content.
     */
    TextView content;

    /**
     * articles public time.
     */
    TextView publicTime;

    /**
     * delete later.
     */
    TextView viewHolderIndex;

    Article article;

    /**
     * Constructor for our ViewHolder. Within this constructor, we get a reference to our TextViews
     * and set an onClickListener to listen for clicks. Those will be handled in the onClick method
     * below.
     *
     * @param itemView The View that you inflated in {@link
     *                 ArticlesAdapter#onCreateViewHolder(ViewGroup, int)}
     */
    public ArticlesViewHolder(View itemView) {
      super(itemView);

      author = itemView.findViewById(R.id.tv_author);
      publicTime = itemView.findViewById(R.id.tv_publish_time);
      title = itemView.findViewById(R.id.tv_item_title);
      content = itemView.findViewById(R.id.tv_item_content);
      viewHolderIndex = itemView.findViewById(R.id.tv_view_holder_instance);

      itemView.setOnClickListener(this);
    }


    /**
     * Bind content from repository to the view.
     * @param article
     */
    void bindContent(Article article) {
      this.article = article;
      updateContent();
    }


    /**
     * This used to update content depend on article.
     *
     */
    private void updateContent() {
      if (article == null)
        return;
      this.title.setText(article.getTitle());
      this.author.setText("AUTHOR");
      this.content.setText(article.getDescription());
      this.publicTime.setText(article.getPublishTime());
    }

    /**
     * Called whenever a user clicks on an item in the list.
     *
     * @param v The View that was clicked
     */
    @Override
    public void onClick(View v) {
      int clickedPosition = getAdapterPosition();
      mOnClickListener.onListItemClick(this.article);
    }
  }
}
