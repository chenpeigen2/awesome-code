%%%-------------------------------------------------------------------
%%% @author chenpeigen
%%% @copyright (C) 2023, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 21. 4月 2023 上午10:36
%%%-------------------------------------------------------------------
-module(helloworld).
-import(io, [fwrite/1]).
-author("chenpeigen").

%% API
-export([start/0]).


%%the main func
start() ->
%%  在 Erlang，所有的变量都与‘ =’语句绑定。所有变量都需要以大写字母开头。在其他编程语言中，“ =”符号用于赋值，但不适用于 Erlang。如前所述，变量是通过使用‘ =’语句定义的。
  X = 12,
  Y = 23,
  Result = X + Y,
  io:fwrite(2 =< 3),
  io:fwrite("~w \n", [Result]),
  io:fwrite("we are fucking fine"),
  io:fwrite("Hello, world!\n"),
  io:fwrite("Hello, world!\n").