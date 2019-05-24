package com.example.icecream.utils;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Handling user setting.
 *
 * @author Kemo
 */
public class UserSettingHandler {

  private static volatile UserSettingHandler instance;
  private SharedPreferences settings;
  private String loginPhone;

  private static final String PREFERENCE_FILE = "IceCreamPreference";

  private static final String CURRENT_LOGIN_PHONE = "CurrentPhone";


  private UserSettingHandler(Application application) {
    // store current phone
    settings = application.getSharedPreferences(PREFERENCE_FILE, 0);
    loginPhone = settings.getString(CURRENT_LOGIN_PHONE, null);
  }

  public static UserSettingHandler getInstance(final Application application) {
    if (instance == null) {
      synchronized (UserSettingHandler.class) {
        if (instance == null) {
          instance = new UserSettingHandler(application);
        }
      }
    }
    return instance;
  }

  /**
   * Gets the previous login phone.
   *
   * @return phone.
   */
  public String getLoginPhone() {
    return loginPhone;
  }

  /**
   * Stores the phone.
   *
   * @param phone phone.
   */
  public void setLoginPhone(String phone) {
    SharedPreferences.Editor editor = settings.edit();
    editor.putString(CURRENT_LOGIN_PHONE, phone);
    editor.apply();
    loginPhone = phone;
  }
}
