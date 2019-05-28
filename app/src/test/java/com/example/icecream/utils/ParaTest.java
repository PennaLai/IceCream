package com.example.icecream.utils;

import org.junit.Test;

public class ParaTest {

  @Test
  public void loadToPara() {
    String json =
        "{'article': None, 'number': 4, 0: {'content': '接下来，我们学习如何对文本进行拆分，即存在一个大数据集文本，我们如何将其切分成单个小的数据集。', 'time': 0}, 1: {'content': '文本拆分适用于在自然语言处理过程中，需要对每通文本进行遍历打标的情况。', 'time': 10676}, 2: {'content': '具体步骤如下：将原始文本merge.', 'time': 18212}, 3: {'content': 'txt放在D盘下，并在D盘中新建空白文件夹test。', 'time': 21848}}";
    Para.loadToPara(json);
  }
}
