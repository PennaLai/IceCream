package utils;

import java.util.regex.Pattern;

/**
 * @author kemo
 * @description This class is used for validating input string (e.g. username or password)
 */
public final class Validator {

    private static Pattern userNamePattern = Pattern.compile("^[a-zA-Z0-9`~!@#$%^&*()_+\\-=\\[\\]{}|\\\\;:'\",./<>?]*$");
    private static Pattern passwordPattern = Pattern.compile("^[a-zA-Z0-9`~!@#$%^&*()_+\\-=\\[\\]{}|\\\\;:'\",./<>?]*$");
    private static Pattern phoneNumberPattern = Pattern.compile("^((13[0-9])|(15[^4])|(18[0-9])|(17[0-9])|(147))\\d{8}$");

    private static final int PASSWORD_MAX_LEN = 24;
    private static final int PASSWORD_MIN_LEN = 4;

    private static final int USERNAME_MAX_LEN = 8;
    private static final int USERNAME_MIN_LEN = 2;

    private static final int PHONE_MAX_LEN = 11;
    private static final int PHONE_MIN_LEN = 11;

    private Validator() {
    }

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

    private static ValState validate(final String input, final Pattern patternType,
                                     final int maxLen, final int minLen) {
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

    public static ValState validatePassword(final String input) {
        return validate(input, passwordPattern, PASSWORD_MAX_LEN, PASSWORD_MIN_LEN);
    }


    public static ValState validateUsername(final String input) {
        return validate(input, userNamePattern, USERNAME_MAX_LEN, USERNAME_MIN_LEN);
    }


    public static ValState validatePhoneNumber(final String input) {
        return validate(input, phoneNumberPattern, PHONE_MAX_LEN, PHONE_MIN_LEN);
    }

}
