package com.example.data.local

import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.model.PushNotificationItem
import com.example.data.model.StoryFeedItem
import kotlinx.coroutines.flow.first

object DatabaseInitializer {

    suspend fun populateInitialData(dao: ChatDao) {
        val existing = dao.getAllConversations().first()
        if (existing.isNotEmpty()) return

        val now = System.currentTimeMillis()

        val conversations = listOf(
            Conversation(
                id = "conv_tech_core",
                name = "Tech Core Engineers ⚡",
                isGroup = true,
                avatar = "TC",
                lastMessage = "Sophia: Shared the cluster telemetry screenshot.",
                lastTimestamp = now - 1000 * 60 * 2,
                unreadCount = 2,
                memberCount = 48,
                isPinned = true,
                category = "GROUP",
                encryptionStatus = "E2EE Ratchet v3"
            ),
            Conversation(
                id = "conv_sophia",
                name = "Sophia Chen (Lead Architect)",
                isGroup = false,
                avatar = "SC",
                lastMessage = "🎤 Voice Note (0:24) - Architecture overview",
                lastTimestamp = now - 1000 * 60 * 15,
                unreadCount = 1,
                memberCount = 2,
                isPinned = true,
                category = "DIRECT",
                encryptionStatus = "Curve25519 Verified"
            ),
            Conversation(
                id = "conv_alpha_charts",
                name = "Alpha Global Charts 📈",
                isGroup = true,
                avatar = "AC",
                lastMessage = "High volume breakout on hourly candlestick frame.",
                lastTimestamp = now - 1000 * 60 * 35,
                unreadCount = 5,
                memberCount = 1420,
                isPinned = false,
                category = "CHANNEL",
                encryptionStatus = "Encrypted Broadcast"
            ),
            Conversation(
                id = "conv_marcus",
                name = "Marcus Vance (Security Lead)",
                isGroup = false,
                avatar = "MV",
                lastMessage = "Double Ratchet key rotation verified successfully.",
                lastTimestamp = now - 1000 * 60 * 120,
                unreadCount = 0,
                memberCount = 2,
                isPinned = false,
                category = "DIRECT",
                encryptionStatus = "Signal Protocol v4"
            ),
            Conversation(
                id = "conv_design_lab",
                name = "AJ UI/UX Innovation Lab",
                isGroup = true,
                avatar = "UX",
                lastMessage = "New glassmorphic reaction dock animations ready for review.",
                lastTimestamp = now - 1000 * 60 * 240,
                unreadCount = 0,
                memberCount = 14,
                isPinned = false,
                category = "GROUP",
                encryptionStatus = "E2EE Ratchet v3"
            )
        )
        dao.insertConversations(conversations)

        // Seed messages for Tech Core Engineers
        val techMessages = listOf(
            ChatMessage(
                id = "msg_1",
                conversationId = "conv_tech_core",
                senderId = "usr_sophia",
                senderName = "Sophia Chen",
                content = "Horizontal sharding milestone achieved! The consistent hashing ring routes 500,000 WebSocket connections with <18ms p99 latency.",
                timestamp = now - 1000 * 60 * 25,
                status = "READ",
                isMe = false,
                reactions = "🚀,🔥"
            ),
            ChatMessage(
                id = "msg_2",
                conversationId = "conv_tech_core",
                senderId = "usr_marcus",
                senderName = "Marcus Vance",
                content = "Verified zero-knowledge media encryption on edge nodes. Ephemeral keys wipe reliably after delivery verification.",
                timestamp = now - 1000 * 60 * 18,
                status = "READ",
                isMe = false,
                reactions = "🛡️"
            ),
            ChatMessage(
                id = "msg_3",
                conversationId = "conv_tech_core",
                senderId = "usr_me",
                senderName = "Aliyu AJ",
                content = "Awesome progress team! Redis Pub/Sub cluster write-behind queue handled 120,000 ops/sec without any packet drops.",
                timestamp = now - 1000 * 60 * 10,
                status = "READ",
                isMe = true,
                reactions = "💪,❤️"
            ),
            ChatMessage(
                id = "msg_4",
                conversationId = "conv_tech_core",
                senderId = "usr_sophia",
                senderName = "Sophia Chen",
                content = "Here is the live cluster telemetry snapshot from our primary shard partition:",
                timestamp = now - 1000 * 60 * 2,
                status = "DELIVERED",
                isMe = false,
                mediaType = "IMAGE",
                mediaUrl = "img_encrypted_media_demo_1791125394205",
                reactions = "⚡,👏"
            )
        )
        dao.insertMessages(techMessages)

        // Seed messages for Sophia Chen
        val sophiaMessages = listOf(
            ChatMessage(
                id = "msg_sc_1",
                conversationId = "conv_sophia",
                senderId = "usr_sophia",
                senderName = "Sophia Chen",
                content = "Hey Aliyu! I recorded a quick audio breakdown of the Kafka message ordering partition keys.",
                timestamp = now - 1000 * 60 * 30,
                status = "READ",
                isMe = false
            ),
            ChatMessage(
                id = "msg_sc_2",
                conversationId = "conv_sophia",
                senderId = "usr_sophia",
                senderName = "Sophia Chen",
                content = "Voice note: Kafka partition strategy & Redis LRU cache layers",
                timestamp = now - 1000 * 60 * 15,
                status = "READ",
                isMe = false,
                mediaType = "VOICE",
                durationSeconds = 24
            ),
            ChatMessage(
                id = "msg_sc_3",
                conversationId = "conv_sophia",
                senderId = "usr_me",
                senderName = "Aliyu AJ",
                content = "Thanks Sophia, great breakdown. That guarantees strict FIFO delivery per channel while keeping horizontal shards decoupled.",
                timestamp = now - 1000 * 60 * 5,
                status = "READ",
                isMe = true,
                reactions = "👍"
            )
        )
        dao.insertMessages(sophiaMessages)

        // Seed Stories and Feed items (combining IG & FB style)
        val storiesAndFeed = listOf(
            StoryFeedItem(
                id = "story_1",
                authorName = "Aliyu AJ",
                authorHandle = "@aliyu_aj",
                authorAvatar = "AJ",
                content = "Building AJ Chart: Next-Gen Sharded Real-Time Messenger 🚀",
                timestamp = now - 1000 * 60 * 30,
                isStory = true,
                hasUnseenStory = false
            ),
            StoryFeedItem(
                id = "story_2",
                authorName = "Sophia Chen",
                authorHandle = "@sophia_arch",
                authorAvatar = "SC",
                content = "Benchmarking 10M concurrent WebSockets over Envoy proxy",
                timestamp = now - 1000 * 60 * 60,
                isStory = true,
                hasUnseenStory = true
            ),
            StoryFeedItem(
                id = "story_3",
                authorName = "Alpha Charts",
                authorHandle = "@alphacharts",
                authorAvatar = "AC",
                content = "Live technical momentum & volume delta charts 📊",
                timestamp = now - 1000 * 60 * 90,
                isStory = true,
                hasUnseenStory = true
            ),
            StoryFeedItem(
                id = "story_4",
                authorName = "Marcus Vance",
                authorHandle = "@marcus_sec",
                authorAvatar = "MV",
                content = "Zero-Knowledge proofs for encrypted group chats",
                timestamp = now - 1000 * 60 * 150,
                isStory = true,
                hasUnseenStory = true
            ),
            StoryFeedItem(
                id = "feed_1",
                authorName = "AJ Engineering Hub",
                authorHandle = "@aj_engineering",
                authorAvatar = "AJ",
                content = "🚀 Milestone Announcement: AJ Chart edge networking protocol is live! Powered by horizontal database sharding, consistent ring hashing, and end-to-end encrypted media pipelines. Check out the real-time telemetry dashboard in the Architecture tab.",
                mediaDrawable = "img_encrypted_media_demo_1791125394205",
                timestamp = now - 1000 * 60 * 45,
                likesCount = 384,
                commentsCount = 42,
                isLiked = true,
                isStory = false,
                tag = "#Engineering"
            ),
            StoryFeedItem(
                id = "feed_2",
                authorName = "Crypto & Market Alpha",
                authorHandle = "@market_alpha",
                authorAvatar = "AC",
                content = "📈 Real-Time Charting Stream: High-frequency order book depth and candlestick volume analysis rendered with sub-millisecond client recalculations. Real-time WebSocket multiplexing keeps battery usage minimal while streaming 200 ticks/sec.",
                mediaDrawable = null,
                timestamp = now - 1000 * 60 * 180,
                likesCount = 920,
                commentsCount = 115,
                isLiked = false,
                isStory = false,
                tag = "#Fintech"
            )
        )
        dao.insertFeedItems(storiesAndFeed)

        // Seed notifications
        val initialNotifications = listOf(
            PushNotificationItem(
                id = "notif_1",
                title = "Tech Core Engineers ⚡",
                body = "Sophia Chen shared telemetry snapshot",
                conversationId = "conv_tech_core",
                timestamp = now - 1000 * 60 * 2,
                latencyMs = 18,
                priority = "HIGH",
                isRead = false
            ),
            PushNotificationItem(
                id = "notif_2",
                title = "Sophia Chen",
                body = "Voice note received (0:24) [Encrypted]",
                conversationId = "conv_sophia",
                timestamp = now - 1000 * 60 * 15,
                latencyMs = 22,
                priority = "HIGH",
                isRead = true
            )
        )
        initialNotifications.forEach { dao.insertNotification(it) }
    }
}
