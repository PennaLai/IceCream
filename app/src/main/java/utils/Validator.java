package utils;

import java.util.regex.Pattern;

/**
 * @author kemo
 * @description This class is used for validating input string (e.g. username or password)
 */
public class Validator {

    private static Pattern userNamePattern = Pattern.compile("^[a-zA-Z0-9`~!@#$%^&*()_+\\-=\\[\\]{}|\\\\;:'\",./<>?]*$");
    private static Pattern passwordPattern = Pattern.compile("^[a-zA-Z0-9`~!@#$%^&*()_+\\-=\\[\\]{}|\\\\;:'\",./<>?]*$");
    private static Pattern phoneNumberPattern = Pattern.compile("^((13[0-9])|(15[^4])|(18[0-9])|(17[0-9])|(147))\\d{8}$");


    private final static int PASSWORD_MAX_LEN = 24;
    private final static int PASSWORD_MIN_LEN = 4;

    private final static int USERNAME_MAX_LEN = 8;
    private final static int USERNAME_MIN_LEN = 2;

    private final static int PHONE_MAX_LEN = 11;
    private final static int PHONE_MIN_LEN = 11;


    /**
     * @author kemo
     * @description Validation result state
     */
    public enum ValState {
        Valid,
        TooLong,
        TooShort,
        Empty,
        InvalidCharacters
    }

    private static ValState validate(String input, Pattern patternType, int maxLen, int minLen) {
        if (input == null || input.trim().length() == 0) {
            return ValState.Empty;
        }
        if (input.length() < minLen) {
            return ValState.TooShort;
        }
        if (input.length() > maxLen) {
            return ValState.TooLong;
        }
        if (!patternType.matcher(input).find()) {
            return ValState.InvalidCharacters;
        }
        return ValState.Valid;
    }

    public static ValState CheckPasswordValidate(String input) {
        return validate(input, passwordPattern, PASSWORD_MAX_LEN, PASSWORD_MIN_LEN);
    }


    public static ValState CheckUserNameValidate(String input) {
        return validate(input, userNamePattern, USERNAME_MAX_LEN, USERNAME_MIN_LEN);
    }


    public static ValState CheckPhoneNumberValidate(String input) {
        return validate(input, phoneNumberPattern, PHONE_MAX_LEN, PHONE_MIN_LEN);
    }

}
