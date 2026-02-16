package org.peter.okio.demo

import okio.Buffer
import okio.Pipe
import okio.buffer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class SourceSinkDemoTest {

    @Test
    fun testSource() {
        val buffer = Buffer()
        buffer.writeUtf8("Hello from Source!")
        
        val source = buffer
        val bufferedSource = source.buffer()
        
        assertEquals("Hello from Source!", bufferedSource.readUtf8())
        bufferedSource.close()
    }

    @Test
    fun testSink() {
        val buffer = Buffer()
        val sink = buffer
        val bufferedSink = sink.buffer()
        
        bufferedSink.writeUtf8("Hello from Sink!")
        bufferedSink.flush()
        
        assertEquals("Hello from Sink!", buffer.readUtf8())
        bufferedSink.close()
    }

    @Test
    fun testPipe() {
        val pipe = Pipe(1024)
        var received: String? = null
        
        val sinkThread = Thread {
            pipe.sink.buffer().use { sink ->
                sink.writeUtf8("Message through pipe")
            }
        }
        
        val sourceThread = Thread {
            pipe.source.buffer().use { source ->
                received = source.readUtf8()
            }
        }
        
        sourceThread.start()
        sinkThread.start()
        
        sinkThread.join()
        sourceThread.join()
        
        assertEquals("Message through pipe", received)
    }

    @Test
    fun testBlackholeSink() {
        val buffer = Buffer()
        buffer.writeUtf8("Data to discard")
        
        val blackhole = Buffer()
        buffer.readAll(blackhole)
        
        assertEquals(0L, buffer.size)
    }
}
