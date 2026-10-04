package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.RoseStory
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialFeedScreen(
    feedItems: List<StoryFeedItem>,
    onStoryClick: (StoryFeedItem) -> Unit,
    onLikeClick: (StoryFeedItem) -> Unit
) {
    var activeCommentsPost by remember { mutableStateOf<StoryFeedItem?>(null) }
    var newPostContent by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("social_feed_screen")
    ) {
        // Story Bar (Instagram-style)
        item {
            StoryTray(
                stories = feedItems,
                onStoryClick = onStoryClick,
                onAddStoryClick = {}
            )
        }

        // Post Creator Box (Facebook-style)
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(EmeraldDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("AJ", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedTextField(
                            value = newPostContent,
                            onValueChange = { newPostContent = it },
                            placeholder = {
                                Text(
                                    "Share charts, updates, or encrypted media...",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1f).testTag("feed_composer_input"),
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

                    if (newPostContent.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { newPostContent = "" },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.align(Alignment.End).testTag("publish_post_btn")
                        ) {
                            Text("Post Update", color = DarkBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Feed Post Cards
        items(feedItems.filter { !it.isStory }) { post ->
            SocialPostCard(
                post = post,
                onLikeClick = { onLikeClick(post) },
                onCommentsClick = { activeCommentsPost = post }
            )
        }
    }

    if (activeCommentsPost != null) {
        ModalBottomSheet(
            onDismissRequest = { activeCommentsPost = null },
            containerColor = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Comments (${activeCommentsPost?.commentsCount ?: 0})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(14.dp))
                CommentRow("Sophia Chen", "Benchmark verified! Zero-latency WebSocket broadcast looks phenomenal.")
                CommentRow("Marcus Vance", "E2EE ratified over the distributed gossip cluster.")
                CommentRow("Elena Rostova", "Can we shard the candlestick telemetry by token ticker?")
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun SocialPostCard(
    post: StoryFeedItem,
    onLikeClick: () -> Unit,
    onCommentsClick: () -> Unit
) {
    val context = LocalContext.current
    var isHeartPopping by remember { mutableStateOf(false) }
    val heartScale by animateFloatAsState(
        targetValue = if (isHeartPopping) 1.3f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "heart_scale"
    )

    val timeFormat = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }
    val formattedDate = remember(post.timestamp) { timeFormat.format(Date(post.timestamp)) }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("feed_card_${post.id}")
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(EmeraldDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.authorAvatar,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = DarkSurfaceVariant
                            ) {
                                Text(
                                    text = post.tag,
                                    fontSize = 10.sp,
                                    color = CyanAccent,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "$formattedDate • ${post.authorHandle}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "Options",
                        tint = TextSecondary
                    )
                }
            }

            // Post Body Text
            Text(
                text = post.content,
                fontSize = 14.sp,
                color = TextPrimary,
                lineHeight = 21.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
            )

            // Post Media / Image (Double-tap to like)
            if (post.mediaDrawable != null) {
                Spacer(modifier = Modifier.height(8.dp))
                val resId = remember(post.mediaDrawable) {
                    context.resources.getIdentifier(post.mediaDrawable, "drawable", context.packageName)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    isHeartPopping = true
                                    onLikeClick()
                                }
                            )
                        }
                ) {
                    if (resId != 0) {
                        Image(
                            painter = painterResource(id = resId),
                            contentDescription = "Post Media",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // E2EE badge on media
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkBackground.copy(alpha = 0.85f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = EmeraldLight,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Encrypted Media Stream",
                                fontSize = 10.sp,
                                color = EmeraldLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Action Row: Like, Comment, Share, Bookmark
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Like button
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier.scale(heartScale).testTag("like_btn_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLiked) RoseStory else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "${post.likesCount}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Comment button
                    IconButton(
                        onClick = onCommentsClick,
                        modifier = Modifier.testTag("comment_btn_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comments",
                            tint = TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = "${post.commentsCount}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Share
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Share",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CommentRow(author: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(DarkSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(author.take(1), fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(author, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CyanAccent)
            Text(text, fontSize = 13.sp, color = TextPrimary, lineHeight = 18.sp)
        }
    }
}
