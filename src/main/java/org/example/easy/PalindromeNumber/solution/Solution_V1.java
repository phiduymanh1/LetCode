package org.example.easy.PalindromeNumber.solution;

public class Solution_V1 {

    public static void main(String[] args) {
        boolean res = isPalindrome(121);
        System.out.println(res);
    }

    public static boolean isPalindrome(int x) {
        if (x < 0 || (x > 0 && x % 10 == 0)) {
            return false;
        }

        int reverseNumber = 0;
        while (reverseNumber < x ){
            reverseNumber = reverseNumber * 10 + x % 10;
            x /= 10;
        }

        return x == reverseNumber || x == reverseNumber / 10;

    }
}
