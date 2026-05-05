package org.example.phase0_java_platform.group1_array_loop.ex2;

import java.util.Arrays;

public class SquaresSortedArray {

    public static void main(String[] args) {
        int[] result = sortedSquares(new int[]{-7,2,3,4,5});
        System.out.println("Result: " + Arrays.toString(result));
    }

    public static int[] sortedSquares(int[] nums) {

        for (int i = 0; i < nums.length; i++){
            nums[i] *= nums[i];
        }
        Arrays.sort(nums);
        return nums;
    }
}
