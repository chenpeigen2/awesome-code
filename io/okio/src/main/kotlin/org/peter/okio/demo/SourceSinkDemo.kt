package org.peter.okio.demo

import okio.Buffer
import okio.Pipe
import okio.Sink
import okio.Source
import okio.buffer

object SourceSinkDemo {

    fun demonstrateSource() {
        println("=== Source 演示 ===")
        
        val buffer = Buffer()
        buffer.writeUtf8("Hello from Source!")
        
        val source: Source = buffer
        val bufferedSource = source.buffer()
        
        println("读取内容: ${bufferedSource.readUtf8()}")
        bufferedSource.close()
    }

    fun demonstrateSink() {
        println("\n=== Sink 演示 ===")
        
        val buffer = Buffer()
        val sink: Sink = buffer
        val bufferedSink = sink.buffer()
        
        bufferedSink.writeUtf8("Hello from Sink!")
        bufferedSink.flush()
        
        println("写入内容: ${buffer.readUtf8()}")
        bufferedSink.close()
    }

    fun demonstratePipe() {
        println("\n=== Pipe 演示 ===")
        
        val pipe = Pipe(1024)
        
        val sinkThread = Thread {
            pipe.sink.buffer().use { sink ->
                sink.writeUtf8("Message through pipe")
            }
        }
        
        val sourceThread = Thread {
            pipe.source.buffer().use { source ->
                println("收到: ${source.readUtf8()}")
            }
        }
        
        sourceThread.start()
        sinkThread.start()
        
        sinkThread.join()
        sourceThread.join()
    }

    fun demonstrateBlackholeSink() {
        println("\n=== BlackholeSink 演示 ===")
        
        val buffer = Buffer()
        buffer.writeUtf8("Data to discard")
        
        val blackhole = Buffer()
        buffer.readAll(blackhole)
        
        println("数据已被丢弃")
        println("原Buffer大小: ${buffer.size}")
    }
}

fun main() {
    SourceSinkDemo.demonstrateSource()
    SourceSinkDemo.demonstrateSink()
    SourceSinkDemo.demonstratePipe()
    SourceSinkDemo.demonstrateBlackholeSink()
}
