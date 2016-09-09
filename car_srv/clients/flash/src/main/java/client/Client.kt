package client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.HttpRequest
import java.net.ConnectException

class Client constructor(val host: String, val port: Int) {

    fun sendRequest(request: HttpRequest) {
        val group = NioEventLoopGroup(1)
        try {
            val bootstrap = Bootstrap()
            bootstrap.group(group).channel(NioSocketChannel().javaClass).handler(ClientInitializer())
            val channelFuture = bootstrap.connect(host, port).sync()
            val channel = channelFuture.channel()
            channel.writeAndFlush(request)
            channel.closeFuture().sync()
        } catch (e: InterruptedException) {
            ClientHandler.requestResult.code = 2
        } catch (e: ConnectException) {
            ClientHandler.requestResult.code = 1
        } finally {
            group.shutdownGracefully()
        }
    }
}