package client

import CodedInputStream
import UploadResult
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.HttpContent

class ClientHandler : SimpleChannelInboundHandler<Any>() {

    object requestResult {
        var code: Int = -1
    }

    var contentBytes: ByteArray = ByteArray(0)

    override fun channelReadComplete(ctx: ChannelHandlerContext) {

        if (contentBytes.size == 0) {
            ctx.close()
            return
        }

        val resultCode: Int
        val uploadResult = UploadResult.BuilderUploadResult(0).build()
        uploadResult.mergeFrom(CodedInputStream(contentBytes))
        resultCode = uploadResult.resultCode
        synchronized(requestResult, {
            requestResult.code = resultCode
        })
        ctx.close()
    }

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: Any?) {
        if (msg is HttpContent) {
            val contentsBytes = msg.content()
            contentBytes = ByteArray(contentsBytes.capacity())
            contentsBytes.readBytes(contentBytes)
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable) {
        cause.printStackTrace()
        ctx?.close()
    }
}