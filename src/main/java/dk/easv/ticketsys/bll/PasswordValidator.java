package dk.easv.ticketsys.bll;

import java.util.regex.Pattern;

public class PasswordValidator {
    public boolean isValidPassword(String password) {
        // Regex Explanation:
        // (?=.*\d)         → At least one digit (0-9)
        // (?=.*[!@#$%^&*.?+_]:;,) → At least one special character
        // .{5,}            → At least 5 characters long
        String regex = "^(?=.*\\d)(?=.*[!@#$%^&*.?+_:;,]).{5,}$";
        return Pattern.matches(regex, password);
    }
}
