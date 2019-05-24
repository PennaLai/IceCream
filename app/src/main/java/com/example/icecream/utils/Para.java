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
    addData();
  }

  /**
   * insert data.
   * @param content
   * @param start
   * @param index
   */
  public void insertEachPara(String content, int start, int index) {
    if (index < this.paraNums) {
      EachPara eachPara = new EachPara(content, start);
      this.paras[index] = eachPara;
    }
  }

  public void addData() {
    paras = new EachPara[17];
    paras[0] = new EachPara("Azathoth  译者：玖羽\\n\\n\\n    漫长的岁月从世界上流逝而过，人们的心中失去了惊奇的能力。",0);
    paras[1] = new EachPara("在灰色的都市里，丑恶而令人反感的高塔直刺天空，在它们的阴影中，没有人会梦见太阳、春天和鲜花盛开的草原。",
        8576);
    paras[2] = new EachPara("知识从大地上剥除了“美”，诗人只懂用模糊的双眼往自己的内心窥探、将扭曲的幻像歌唱。",
        20712);
    paras[3] = new EachPara("当这些事情真的降临、当童真的希望永远丧失的时候，有一个人抛弃人生，踏上寻求之旅。",
        29608);
    paras[4] = new EachPara("他寻找的，正是世界的梦想逃去之所",
        38964);
    paras[5] = new EachPara("\\n\\n\\u3000\\u3000这个人的姓名和住所都是属于清醒世界的俗物，微不足道、鲜为人知。",
        42580);
    paras[6] = new EachPara("我们需要知道的，只是他住在一个被不毛的黄昏永远笼罩的城市，那个城市被高高的壁垒围起。",
        49176);
    paras[7] = new EachPara("他日复一日地在阴影和混乱中劳苦，晚上回到住处、打开窗户之后，所面对的也不是原野或森林，而只是一个被所有窗口愚钝而绝望地凝视的昏暗庭园。",
        58192);
    paras[8] = new EachPara("他从窗户里只能看见垒壁和别的窗口，唯有把身体大大地探出窗外，才有可能望到在夜空中运行的微小星辰",
        74068);
    paras[9] = new EachPara("一成不变的垒壁和窗口足可把一个经常做梦、读书的人迅速逼疯，因此这房客便夜复一夜地将身体探出窗外，望向高天，只为瞥见一眼那存在于清醒世界和灰色都市彼方的断片",
        84824);
    paras[10] = new EachPara("他年复一年地仰望，甚至给那些缓慢运行的星辰取了名字，即使星辰遗憾地滑出视野，他依然在想像中将它们紧紧跟随。",
        102840);
    paras[11] = new EachPara("就这样，常人无法察觉的诸多秘密幻景终于能被他看见。",
        115056);
    paras[12] = new EachPara("一夜，巨大的鸿沟上架起了桥梁，萦绕着幻梦的天空越来越近，沉进那孤独的观星者的窗户、化入他周遭的空气，使他与难以置信的惊奇融为一体\\n\\n\\u3000\\u3000于是在他的屋内，飘浮着黄金尘埃的紫罗兰色暗夜奔涌而入，尘埃与火焰的漩涡从终极的虚空里喷出，又沉淀在来自世界彼方的芳香之中。",
        120852);
    paras[13] = new EachPara("催人入眠的大海涌了上来，在人的眼睛从未目睹过的阳光的照耀下，游弋在深不见底的漩涡中的奇异海豚和海中女仙现出身形。",
        149868);
    paras[14] = new EachPara("寂静的无限在入梦者身边缠卷而上，不需触碰从孤寂的窗口里僵硬地探出的身体，便将他轻轻卷起。",
        162424);
    paras[15] = new EachPara("在不可用人类的历法计量的许多天后，来自遥远领域的浪潮温柔地将他运进梦境——那正是他渴望的梦境、是人类已然失却的梦境。",
        172620);
    paras[16] = new EachPara("过了无数个周期，潮水只是体贴地让他留在绿色太阳照耀的岸边安眠，那岸边有盛开的莲花的芬芳、有红色的水生植物装点",
        184756);
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
   * @param json speaker json data.
   * @return
   */
  public static Para loadToPara(String json) {
    try {
      JSONObject initObject = new JSONObject(json);
      int num = initObject.getInt("number");
      Para para = new Para(num);
      for (int i = 0; i < num ;i++) {
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
