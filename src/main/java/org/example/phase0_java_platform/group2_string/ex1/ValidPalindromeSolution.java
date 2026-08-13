package org.example.phase0_java_platform.group2_string.ex1;

import java.util.Arrays;

public class ValidPalindromeSolution {
    public static void main(String[] args) {
        System.out.println(isPalindrome("A man, a plan, a canal: Panama"));
    }

    public static boolean isPalindrome(String s) {

        int lengthTrim = 0;
        for (int i = 0; i < s.length(); i++){
            char charValid = s.charAt(i);
            if (Character.isDigit(charValid)){
                lengthTrim++;
            } else if (Character.isLetter(charValid) && !Character.isWhitespace(charValid)) {
                lengthTrim++;
            }
        }

        char[] listOne = new char[lengthTrim];
        char[] listTwo = new char[lengthTrim];
        int indexOne = 0;
        int indexTwo = lengthTrim -1;

        for (int i = 0; i < s.length(); i++){
            char charValid = s.toLowerCase().charAt(i);
            if (Character.isDigit(charValid)){
                listOne[indexOne] = charValid;
                listTwo[indexTwo] = charValid;
                indexOne++;
                indexTwo--;
            } else if (Character.isLetter(charValid) && !Character.isWhitespace(charValid)) {
                listOne[indexOne] = charValid;
                listTwo[indexTwo] = charValid;
                indexOne++;
                indexTwo--;
            }
        }

        return Arrays.equals(listTwo,listOne);
    }
}
