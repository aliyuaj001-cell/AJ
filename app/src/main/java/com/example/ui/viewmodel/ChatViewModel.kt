package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DatabaseInitializer
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.model.PushNotificationItem
import com.example.data.model.StoryFeedItem
import com.example.data.model.UserProfile
import com.example.data.repository.ChatRepository
import com.example.websocket.SocketStatus
import com.example.websocket.WebSocketSimulator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    CHATS, FEED, ARCHITECTURE, NOTIFICATIONS, PROFILE
}

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = ChatRepository(database.chatDao())
    val webSocket = WebSocketSimulator(viewModelScope)

    private val _currentTab = MutableStateFlow(MainTab.CHATS)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _selectedConversationId = MutableStateFlow<String?>(null)
    val selectedConversationId: StateFlow<String?> = _selectedConversationId.asStateFlow()

    private val _activeStory = MutableStateFlow<StoryFeedItem?>(null)
    val activeStory: StateFlow<StoryFeedItem?> = _activeStory.asStateFlow()

    private val _pushBanner = MutableStateFlow<PushNotificationItem?>(null)
    val pushBanner: StateFlow<PushNotificationItem?> = _pushBanner.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Query filter for conversations: ALL, DIRECT, GROUP, CHANNEL
    private val _conversationFilter = MutableStateFlow("ALL")
    val conversationFilter: StateFlow<String> = _conversationFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val conversations: StateFlow<List<Conversation>> = repository.getConversations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val feedItems: StateFlow<List<StoryFeedItem>> = repository.getFeedItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<PushNotificationItem>> = repository.getNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeMessages: StateFlow<List<ChatMessage>> = _selectedConversationId.flatMapLatest { convId ->
        if (convId != null) repository.getMessages(convId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            DatabaseInitializer.populateInitialData(database.chatDao())
        }
    }

    fun setTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun setConversationFilter(filter: String) {
        _conversationFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openConversation(id: String) {
        _selectedConversationId.value = id
        viewModelScope.launch {
            repository.clearUnread(id)
        }
    }

    fun closeConversation() {
        _selectedConversationId.value = null
    }

    fun openStory(story: StoryFeedItem) {
        _activeStory.value = story
    }

    fun closeStory() {
        _activeStory.value = null
    }

    fun dismissPushBanner() {
        _pushBanner.value = null
    }

    fun sendMessage(
        content: String,
        mediaType: String = "NONE",
        mediaUrl: String? = null,
        durationSeconds: Int = 0
    ) {
        val convId = _selectedConversationId.value ?: return
        viewModelScope.launch {
            val msg = repository.sendMessage(convId, content, mediaType, mediaUrl, durationSeconds)
            webSocket.pushPacket("MESSAGE", "chat:$convId", content.toByteArray().size + 64, 15)

            // Simulate server ACK & read status transitions
            delay(800)
            repository.markMessageRead(msg.id)

            // Automatic intelligent simulated response in groups or direct chats
            delay(1500)
            triggerSimulatedReply(convId)
        }
    }

    private suspend fun triggerSimulatedReply(convId: String) {
        webSocket.triggerTyping("Sophia Chen")
        delay(2200)

        val replies = when (convId) {
            "conv_tech_core" -> listOf(
                "Telemetry packet received. P99 latency dropped to 14.8ms on Redis Pub/Sub ring!",
                "Kafka producer offset acknowledged with zero packet drop.",
                "E2EE ratchet rotated key successfully. Ready for load test."
            )
            "conv_sophia" -> listOf(
                "Agreed! I will configure the WebSocket gateway autoscaler to spin up 4 new shard workers.",
                "That matches our benchmark target perfectly. Sending updated schema docs now."
            )
            "conv_alpha_charts" -> listOf(
                "Bullish volume spike detected on 15m chart. Moving average convergence confirmed.",
                "Real-time order book liquidity updated over shard node #4."
            )
            else -> listOf("Message received and decrypted via AES-256-GCM securely.")
        }

        val sender = if (convId == "conv_sophia") "Sophia Chen" else if (convId == "conv_alpha_charts") "Alpha Charts Bot" else "Sophia Chen"
        val replyMsg = repository.receiveIncomingMessage(convId, sender, replies.random())

        // Show push notification toast banner if not looking at that exact chat or in another tab
        if (_selectedConversationId.value != convId || _currentTab.value != MainTab.CHATS) {
            _pushBanner.value = PushNotificationItem(
                id = replyMsg.id,
                title = sender,
                body = replyMsg.content,
                conversationId = convId,
                timestamp = System.currentTimeMillis(),
                latencyMs = 18,
                priority = "HIGH"
            )
        }
    }

    fun addReaction(messageId: String, emoji: String) {
        viewModelScope.launch {
            repository.updateMessageReactions(messageId, emoji)
            webSocket.pushPacket("REACTION", "rx:$emoji", 32, 12)
        }
    }

    fun toggleFeedLike(item: StoryFeedItem) {
        viewModelScope.launch {
            repository.toggleFeedLike(item)
            webSocket.pushPacket("FEED_LIKE", "feed:${item.id}", 32, 16)
        }
    }

    fun simulatePushArrival() {
        viewModelScope.launch {
            val sampleTitles = listOf("Tech Core Engineers ⚡", "Sophia Chen", "Alpha Global Charts 📈")
            val sampleBodies = listOf(
                "New benchmark telemetry recorded: 4.8M ops/sec",
                "Curve25519 session renewed successfully",
                "High-frequency orderbook update on shard 12"
            )
            val index = (0..2).random()
            val notif = PushNotificationItem(
                id = "notif_${System.currentTimeMillis()}",
                title = sampleTitles[index],
                body = sampleBodies[index],
                conversationId = "conv_tech_core",
                timestamp = System.currentTimeMillis(),
                latencyMs = (14..25).random(),
                priority = "HIGH"
            )
            repository.receiveIncomingMessage("conv_tech_core", sampleTitles[index], sampleBodies[index])
            _pushBanner.value = notif
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }
}
