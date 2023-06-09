%%%-------------------------------------------------------------------
%%% @author chenpeigen
%%% @copyright (C) 2023, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 21. 4月 2023 上午10:36
%%%-------------------------------------------------------------------
-module(helloworld).
-version("1.0"). %% Erlang 模块
-import(io, [fwrite/1]).
-author("chenpeigen").

%% API

-export([while/1, while/2, start/0, add/2, add/1, fac/1, covert/2]).

while(L) -> while(L, 0).

%%split here
while([], Acc) -> Acc;
while([_ | T], Acc) ->
  io:fwrite("~w~n", [Acc]),
  while(T, Acc + 1).

%%使用多个参数的函数
add(X, Y) ->
  Z = X + Y,
  io:fwrite("~w~n", [Z]).
%%Erlang函数可以使用零个或多个参数来定义。函数重载也是可以的，可以定义一个相同名称的函数，只要它们具有不同数目的参数。
add(X, Y, Z) ->
  A = X + Y + Z,
  io:fwrite("~w~n", [A]).


%%在 Erlang 中函数也都有保护序列的能力。这些都不算什么它只不过是一个表达式，只有当评估(计算)为 true 时函数才运行。
add(X) when X > 3 ->
  io:fwrite("~w~n", [X]).

%%需要注意的是，这一部分是以分号结束的，这也就表示后面还有 fac 函数的更多内容。
fac(1) ->
  1;
%%与前面不同，这部分是以句号结尾的。这也就是说，后面没有这个函数更多的内容了。
fac(N) ->
  N * fac(N - 1).

%%原子类型
%%原子类型是 Erlang 语言中另一种数据类型。所有原子类型都以小写字母开头 （参见 原子类型）。
%% 例如，charles，centimeter，inch 等。原子类型就是名字而已，没有其它含义。它们与变量不同，变量拥有值，而原子类型没有。
covert(M, inch) ->
  M / 2.54;
covert(M, centimeter) ->
  M * 2.54.

%%the main func
start() ->
%%  在 Erlang，所有的变量都与‘ =’语句绑定。所有变量都需要以大写字母开头。在其他编程语言中，“ =”符号用于赋值，但不适用于 Erlang。如前所述，变量是通过使用‘ =’语句定义的。
  X = 12,
  Y = 23,
%%  匿名函数
  Fn = fun() -> io:fwrite("Anonymous Function \n") end,
  Fn(),
  Result = X + Y,
  io:fwrite(2 =< 3),
  io:fwrite("~w \n", [Result]),
  io:fwrite("we are fucking fine"),
  io:fwrite("Hello, world!\n"),
  io:fwrite("Hello, world!\n"),
  X1 = [1, 2, 3, 4],
  add(5, 6),
  while(X1).