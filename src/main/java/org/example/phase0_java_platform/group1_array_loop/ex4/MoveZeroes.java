package org.example.phase0_java_platform.group1_array_loop.ex4;

import java.util.Arrays;

public class MoveZeroes {

    public static void main(String[] args) {
        moveZeroes(new int[]{0,1,0,3,12});
    }

    public static void moveZeroes(int[] nums) {
        int lengthNums = nums.length;
        int[] numTemps = new int[lengthNums];
        int index = 0;

        for (int num: nums){
            if (num != 0){
                numTemps[index] = num;
                index++;
            }
        }

        for (int i = 0; i < lengthNums; i++){
            nums[i] = numTemps[i];
        }
        System.out.println(Arrays.toString(nums));
    }
}
