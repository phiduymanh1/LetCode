package org.example.phase0_java_platform.group1_array_loop.ex3;

import java.util.Arrays;

public class RemoveDuplicatesSortedArray {

    public static void main(String[] args) {
        int result = removeDuplicates(new int[]{2,2,3,4,5});
        System.out.println("Result: " + result);
    }

    public static int removeDuplicates(int[] nums) {
        int index = 0;

        for (int i = 0; i < nums.length; i++){
            if (nums[i] > nums[index]){

                index ++;
                nums[index] = nums[i];
            }
        }
        System.out.println(Arrays.toString(nums));
        return index + 1;
    }
}
