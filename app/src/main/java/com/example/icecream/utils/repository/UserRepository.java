package com.example.icecream.utils.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.icecream.database.AppDataBase;
import com.example.icecream.database.dao.UserDao;
import com.example.icecream.database.entity.User;

import java.util.List;

/**
 * The repository to handle users in database
 */
public class UserRepository {

  private UserDao userDao;
  private LiveData<List<User>> allUsers;

  /**
   * Constructor<br/>
   * register the application to repository
   *
   * @param application input application
   */
  public UserRepository(Application application) {
    AppDataBase db = AppDataBase.getDatabase(application);
    userDao = db.userDao();
    allUsers = userDao.getAllUsers();
  }

  /**
   * Get all users from local database
   *
   * @return all users
   */
  public LiveData<List<User>> getAllUsers() {
    return allUsers;
  }

  /**
   * Insert a user to database asynchronously
   *
   * @param user the input user
   */
  public void insert(User user) {
    new UserRepository.insertAsyncTask(userDao).execute(user);
  }

  /**
   * Delete a user from database asynchronously
   *
   * @param phone the input user's phone number
   */
  public void delete(String phone) {
    new UserRepository.deleteAsyncTask(userDao).execute(phone);
  }

  /**
   * Find the user by phone number from database asynchronously
   *
   * @param phone the input user's phone number
   */
  public void getUserByPhone(String phone) {
    new UserRepository.queryAsyncTask(userDao).execute(phone);
  }

  private static class insertAsyncTask extends AsyncTask<User, Void, Void> {
    private UserDao dao;

    insertAsyncTask(UserDao dao) {
      this.dao = dao;
    }

    @Override
    protected Void doInBackground(final User... params) {
      dao.insert(params[0]);
      return null;
    }

    // TODO onPostExecute(Result) 存储结束
  }

  private static class deleteAsyncTask extends AsyncTask<String, Void, Void> {
    private UserDao dao;

    deleteAsyncTask(UserDao dao) {
      this.dao = dao;
    }

    @Override
    protected Void doInBackground(final String... params) {
      dao.delete(params[0]);
      return null;
    }

    // TODO onPostExecute(Result) 删除结束
  }

  private static class queryAsyncTask extends AsyncTask<String, Void, LiveData<User>> {
    private UserDao dao;

    queryAsyncTask(UserDao dao) {
      this.dao = dao;
    }

    @Override
    protected LiveData<User> doInBackground(final String... params) {
      return dao.getUserByPhone(params[0]);
    }

    // TODO onPostExecute(Result) 查询结束

  }

}
