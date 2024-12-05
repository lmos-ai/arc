package ai.ancf.lmos.arc.ws.inbound

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DataProvider(private val list: List<ByteArray>) {

    fun provide(): Flow<Pair<ByteArray, Boolean>> = flow {
        val channel = Channel<Pair<ByteArray, Boolean>>()
        list.forEachIndexed { index, byteArray ->
            emit(byteArray to (index == list.size - 1))
        }
        channel.close()
    }
}