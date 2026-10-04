package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AJBottomNavigation
import com.example.ui.components.AJTopBar
import com.example.ui.components.PushBannerToast
import com.example.ui.components.StoryViewerDialog
import com.example.ui.screens.ArchitectureScreen
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.ChatListScreen
import com.example.ui.screens.NotificationCenterScreen
import com.example.ui.screens.ProfileAuthScreen
import com.example.ui.screens.SocialFeedScreen
import com.example.ui.theme.AJChartTheme
import com.example.ui.theme.DarkBackground
import com.example.ui.viewmodel.ChatViewModel
import com.example.ui.viewmodel.MainTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AJChartTheme {
                AJChartApp()
            }
        }
    }
}

@Composable
fun AJChartApp(viewModel: ChatViewModel = viewModel()) {
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedConvId by viewModel.selectedConversationId.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val feedItems by viewModel.feedItems.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val activeMessages by viewModel.activeMessages.collectAsState()

    val socketStatus by viewModel.webSocket.status.collectAsState()
    val latencyMs by viewModel.webSocket.latencyMs.collectAsState()
    val activeShard by viewModel.webSocket.activeShard.collectAsState()
    val typingUser by viewModel.webSocket.typingUser.collectAsState()

    val conversationFilter by viewModel.conversationFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeStory by viewModel.activeStory.collectAsState()
    val pushBanner by viewModel.pushBanner.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val unreadChatsCount = conversations.sumOf { it.unreadCount }
    val unreadNotifsCount = notifications.count { !it.isRead }

    val activeConversation = conversations.find { it.id == selectedConvId }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectedConvId != null) {
            // Active Chat Detail Screen
            ChatDetailScreen(
                conversation = activeConversation,
                messages = activeMessages,
                typingUser = typingUser,
                onBackClick = { viewModel.closeConversation() },
                onSendMessage = { text, mediaType, url, duration ->
                    viewModel.sendMessage(text, mediaType, url, duration)
                },
                onReactionClick = { msgId, emoji ->
                    viewModel.addReaction(msgId, emoji)
                }
            )
        } else {
            // Main Scaffold with Top Bar and Bottom Nav
            Scaffold(
                containerColor = DarkBackground,
                topBar = {
                    AJTopBar(
                        title = when (currentTab) {
                            MainTab.CHATS -> "AJ Chart"
                            MainTab.FEED -> "Feed & Stories"
                            MainTab.ARCHITECTURE -> "Architecture"
                            MainTab.NOTIFICATIONS -> "Alerts"
                            MainTab.PROFILE -> "Profile Vault"
                        },
                        socketStatus = socketStatus,
                        latencyMs = latencyMs,
                        activeShard = activeShard,
                        unreadNotifsCount = unreadNotifsCount,
                        onNotifsClick = { viewModel.setTab(MainTab.NOTIFICATIONS) },
                        onSecurityClick = { viewModel.setTab(MainTab.ARCHITECTURE) }
                    )
                },
                bottomBar = {
                    AJBottomNavigation(
                        currentTab = currentTab,
                        onTabSelected = { viewModel.setTab(it) },
                        unreadChatsCount = unreadChatsCount,
                        unreadNotifsCount = unreadNotifsCount
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentTab) {
                        MainTab.CHATS -> {
                            ChatListScreen(
                                conversations = conversations,
                                stories = feedItems,
                                filter = conversationFilter,
                                searchQuery = searchQuery,
                                onFilterChange = { viewModel.setConversationFilter(it) },
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onConversationClick = { viewModel.openConversation(it) },
                                onStoryClick = { viewModel.openStory(it) },
                                onNewChatCreate = { name, isGroup ->
                                    // Start a new chat
                                    viewModel.sendMessage(
                                        content = "Hello, initiating encrypted session."
                                    )
                                }
                            )
                        }

                        MainTab.FEED -> {
                            SocialFeedScreen(
                                feedItems = feedItems,
                                onStoryClick = { viewModel.openStory(it) },
                                onLikeClick = { viewModel.toggleFeedLike(it) }
                            )
                        }

                        MainTab.ARCHITECTURE -> {
                            ArchitectureScreen(
                                webSocket = viewModel.webSocket
                            )
                        }

                        MainTab.NOTIFICATIONS -> {
                            NotificationCenterScreen(
                                notifications = notifications,
                                onNotificationClick = { convId ->
                                    viewModel.openConversation(convId)
                                },
                                onSimulatePush = { viewModel.simulatePushArrival() },
                                onMarkAllRead = { viewModel.markAllNotificationsRead() }
                            )
                        }

                        MainTab.PROFILE -> {
                            ProfileAuthScreen(
                                userProfile = userProfile
                            )
                        }
                    }
                }
            }
        }

        // In-app heads-up Push Notification toast
        PushBannerToast(
            notification = pushBanner,
            onDismiss = { viewModel.dismissPushBanner() },
            onClick = { convId ->
                viewModel.dismissPushBanner()
                viewModel.openConversation(convId)
            }
        )

        // Fullscreen Story Viewer Dialog
        activeStory?.let { story ->
            StoryViewerDialog(
                story = story,
                onDismiss = { viewModel.closeStory() }
            )
        }
    }
}
