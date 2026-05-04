package org.example.phase0_java_platform.group1_array_loop.Ex1;

public class Solution {
    public static void main(String[] args) {
        int result = findNumbers(new int[]{12,345,2,6,7896});
        System.out.println("Result: " + result);
    }

    public static int findNumbers(int[] nums) {

        int res = 0;
        for(int num: nums){
            if ((int)Math.log10(num) % 2 == 1 ){
                res++;
            }
        }
        return res;
    }

    /** version 1*/
    public int findNumbersV1(int[] nums) {

        int res = 0;
        for (int num : nums) {
            int digits = 0;
            while (num > 0) {
                num /= 10;
                digits++;
            }
            if (digits % 2 == 0)
                res++;
        }
        return res;
    }
}
