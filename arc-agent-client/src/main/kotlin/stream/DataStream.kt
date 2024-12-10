package ai.ancf.lmos.arc.agent.client.stream

import io.ktor.utils.io.core.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * A stream of data that can be read from and written to.
 */
class DataStream : Closeable {

    private val channel = Channel<ByteArray>()

    fun read(): Flow<ByteArray> = channel.receiveAsFlow()

    suspend fun send(data: ByteArray) {
        channel.send(data)
    }

    override fun close() {
        channel.close()
    }
}
