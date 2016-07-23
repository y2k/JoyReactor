package y2k.joyreactor.common

/**
 * Created by y2k on 23/07/16.
 */

private const val HSTART = -4953706369002393500L
private const val HMULT = 7664345821815920749L
private val byteTable = createLookupTable()

fun CharSequence.strongHashCode(): Long {
    var h = HSTART
    val hmult = HMULT
    val ht = byteTable
    val len = length
    for (i in 0..len - 1) {
        val ch = this[i]
        h = h * hmult xor ht[ch.toInt() and 0xff]
        h = h * hmult xor ht[ch.toInt().ushr(8) and 0xff]
    }
    return h
}

private fun createLookupTable(): LongArray {
    val result = LongArray(256)
    var h = 0x544B2FBACAAF1684L
    for (i in 0..255) {
        for (j in 0..30) {
            h = h.ushr(7) xor h
            h = h shl 11 xor h
            h = h.ushr(10) xor h
        }
        result[i] = h
    }
    return result
}