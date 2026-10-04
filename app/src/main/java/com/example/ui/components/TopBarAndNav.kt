package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.DynamicFeed
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PushNotificationItem
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.RoseStory
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent
import com.example.ui.viewmodel.MainTab
import com.example.websocket.SocketStatus

@Composable
fun AJTopBar(
    title: String = "AJ Chart",
    socketStatus: SocketStatus,
    latencyMs: Int,
    activeShard: String,
    unreadNotifsCount: Int,
    onNotifsClick: () -> Unit,
    onSecurityClick: () -> Unit
) {
    Surface(
        color = DarkBackground,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Branding & Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(EmeraldPrimary, CyanAccent, VioletAccent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AJ",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Real-time • E2EE Mesh",
                        fontSize = 11.sp,
                        color = EmeraldLight,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Live telemetry status pill & actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Shard & Ping Pill
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.clickable { onSecurityClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    when (socketStatus) {
                                        SocketStatus.CONNECTED -> EmeraldPrimary
                                        SocketStatus.RECONNECTING -> RoseStory
                                        else -> Color.Gray
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${latencyMs}ms",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Security shield icon
                IconButton(
                    onClick = onSecurityClick,
                    modifier = Modifier.size(38.dp).testTag("topbar_security_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "E2EE Security Info",
                        tint = EmeraldLight,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Notifications icon with badge
                IconButton(
                    onClick = onNotifsClick,
                    modifier = Modifier.size(38.dp).testTag("topbar_notifs_btn")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotifsCount > 0) {
                                Badge(
                                    containerColor = RoseStory,
                                    contentColor = Color.White
                                ) {
                                    Text(text = "$unreadNotifsCount")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AJBottomNavigation(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    unreadChatsCount: Int,
    unreadNotifsCount: Int
) {
    NavigationBar(
        containerColor = DarkSurface,
        contentColor = TextPrimary,
        tonalElevation = 8.dp,
        modifier = Modifier
            .navigationBarsPadding()
            .height(64.dp)
            .testTag("bottom_nav_bar")
    ) {
        // Chats (WhatsApp vibe)
        NavigationBarItem(
            selected = currentTab == MainTab.CHATS,
            onClick = { onTabSelected(MainTab.CHATS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (unreadChatsCount > 0) {
                            Badge(containerColor = EmeraldPrimary, contentColor = Color.Black) {
                                Text("$unreadChatsCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentTab == MainTab.CHATS) Icons.Filled.Forum else Icons.Outlined.Forum,
                        contentDescription = "Chats"
                    )
                }
            },
            label = { Text("Chats", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DarkBackground,
                indicatorColor = EmeraldPrimary,
                unselectedIconColor = TextSecondary,
                selectedTextColor = EmeraldLight,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("tab_chats")
        )

        // Feed (IG & FB vibe)
        NavigationBarItem(
            selected = currentTab == MainTab.FEED,
            onClick = { onTabSelected(MainTab.FEED) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.FEED) Icons.Filled.DynamicFeed else Icons.Outlined.DynamicFeed,
                    contentDescription = "Feed"
                )
            },
            label = { Text("Feed", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DarkBackground,
                indicatorColor = CyanAccent,
                unselectedIconColor = TextSecondary,
                selectedTextColor = CyanAccent,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("tab_feed")
        )

        // System Architecture & Sharding (The requested technical architecture visualizer!)
        NavigationBarItem(
            selected = currentTab == MainTab.ARCHITECTURE,
            onClick = { onTabSelected(MainTab.ARCHITECTURE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.ARCHITECTURE) Icons.Filled.Hub else Icons.Outlined.Hub,
                    contentDescription = "Architecture"
                )
            },
            label = { Text("Architecture", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DarkBackground,
                indicatorColor = VioletAccent,
                unselectedIconColor = TextSecondary,
                selectedTextColor = VioletAccent,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("tab_architecture")
        )

        // Notifications
        NavigationBarItem(
            selected = currentTab == MainTab.NOTIFICATIONS,
            onClick = { onTabSelected(MainTab.NOTIFICATIONS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (unreadNotifsCount > 0) {
                            Badge(containerColor = RoseStory, contentColor = Color.White) {
                                Text("$unreadNotifsCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentTab == MainTab.NOTIFICATIONS) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                        contentDescription = "Alerts"
                    )
                }
            },
            label = { Text("Alerts", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DarkBackground,
                indicatorColor = RoseStory,
                unselectedIconColor = TextSecondary,
                selectedTextColor = RoseStory,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("tab_notifications")
        )

        // Profile
        NavigationBarItem(
            selected = currentTab == MainTab.PROFILE,
            onClick = { onTabSelected(MainTab.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DarkBackground,
                indicatorColor = EmeraldLight,
                unselectedIconColor = TextSecondary,
                selectedTextColor = EmeraldLight,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("tab_profile")
        )
    }
}

@Composable
fun PushBannerToast(
    notification: PushNotificationItem?,
    onDismiss: () -> Unit,
    onClick: (String) -> Unit
) {
    AnimatedVisibility(
        visible = notification != null,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it })
    ) {
        if (notification != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldDark),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick(notification.conversationId) }
                        .testTag("push_banner_toast")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Forum,
                                contentDescription = null,
                                tint = DarkBackground,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = notification.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = DarkBackground
                                ) {
                                    Text(
                                        text = "${notification.latencyMs}ms",
                                        fontSize = 10.sp,
                                        color = EmeraldLight,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = notification.body,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                maxLines = 1
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
