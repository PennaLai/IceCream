package utils;

import java.util.regex.Pattern;

/**
 * @author kemo
 * @description This class is used for validating input string (e.g. username or password)
 */
public class Validator {

    private static Pattern pattern = Pattern.compile("^[a-zA-Z0-9`~!@#$%^&*()_+\\-=\\[\\]{}|\\\\;:'\",./<>?]*$");

    public final static int MAX_LEN = 24;
    public final static int MIN_LEN = 4;

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

    public static ValState validate(String input) {
        if (input == null || input.trim().length() == 0) {
            return ValState.Empty;
        }
        if (input.length() < MIN_LEN) {
            return ValState.TooShort;
        }
        if (input.length() > MAX_LEN) {
            return ValState.TooLong;
        }
        if (!pattern.matcher(input).find()) {
            return ValState.InvalidCharacters;
        }
        return ValState.Valid;
    }
}
