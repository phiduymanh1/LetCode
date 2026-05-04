package org.example.phase0_java_platform.group1_array_loop.Ex2;

import java.util.Arrays;

public class Solution {

    public static void main(String[] args) {
        int[] result = sortedSquares(new int[]{-7,2,3,4,5});
        System.out.println("Result: " + Arrays.toString(result));
    }

    public static int[] sortedSquares(int[] nums) {
        int length = nums.length;
        int[] result = new int[length];

        int left = 0;
        int right = length - 1;
        int index = length - 1;

        while (left <= right) {
            int leftSquare = nums[left] * nums[left];
            int rightSquare = nums[right] * nums[right];

            if (leftSquare > rightSquare){
                result[index] = leftSquare;
                left ++;
            }else {
                result[index] = rightSquare;
                right --;
            }
            index --;
        }

        return result;
    }
}
