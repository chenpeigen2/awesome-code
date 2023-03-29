mytables = { "apple", "orange", "banana" }
print(collectgarbage("count"))
mytables = nil

print(collectgarbage("count"))

print(collectgarbage("collect"))

print(collectgarbage("count"))

html = [[
<html>
<head></head>
<body>
    <a href="http://www.runoob.com/">菜鸟教程</a>
</body>
</html>
]]
print(html)


-- 创建一个空的 table
local tbl1 = {}

-- 直接初始表
local tbl2 = { "apple", "pear", "orange", "grape" }

--不同于其他语言的数组把 0 作为数组的初始索引，在 Lua 里表的默认初始索引一般以 1 开始。



-- functions

function factorial1(n)
    if n == 0 then
        return 1
    else
        return n * factorial1(n - 1)
    end

end

print(factorial1(5))


-- function_test2.lua 脚本文件
function testFun(tab, fun)
    for k, v in pairs(tab) do
        print(fun(k, v));
    end
end

tab = { key1 = "val1", key2 = "val2" };
testFun(tab,
        function(key, val)
            --匿名函数
            return key .. "=" .. val;
        end
);