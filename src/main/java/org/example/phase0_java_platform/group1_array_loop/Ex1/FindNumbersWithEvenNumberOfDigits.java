package org.example.phase0_java_platform.group1_array_loop.Ex1;

public class FindNumbersWithEvenNumberOfDigits {

    public static void main(String[] args) {
        findNumbers(new int[]{12,345,2,6,7896});
    }

    public static int findNumbers(int[] nums) {
        if (nums == null || nums.length > 500 ) return 0;

        int res = 0;
        for(int num: nums){
            if (1> num || num > 100000) continue;
            String numStr = String.valueOf(num);
            if(numStr.length() % 2 == 0){
                res ++;
            }
        }
        System.out.println(res);
        return res;
    }
}
