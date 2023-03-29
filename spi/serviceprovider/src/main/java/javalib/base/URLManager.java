package javalib.base;

import java.net.URLStreamHandler;
import java.net.spi.URLStreamHandlerProvider;
import java.util.List;
import java.util.ServiceLoader;

import static java.util.stream.Collectors.toList;

public class URLManager extends URLStreamHandlerProvider {


    public void hello() {
        System.out.println(1);
    }

    public static void main(String[] args) {
        List<URLStreamHandlerProvider> moduleServices = ServiceLoader
                .load(URLStreamHandlerProvider.class).stream()
                .map(ServiceLoader.Provider::get)
                .collect(toList());
        URLManager urlManager = (URLManager) moduleServices.get(0);
        urlManager.hello();
        System.out.println(moduleServices.get(0).toString());
    }


    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        return null;
    }
}
