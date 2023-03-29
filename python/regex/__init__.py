import re
import string

# https://docs.python.org/zh-cn/3/library/re.html

# https://www.runoob.com/python/python-reg-expressions.html


# . ^ $
# random start tail

# ? * +
# [0,1] >=0 >=1

""""
?
对它前面的正则式匹配0到1次重复。 ab? 会匹配 'a' 或者 'ab'。


"""

# *?, +?, ?? what is this ???

# (...) （组合），匹配括号内的任意正则表达式，并标识出组合的开始和结尾
# lots of things

# [...]
# 用来表示一组字符,单独列出：[amk] 匹配 'a'，'m'或'k'

# { .. }
# {m} {m,n} {m,n}?

# | \


# 由 '\' 和一个字符组成的特殊序列在以下列出。
# \w 只匹配 [a-zA-Z0-9_]
# \W 这就等价于 [^a-zA-Z0-9_]
# \d 匹配任意数字，等价于 [0-9].
# \D 匹配任意非数字
# \s 匹配任何Unicode空白字符（包括 [ \t\n\r\f\v] ，还有很多其他字符，比如不同语言排版规则约定的不换行空格）。如果 ASCII 被设置，就只匹配 [ \t\n\r\f\v] 。
# \S 匹配任何非空白字符。就是 \s 取非。如果设置了 ASCII 标志，就相当于 [^ \t\n\r\f\v] 。

# \A Matches only at the start of the string.
# \Z Matches only at the end of the string.


# re.match 尝试从字符串的起始位置匹配一个模式，如果不是起始位置匹配成功的话，match() 就返回 none。
# re.match只匹配字符串的开始，如果字符串开始不符合正则表达式，则匹配失败，函数返回None；而re.search匹配整个字符串，直到找到一个匹配。
# 如果 string 的 开始位置 能够找到这个正则样式的任意个匹配，就返回一个相应的 匹配对象。如果不匹配，就返回 None ；注意它与零长度匹配是不同的。
print(re.match('www', 'www.baidu.com'))
print(re.match('com', 'www.baidu.com'))
print("=====================================")
re.purge()
pattern = re.compile("o[gh]")
print(pattern.fullmatch("dog"))
print(pattern.fullmatch("ogre"))
print(pattern.match("ogre"))
print(pattern.fullmatch("doggie", 1, 3))

# re.search 扫描整个字符串并返回第一个成功的匹配。
print(re.search('www', 'www.runoob.com').span())  # 在起始位置匹配
print(re.search('com', 'www.runoob.com').span())  # 不在起始位置匹配

# re.sub Python 的 re 模块提供了re.sub用于替换字符串中的匹配项。

phone = '2004-959-559'  # 这是一个国外电话号码"
# 删除字符串中的 Python注释
num = re.sub(r'#.*$', "", phone)
print("电话号码是: ", num)

# 删除非数字(-)的字符串
# \D       Matches any non-digit character; equivalent to [^\d].
num = re.sub(r'\D', "", phone)
print("电话号码是1 : ", num)
# 行为与 sub() 相同，但是返回一个元组 (字符串, 替换次数).
num = re.subn(r'\D', "", phone)
print("电话号码是1 : ", num)

# compile 函数用于编译正则表达式，生成一个正则表达式（ Pattern ）对象，供 match() 和 search() 这两个函数使用。
pattern = re.compile(r'\d+')
m = pattern.match('one12twothree34four')
print(m)
m = pattern.match('one12twothree34four', 2, 10)
print(m)
m = pattern.match('one12twothree34four', 3, 10)
print(m)

# findall 在字符串中找到正则表达式所匹配的所有子串，并返回一个列表，如果有多个匹配模式，则返回元组列表，如果没有找到匹配的，则返回空列表。
pattern = re.compile(r'\d+')
print(type(pattern), "==================")
result1 = pattern.findall('runoob 123 google 456')
result2 = pattern.findall('run88oob123google456', 0, 10)
print(result1)
print(result2)

# 多个匹配模式，返回元组列表：
result = re.findall(r'(\w+)=(\d+)', 'set width=20 and height=10')
print(result)

# re.finditer 和 findall 类似，在字符串中找到正则表达式所匹配的所有子串，并把它们作为一个迭代器返回。
it = re.finditer(r"(\d+)", "12a32bc43jf3")
for match in it:
    print(match.group())

# re.split  split 方法按照能够匹配的子串将字符串分割后返回列表，它的使用形式如下：
print(re.split(r'\W+', 'runoob, runoob, runoob.'))
print(re.findall(r'\W', 'runoob,  runoob, runoob.'))
# print(re.split(r'\W*', 'runoob, runoob, runoob.'))

print(re.split('a*', 'hello world'))

# re.escape(pattern)
# 转义 pattern 中的特殊字符。如果你想对任意可能包含正则表达式元字符的文本字符串进行匹配，它就是有用的。比如
print(re.escape('https://www.python.org'))
legal_chars = string.ascii_lowercase + string.digits + "!#$%&'*+-.^_`|~:"
print('[%s]+' % re.escape(legal_chars))

digits_re = r'\d+'
sample = '/usr/sbin/sendmail - 0 errors, 12 warnings'
print(re.sub(digits_re, digits_re.replace('\\', r'\\'), sample))

# 清除正则表达式的缓存。
re.purge()

print(re.search("(^hekk).*", "hekkdfff"))
