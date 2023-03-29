package org.peter;


import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;

import java.util.Properties;
import java.util.concurrent.Executor;

public class SimpleDemoMain {

    public static void main(String[] args) throws NacosException, InterruptedException {

        // some configs
        String serverAddr = "127.0.0.1:8848";
        String dataId = "nacos-simple-demo.yml";
        String group = "DEFAULT_GROUP";

        // 指定nacos服务的地址
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);

        // 配置连接
        ConfigService configService = NacosFactory.createConfigService(properties);

        // 获取配置
        String content = configService.getConfig(dataId, group, 5000);
        System.out.println("content: " + content);


        // 添加监听事件，监听配置有变更
        configService.addListener(dataId, group, new Listener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                System.out.println("recieve:" + configInfo);
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });

//        发布配置
        boolean isPublishOk = configService.publishConfig(dataId, group, "content");
        System.out.println("isPublishOk: " + isPublishOk);

        Thread.sleep(3000);
        content = configService.getConfig(dataId, group, 5000);
        System.out.println("content 1: " + content);

//         移除配置
        boolean isRemoveOk = configService.removeConfig(dataId, group);
        System.out.println(isRemoveOk);
        Thread.sleep(3000);

        content = configService.getConfig(dataId, group, 5000);
        System.out.println("content 2: " + content);
        Thread.sleep(300000);
    }
}
