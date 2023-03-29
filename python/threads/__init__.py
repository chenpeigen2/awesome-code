import threading
import time


# https://blog.csdn.net/u010701274/article/details/122559912


# __all__ = ['get_ident', 'active_count', 'Condition', 'current_thread',
#            'enumerate', 'main_thread', 'TIMEOUT_MAX',
#            'Event', 'Lock', 'RLock', 'Semaphore', 'BoundedSemaphore', 'Thread',
#            'Barrier', 'BrokenBarrierError', 'Timer', 'ThreadError',
#            'setprofile', 'settrace', 'local', 'stack_size',
#            'excepthook', 'ExceptHookArgs']


def thread():
    time.sleep(2)
    print('---子线程结束---')


def test():
    for i in range(5):
        print('test', i)
        print(threading.current_thread())
        time.sleep(1)


def main():
    t1 = threading.Thread(target=thread)
    t1.setDaemon(daemonic=False)  # daemonic=true means daemon
    t1.start()
    t2 = threading.Thread(name='testThread', target=test)
    t2.start()
    t2.join(12)  # 调用一个 Thread 的 join() 方法，可以阻塞自身所在的线程。

    print(t1.is_alive())
    print('---主线程---结束')


if __name__ == '__main__':
    main()
