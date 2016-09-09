package objects

import CodedInputStream
import SonarResponse
import org.asynchttpclient.Response
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class SonarDataResponse(private val future: Future<Response>) : Future<IntArray> {

    override fun cancel(cancel: Boolean): Boolean {
        return future.cancel(cancel)
    }

    override fun get(): IntArray {
        val bytes = future.get().responseBodyAsBytes
        return getSonarResponse(bytes).distances
    }

    override fun get(time: Long, timeUnit: TimeUnit): IntArray {
        val bytes = future.get(time, timeUnit).responseBodyAsBytes
        return getSonarResponse(bytes).distances
    }

    override fun isDone(): Boolean {
        return future.isDone
    }

    override fun isCancelled(): Boolean {
        return future.isCancelled
    }

    private fun getSonarResponse(bytes: ByteArray): SonarResponse {
        val sonarResponse = SonarResponse.BuilderSonarResponse(IntArray(0)).build()
        sonarResponse.mergeFrom(CodedInputStream(bytes))
        return sonarResponse
    }
}