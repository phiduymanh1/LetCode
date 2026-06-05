package org.example.phase0_java_platform.group1_array_loop.ex5;

public class MaximumSubarray extends Thread{

    public static void main(String[] args) {
        int result = maxSubArray(new int[]{5, -2, 10});
        System.out.println("Result: " + result);
    }

    public static int maxSubArray(int[] nums) {
        int max = nums[0];
        int cur = nums[0];

        for (int i = 1; i < nums.length; i++) {
            cur = Math.max(nums[i], cur + nums[i]);

            max = Math.max(max, cur);
        }

        return max;
    }
}
