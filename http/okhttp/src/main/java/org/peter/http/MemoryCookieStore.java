package org.peter.http;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryCookieStore implements CookieStore {

    private static final String HOST_NAME_PREFIX = "host_";
    private static final String COOKIE_NAME_PREFIX = "cookie_";

    private final HashMap<String, ConcurrentHashMap<String, Cookie>> cookies;

    public MemoryCookieStore() {
        this.cookies = new HashMap<>();
    }

    @Override
    public void add(HttpUrl httpUrl, Cookie cookie) {
        // 原来这里有一句 `if (!cookie.persistent()) return;`，是个真 bug：
        // 会话 cookie（没有 Max-Age / Expires）天生就是非持久化的，而登录 session
        // 恰恰几乎都是会话 cookie —— 于是所有登录态被静默丢弃，请求永远不带 Cookie。
        // 过期判断交给 isCookieExpired：OkHttp 把会话 cookie 的 expiresAt 设成
        // Long.MAX_VALUE，所以它不会被误删。
        if (isCookieExpired(cookie)) {
            return;
        }

        String name = this.cookieName(cookie);
        String hostKey = this.hostName(httpUrl);

        this.cookies.computeIfAbsent(hostKey, key -> new ConcurrentHashMap<>()).put(name, cookie);
    }

    @Override
    public void add(HttpUrl httpUrl, List<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            if (isCookieExpired(cookie)) {
                continue;
            }
            this.add(httpUrl, cookie);
        }
    }

    @Override
    public List<Cookie> get(HttpUrl httpUrl) {
        return this.get(this.hostName(httpUrl));
    }

    @Override
    public List<Cookie> getCookies() {
        ArrayList<Cookie> result = new ArrayList<>();

        for (String hostKey : this.cookies.keySet()) {
            result.addAll(this.get(hostKey));
        }

        return result;
    }

    /**
     * 获取cookie集合
     */
    private List<Cookie> get(String hostKey) {
        ArrayList<Cookie> result = new ArrayList<>();

        if (this.cookies.containsKey(hostKey)) {
            Collection<Cookie> cookies = this.cookies.get(hostKey).values();
            for (Cookie cookie : cookies) {
                if (isCookieExpired(cookie)) {
                    this.remove(hostKey, cookie);
                } else {
                    result.add(cookie);
                }
            }
        }
        return result;
    }

    @Override
    public boolean remove(HttpUrl httpUrl, Cookie cookie) {
        return this.remove(this.hostName(httpUrl), cookie);
    }

    /**
     * 从缓存中移除cookie
     */
    private boolean remove(String hostKey, Cookie cookie) {
        String name = this.cookieName(cookie);
        if (this.cookies.containsKey(hostKey) && this.cookies.get(hostKey).containsKey(name)) {
            // 从内存中移除httpUrl对应的cookie
            this.cookies.get(hostKey).remove(name);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll() {
        this.cookies.clear();
        return true;
    }

    /**
     * 判断cookie是否失效
     */
    private boolean isCookieExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }

    private String hostName(HttpUrl httpUrl) {
        // 原来的实现是 host().startsWith(HOST_NAME_PREFIX) ? host() : PREFIX + host()，
        // 遇到真的以 "host_" 开头的域名就会跳过加前缀，和其它主机行为不一致。
        // 直接统一加前缀即可。
        return HOST_NAME_PREFIX + httpUrl.host();
    }

    private String cookieName(Cookie cookie) {
        return cookie == null ? null : cookie.name() + cookie.domain();
    }

    @Override
    public String toString() {
        return com.google.common.base.MoreObjects.toStringHelper(this)
                .add("cookies", cookies)
                .toString();
    }
}