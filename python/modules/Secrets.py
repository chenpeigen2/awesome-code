import string
import secrets

"""

"""

# https://docs.python.org/zh-cn/3/library/secrets.html#module-secrets

alphabet = string.ascii_letters + string.digits

# 生成长度为八个字符的字母数字密码：
password = ''.join(secrets.choice(alphabet) for i in range(8))

print(alphabet)
print(password)

# 生成长度为十个字符的字母数字密码，包含至少一个小写字母，至少一个大写字母以及至少三个数字：
while True:
    password = ''.join(secrets.choice(alphabet) for i in range(10))
    if (any(c.islower() for c in password)
            and any(c.isupper() for c in password)
            and sum(c.isdigit() for c in password) >= 3):
        print(password)
        break

# 生成 XKCD 风格的密码串：
with open('/usr/share/dict/cracklib-small') as f:
    words = [word.strip() for word in f]
    password = ' '.join(secrets.choice(words) for i in range(4))

"""
生成 Token
secrets 模块提供了生成安全 Token 的函数，适用于密码重置、密保 URL 等应用场景。

'token_bytes', 'token_hex', 'token_urlsafe'
"""

# 生成临时密保 URL，包含密码恢复应用的安全 Token：
url = 'https://mydomain.com/reset=' + secrets.token_urlsafe()
print(url)
