package lee.pkg20220629;

import java.util.HashMap;
import java.util.Map;

public class Codec {
    //    https://leetcode.cn/problems/encode-and-decode-tinyurl/
    private Map<Integer, String> dataBase = new HashMap<>();

    private int id;

    // Encodes a URL to a shortened URL.
    public String encode(String longUrl) {
        id++;
        dataBase.put(id, longUrl);
        return "http://tinyurl.com/" + id;
    }

    // Decodes a shortened URL to its original URL.
    public String decode(String shortUrl) {
        int p = shortUrl.lastIndexOf('/') + 1;
        int key = Integer.parseInt(shortUrl.substring(p));
        return dataBase.get(key);
    }
}