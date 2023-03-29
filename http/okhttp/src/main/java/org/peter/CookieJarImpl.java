package org.peter;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

/**
 * CookieJarImpl
 *
 * @author linzhiyong
 * @email wflinzhiyong@163.com
 * @blog https://blog.csdn.net/u012527802
 * @time 2018/7/20
 * @desc
 */
public class CookieJarImpl implements CookieJar {

    private static boolean DEBUG = true;

    private CookieStore cookieStore;

    public CookieJarImpl(CookieStore cookieStore) {
        if (cookieStore == null) {
            throw new IllegalArgumentException("cookieStore can not be null.");
        }
        this.cookieStore = cookieStore;
    }

    @Override
    public synchronized void saveFromResponse(@NotNull HttpUrl url, @NotNull List<Cookie> cookies) {
        if (DEBUG) {
            System.out.println("save in url:" + url);
            cookies.forEach(System.out::println);
        }
        this.cookieStore.add(url, cookies);
    }

    @NotNull
    @Override
    public synchronized List<Cookie> loadForRequest(@NotNull HttpUrl url) {
        if (DEBUG) {
            System.out.println("load in url:" + url);
            this.cookieStore.get(url).forEach(cookie -> System.out.println(cookie.toString()));
        }
        return this.cookieStore.get(url);
    }

    public CookieStore getCookieStore() {
        return this.cookieStore;
    }
}