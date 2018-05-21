import Jama.Matrix
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
