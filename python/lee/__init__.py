from random import random
from typing import List
import random


class TreeNode:
    def __init__(self, x):
        self.val = x
        self.left = None
        self.right = None


class Solution(object):
    # https://leetcode-cn.com/problems/count-numbers-with-unique-digits/
    def countNumbersWithUniqueDigits(self, n):
        """
        :type n: int
        :rtype: int
        """
        # if n == 0:
        #     return 1
        # ans = 10
        # last = 9
        # for i in range(2, n + 1):
        #     cur = last * (10 - i + 1)
        #     ans += cur
        #     last = cur
        # return ans

        dp = [0 for i in range(9)]
        dp[0], dp[1] = 1, 10
        for i in range(2, n + 1):
            dp[i] = dp[i - 1] + (dp[i - 1] - dp[i - 2]) * (10 - (i - 1))
        return dp[n]

    # https://leetcode-cn.com/problems/number-of-lines-to-write-string/
    def numberOfLines(self, widths: List[int], s: str) -> List[int]:
        lines = 1
        width = 0
        for ch in s:
            needs = width[ord(ch) - ord('a')]
            if needs + width > 100:
                lines += 1
                width = needs
            else:
                width += needs

        return [lines, width]

    ans = []

    def lexicalOrder(self, n: int) -> List[int]:
        for i in range(1, 10):
            self.dfs(i, n)
        return self.ans

    def dfs(self, idx: int, limit: int):
        if idx > limit:
            return
        self.ans.append(idx)
        for i in range(10):
            self.dfs(10 * idx + i, limit)

    # public List<Integer> lexicalOrder(int n) {
    #     for (int i = 1; i <= 9; i++) dfs(i, n);
    #     return ans;
    # }
    #
    # void dfs(int cur, int limit) {
    #     if (cur > limit) return;
    #     ans.add(cur);
    #     for (int i = 0; i <= 9; i++)
    #         dfs(cur * 10 + i, limit);
    # }


# https://leetcode-cn.com/problems/insert-delete-getrandom-o1/
class RandomizedSet:

    def __init__(self):
        self.nums = []
        self.m = {}

    def insert(self, val: int) -> bool:
        if val in self.m.keys():
            return False
        self.m[val] = len(self.nums)
        self.nums.append(val)
        return True

    def remove(self, val: int) -> bool:
        if val not in self.m.keys():
            return False
        loc = self.m[val]
        self.nums[loc] = self.nums[-1]
        self.m[self.nums[-1]] = loc

        self.nums.pop()
        del self.m[val]

        return True

    def getRandom(self) -> int:
        return random.choice(self.nums)

    def findTheWinner(self, n: int, k: int) -> int:
        if n <= 1:
            return n
        ans = (self.findTheWinner(n - 1, k) + k) % n
        if ans == 0:
            return k
        else:
            return ans

    def numSubarrayProductLessThanK(self, nums: List[int], k: int) -> int:
        ret = 0
        prod = 1
        i = 0
        for j, value in enumerate(nums):
            prod *= value
            while i <= j and prod >= k:
                prod /= nums[i]
                i += 1
            ret += (j - i + 1)
        return ret

    # https://leetcode.cn/problems/di-string-match/
    def diStringMatch(self, s: str) -> List[int]:
        l, r = 0, len(s)
        ans = []
        for s1 in s:
            if s1 == "I":
                ans.append(l)
                l += 1
            else:
                ans.append(r)
                r -= 1
        ans.append(l)
        return ans

    def serialize(self, root: TreeNode) -> str:
        """Encodes a tree to a single string.
        """
        if root is None:
            return "#"
        return str(root.val) + "," + self.serialize(root.left) + "," + self.serialize(root.right)

    def deserialize(self, data: str) -> TreeNode:
        """Decodes your encoded data to tree.
        """
        return self.deserialize1(data.split(","))

    def deserialize1(self, li: List) -> TreeNode:
        if li[0] is "#":
            li.remove(li[0])
            return None
        n = TreeNode()
        n.val = li[0]
        li.remove(li[0])
        n.left = self.deserialize1(li)
        n.right = self.deserialize1(li)
        return n


if __name__ == '__main__':
    soul = Solution()
    soul.countNumbersWithUniqueDigits(7)

    a = "abc"
    help(a)
    for s in a:
        print(s)
