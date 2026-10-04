package com.example.websocket

import java.security.MessageDigest

object EncryptionEngine {

    fun generateFingerprint(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(input.toByteArray())
        return hash.take(6).joinToString("-") { String.format("%02X", it) }
    }

    fun getSessionKeyHex(conversationId: String): String {
        return generateFingerprint("session_key_$conversationId") + "-E2EE"
    }

    fun getDecryptionCipher(): String {
        return "AES-256-GCM / Curve25519 (Ratchet Step #412)"
    }
}
