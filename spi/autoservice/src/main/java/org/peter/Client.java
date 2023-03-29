package org.peter;

import java.util.ServiceLoader;

//https://pdai.tech/md/java/advanced/java-advanced-spi.html
public class Client {
    public static void main(String[] args) {
        ServiceLoader<UserService> serviceLoader = ServiceLoader.load(UserService.class);
        for (UserService userService : serviceLoader) {
            System.out.println(userService.userName());
        }
        System.out.println("============================");
    }
}
