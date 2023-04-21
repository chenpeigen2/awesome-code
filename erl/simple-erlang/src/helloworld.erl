%%%-------------------------------------------------------------------
%%% @author chenpeigen
%%% @copyright (C) 2023, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 21. 4月 2023 上午10:36
%%%-------------------------------------------------------------------
-module(helloworld).
-author("chenpeigen").

%% API
-export([start/0]).

start() ->
  io:fwrite("Hello, world!\n").