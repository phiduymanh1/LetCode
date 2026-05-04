package org.example.easy.PalindromeNumber;

/**
 * 9. Palindrome Number
 * Given an integer x, return true if x is a palindrome, and false otherwise.
 * -
 * Example 1:
 * Input: x = 121
 * Output: true
 * Explanation: 121 reads as 121 from left to right and from right to left.
 * -
 * Example 2:
 * Input: x = -121
 * Output: false
 * Explanation: From left to right, it reads -121. From right to left, it becomes 121-. Therefore it is not a palindrome.
 * -
 * Example 3:
 * Input: x = 10
 * Output: false
 * Explanation: Reads 01 from right to left. Therefore it is not a palindrome.
 * -
 * Constraints:
 * -231 <= x <= 231 - 1
 * -
 * Follow up: Could you solve it without converting the integer to a string?
 */
public class ExecuteMain {

    public static void main(String[] args) {
        boolean res = isPalindrome(1212);
        System.out.println(res);
    }

    public static boolean isPalindrome(int x) {

        String xString = String.valueOf(x);
        StringBuilder strRes = new StringBuilder();
        for (int i = 1; i <= xString.length(); i++){
            String lastIndex = xString.substring(xString.length() - i);

            if (!lastIndex.isEmpty() || lastIndex != null) {
                if (i != 1){
                    lastIndex.substring(0,1);
                }
                strRes.append(i != 1 ? lastIndex.substring(0,1) : lastIndex);
            }
        }

        if (strRes.toString().equals(xString)){
            return true;
        }

        return false;
    }

}
