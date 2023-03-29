package lee.pkg20210915;

public class Solution {
    public int findPeakElement(int[] nums) {
        int min = Integer.MIN_VALUE;
        int tmp = nums[0];
        int index = 0;

        for (int i = 1; i < nums.length; i++) {
            if (nums[i] >= tmp) {
                tmp = nums[i];
                index = i;
            } else {
                return index;
            }
        }

        return index;
    }


    //二分，按上坡缩小左边界
//    public int findPeakElement(int[] nums)
//    {
//        int left=0;
//        int right=nums.length-1;
//        while(left<right)
//        {
//            int mid=(right+left)/2;
//            if(nums[mid]<=nums[mid+1])
//            {
//                left=mid+1;
//            }
//            else
//            {
//                right=mid;
//            }
//        }
//        return left;
//    }

    public static void main(String[] args) {
        var app = new Solution();
        var result = app.findPeakElement(new int[]{1, 2, 3,1});
        System.out.println(result);
    }
}
