package com.example.icecream.ui.component.paragraph;

public class Paragraph {

  private String content;
  private int type;
  // 0 for Title, 1 for paragraph, 2 for time

  public Paragraph(String content, int type){
    this.content = content;
    this.type = type;
  }

  public String getContent() {
    return content;
  }

  public int getType() {
    return type;
  }
}
