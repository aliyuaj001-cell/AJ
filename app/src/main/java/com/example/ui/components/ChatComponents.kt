package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.ui.theme.BubbleMe
import com.example.ui.theme.BubbleMeBorder
import com.example.ui.theme.BubbleOther
import com.example.ui.theme.BubbleOtherBorder
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
fun MessageBubble(
    message: ChatMessage,
    onReactionClick: (String) -> Unit,
    onMediaClick: () -> Unit
) {
    var showReactionDock by remember { mutableStateOf(false) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    val bubbleShape = if (message.isMe) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = if (message.isMe) Alignment.End else Alignment.Start
    ) {
        // Floating reaction dock if expanded
        AnimatedVisibility(visible = showReactionDock) {
            ReactionDock(
                onSelectEmoji = { emoji ->
                    onReactionClick(emoji)
                    showReactionDock = false
                }
            )
        }

        Surface(
            shape = bubbleShape,
            color = if (message.isMe) BubbleMe else BubbleOther,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (message.isMe) BubbleMeBorder else BubbleOtherBorder
            ),
            shadowElevation = 2.dp,
            modifier = Modifier
                .widthIn(max = 310.dp)
                .clickable { showReactionDock = !showReactionDock }
                .testTag("message_bubble_${message.id}")
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                // Sender name in groups for other people
                if (!message.isMe) {
                    Text(
                        text = message.senderName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                // Encrypted Media: Image preview
                if (message.mediaType == "IMAGE") {
                    EncryptedImageCard(
                        mediaUrl = message.mediaUrl,
                        onClick = onMediaClick
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Encrypted Media: Voice Note Player
                if (message.mediaType == "VOICE") {
                    VoiceNotePlayer(durationSeconds = message.durationSeconds)
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Text Content
                if (message.content.isNotEmpty() && message.mediaType != "VOICE") {
                    Text(
                        text = message.content,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Metadata row: Lock icon + Time + Read receipts
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (message.isEncrypted) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Encrypted",
                            tint = EmeraldLight.copy(alpha = 0.7f),
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }

                    Text(
                        text = formattedTime,
                        fontSize = 10.sp,
                        color = TextSecondary
                    )

                    if (message.isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        when (message.status) {
                            "READ" -> {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = "Read",
                                    tint = CyanAccent, // Double blue tick!
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            "DELIVERED" -> {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = "Delivered",
                                    tint = TextMuted,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            else -> {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "Sent",
                                    tint = TextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Reaction chips below bubble
        if (message.reactions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                message.reactions.split(",").forEach { emoji ->
                    Text(text = emoji, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 2.dp))
                }
            }
        }
    }
}

@Composable
fun VoiceNotePlayer(durationSeconds: Int = 24) {
    var isPlaying by remember { mutableStateOf(false) }
    var currentSeconds by remember { mutableStateOf(0) }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkBackground.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play / Pause toggle
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(EmeraldPrimary)
                    .clickable { isPlaying = !isPlaying }
                    .testTag("voice_play_toggle"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = DarkBackground,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Waveform equalizer bars
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val heights = listOf(8, 16, 22, 12, 28, 18, 10, 24, 14, 20, 26, 12, 18, 22, 8, 14)
                heights.forEachIndexed { index, h ->
                    val active = isPlaying && (index < 8)
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(h.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (active) EmeraldLight else DarkBorder)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Duration tag
            Text(
                text = String.format("0:%02d", if (isPlaying) 12 else durationSeconds),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun EncryptedImageCard(
    mediaUrl: String?,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val resId = remember(mediaUrl) {
        if (mediaUrl != null) {
            context.resources.getIdentifier(mediaUrl, "drawable", context.packageName)
        } else 0
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldDark),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Box {
            if (resId != 0) {
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = "Encrypted Media",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(DarkSurfaceVariant, DarkBackground)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "AES-256 Encrypted Payload",
                            fontSize = 11.sp,
                            color = EmeraldLight
                        )
                    }
                }
            }

            // Security badge overlay
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkBackground.copy(alpha = 0.85f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = EmeraldLight,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "E2EE Verified",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldLight
                    )
                }
            }
        }
    }
}

@Composable
fun ReactionDock(onSelectEmoji: (String) -> Unit) {
    val emojis = listOf("❤️", "🔥", "🚀", "😂", "👍", "👏")
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        shadowElevation = 6.dp,
        modifier = Modifier
            .padding(bottom = 6.dp)
            .testTag("reaction_dock")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            emojis.forEach { emoji ->
                Text(
                    text = emoji,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clickable { onSelectEmoji(emoji) }
                        .padding(4.dp)
                )
            }
        }
    }
}
