package com.example.data.repository

import com.example.data.local.ChatDao
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.model.PushNotificationItem
import com.example.data.model.StoryFeedItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ChatRepository(private val chatDao: ChatDao) {

    fun getConversations(): Flow<List<Conversation>> = chatDao.getAllConversations()

    fun getConversation(id: String): Flow<Conversation?> = chatDao.getConversationById(id)

    fun getMessages(conversationId: String): Flow<List<ChatMessage>> =
        chatDao.getMessagesForConversation(conversationId)

    fun getFeedItems(): Flow<List<StoryFeedItem>> = chatDao.getAllFeedItems()

    fun getNotifications(): Flow<List<PushNotificationItem>> = chatDao.getAllNotifications()

    suspend fun sendMessage(
        conversationId: String,
        content: String,
        mediaType: String = "NONE",
        mediaUrl: String? = null,
        durationSeconds: Int = 0
    ): ChatMessage {
        val now = System.currentTimeMillis()
        val msgId = "msg_${UUID.randomUUID().toString().take(8)}"
        val message = ChatMessage(
            id = msgId,
            conversationId = conversationId,
            senderId = "usr_me",
            senderName = "Aliyu AJ",
            content = content,
            timestamp = now,
            status = "SENT",
            isMe = true,
            mediaType = mediaType,
            mediaUrl = mediaUrl,
            durationSeconds = durationSeconds
        )
        chatDao.insertMessage(message)
        val preview = when (mediaType) {
            "IMAGE" -> "📷 Encrypted Photo"
            "VOICE" -> "🎤 Voice Note (${durationSeconds}s)"
            else -> content
        }
        chatDao.updateConversationLastMsg(conversationId, preview, now, 0)
        return message
    }

    suspend fun receiveIncomingMessage(
        conversationId: String,
        senderName: String,
        content: String,
        mediaType: String = "NONE",
        mediaUrl: String? = null
    ): ChatMessage {
        val now = System.currentTimeMillis()
        val msgId = "msg_${UUID.randomUUID().toString().take(8)}"
        val message = ChatMessage(
            id = msgId,
            conversationId = conversationId,
            senderId = "usr_${senderName.lowercase().replace(" ", "_")}",
            senderName = senderName,
            content = content,
            timestamp = now,
            status = "DELIVERED",
            isMe = false,
            mediaType = mediaType,
            mediaUrl = mediaUrl
        )
        chatDao.insertMessage(message)
        val preview = when (mediaType) {
            "IMAGE" -> "📷 Encrypted Photo"
            "VOICE" -> "🎤 Voice Note"
            else -> content
        }
        chatDao.updateConversationLastMsg(conversationId, "$senderName: $preview", now, 1)

        // Generate push notification
        val notif = PushNotificationItem(
            id = "notif_${UUID.randomUUID().toString().take(8)}",
            title = senderName,
            body = preview,
            conversationId = conversationId,
            timestamp = now,
            latencyMs = (12..28).random(),
            priority = "HIGH"
        )
        chatDao.insertNotification(notif)
        return message
    }

    suspend fun markMessageRead(msgId: String) {
        chatDao.updateMessageStatus(msgId, "READ")
    }

    suspend fun updateMessageReactions(msgId: String, reactionEmoji: String) {
        chatDao.updateMessageReactions(msgId, reactionEmoji)
    }

    suspend fun clearUnread(conversationId: String) {
        chatDao.clearUnread(conversationId)
    }

    suspend fun toggleFeedLike(item: StoryFeedItem) {
        val newLiked = !item.isLiked
        val delta = if (newLiked) 1 else -1
        chatDao.updateFeedItemLike(item.id, newLiked, delta)
    }

    suspend fun markAllNotificationsRead() {
        chatDao.markAllNotificationsRead()
    }
}
