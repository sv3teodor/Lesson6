package utils;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<Character> getSpecialCharacters() {
        List<Character> specialCharacters = new ArrayList<>();
        for (char c = 0; c < Character.MAX_VALUE; c++) {
            if (Character.isISOControl(c)) {
                specialCharacters.add(c);
            }
        }
        return specialCharacters;
    }
}
