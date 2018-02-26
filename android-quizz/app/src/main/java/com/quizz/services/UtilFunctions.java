package com.quizz.services;

/**
 * Created by Florian on 26/02/2018.
 */

public class UtilFunctions {
    public static String makeMultilineString(String text, int nbCharPerLine) {
        String newVal = "";

        if (text.length() > nbCharPerLine * 2) {
            text = text.substring(0, nbCharPerLine * 2);
        }

        for (int i = 0; i <= text.length(); i += nbCharPerLine) {
            if (i < text.length() - nbCharPerLine) {
                newVal = newVal + text.substring(i, i + nbCharPerLine) + "\n";
            } else {
                newVal = newVal + text.substring(i);
            }
        }
        return newVal;
    }
}
