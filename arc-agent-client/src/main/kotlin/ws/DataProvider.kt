package ai.ancf.lmos.arc.agent.client.ws

import io.ktor.utils.io.core.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class DataProvider : Closeable {

    private val channel = Channel<Pair<ByteArray, Boolean>>()

    fun provide(): Flow<Pair<ByteArray, Boolean>> = channel.receiveAsFlow()

    suspend fun send(data: ByteArray, last: Boolean = false) {
        channel.send(data to last)
    }

    override fun close() {
        channel.close()
    }
}