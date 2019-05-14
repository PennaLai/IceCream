package com.example.icecream.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class ConvertersTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void fromTimestampTest() {
    Long value = 123L;
    Date date = new Date(value);
    assertEquals(date, Converters.fromTimestamp(value));
  }

  @Test
  public void dateToTimestampTest() {
    Date date = new Date(123L);
    assertEquals(123L, Converters.dateToTimestamp(date).longValue());
  }
}