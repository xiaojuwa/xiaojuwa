package com.xiaojuwa.xuexi.array;
//数组的进阶用法
public class Test04ShuZhu04 {
    public static void main(String[] args) {
        int[] arrays = {1,2,3,4,5,6};

        //for-each循环
        for (int array : arrays) {  //增强for循环 jdk1.5以上使用  没有下标
            System.out.println("array = " + array);
        }
        System.out.println("-------------");
        //数组作为方法入参
        printArray(arrays);
        System.out.println("-------------");

        //数组作为返回值
        int[] re = fanzhuan(arrays);
        printArray(re);

    }
    //打印数组元素方法
    public static void printArray(int[] arrays){
        for (int i = 0; i < arrays.length; i++) {
            System.out.println("arrays[i] = " + arrays[i]);
        }
    }

    //反转数组方法
    public static int[] fanzhuan(int[] arrays){
        int[] re = new int[arrays.length];
        //反转操作
        for (int i = 0,j = re.length-1; i < arrays.length; i++,j--) {
            re[j]=arrays[i];
        }
        return re;
    }
}
