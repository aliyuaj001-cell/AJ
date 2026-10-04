package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StoryFeedItem
import com.example.ui.theme.AmberStory
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.RoseStory
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VioletAccent

@Composable
fun StoryTray(
    stories: List<StoryFeedItem>,
    onStoryClick: (StoryFeedItem) -> Unit,
    onAddStoryClick: () -> Unit
) {
    val storyRingGradient = Brush.sweepGradient(
        listOf(
            RoseStory,
            AmberStory,
            VioletAccent,
            CyanAccent,
            RoseStory
        )
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .testTag("story_tray"),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        // "Your Story" item with plus badge
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(end = 14.dp)
                    .clickable { onAddStoryClick() }
            ) {
                Box(
                    modifier = Modifier.size(68.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                            .border(1.5.dp, DarkBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AJ",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 18.sp
                        )
                    }

                    // Plus icon badge
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                            .border(2.dp, DarkBackground, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Story",
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Your Story",
                    fontSize = 11.sp,
                    color = TextMuted,
                    maxLines = 1
                )
            }
        }

        // Friends & Channels Stories
        items(stories.filter { it.isStory }) { story ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(end = 14.dp)
                    .clickable { onStoryClick(story) }
                    .testTag("story_item_${story.id}")
            ) {
                Box(
                    modifier = Modifier.size(68.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Instagram-style gradient ring if unseen
                    val ringModifier = if (story.hasUnseenStory) {
                        Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(storyRingGradient)
                            .padding(2.5.dp)
                    } else {
                        Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(DarkBorder)
                            .padding(1.5.dp)
                    }

                    Box(
                        modifier = ringModifier
                            .clip(CircleShape)
                            .background(DarkBackground)
                            .padding(2.5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(DarkSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = story.authorAvatar,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = story.authorName.split(" ").first(),
                    fontSize = 11.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(64.dp)
                )
            }
        }
    }
}
