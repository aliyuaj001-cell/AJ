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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PushNotificationItem
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.RoseStory
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationCenterScreen(
    notifications: List<PushNotificationItem>,
    onNotificationClick: (String) -> Unit,
    onSimulatePush: () -> Unit,
    onMarkAllRead: () -> Unit
) {
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
            .testTag("notification_center_screen"),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Push Notification Center",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Low-latency APNs & FCM edge delivery queue",
                            fontSize = 12.sp,
                            color = RoseStory
                        )
                    }

                    IconButton(
                        onClick = onMarkAllRead,
                        modifier = Modifier.testTag("mark_read_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Mark All Read",
                            tint = EmeraldLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Delivery Benchmarks Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = CyanAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Push Telemetry Benchmark",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("P50 LATENCY", fontSize = 10.sp, color = TextMuted)
                                Text("14.2ms", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldLight)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("P99 LATENCY", fontSize = 10.sp, color = TextMuted)
                                Text("21.8ms", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = CyanAccent)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("PRIORITY", fontSize = 10.sp, color = TextMuted)
                                Text("HIGH_FREQ", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = RoseStory)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onSimulatePush,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("simulate_push_btn")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Simulate Background Push Alert", color = DarkBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Delivered Push Payloads (${notifications.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        if (notifications.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No push notification logs yet.", color = TextMuted, fontSize = 13.sp)
                }
            }
        } else {
            items(notifications) { notif ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (!notif.isRead) DarkSurfaceVariant else DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (!notif.isRead) RoseStory.copy(alpha = 0.5f) else DarkBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onNotificationClick(notif.conversationId) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(RoseStory.copy(alpha = 0.2f))
                                .border(1.dp, RoseStory, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = RoseStory,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = notif.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = timeFormat.format(Date(notif.timestamp)),
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = notif.body,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Routing: ${notif.fcmTokenPrefix} • Delivery Latency: ${notif.latencyMs}ms",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = EmeraldLight
                            )
                        }
                    }
                }
            }
        }
    }
}
