package org.jwttest.util;

import java.util.regex.Pattern;

public class ValidationUtility {

    private static final String EMAIL_REGEX = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";

    private static final String PASSWORD_REGEX =
            "^(?=(?:[^A-Z]*[A-Z]){1}[^A-Z]*$)(?=(?:[^\\d]*\\d){2}[^\\d]*$)(?!.*\\d{2})[a-zA-Z\\d]{8,12}$";

    public static boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public static boolean isValidPassword(String password) {
        return Pattern.matches(PASSWORD_REGEX, password);
    }
}
