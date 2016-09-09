package objects.emulator

import io.netty.buffer.Unpooled
import org.asynchttpclient.Response
import org.asynchttpclient.netty.EagerResponseBodyPart
import org.asynchttpclient.netty.NettyResponse
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class ListenableFutureEmulator(bytes: ByteArray) : Future<Response> {

    val response = NettyResponse(null, null, listOf(EagerResponseBodyPart(Unpooled.copiedBuffer(bytes), true)))

    override fun cancel(p0: Boolean): Boolean {
        return false
    }

    override fun get(): Response {
        return response
    }

    override fun get(p0: Long, p1: TimeUnit): Response {
        return response
    }

    override fun isDone(): Boolean {
        return true
    }

    override fun isCancelled(): Boolean {
        return false
    }
}