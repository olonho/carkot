import client.Client
import com.martiansoftware.jsap.JSAP
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.*
import java.io.FileInputStream
import java.io.IOException

val configFileName = "./config.cfg"

fun main(args: Array<String>) {
    val jsap: JSAP = JSAP()
    setOptions(jsap)

    val clArgsConfig = jsap.parse(args)
    if (!clArgsConfig.success() || clArgsConfig.getBoolean("help")) {
        println(jsap.help)
        return
    }
    val fileConfig = readFileConfig()

    val host = getActualValue("host", clArgsConfig, fileConfig)
    val port = getActualValue("port", clArgsConfig, fileConfig, "8888").toInt()
    val flashFilePath = getActualValue("flash", clArgsConfig, fileConfig)

    val actualValues = mapOf(
            Pair("host", host),
            Pair("port", port.toString()),
            Pair("flash", flashFilePath))

    if (!isCorrectArguments(actualValues)) {
        return
    }
    var fileBytes: ByteArray = ByteArray(0)
    try {
        FileInputStream(flashFilePath).use { fis ->
            fileBytes = ByteArray(fis.available())
            fis.read(fileBytes)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        println("error reading file $flashFilePath")
        return
    }
    saveFileConfig(actualValues)

    val uploadObject = Upload.BuilderUpload(fileBytes).build()
    val uploadBytes = ByteArray(uploadObject.getSizeNoTag())
    uploadObject.writeTo(CodedOutputStream(uploadBytes))
    val request = DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/loadBin", Unpooled.copiedBuffer(uploadBytes))
    request.headers().set(HttpHeaderNames.HOST, host)
    request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
    request.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes())
    request.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8")

    val clientInstance = Client(host, port)
    clientInstance.sendRequest(request)
    val requestResult = client.ClientHandler.requestResult
    var printString: String = ""
    synchronized(requestResult, {
        printString = "result code: ${requestResult.code}"
    })
    println(printString)
}