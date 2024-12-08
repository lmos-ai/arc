package ai.ancf.lmos.arc.ws.inbound

import ai.ancf.lmos.arc.agents.conversation.DataStream
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class WritableDataStream : DataStream {
    private val channel = Channel<ByteArray>()

    override fun stream(): Flow<ByteArray> = channel.receiveAsFlow()

    suspend fun send(data: ByteArray) {
        channel.send(data)
    }

    fun close() {
        channel.close()
    }
}
