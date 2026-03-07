// IRemoteService.aidl
package com.peter.components.demo.service;

/**
 * 远程服务接口 (AIDL)
 *
 * 知识点：
 * 1. AIDL 文件定义跨进程通信接口
 * 2. 支持基本类型：int, long, boolean, float, double, String
 * 3. 支持 List, Map（需要元素也是 AIDL 支持的类型）
 * 4. 支持自定义 Parcelable 对象（需要另建.aidl声明）
 *
 * 注意：
 * - 方法同步执行，不要在主线程调用远程方法
 * - 默认不支持 null 参数
 */
interface IRemoteService {
    /**
     * 获取随机数
     * @return 0-100 之间的随机整数
     */
    int getRandomNumber();

    /**
     * 获取服务运行时长（秒）
     * @return 服务启动后经过的秒数
     */
    long getUptimeSeconds();

    /**
     * 检查服务是否运行中
     * @return true 表示服务正在运行
     */
    boolean isRunning();

    /**
     * 发送消息给服务
     * @param message 消息内容
     * @return 服务端的响应消息
     */
    String sendMessage(String message);

    /**
     * 演示基本类型参数
     * AIDL 要求必须声明此方法（即使不使用）
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean,
                    float aFloat, double aDouble, String aString);
}
