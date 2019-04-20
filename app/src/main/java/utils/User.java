package utils;


/**
 * Simple User class.
 *
 * @author Hanmei
 * @version V1.0
 */

public class User {
  private String username;
  private String password;

  /**
   * This method is to set the username of a {@link User} object.
   *
   * @param username The username String.
   */
  public void setUsername(final String username) {
    this.username = username;
  }

  /**
   * This method is to set the password of a {@link User} object.
   *
   * @param password The password String.
   */
  public void setPassword(final String password) {
    this.password = password;
  }

  /**
   * This method is to get the password of a {@link User} object.
   */
  public String getPassword() {
    return password;
  }

  /**
   * This method is to get the username of a {@link User} object.
   */
  public String getUsername() {
    return username;
  }


}
