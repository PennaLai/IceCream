package com.example.icecream.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.example.icecream.database.dao.ArticleDao;
import com.example.icecream.database.dao.RssFeedDao;
import com.example.icecream.database.dao.UserArticleJoinDao;
import com.example.icecream.database.dao.UserDao;
import com.example.icecream.database.dao.UserRssFeedJoinDao;
import com.example.icecream.database.entity.Article;
import com.example.icecream.database.entity.RssFeed;
import com.example.icecream.database.entity.User;
import com.example.icecream.database.entity.UserArticleJoin;
import com.example.icecream.database.entity.UserRssFeedJoin;
import com.example.icecream.utils.Converters;

/**
 * The local database class.
 *
 * @author Kemo
 */
@Database(entities = {User.class, RssFeed.class, Article.class,
    UserRssFeedJoin.class, UserArticleJoin.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDataBase extends RoomDatabase {
  // singleton
  private static volatile AppDataBase instance;

  public abstract UserDao userDao();

  public abstract RssFeedDao rssFeedDao();

  public abstract ArticleDao articleDao();

  public abstract UserRssFeedJoinDao userRssFeedJoinDao();

  public abstract UserArticleJoinDao userArticleJoinDao();

  /**
   * Get the singleton instance of the database.
   *
   * @param context app context.
   * @return the singleton instance of the database.
   * @author Kemo
   */
  static AppDataBase getDatabase(final Context context) {
    if (instance == null) {
      synchronized (AppDataBase.class) {
        if (instance == null) {
          // create local database
          instance = Room.databaseBuilder(context.getApplicationContext(),
              AppDataBase.class, "app_database").build();
        }
      }
    }
    return instance;
  }

}
