package com.example.icecream.database;

import com.example.icecream.database.dao.ArticleDao;
import com.example.icecream.database.dao.RssFeedDao;
import com.example.icecream.database.dao.UserDao;
import com.example.icecream.database.entity.RssFeed;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * The local database class
 *
 * @author Kemo
 */
@Database(entities = {RssFeed.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
  // singleton
  private static volatile AppDataBase INSTANCE;

  public abstract UserDao userDao();

  public abstract RssFeedDao rssFeedDao();

  public abstract ArticleDao articleDao();

  /**
   * Get the singleton instance of the database
   *
   * @param context app context
   * @return the singleton instance of the database
   * @author Kemo
   */
  public static AppDataBase getDatabase(final Context context) {
    if (INSTANCE == null) {
      synchronized (AppDataBase.class) {
        if (INSTANCE == null) {
          // create com.example.icecream.database
          INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
              AppDataBase.class, "app_database").build();
        }
      }
    }
    return INSTANCE;
  }

}
