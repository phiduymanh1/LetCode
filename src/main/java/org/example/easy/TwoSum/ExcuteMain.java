package org.example.easy.TwoSum;

public class ExcuteMain {
    public static void main(String[] args) {
        twoSum(new int[]{3,2,4},6);
    }
    private static int[] twoSum(int[] nums, int target) {
        int num1 = 0;
        int num2 = 1;
        int countLoop = nums.length * (nums.length - 1) / 2;
        int index = 0;
        for(int i = index; i <= countLoop - 1; i++){
            if(i == nums.length-1){
                index += 1;
                i = index;
            }
            if(index >= nums.length-1) break;
            int res = nums[index] + nums[i+1];
            if(res == target){
                num1 = index;
                num2 = i+1;
                break;
            }
        }
        System.out.println("Num1= "+ num1);
        System.out.println("Num2= "+ num2);

        return new int[]{num1,num2};
    }
}
