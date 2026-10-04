package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShardNode
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
import com.example.websocket.WebSocketSimulator
import kotlin.math.abs

@Composable
fun ArchitectureScreen(
    webSocket: WebSocketSimulator
) {
    var selectedSection by remember { mutableStateOf("SHARDING") } // "SHARDING", "SCHEMA", "WEBSOCKET", "CACHE_KAFKA"
    val context = LocalContext.current

    val socketStatus by webSocket.status.collectAsState()
    val latencyMs by webSocket.latencyMs.collectAsState()
    val activeShard by webSocket.activeShard.collectAsState()
    val packetHistory by webSocket.packetHistory.collectAsState()

    // Interactive Shard routing test input
    var testEntityId by remember { mutableStateOf("conv_tech_core_9942") }
    val calculatedShard = remember(testEntityId) {
        abs(testEntityId.hashCode()) % 8
    }

    val shardNodes = remember {
        listOf(
            ShardNode(0, "Shard-00 [London Edge]", "eu-west-2", 0x0000, 0x1FFF, 48210, 14.2),
            ShardNode(1, "Shard-01 [Frankfurt Edge]", "eu-central-1", 0x2000, 0x3FFF, 54100, 15.6),
            ShardNode(2, "Shard-02 [N. Virginia Edge]", "us-east-1", 0x4000, 0x5FFF, 89300, 16.4),
            ShardNode(3, "Shard-03 [Oregon Edge]", "us-west-2", 0x6000, 0x7FFF, 62450, 17.1),
            ShardNode(4, "Shard-04 [Tokyo Edge]", "ap-northeast-1", 0x8000, 0x9FFF, 41200, 18.0),
            ShardNode(5, "Shard-05 [Singapore Edge]", "ap-southeast-1", 0xA000, 0xBFFF, 38900, 17.5),
            ShardNode(6, "Shard-06 [São Paulo Edge]", "sa-east-1", 0xC000, 0xDFFF, 24100, 22.3),
            ShardNode(7, "Shard-07 [Sydney Edge]", "ap-southeast-2", 0xE000, 0xFFFF, 29800, 21.0)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
            .testTag("architecture_screen"),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        // Section Header
        item {
            Column {
                Text(
                    text = "System Architecture & Sharding",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = TextPrimary
                )
                Text(
                    text = "High-concurrency horizontally sharded backend specification",
                    fontSize = 12.sp,
                    color = EmeraldLight
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Section Selector Tabs
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                val sections = listOf(
                    "SHARDING" to "Horizontal Sharding",
                    "SCHEMA" to "Database Schema",
                    "WEBSOCKET" to "WebSocket Gateway",
                    "CACHE_KAFKA" to "Cache & Kafka"
                )
                items(sections) { (key, label) ->
                    val isSelected = selectedSection == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSection = key },
                        label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = DarkSurfaceVariant,
                            labelColor = TextSecondary,
                            selectedContainerColor = VioletAccent,
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = DarkBorder,
                            selectedBorderColor = VioletAccent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        when (selectedSection) {
            "SHARDING" -> {
                // Interactive Shard Router
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AltRoute, contentDescription = null, tint = CyanAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Consistent Hashing & Shard Router",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Calculate which physical database partition & WebSocket node receives a connection:",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = testEntityId,
                                onValueChange = { testEntityId = it },
                                label = { Text("Conversation ID or User ID") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("shard_input_field"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = DarkSurfaceVariant,
                                    unfocusedContainerColor = DarkSurfaceVariant,
                                    focusedBorderColor = CyanAccent,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Route Result Card
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkBackground,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "ROUTED SHARD: Node #$calculatedShard",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            color = CyanAccent
                                        )
                                        Text(
                                            text = "${shardNodes[calculatedShard].nodeName} (${shardNodes[calculatedShard].hostRegion})",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = "Consistent Hash Slot: 0x${Integer.toHexString(abs(testEntityId.hashCode()) % 65535).uppercase()}",
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = TextMuted
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = EmeraldDark
                                    ) {
                                        Text(
                                            text = "${shardNodes[calculatedShard].latencyP99Ms}ms",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Shard Cluster Nodes List
                item {
                    Text(
                        text = "Global Shard Ring (8 Virtual Edge Partitions)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                items(shardNodes) { node ->
                    val isCurrent = node.shardId == calculatedShard
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isCurrent) DarkSurfaceVariant else DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isCurrent) CyanAccent else DarkBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isCurrent) CyanAccent else EmeraldPrimary)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = node.nodeName,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Active Sockets: ${node.activeConnections.toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1,")}",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "p99 ${node.latencyP99Ms}ms",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldLight
                                )
                                Text(
                                    text = node.status,
                                    fontSize = 10.sp,
                                    color = CyanAccent
                                )
                            }
                        }
                    }
                }
            }

            "SCHEMA" -> {
                // Production Sharded Database Schema Code Block
                item {
                    val schemaCode = """
-- ========================================================
-- AJ CHART HORIZONTALLY SHARDED DATABASE SCHEMA (CockroachDB / PostgreSQL)
-- Partitioned by HASH(conversation_id) to eliminate hot-spots
-- ========================================================

-- 1. CONVERSATIONS METADATA TABLE
CREATE TABLE conversations (
    conversation_id UUID NOT NULL,
    shard_id INT2 NOT NULL, -- Computed: abs(hash(conversation_id)) % 8
    channel_type VARCHAR(16) NOT NULL, -- 'DIRECT', 'GROUP', 'CHANNEL'
    title VARCHAR(128) NOT NULL,
    encryption_algorithm VARCHAR(32) DEFAULT 'AES-256-GCM',
    ratchet_version INT NOT NULL DEFAULT 3,
    created_at TIMESTAMPTZ NOT NULL DEFAULT clock_timestamp(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT clock_timestamp(),
    PRIMARY KEY (shard_id, conversation_id)
) PARTITION BY LIST (shard_id);

-- 2. SHARDED MESSAGES TABLE (Ultra High-Throughput Write-Behind)
CREATE TABLE chat_messages (
    shard_id INT2 NOT NULL,
    conversation_id UUID NOT NULL,
    message_id UUID NOT NULL,
    sender_id UUID NOT NULL,
    encrypted_payload BYTEA NOT NULL, -- AES-256-GCM ciphertext
    iv BYTEA NOT NULL, -- 12-byte initialization vector
    auth_tag BYTEA NOT NULL, -- 16-byte GCM authentication tag
    media_url VARCHAR(512),
    media_type VARCHAR(16) DEFAULT 'NONE',
    reply_to_id UUID,
    status VARCHAR(16) NOT NULL DEFAULT 'SENT',
    client_timestamp BIGINT NOT NULL,
    server_timestamp TIMESTAMPTZ NOT NULL DEFAULT clock_timestamp(),
    PRIMARY KEY (shard_id, conversation_id, server_timestamp DESC, message_id)
) PARTITION BY LIST (shard_id);

-- Sub-partitions created for each physical shard:
CREATE TABLE chat_messages_shard_0 PARTITION OF chat_messages FOR VALUES IN (0);
CREATE TABLE chat_messages_shard_1 PARTITION OF chat_messages FOR VALUES IN (1);
-- (Repeated across all 8 shard clusters)

-- 3. USER CONVERSATION ROUTING & MEMBERSHIP (Lookup index)
CREATE TABLE conversation_members (
    user_id UUID NOT NULL,
    conversation_id UUID NOT NULL,
    shard_id INT2 NOT NULL,
    role VARCHAR(16) DEFAULT 'MEMBER',
    last_read_watermark TIMESTAMPTZ,
    PRIMARY KEY (user_id, conversation_id)
);

CREATE INDEX idx_user_conv_lookup ON conversation_members (user_id, last_read_watermark DESC);

-- 4. EPHEMERAL DEVICE KEYS TABLE (Signal Double Ratchet Pre-Keys)
CREATE TABLE user_prekeys (
    user_id UUID NOT NULL,
    device_id VARCHAR(64) NOT NULL,
    identity_key BYTEA NOT NULL, -- Curve25519 Public Key
    signed_prekey BYTEA NOT NULL,
    signature BYTEA NOT NULL,
    one_time_prekeys JSONB,
    PRIMARY KEY (user_id, device_id)
);
                    """.trimIndent()

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Storage, contentDescription = null, tint = EmeraldPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("DDL Schema Specification", fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("AJ Chart Schema", schemaCode))
                                        Toast.makeText(context, "Schema copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.testTag("copy_schema_btn")
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = EmeraldLight)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkBackground,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = schemaCode,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFFE2E8F0),
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            "WEBSOCKET" -> {
                // Live WebSocket Telemetry & Gateway Architecture
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DeviceHub, contentDescription = null, tint = EmeraldPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("WebSocket Edge Gateway", fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Button(
                                    onClick = { webSocket.simulateReconnect() },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("reconnect_btn")
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Failover", color = CyanAccent, fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Status metrics
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                MetricBox("SOCKET STATUS", socketStatus.name, if (socketStatus.name == "CONNECTED") EmeraldLight else RoseStory)
                                MetricBox("ACTIVE GATEWAY", activeShard.split(" ").first(), CyanAccent)
                                MetricBox("ROUNDTRIP PING", "${latencyMs}ms", EmeraldLight)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Multiplexed WebSocket Packet Stream (Live):", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(8.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                packetHistory.take(6).forEach { pkt ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = DarkBackground,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(pkt.type, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(pkt.channel, fontSize = 11.sp, color = TextPrimary)
                                            }
                                            Text("${pkt.payloadSize}B • ${pkt.latencyMs}ms", fontSize = 10.sp, color = TextMuted)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "CACHE_KAFKA" -> {
                // Redis & Kafka Architecture
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Layers, contentDescription = null, tint = VioletAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("High-Frequency Caching & Kafka", fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            PipelineStepCard(
                                step = "1. INGRESS & TERMINATION",
                                desc = "TLS 1.3 terminated at Envoy Proxy. Binary WebSocket frames decoded via Protobuf."
                            )
                            PipelineStepCard(
                                step = "2. REDIS CLUSTER LRU CACHE",
                                desc = "Recent 100 messages cached in Redis List: `chat:conv:{id}:recent`. Presence stored in 24-bit Redis Bitmaps."
                            )
                            PipelineStepCard(
                                step = "3. REDIS PUB/SUB MULTIPLEXER",
                                desc = "Fan-out messages to all active Gateway pods where conversation participants maintain open TCP sockets."
                            )
                            PipelineStepCard(
                                step = "4. APACHE KAFKA EVENT BUS",
                                desc = "Topic `chat.messages.v1` partitioned strictly by `conversation_id`. Guarantees exact FIFO order."
                            )
                            PipelineStepCard(
                                step = "5. ASYNC WRITE-BEHIND TO SHARD",
                                desc = "Worker fleet batches 500 messages per transaction into CockroachDB / Cassandra shards, keeping write latency under 2ms."
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricBox(label: String, value: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 9.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}

@Composable
fun PipelineStepCard(step: String, desc: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkBackground,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(step, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VioletAccent)
            Spacer(modifier = Modifier.height(2.dp))
            Text(desc, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
        }
    }
}
