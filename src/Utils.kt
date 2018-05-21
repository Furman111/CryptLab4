import Jama.Matrix
import sun.text.normalizer.UTF16.append
import java.lang.Math.abs

val List<Int>.asMatrix: Matrix
    get() = Matrix.constructWithCopy(arrayOf(
            DoubleArray(this.size) {
                this[it].toDouble()
            }
    ))

val Matrix.asList
    get() = List(columnDimension) {
        get(0, it).toInt()
    }

val Matrix.asBinary: Matrix
    get() = apply {
        for (i in 0 until rowDimension)
            for (j in 0 until columnDimension) {
                set(i, j, abs(get(i, j) % 2))
            }
    }

fun findErrorPosition(H: Matrix, syndrom: Matrix): Int {
    var res: Int = -1
    for (i in 0 until H.columnDimension) {
        var isEquals = true
        for (j in 0 until syndrom.rowDimension) {
            if (syndrom[j, 0] != H[j, i]) {
                isEquals = false
            }
        }
        if (isEquals) {
            res = i
            break
        }
    }
    if (res == -1) throw IllegalArgumentException() else
        return res
}

val Matrix.isZeros: Boolean
    get() {
        var res = true
        for (i in 0 until rowDimension) {
            for (j in 0 until columnDimension) {
                if (get(i, j) != 0.0)
                    res = false
            }
        }
        return res
    }

fun zerosMatrix(rows: Int, columns: Int) = Matrix.identity(rows, columns).apply {
    for (i in 0 until rowDimension)
        for (j in 0 until columnDimension) {
            set(i, j, 0.0)
        }
}

fun String.encrypt(encrypter: McEliece, openKey: OpenKey): EncryptedString {

    val binaryChars = List(length) {
        this[it].toBinaryChar()
    }

    val encryptedChars = List(length) { charPosition ->
        EncryptedChar(
                List(4) { partNumber ->
                    encrypter.encrypt(openKey,
                            when (partNumber) {
                                0 -> binaryChars[charPosition].part1.asMatrix
                                1 -> binaryChars[charPosition].part2.asMatrix
                                2 -> binaryChars[charPosition].part3.asMatrix
                                3 -> binaryChars[charPosition].part4.asMatrix
                                else -> Matrix.random(0, 0)
                            }
                    ).asList
                }
        )
    }

    return EncryptedString(encryptedChars)

}

fun EncryptedString.decrypt(decrypter: McEliece): String {

    val binaryChars = List(chars.size) { charPosition ->

        val part1 = decrypter.decrypt(chars[charPosition].parts[0].asMatrix).asList
        val part2 = decrypter.decrypt(chars[charPosition].parts[1].asMatrix).asList
        val part3 = decrypter.decrypt(chars[charPosition].parts[2].asMatrix).asList
        val part4 = decrypter.decrypt(chars[charPosition].parts[3].asMatrix).asList

        BinaryChar(part1, part2, part3, part4)
    }

    return StringBuilder().apply {
        for (char in binaryChars) {
            append(char.asChar)
        }
    }.toString()

}

data class EncryptedChar(val parts: List<List<Int>>)

data class EncryptedString(val chars: List<EncryptedChar>)

data class BinaryChar(
        val part1: List<Int>,
        val part2: List<Int>,
        val part3: List<Int>,
        val part4: List<Int>
) {

    val asChar
        get() = this.asInt.toChar()

    val asInt
        get() = part1.partToInt(1) +
                part2.partToInt(16) +
                part3.partToInt(256) +
                part4.partToInt(4096)

    private fun List<Int>.partToInt(beginValue: Int) =
            this[0] * 1 * beginValue + this[1] * 2 * beginValue + this[2] * 4 * beginValue + this[3] * 8 * beginValue


}

fun Char.toBinaryChar(): BinaryChar {

    val binaryString = StringBuilder(Integer.toBinaryString(toInt())).reverse().toString()

    val part1 = List(4) {
        binaryString.getBit(it)
    }

    val part2 = List(4) {
        binaryString.getBit(it + 4)
    }

    val part3 = List(4) {
        binaryString.getBit(it + 8)
    }

    val part4 = List(4) {
        binaryString.getBit(it + 12)
    }

    return BinaryChar(part1, part2, part3, part4)

}

private fun String.getBit(position: Int) =
        if (position < length) {
            if (this[position] == '0') 0 else 1
        } else {
            0
        }