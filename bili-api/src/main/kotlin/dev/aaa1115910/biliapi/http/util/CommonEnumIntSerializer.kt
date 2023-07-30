package dev.aaa1115910.biliapi.http.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Suppress("MemberVisibilityCanBePrivate")
open class CommonEnumIntSerializer<T>(val serialName: String, val choices: Array<T>, val choicesNumbers: Array<Int>) :
    KSerializer<T> {
    override val descriptor: SerialDescriptor = serialDescriptor<String>()

    init {
        require(choicesNumbers.size == choices.size){"There must be exactly one serial number for every enum constant."}
        require(choicesNumbers.distinct().size == choicesNumbers.size){"There must be no duplicates of serial numbers."}
    }

    final override fun serialize(encoder: Encoder, value: T) {
        val index = choices.indexOf(value)
            .also { check(it != -1) { "$value is not a valid enum $serialName, choices are $choices" } }
        encoder.encodeInt(choicesNumbers[index])
    }

    final override fun deserialize(decoder: Decoder): T {
        val serialNumber = decoder.decodeInt()
        val index = choicesNumbers.indexOf(serialNumber)
        check(index != -1) {"$serialNumber is not a valid serial value of $serialName, choices are $choicesNumbers"}
        check(index in choices.indices)
        { "$index is not among valid $serialName choices, choices size is ${choices.size}" }
        return choices[index]
    }
}

interface SerialEnum {
    val serialNumber: Int?
}
fun <T> Array<T>.serial() where T : SerialEnum, T : Enum<T> = this.map { it.serialNumber ?: it.ordinal }.toTypedArray()