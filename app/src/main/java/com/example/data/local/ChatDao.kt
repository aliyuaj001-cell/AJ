package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.model.PushNotificationItem
import com.example.data.model.StoryFeedItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    // Conversations
    @Query("SELECT * FROM conversations ORDER BY isPinned DESC, lastTimestamp DESC")
    fun getAllConversations(): Flow<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    fun getConversationById(id: String): Flow<Conversation?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: Conversation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<Conversation>)

    @Query("UPDATE conversations SET lastMessage = :text, lastTimestamp = :timestamp, unreadCount = unreadCount + :unreadDelta WHERE id = :convId")
    suspend fun updateConversationLastMsg(convId: String, text: String, timestamp: Long, unreadDelta: Int = 0)

    @Query("UPDATE conversations SET unreadCount = 0 WHERE id = :convId")
    suspend fun clearUnread(convId: String)

    // Messages
    @Query("SELECT * FROM chat_messages WHERE conversationId = :convId ORDER BY timestamp ASC")
    fun getMessagesForConversation(convId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessage>)

    @Update
    suspend fun updateMessage(message: ChatMessage)

    @Query("UPDATE chat_messages SET status = :status WHERE id = :msgId")
    suspend fun updateMessageStatus(msgId: String, status: String)

    @Query("UPDATE chat_messages SET reactions = :reactions WHERE id = :msgId")
    suspend fun updateMessageReactions(msgId: String, reactions: String)

    // Social Feed & Stories
    @Query("SELECT * FROM story_feed_items ORDER BY isStory DESC, timestamp DESC")
    fun getAllFeedItems(): Flow<List<StoryFeedItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedItems(items: List<StoryFeedItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedItem(item: StoryFeedItem)

    @Query("UPDATE story_feed_items SET isLiked = :isLiked, likesCount = likesCount + :delta WHERE id = :id")
    suspend fun updateFeedItemLike(id: String, isLiked: Boolean, delta: Int)

    // Push Notifications
    @Query("SELECT * FROM push_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<PushNotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(item: PushNotificationItem)

    @Query("UPDATE push_notifications SET isRead = 1")
    suspend fun markAllNotificationsRead()

    @Query("DELETE FROM push_notifications")
    suspend fun clearAllNotifications()
}
