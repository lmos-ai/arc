package ai.ancf.lmos.arc.ws.inbound

import ai.ancf.lmos.arc.agents.conversation.DataStream
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WritableDataStream : DataStream {
    private val channel = Channel<ByteArray>()

    private val datas = mutableListOf<ByteArray>()

    // override fun stream(): Flow<ByteArray> = channel.receiveAsFlow()

    override fun stream(): Flow<ByteArray> = flow { datas.forEach { emit(it) } }

    suspend fun send(data: ByteArray) {
        // channel.send(data)
        datas.add(data)
    }

    fun close() {
        channel.close()
    }
}
