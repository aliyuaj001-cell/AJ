package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.ui.components.MessageBubble
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
import com.example.websocket.EncryptionEngine
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    conversation: Conversation?,
    messages: List<ChatMessage>,
    typingUser: String?,
    onBackClick: () -> Unit,
    onSendMessage: (String, String, String?, Int) -> Unit,
    onReactionClick: (String, String) -> Unit
) {
    BackHandler { onBackClick() }

    var textInput by remember { mutableStateOf("") }
    var showSecurityDialog by remember { mutableStateOf(false) }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showMediaDetailDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            Surface(
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("chat_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                when (conversation?.category) {
                                    "CHANNEL" -> VioletAccent
                                    "GROUP" -> CyanAccent
                                    else -> EmeraldDark
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = conversation?.avatar ?: "AJ",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = conversation?.name ?: "Chat",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = if (typingUser != null) "$typingUser is typing..." else "Online • E2EE Mesh",
                            fontSize = 11.sp,
                            color = if (typingUser != null) CyanAccent else EmeraldLight,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // E2EE Security Shield button
                    IconButton(
                        onClick = { showSecurityDialog = true },
                        modifier = Modifier.testTag("chat_security_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Verify Encryption",
                            tint = EmeraldLight,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = DarkSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Attachment button
                    IconButton(
                        onClick = { showAttachmentSheet = true },
                        modifier = Modifier.size(40.dp).testTag("chat_attachment_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Attach Media",
                            tint = TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Voice note quick action
                    IconButton(
                        onClick = {
                            onSendMessage("Voice note: 0:18 audio clip", "VOICE", null, 18)
                        },
                        modifier = Modifier.size(40.dp).testTag("chat_voice_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Send Voice Note",
                            tint = EmeraldLight,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Text Input
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_message_input"),
                        placeholder = { Text("Encrypted message...", color = TextMuted, fontSize = 13.sp) },
                        maxLines = 4,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Send Button
                    IconButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                onSendMessage(textInput.trim(), "NONE", null, 0)
                                textInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                            .testTag("chat_send_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = DarkBackground,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("chat_messages_column"),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // E2EE Golden Security Notice
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceVariant.copy(alpha = 0.8f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldDark.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = EmeraldLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Messages & calls are end-to-end encrypted with Signal Double Ratchet.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Message items
            items(messages) { message ->
                MessageBubble(
                    message = message,
                    onReactionClick = { emoji -> onReactionClick(message.id, emoji) },
                    onMediaClick = { showMediaDetailDialog = true }
                )
            }

            // Typing indicator bubble
            if (typingUser != null) {
                item {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceVariant)
                                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "$typingUser is typing...",
                                fontSize = 12.sp,
                                color = CyanAccent,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }

    // Media Details Dialog
    if (showMediaDetailDialog) {
        AlertDialog(
            onDismissRequest = { showMediaDetailDialog = false },
            containerColor = DarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = EmeraldLight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Encrypted Media Inspector", color = TextPrimary, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text(
                        "Payload verified using on-device AES-256-GCM authenticated decryption tag.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkBackground,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Cipher: AES-256-GCM / Curve25519", fontSize = 11.sp, color = EmeraldLight)
                            Text("Integrity HMAC: 9A4F-C002-88B1-EA21", fontSize = 11.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                            Text("Ephemeral Key Lifetime: 300s", fontSize = 11.sp, color = TextMuted)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMediaDetailDialog = false }) {
                    Text("Close", color = EmeraldPrimary)
                }
            }
        )
    }

    // Security Verification Dialog
    if (showSecurityDialog) {
        AlertDialog(
            onDismissRequest = { showSecurityDialog = false },
            containerColor = DarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("E2EE Security Fingerprint", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        "End-to-End Encryption verified between your device and recipient. Only participants possess the private decryption keys.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Safety Number / Fingerprint:", fontSize = 11.sp, color = TextMuted)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkBackground,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = EncryptionEngine.generateFingerprint(conversation?.id ?: "conv_id") +
                                    " - " + EncryptionEngine.generateFingerprint("peer_id"),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = CyanAccent,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Text("Session Key Hex: ${EncryptionEngine.getSessionKeyHex(conversation?.id ?: "c")}", fontSize = 11.sp, color = TextMuted)
                    Text("Ratchet State: Active (DH ratchet step #12)", fontSize = 11.sp, color = EmeraldLight)
                }
            },
            confirmButton = {
                TextButton(onClick = { showSecurityDialog = false }) {
                    Text("Done", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Attachment Sheet
    if (showAttachmentSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAttachmentSheet = false },
            containerColor = DarkSurface,
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Share Encrypted Media",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    AttachmentOptionItem(
                        icon = Icons.Default.Image,
                        label = "Photo / Media",
                        color = CyanAccent,
                        onClick = {
                            showAttachmentSheet = false
                            onSendMessage("Encrypted Telemetry Snapshot", "IMAGE", "img_encrypted_media_demo_1791125394205", 0)
                        }
                    )
                    AttachmentOptionItem(
                        icon = Icons.Default.Mic,
                        label = "Voice Note",
                        color = EmeraldPrimary,
                        onClick = {
                            showAttachmentSheet = false
                            onSendMessage("Voice note: 0:24 live memo", "VOICE", null, 24)
                        }
                    )
                    AttachmentOptionItem(
                        icon = Icons.Default.Code,
                        label = "Code Shard",
                        color = VioletAccent,
                        onClick = {
                            showAttachmentSheet = false
                            onSendMessage("```sql\nSELECT * FROM messages WHERE shard_id = 4;\n```", "NONE", null, 0)
                        }
                    )
                    AttachmentOptionItem(
                        icon = Icons.Default.Description,
                        label = "Secure Doc",
                        color = Color(0xFFF59E0B),
                        onClick = {
                            showAttachmentSheet = false
                            onSendMessage("Shared: system_architecture_spec_v2.pdf (Encrypted)", "NONE", null, 0)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun AttachmentOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f))
                .border(1.5.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}
