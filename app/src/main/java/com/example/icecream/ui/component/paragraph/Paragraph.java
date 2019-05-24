package com.example.icecream.ui.component.paragraph;

/**
 * Paragraph for the read fragment.
 *
 * @author aaron penna.
 */
public class Paragraph {

  /** paragraph content. */
  private String content;

  /** the paragraph type 0 for Title, 1 for paragraph, 2 for time. */
  private int type;

  /**
   * construct for paragraph.
   * @param content content.
   * @param type type.
   */
  public Paragraph(String content, int type) {
    this.content = content;
    this.type = type;
  }

  /**
   * Getter of attribute content.
   * @return The content of the paragraph instance.
   */
  public String getContent() {
    return content;
  }

  /**
   * Getter of attribute type.
   * @return The type of the paragraph.
   */
  int getType() {
    return type;
  }
}
