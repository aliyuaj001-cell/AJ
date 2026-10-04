package com.example.websocket

import com.example.data.model.WebSocketPacket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

enum class SocketStatus {
    CONNECTING, CONNECTED, RECONNECTING, DISCONNECTED
}

class WebSocketSimulator(private val scope: CoroutineScope) {

    private val _status = MutableStateFlow(SocketStatus.CONNECTED)
    val status: StateFlow<SocketStatus> = _status.asStateFlow()

    private val _latencyMs = MutableStateFlow(16)
    val latencyMs: StateFlow<Int> = _latencyMs.asStateFlow()

    private val _activeShard = MutableStateFlow("Shard-Node-04 [Frankfurt Edge]")
    val activeShard: StateFlow<String> = _activeShard.asStateFlow()

    private val _packetHistory = MutableStateFlow<List<WebSocketPacket>>(emptyList())
    val packetHistory: StateFlow<List<WebSocketPacket>> = _packetHistory.asStateFlow()

    private val _typingUser = MutableStateFlow<String?>(null)
    val typingUser: StateFlow<String?> = _typingUser.asStateFlow()

    private var heartbeatJob: Job? = null

    init {
        startHeartbeat()
    }

    private fun startHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = scope.launch(Dispatchers.Default) {
            while (true) {
                delay(3000)
                if (_status.value == SocketStatus.CONNECTED) {
                    val ping = (14..22).random()
                    _latencyMs.value = ping
                    pushPacket("HEARTBEAT", "sys:gateway", 32, ping)
                }
            }
        }
    }

    fun pushPacket(type: String, channel: String, sizeBytes: Int, latency: Int) {
        val packet = WebSocketPacket(
            packetId = "pkt_${UUID.randomUUID().toString().take(6)}",
            type = type,
            channel = channel,
            payloadSize = sizeBytes,
            latencyMs = latency
        )
        val updated = (_packetHistory.value.take(19) + packet).sortedByDescending { it.timestamp }
        _packetHistory.value = updated
    }

    fun simulateReconnect() {
        scope.launch {
            _status.value = SocketStatus.RECONNECTING
            delay(1200)
            val shards = listOf(
                "Shard-Node-01 [London Edge]",
                "Shard-Node-04 [Frankfurt Edge]",
                "Shard-Node-08 [N. Virginia Edge]",
                "Shard-Node-12 [Singapore Edge]"
            )
            _activeShard.value = shards.random()
            _status.value = SocketStatus.CONNECTED
            _latencyMs.value = (12..19).random()
            pushPacket("SYNC", "sub:all", 128, _latencyMs.value)
        }
    }

    fun triggerTyping(userName: String) {
        scope.launch {
            _typingUser.value = userName
            pushPacket("TYPING", "chat:conv_tech_core", 48, 14)
            delay(3500)
            if (_typingUser.value == userName) {
                _typingUser.value = null
            }
        }
    }
}
