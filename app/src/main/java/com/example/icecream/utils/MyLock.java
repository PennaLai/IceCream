package com.example.icecream.utils;

public class MyLock {
  private boolean isBusy;

  public MyLock() {
    this.isBusy = true;
  }

  public boolean isBusy() {
    return isBusy;
  }

  public void exitLoop() {
    isBusy = false;
  }
}
