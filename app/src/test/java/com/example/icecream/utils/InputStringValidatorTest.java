package com.example.icecream.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InputStringValidatorTest {

  @Test
  public void checkPasswordValid() {
    String password = "1dgkajhdkh";
    InputStringValidator.ValState passwordState = InputStringValidator.validatePassword(password);
    assertEquals(InputStringValidator.ValState.Valid, passwordState);
  }

  @Test
  public void checkPasswordEmpty() {
    String password = "";
    InputStringValidator.ValState passwordState = InputStringValidator.validatePassword(password);
    assertEquals(InputStringValidator.ValState.Empty, passwordState);
  }

  @Test
  public void checkPasswordInvalid() {
    String password = "中文密码";
    InputStringValidator.ValState passwordState = InputStringValidator.validatePassword(password);
    assertEquals(InputStringValidator.ValState.InvalidCharacters, passwordState);
  }

  @Test
  public void checkPasswordTooLong() {
    String password = "hkjdshajkadkjhsdkhakjdhdasda";
    InputStringValidator.ValState passwordState = InputStringValidator.validatePassword(password);
    assertEquals(InputStringValidator.ValState.TooLong, passwordState);
  }


  @Test
  public void checkPhoneNumberValid() {
    String phoneNumber = "15602432271";
    InputStringValidator.ValState phoneNumberState = InputStringValidator.validatePhone(phoneNumber);
    assertEquals(InputStringValidator.ValState.Valid, phoneNumberState);
  }

  @Test
  public void checkPhoneNumberInvalidCharacters() {
    String phoneNumber = "156024d2271";
    InputStringValidator.ValState phoneNumberState = InputStringValidator.validatePhone(phoneNumber);
    assertEquals(InputStringValidator.ValState.InvalidCharacters, phoneNumberState);
  }

  @Test
  public void checkPhoneNumberTooLong() {
    String phoneNumber = "1560241227111";
    InputStringValidator.ValState phoneNumberState = InputStringValidator.validatePhone(phoneNumber);
    assertEquals(InputStringValidator.ValState.InvalidCharacters, phoneNumberState);
  }

  @Test
  public void checkPhoneNumberEmpty() {
    String phoneNumber = "";
    InputStringValidator.ValState phoneNumberState = InputStringValidator.validatePhone(phoneNumber);
    assertEquals(InputStringValidator.ValState.Empty, phoneNumberState);
  }

}