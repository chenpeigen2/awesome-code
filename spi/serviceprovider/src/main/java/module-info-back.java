/*
module serviceprovider {
    uses java.net.spi.URLStreamHandlerProvider;
    uses com.corn.javalib.IMyServiceProvider;
    provides java.net.spi.URLStreamHandlerProvider with com.corn.javalib.base.URLManager;
    provides com.corn.javalib.IMyServiceProvider with com.corn.javalib.MyServiceProviderImpl1;
//    requires
//    requires
}
*/





//        exports:导出，可供其他模块使用
//        requires：引入，可使用引入的模块

//        opens：可开放资源文件夹

//        provides AImpl with AI；构建跨模块的接口于接口实现的联系（比如ServiceLoader.load可用）
//        uses：与provides AImpl with AI对应，uses AI
//        AI：A接口
//        AImpl：A接口实现
//        ————————————————
//        版权声明：本文为CSDN博主「风铃峰顶」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//        原文链接：https://blog.csdn.net/haoranhaoshi/article/details/83658173