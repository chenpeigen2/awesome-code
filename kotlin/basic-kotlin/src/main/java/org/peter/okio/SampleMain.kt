package org.peter.okio

import java.io.File
import okio.*
import java.io.InterruptedIOException
import java.util.concurrent.TimeUnit


//https://square.github.io/okio/3.x/okio/okio/okio/
object SampleMain {

    fun writeFile() {
        val file = File("data.txt")
        file.sink().buffer().use { sink -> // .use()自动关闭资源
            sink.writeUtf8("Hello, Okio!\n")
            sink.write("Binary data: ".toByteArray())
            sink.write(byteArrayOf(0x48, 0x69)) // Hex: H, i
        }
    }

    fun readFile() {
        val file = File("data.txt")
        file.source().buffer().use { source ->
            val content = source.readUtf8() // 全部读取
            println("Content: $content")

            // 分块读取示例
            source.request(Long.MAX_VALUE) // 重置读取位置
            while (!source.exhausted()) {
                val chunk = source.readByteString(4)
                println("Chunk: $chunk")
            }
        }
    }

    fun bufferDemo() {
        val buffer = Buffer()
        buffer.writeUtf8("Dynamic ") // 写入数据
        buffer.writeInt(123) // 写入Int(4字节)

        // 读取操作
        println("String: ${buffer.readUtf8()}") // 输出: Dynamic
        println("Int: ${buffer.readInt()}")     // 输出: 123
    }

    fun timeoutDemo() {
        File("test.txt").source().use { source ->
            source.timeout().deadline(5, TimeUnit.SECONDS) // 5秒超时
            try {
                println(source)
            } catch (e: InterruptedIOException) {
                println("Read timed out!")
            }
        }
    }

    fun gzipDemo() {
        // 压缩写入
        File("data.gz").sink().gzip().buffer().use { sink ->
            sink.writeUtf8("Compressed text")
        }

        // 解压读取
        File("data.gz").source().gzip().buffer().use { source ->
            println(source.readUtf8()) // 输出: Compressed text
        }
    }
}


fun main() {
    SampleMain.writeFile()
    SampleMain.readFile()
    SampleMain.bufferDemo()
    SampleMain.gzipDemo()
}