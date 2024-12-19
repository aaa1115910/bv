@file:Suppress("unused", "UNUSED_VARIABLE")

package dev.aaa1115910.biliapi.http.entity.live

import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.toByteArray
import io.ktor.utils.io.core.writePacket
import kotlinx.io.Source
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.io.DataInputStream

internal fun Source.readFrameHeader(): FrameHeader = FrameHeader(
    readInt(), readShort(), readShort(), readInt(), readInt()
)

/**
 * 数据包头部
 *
 * @param totalLength 封包总大小（头部大小+正文大小）
 * @param headerLength 头部大小（一般为0x0010，16字节）
 * @param version 协议版本: 0普通包正文不使用压缩 1心跳及认证包正文不使用压缩2普通包正文使用zlib压缩 3普通包正文使用brotli压缩,解压为一个带头部的协议0普通包
 * @param type 操作码（封包类型）
 * @param sequence 保留字段不使用
 */
data class FrameHeader(
    val totalLength: Int,
    val headerLength: Short,
    val version: Short,
    val type: Int,
    val sequence: Int
) {
    val dataLength get() = totalLength - headerLength
    fun toBinary(): Source {
        return buildPacket {
            writeInt(this@FrameHeader.totalLength)
            writeShort(headerLength)
            writeShort(version)
            writeInt(this@FrameHeader.type)
            writeInt(sequence)
        }
    }
}

enum class FrameType(val code: Int) {
    HeartRequest(2), HeartResponse(3), Normal(5), AuthRequest(7), AuthResponse(8)
}

interface RequestFrame {
    fun toBinary(): Source
}

@Serializable
data class AuthRequest(
    val uid: Int = 0,
    @SerialName("roomid")
    val roomId: Int,
    @SerialName("protover")
    val protoVer: Int = 3,
    val platform: String = "web",
    val type: Int = 2,
    val key: String = ""
) : RequestFrame {
    override fun toBinary(): Source {
        val data = Json.encodeToString(this).toByteArray()
        val header = FrameHeader(
            totalLength = data.size + 16,
            headerLength = 16,
            version = 1,
            type = FrameType.AuthRequest.code,
            sequence = 1
        )
        return buildPacket {
            this.writePacket(header.toBinary())
            writePacket(ByteReadPacket(data))
        }
    }
}

@Serializable
data class AuthResponse(
    val code: Int = -1
) {
    companion object {
        fun parse(data: ByteArray): AuthResponse {
            val bis = ByteArrayInputStream(data)
            val dis = DataInputStream(bis)
            val totalLength = dis.readInt()
            val headerLength = dis.readUnsignedShort()
            val version = dis.readUnsignedShort()
            val type = dis.readInt()
            val sequence = dis.readInt()

            //TODO do some verity

            val jsonString = String(dis.readBytes())
            return Json.decodeFromString(jsonString)
        }
    }

}