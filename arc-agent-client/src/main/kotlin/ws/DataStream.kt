package ai.ancf.lmos.arc.agent.client.ws

import io.ktor.utils.io.core.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class DataStream : Closeable {

    private val channel = Channel<ByteArray>()

    fun provide(): Flow<ByteArray> = channel.receiveAsFlow()

    suspend fun send(data: ByteArray) {
        channel.send(data)
    }

    override fun close() {
        channel.close()
    }
}
