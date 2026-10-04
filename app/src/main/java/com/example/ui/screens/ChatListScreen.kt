package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Conversation
import com.example.data.model.StoryFeedItem
import com.example.ui.components.StoryTray
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatListScreen(
    conversations: List<Conversation>,
    stories: List<StoryFeedItem>,
    filter: String,
    searchQuery: String,
    onFilterChange: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onConversationClick: (String) -> Unit,
    onStoryClick: (StoryFeedItem) -> Unit,
    onNewChatCreate: (String, Boolean) -> Unit
) {
    var showNewChatDialog by remember { mutableStateOf(false) }

    val filteredList = remember(conversations, filter, searchQuery) {
        conversations.filter { conv ->
            val matchesFilter = when (filter) {
                "DIRECT" -> !conv.isGroup && conv.category == "DIRECT"
                "GROUPS" -> conv.isGroup && conv.category == "GROUP"
                "CHANNELS" -> conv.category == "CHANNEL"
                else -> true
            }
            val matchesSearch = conv.name.contains(searchQuery, ignoreCase = true) ||
                    conv.lastMessage.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewChatDialog = true },
                containerColor = EmeraldPrimary,
                contentColor = DarkBackground,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_new_chat")
            ) {
                Icon(
                    imageVector = Icons.Default.AddComment,
                    contentDescription = "New Chat",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("chat_list_column")
        ) {
            // Instagram / WhatsApp Status Story Bar
            item {
                StoryTray(
                    stories = stories,
                    onStoryClick = onStoryClick,
                    onAddStoryClick = { showNewChatDialog = true }
                )
            }

            // Search Bar
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("search_chats_input"),
                        placeholder = {
                            Text(
                                "Search chats, groups, channels...",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            }

            // Category Filter Chips
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categories = listOf("ALL", "DIRECT", "GROUPS", "CHANNELS")
                    items(categories) { cat ->
                        val isSelected = filter == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { onFilterChange(cat) },
                            label = {
                                Text(
                                    text = when (cat) {
                                        "ALL" -> "All Chats"
                                        "DIRECT" -> "Direct Messages"
                                        "GROUPS" -> "Groups"
                                        "CHANNELS" -> "Channels"
                                        else -> cat
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = DarkSurfaceVariant,
                                labelColor = TextSecondary,
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = DarkBackground
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = DarkBorder,
                                selectedBorderColor = EmeraldPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Conversations List
            if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No conversations found",
                                color = TextSecondary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else {
                items(filteredList) { conv ->
                    ConversationItemRow(
                        conversation = conv,
                        onClick = { onConversationClick(conv.id) }
                    )
                }
            }
        }
    }

    if (showNewChatDialog) {
        NewChatModal(
            onDismiss = { showNewChatDialog = false },
            onConfirm = { name, isGroup ->
                onNewChatCreate(name, isGroup)
                showNewChatDialog = false
            }
        )
    }
}

@Composable
fun ConversationItemRow(
    conversation: Conversation,
    onClick: () -> Unit
) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val formattedTime = remember(conversation.lastTimestamp) {
        timeFormat.format(Date(conversation.lastTimestamp))
    }

    Surface(
        color = DarkBackground,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("conversation_row_${conversation.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with initials and category badge
            Box(
                modifier = Modifier.size(52.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            when (conversation.category) {
                                "CHANNEL" -> VioletAccent
                                "GROUP" -> CyanAccent
                                else -> EmeraldDark
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = conversation.avatar,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                // Small badge for group or channel
                if (conversation.isGroup) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                            .border(1.dp, DarkBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = EmeraldLight,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Middle Column: Title & Last Message
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = conversation.name,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (conversation.isPinned) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pinned",
                                tint = EmeraldLight,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    Text(
                        text = formattedTime,
                        fontSize = 11.sp,
                        color = if (conversation.unreadCount > 0) EmeraldLight else TextMuted,
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "E2EE",
                            tint = EmeraldLight.copy(alpha = 0.6f),
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = conversation.lastMessage,
                            fontSize = 13.sp,
                            color = if (conversation.unreadCount > 0) TextPrimary else TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (conversation.unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldPrimary)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${conversation.unreadCount}",
                                color = DarkBackground,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewChatModal(
    onDismiss: () -> Unit,
    onConfirm: (String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var isGroup by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text("Start New Secure Channel", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    "Messages in AJ Chart are end-to-end encrypted with Double Ratchet key rotation.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Contact or Group Name") },
                    placeholder = { Text("e.g. Distributed Systems Lab") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = DarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("new_chat_name_input")
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isGroup = !isGroup }
                ) {
                    androidx.compose.material3.Checkbox(
                        checked = isGroup,
                        onCheckedChange = { isGroup = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create as Multi-User Group", color = TextPrimary, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) onConfirm(name.trim(), isGroup)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Create Chat", color = DarkBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
