//async function name([param[, param[, ... param]]]) {
//    statements
//}
//参数
//name
//函数名称。
//param
//要传递给函数的参数的名称。
//statements
//包含函数主体的表达式。可以使用await机制。
//返回值
//一个Promise，这个promise要么会通过一个由async函数返回的值被解决，要么会通过一个从async函数中抛出的（或其中没有被捕获到的）异常被拒绝。


//async函数可能包含0个或者多个await表达式。await表达式会暂停整个async函数的执行进程并出让其控制权，
//只有当其等待的基于promise的异步操作被兑现或被拒绝之后才会恢复进程。
//promise的解决值会被当作该await表达式的返回值。使用async / await关键字就可以在异步代码中使用普通的try / catch代码块。


//连续执行两个或者多个异步操作是一个常见的需求，
//在上一个操作执行成功之后，开始下一个的操作，并带着上一步操作所返回的结果。我们可以通过创造一个 Promise 链来实现这种需求。

function resolveAfter2Seconds() {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve('resolved func');
    }, 2000);
  });
}

async function asyncCall() {
  console.log('calling');
  const result = await resolveAfter2Seconds();
  console.log(result);
  // expected output: "resolved"
}

asyncCall();