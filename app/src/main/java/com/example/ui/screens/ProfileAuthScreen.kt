package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.UserProfile
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

@Composable
fun ProfileAuthScreen(
    userProfile: UserProfile
) {
    val context = LocalContext.current
    var biometricsEnabled by remember { mutableStateOf(userProfile.isBiometricsActive) }
    var passkeyAuthEnabled by remember { mutableStateOf(true) }
    var ephemeralWipeEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
            .testTag("profile_auth_screen"),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        // User Avatar & Bio
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(EmeraldDark)
                        .border(2.dp, EmeraldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AJ",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 28.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = userProfile.displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
                Text(
                    text = userProfile.username,
                    fontSize = 13.sp,
                    color = CyanAccent
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = userProfile.bio,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        // Cryptographic Credentials Card
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
                            Icon(Icons.Default.Key, contentDescription = null, tint = EmeraldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Curve25519 Identity Key",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Public Key", userProfile.publicKeyFingerprint))
                                Toast.makeText(context, "Public Key Fingerprint copied!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = EmeraldLight, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkBackground,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = userProfile.publicKeyFingerprint,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = CyanAccent,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Used for Double Ratchet pre-key bundles and authenticated E2EE message encryption.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Security & Authentication Toggles
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = VioletAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Authentication & Vault Security",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    AuthToggleRow(
                        title = "Biometric Lock (Face / Fingerprint)",
                        subtitle = "Require biometric verification on app resume",
                        checked = biometricsEnabled,
                        onCheckedChange = { biometricsEnabled = it }
                    )

                    AuthToggleRow(
                        title = "FIDO2 / WebAuthn Passkeys",
                        subtitle = "Hardware cryptographic token verification",
                        checked = passkeyAuthEnabled,
                        onCheckedChange = { passkeyAuthEnabled = it }
                    )

                    AuthToggleRow(
                        title = "Ephemeral Key Rotation (300s)",
                        subtitle = "Auto-destroy session keys after expiry",
                        checked = ephemeralWipeEnabled,
                        onCheckedChange = { ephemeralWipeEnabled = it }
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Re-key Session Button
        item {
            Button(
                onClick = {
                    Toast.makeText(context, "Curve25519 Double Ratchet keys regenerated!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("rekey_btn")
            ) {
                Icon(Icons.Default.LockReset, contentDescription = null, tint = EmeraldLight)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Regenerate Ephemeral Pre-Keys", color = EmeraldLight, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AuthToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
            Text(subtitle, fontSize = 11.sp, color = TextMuted)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = DarkBackground,
                checkedTrackColor = EmeraldPrimary,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = DarkBackground
            )
        )
    }
}
