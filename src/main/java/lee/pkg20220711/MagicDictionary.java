package lee.pkg20220711;

//https://leetcode.cn/problems/implement-magic-dictionary/
public class MagicDictionary {
    private String[] dictionary;

    public MagicDictionary() {
    }

    public void buildDict(String[] dic) {
        this.dictionary = dic;
    }

    public boolean search(String searchWord) {
        for (String dictWord : dictionary) {
            if (dictWord.length() == searchWord.length()) {
                int cnt = 0;
                for (int i = 0; i < searchWord.length(); i++) {
                    if (searchWord.charAt(i) != dictWord.charAt(i)) {
                        cnt++;
                    }
                    if (cnt > 1) break;
                }
                if (cnt == 1) return true;
            }
        }
        return false;
    }
}
