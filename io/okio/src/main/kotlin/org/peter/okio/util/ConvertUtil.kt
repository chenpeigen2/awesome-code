package org.peter.okio.util

import okio.Buffer
import okio.ByteString
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

object ConvertUtil {

    fun bytesToHex(bytes: ByteArray): String {
        return ByteString.of(*bytes).hex()
    }

    fun hexToBytes(hex: String): ByteArray {
        val buffer = Buffer()
        val hexChars = hex.lowercase()
        for (i in hexChars.indices step 2) {
            val byte = ((hexChars[i].digitToInt(16) shl 4) + hexChars[i + 1].digitToInt(16)).toByte()
            buffer.writeByte(byte.toInt())
        }
        return buffer.readByteArray()
    }

    fun bytesToBase64(bytes: ByteArray): String {
        return ByteString.of(*bytes).base64()
    }

    fun base64ToBytes(base64: String): ByteArray {
        return java.util.Base64.getDecoder().decode(base64)
    }

    fun stringToHex(text: String, charset: Charset = StandardCharsets.UTF_8): String {
        return ByteString.of(*text.toByteArray(charset)).hex()
    }

    fun hexToString(hex: String, charset: Charset = StandardCharsets.UTF_8): String {
        return hexToBytes(hex).toString(charset)
    }

    fun stringToBase64(text: String, charset: Charset = StandardCharsets.UTF_8): String {
        return ByteString.of(*text.toByteArray(charset)).base64()
    }

    fun base64ToString(base64: String, charset: Charset = StandardCharsets.UTF_8): String {
        return base64ToBytes(base64).toString(charset)
    }

    fun md5(text: String, charset: Charset = StandardCharsets.UTF_8): String {
        return ByteString.of(*text.toByteArray(charset)).md5().hex()
    }

    fun md5(bytes: ByteArray): String {
        return ByteString.of(*bytes).md5().hex()
    }

    fun sha256(text: String, charset: Charset = StandardCharsets.UTF_8): String {
        return ByteString.of(*text.toByteArray(charset)).sha256().hex()
    }

    fun sha256(bytes: ByteArray): String {
        return ByteString.of(*bytes).sha256().hex()
    }

    fun sha512(text: String, charset: Charset = StandardCharsets.UTF_8): String {
        return ByteString.of(*text.toByteArray(charset)).sha512().hex()
    }

    fun sha512(bytes: ByteArray): String {
        return ByteString.of(*bytes).sha512().hex()
    }

    fun hmacSha256(data: ByteArray, key: ByteArray): String {
        return ByteString.of(*data).hmacSha256(ByteString.of(*key)).hex()
    }

    fun hmacSha256(text: String, key: String, charset: Charset = StandardCharsets.UTF_8): String {
        val dataBytes = text.toByteArray(charset)
        val keyBytes = key.toByteArray(charset)
        return hmacSha256(dataBytes, keyBytes)
    }
}
