#if condition
#then
#    command1
#    command2
#    ...
#    commandN
#fi
#
#if condition
#then
#    command1
#    command2
#    ...
#    commandN
#else
#    command
#fi
#
#if condition1
#then
#    command1
#elif condition2
#then
#    command2
#else
#    commandN
#fi

a=10
b=20
if [ $a == $b ]
then
   echo "a 等于 b"
elif [ $a -gt $b ]
then
   echo "a 大于 b"
elif [ $a -lt $b ]
then
   echo "a 小于 b"
else
   echo "没有符合的条件"
fi

