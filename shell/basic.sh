#!/bin/bash
echo "Hello World !"

your_name="qinjx"
echo $your_name
echo ${your_name}

for skill in Ada Coffe Action Java; do
    echo "I am good at ${skill}Script"
done


myUrl="https://www.google.com"
#readonly myUrl
myUrl="https://www.runoob.com"


myUrl="https://www.runoob.com"
unset myUrl
echo $myUrl

#1) 局部变量 局部变量在脚本或命令中定义，仅在当前shell实例中有效，其他shell启动的程序不能访问局部变量。
#2) 环境变量 所有的程序，包括shell启动的程序，都能访问环境变量，有些程序需要环境变量来保证其正常运行。必要的时候shell脚本也可以定义环境变量。
#3) shell变量 shell变量是由shell程序设置的特殊变量。shell变量中有一部分是环境变量，有一部分是局部变量，这些变量保证了shell的正常运行


your_name="runoob"
str="Hello, I know you are \"$your_name\"! \n"
echo -e $str


your_name="runoob"
# 使用双引号拼接
greeting="hello, "$your_name" !"
greeting_1="hello, ${your_name} !"
echo $greeting  $greeting_1
# 使用单引号拼接
greeting_2='hello, '$your_name' !'
greeting_3='hello, ${your_name} !'
echo $greeting_2  $greeting_3

string="abcd"
echo ${#string} #输出 4

string="runoob is a great site"
echo ${string:1:4} # 输出 unoo

string="runoob is a great site"
echo `expr index "$string" io`  # 输出 4
