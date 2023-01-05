package com.xiaojuwa.xuexi.array;
//二维数组（数组嵌套）
public class Test05ErWeiShuZhu0 {
    public static void main(String[] args) {
        int[][] arrays = {{1,2},{2,2},{3,4},{4,5}};
        //[4]行[2]列
        /*
        * 1,2   arrays[0]
        * 2,2   arrays[1]
        * 3,4   arrays[2]
        * 4,5   arrays[3]
        * */

        //尝试打印arrays[0]（会发现输出的内容看不懂 是一个数组）
        System.out.println("arrays = " + arrays[0]);
        //尝试打印数组元素方法打印出来arrays[0]
        printArray(arrays[0]);
        //尝试打印arrays[0][0]和arrays[0][1]元素
        System.out.println("arrays[0][0] = " + arrays[0][0]);
        System.out.println("arrays[0][1] = " + arrays[0][1]);

        //尝试打印arrays[2][1]
        System.out.println("arrays[2][1] = " + arrays[2][1]);
    }

    //打印数组元素方法
    public static void printArray(int[] arrays){
        for (int i = 0; i < arrays.length; i++) {
            System.out.println("arrays[i] = " + arrays[i]);
        }
    }
}
