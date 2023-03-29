a = 5
local b = 5

function joke()
    c = 5
    local d = 6
end

joke()
print(c, d)

do
    local a = 6
    b = 6
    print(a, b)
end

print(a, b)

a = "hello" .. "world" --字符串连接

f = function (x)
    print("function")
    return x * 2
end

for i = 1, f(5) do
    print(i)
end


--[ 变量定义 --]
a = 10
--[ 执行循环 --]
repeat
    print("a的值为:", a)
    a = a + 1
until (a > 15)


--[ 定义变量 --]
a = 10;

--[ 使用 if 语句 --]
if (a < 20)
then
    --[ if 条件为 true 时打印以下信息 --]
    print("a 小于 20");
end
print("a 的值为:", a);


--[ 定义变量 --]
a = 100;
--[ 检查条件 --]
if (a < 20)
then
    --[ if 条件为 true 时执行该语句块 --]
    print("a 小于 20")
else
    --[ if 条件为 false 时执行该语句块 --]
    print("a 大于 20")
end
print("a 的值为 :", a)


--[ 定义变量 --]
a = 100

--[ 检查布尔条件 --]
if (a == 10)
then
    --[ 如果条件为 true 打印以下信息 --]
    print("a 的值为 10")
elseif (a == 20)
then
    --[ if else if 条件为 true 时打印以下信息 --]
    print("a 的值为 20")
elseif (a == 30)
then
    --[ if else if condition 条件为 true 时打印以下信息 --]
    print("a 的值为 30")
else
    --[ 以上条件语句没有一个为 true 时打印以下信息 --]
    print("没有匹配 a 的值")
end
print("a 的真实值为: ", a)


module = {}

-- 定义一个常量
module["constant"] = "这是一个常量"
module.ccc = "这是一个常量"