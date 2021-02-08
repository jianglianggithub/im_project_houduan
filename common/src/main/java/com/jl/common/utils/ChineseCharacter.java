package com.jl.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChineseCharacter {


    public static boolean check(char str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str + "");
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static boolean checkZM(char oneChar) {
        String temp = (oneChar + "");
        return temp.matches("[a-zA-Z]+");
    }
}
