package objects

import org.asynchttpclient.Response
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class RouteMetricResponse (private val future: Future<Response>): Future<Unit> {

    override fun cancel(cancel: Boolean): Boolean {
        return future.cancel(cancel)
    }

    override fun get() {
        future.get()//just block thread
    }

    override fun get(time: Long, timeUnit: TimeUnit) {
        future.get(time, timeUnit)
    }

    override fun isCancelled(): Boolean {
        return future.isCancelled
    }

    override fun isDone(): Boolean {
        return future.isDone
    }
}