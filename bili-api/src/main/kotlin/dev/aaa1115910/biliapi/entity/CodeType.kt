package dev.aaa1115910.biliapi.entity

import bilibili.pgc.gateway.player.v2.CodeType as PgcPlayUrlCodeType
import bilibili.playershared.CodeType as PlayerSharedCodeType

enum class CodeType {
    NoCode,
    Code264,
    Code265,
    CodeAv1,
    Unrecognized;

    fun toPlayerSharedCodeType() = when (this) {
        NoCode -> PlayerSharedCodeType.NOCODE
        Code264 -> PlayerSharedCodeType.CODE264
        Code265 -> PlayerSharedCodeType.CODE265
        CodeAv1 -> PlayerSharedCodeType.CODEAV1
        Unrecognized -> PlayerSharedCodeType.UNRECOGNIZED
    }

    fun toPgcPlayUrlCodeType() = when (this) {
        NoCode, CodeAv1 -> PgcPlayUrlCodeType.NOCODE
        Code264 -> PgcPlayUrlCodeType.CODE264
        Code265 -> PgcPlayUrlCodeType.CODE265
        Unrecognized -> PgcPlayUrlCodeType.UNRECOGNIZED
    }
}