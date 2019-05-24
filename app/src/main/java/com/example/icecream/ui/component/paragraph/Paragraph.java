package com.example.icecream.ui.component.paragraph;

/**
 * Paragraph for the read fragment.
 */
public class Paragraph {

  /** paragraph content. */
  private String content;

  /** the paragraph type 0 for Title, 1 for paragraph, 2 for time. */
  private int type;

  /***
   * construct for paragraph.
   * @param content content.
   * @param type type.
   */
  public Paragraph(String content, int type) {
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
