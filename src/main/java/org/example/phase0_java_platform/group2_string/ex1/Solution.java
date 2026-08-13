package org.example.phase0_java_platform.group2_string.ex1;

public class Solution {

    public boolean isPalindrome(String s) {
        int left = 0;
        int right = s.length() - 1;

        while (left < right) {
            // Bỏ qua ký tự không phải chữ/số bên trái
            while (left < right && !Character.isLetterOrDigit(s.charAt(left))) {
                left++;
            }
            // Bỏ qua ký tự không phải chữ/số bên phải
            while (left < right && !Character.isLetterOrDigit(s.charAt(right))) {
                right--;
            }

            // So sánh 2 ký tự sau khi đưa về chữ thường
            if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                return false;
            }

            left++;
            right--;
        }

        return true;
    }
}
