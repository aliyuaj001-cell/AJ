package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long,
    val status: String, // "SENDING", "SENT", "DELIVERED", "READ"
    val isMe: Boolean,
    val mediaType: String = "NONE", // "NONE", "IMAGE", "VOICE", "DOCUMENT"
    val mediaUrl: String? = null,
    val isEncrypted: Boolean = true,
    val cipherAlgorithm: String = "AES-256-GCM / Curve25519",
    val reactions: String = "", // comma-separated e.g. "❤️,🔥"
    val durationSeconds: Int = 0 // for voice notes
)

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey val id: String,
    val name: String,
    val isGroup: Boolean,
    val avatar: String, // resource id name or initials
    val lastMessage: String,
    val lastTimestamp: Long,
    val unreadCount: Int = 0,
    val memberCount: Int = 2,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val encryptionStatus: String = "E2EE Ratchet v3",
    val category: String = "DIRECT" // "DIRECT", "GROUP", "CHANNEL"
)

@Entity(tableName = "story_feed_items")
data class StoryFeedItem(
    @PrimaryKey val id: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatar: String,
    val content: String,
    val mediaDrawable: String? = null,
    val timestamp: Long,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLiked: Boolean = false,
    val isStory: Boolean = false,
    val hasUnseenStory: Boolean = true,
    val tag: String = "#Tech"
)

@Entity(tableName = "push_notifications")
data class PushNotificationItem(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val conversationId: String,
    val timestamp: Long,
    val latencyMs: Int,
    val priority: String = "HIGH",
    val fcmTokenPrefix: String = "fcm_edge_node_4",
    val isRead: Boolean = false
)

data class UserProfile(
    val userId: String = "usr_aj_9942",
    val displayName: String = "Aliyu AJ",
    val username: String = "@aliyu_aj",
    val bio: String = "Building high-performance distributed systems & low-latency real-time apps 🚀",
    val publicKeyFingerprint: String = "4F8A-9B21-EE03-772A-D004-98F1",
    val isBiometricsActive: Boolean = true,
    val isOnline: Boolean = true
)

// Data structure for horizontal sharding simulator
data class ShardNode(
    val shardId: Int,
    val nodeName: String,
    val hostRegion: String,
    val rangeMin: Long,
    val rangeMax: Long,
    val activeConnections: Int,
    val latencyP99Ms: Double,
    val status: String = "HEALTHY"
)

data class WebSocketPacket(
    val packetId: String,
    val type: String, // "ACK", "MESSAGE", "PRESENCE", "TYPING", "SYNC"
    val channel: String,
    val payloadSize: Int,
    val latencyMs: Int,
    val timestamp: Long = System.currentTimeMillis()
)
