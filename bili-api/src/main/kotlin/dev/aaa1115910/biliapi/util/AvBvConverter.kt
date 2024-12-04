package dev.aaa1115910.biliapi.util

object AvBvConverter {
    private val XOR_CODE = 23442827791579L.toBigInteger()
    private val MASK_CODE = 2251799813685247L.toBigInteger()
    private val MAX_AID = 1.toBigInteger() shl 51
    private val BASE = 58.toBigInteger()

    private const val DATA = "FcwAPNKTMug3GV5Lj7EJnHpWsx4tb8haYeviqBz6rkCy12mUSDQX9RdoZf"

    fun av2bv(aid: Long): String {
        val bytes = "BV1000000000".toCharArray()
        var bvIndex = bytes.size - 1
        var tmp = MAX_AID or aid.toBigInteger() xor XOR_CODE
        while (tmp > 0.toBigInteger()) {
            bytes[bvIndex] = DATA[(tmp % BASE).toInt()]
            tmp /= BASE
            bvIndex--
        }
        bytes.swap(3, 9)
        bytes.swap(4, 7)
        return String(bytes)
    }

    fun bv2av(bvid: String): Long {
        val bvidArr = bvid.toCharArray()
        bvidArr.swap(3, 9)
        bvidArr.swap(4, 7)
        val adjustedBvid = String(bvidArr, 3, bvidArr.size - 3)
        var tmp = 0.toBigInteger()
        for (c in adjustedBvid.toCharArray()) {
            tmp = tmp * BASE + DATA.indexOf(c).toBigInteger()
        }
        val xor = tmp and MASK_CODE xor XOR_CODE
        return xor.toLong()
    }

    private fun CharArray.swap(i: Int, j: Int) {
        this[i] = this[j].also { this[j] = this[i] }
    }
}
