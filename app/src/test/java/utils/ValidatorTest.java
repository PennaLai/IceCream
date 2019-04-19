package utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class ValidatorTest {

    @Test
    public void checkPasswordValid() {
        String password = "1dgkajhdkh";
        Validator.ValState passwordState = Validator.validatePassword(password);
        assertEquals(Validator.ValState.Valid, passwordState);
    }

    @Test
    public void checkPasswordEmpty(){
        String password = "";
        Validator.ValState passwordState = Validator.validatePassword(password);
        assertEquals(Validator.ValState.Empty, passwordState);
    }

    @Test
    public void checkPasswordInvalid(){
        String password = "中文密码";
        Validator.ValState passwordState = Validator.validatePassword(password);
        assertEquals(Validator.ValState.InvalidCharacters, passwordState);
    }

    @Test
    public void checkPasswordTooLong(){
        String password = "hkjdshajkadkjhsdkhakjdhdasda";
        Validator.ValState passwordState = Validator.validatePassword(password);
        assertEquals(Validator.ValState.TooLong, passwordState);
    }



    @Test
    public void checkPhoneNumberValid() {
        String phoneNumber = "15602432271";
        Validator.ValState phoneNumberState = Validator.validatePhoneNumber(phoneNumber);
        assertEquals(Validator.ValState.Valid, phoneNumberState);
    }

    @Test
    public void checkPhoneNumberInvalidCharacters() {
        String phoneNumber = "156024d2271";
        Validator.ValState phoneNumberState = Validator.validatePhoneNumber(phoneNumber);
        assertEquals(Validator.ValState.InvalidCharacters, phoneNumberState);
    }

    @Test
    public void checkPhoneNumberTooLong() {
        String phoneNumber = "1560241227111";
        Validator.ValState phoneNumberState = Validator.validatePhoneNumber(phoneNumber);
        assertEquals(Validator.ValState.TooLong, phoneNumberState);
    }

    @Test
    public void checkPhoneNumberEmpty() {
        String phoneNumber = "";
        Validator.ValState phoneNumberState = Validator.validatePhoneNumber(phoneNumber);
        assertEquals(Validator.ValState.Empty, phoneNumberState);
    }

}