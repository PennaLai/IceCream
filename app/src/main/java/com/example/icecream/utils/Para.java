package com.example.icecream.utils;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

public class Para {

  /** the number of paragraph numbers*/
  private int paraNums;

  /** store all the paragraph. */
  private EachPara [] paras;

  /**
   *
   * @param paraNum the number of paragraph.
   */
  public Para(int paraNum) {
    this.paraNums = paraNum;
    paras = new EachPara[paraNum];
  }

  public void insertEachPara(String content, int start, int index) {
    if (index < this.paraNums) {
      EachPara eachPara = new EachPara(content, start);
      this.paras[index] = eachPara;
    }
  }

  public int getParaNums() {
    return paraNums;
  }

  public EachPara[] getParas() {
    return paras.clone();
  }

  /**
   * each paragraph information.
   */
  public static class EachPara {

    /** the paragraph content. */
    private String content;

    /** this paragraph start time. */
    private int startTime;

    private EachPara(String content, int startTime) {
      this.content = content;
      this.startTime = startTime;
    }

    public String getContent() {
      return content;
    }

    public int getStartTime() {
      return startTime;
    }
  }

  /***
   * load the json to para object.
   * @param json
   * @return
   */
  public static Para loadToPara(String json) {
    try {
      JSONObject initObject = new JSONObject(json);
      int num = initObject.getInt("number");
      Para para = new Para(num);
      for (int i = 0; i < num ; i++) {
        JSONObject eachPara = initObject.getJSONObject(String.valueOf(i));
        String content = eachPara.getString("content");
        int time = eachPara.getInt("time");
        para.insertEachPara(content, time, i);
      }
      return para;
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }


}
