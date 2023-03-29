import io
import os

# 读取键盘输入
# Python提供了个内置函数从标准输入读入一行文本，默认的标准输入是键盘。如下：
# input

# 打开和关闭文件 open 函数

# https://www.runoob.com/python/python-files-io.html
# https://www.runoob.com/python/file-methods.html

if __name__ == '__main__':
    str = input("please input: ")
    print(str)
    with open(file="../hello.txt", mode="w+") as f:
        print(type(f))
        print(f.name)
        print(f.closed)
        f.write("h")
        print(f.tell())  # tell()方法告诉你文件内的当前位置, 换句话说，下一次的读写会发生在文件开头这么多字节之后。

        print(f.mode)
    # 重命名和删除文件
    os.rename("../hello.txt", "../hello1.txt")
    os.remove("../hello1.txt")
    # os模块有许多方法能帮你创建，删除和更改目录。
    # 创建目录test
    os.mkdir("test")
    # os.chdir("newdir")
    # 显示当前的工作目录。
    os.getcwd()
    # rmdir()方法删除目录，目录名称以参数传递。
    os.rmdir('test')
