// IMyAidlInterface.aidl
package com.example.aidl_common;

/**
 * AIDL 接口定义
 * 服务端和客户端共享此接口
 */
interface IMyAidlInterface {
    /**
     * 加法运算
     * @param a 第一个操作数
     * @param b 第二个操作数
     * @return 两数之和
     */
    int add(int a, int b);
    
    /**
     * 减法运算
     * @param a 被减数
     * @param b 减数
     * @return 差值
     */
    int subtract(int a, int b);
    
    /**
     * 乘法运算
     * @param a 第一个操作数
     * @param b 第二个操作数
     * @return 乘积
     */
    int multiply(int a, int b);
    
    /**
     * 除法运算
     * @param a 被除数
     * @param b 除数
     * @return 商（整数）
     */
    int divide(int a, int b);
}
