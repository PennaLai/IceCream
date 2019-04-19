package utils;

import java.util.regex.Pattern;

/**
 * This class is used for validating input string (e.g. username or password).
 *
 * @author kemo
 *
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
     * Validation result states.
     */
    public enum ValState {
        Valid,
        TooLong,
        TooShort,
        Empty,
        InvalidCharacters
    }

    /**
     * This method is to get the validation state of a input String.
     *
     * @param input The input String.
     * @param patternType Defined regular pattern.
     * @param maxLen The allowed maximum length.
     * @param minLen The allowed minimum length.
     * @return utils.Validator.ValState The defined validation result states.
     */
    private static ValState validate(final String input, final Pattern patternType,
                                     final int maxLen, final int minLen) {
        if (input == null || input.trim().length() == 0) {
            return ValState.Empty;
        }
        if (!patternType.matcher(input).find()) {
            return ValState.InvalidCharacters;
        }
        if (input.length() < minLen) {
            return ValState.TooShort;
        }
        if (input.length() > maxLen) {
            return ValState.TooLong;
        }
        return ValState.Valid;
    }

    /**
     * This method is to get the validation state of input password.
     *
     * @param input The input password String.
     * @return utils.Validator.ValState
     */
    public static ValState validatePassword(final String input) {
        return validate(input, passwordPattern, PASSWORD_MAX_LEN, PASSWORD_MIN_LEN);
    }

    /**
     * This method is to get the validation state of input username.
     *
     * @param input The input user name String.
     * @return utils.Validator.ValState The defined validation state.
     */
    public static ValState validateUsername(final String input) {
        return validate(input, userNamePattern, USERNAME_MAX_LEN, USERNAME_MIN_LEN);
    }


    /**
     * This method is to get the validation state of input phone number.
     *
     * @param input The input phone number String.
     * @return utils.Validator.ValState The defined validation state.
     */
    public static ValState validatePhoneNumber(final String input) {
        return validate(input, phoneNumberPattern, PHONE_MAX_LEN, PHONE_MIN_LEN);
    }

}
