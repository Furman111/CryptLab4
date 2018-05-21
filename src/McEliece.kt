import Jama.Matrix
import java.util.*

private const val n = 7
private const val k = 4
private const val t = 1

val G = Matrix.constructWithCopy(
        arrayOf(
                doubleArrayOf(1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0),
                doubleArrayOf(0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0),
                doubleArrayOf(0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0),
                doubleArrayOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0)))

data class OpenKey(
        val gMatrix: Matrix,
        val t: Int)

data class SecretKey(
        val S: Matrix,
        val G: Matrix,
        val P: Matrix)

class McEliece {

    fun generateKey(): Pair<OpenKey, SecretKey> {

/*        val S = Matrix.constructWithCopy(
                arrayOf(
                        doubleArrayOf(1.0, 1.0, 0.0, 1.0),
                        doubleArrayOf(1.0, 0.0, 0.0, 1.0),
                        doubleArrayOf(0.0, 1.0, 1.0, 1.0),
                        doubleArrayOf(1.0, 1.0, 0.0, 0.0)
                )
        )*/

        val S = Matrix.identity(k, k)

        val random = Random()
        do {
            for (i in 0 until k * k) {
                val rowInd = i / k
                val colInd = i % k
                S.set(rowInd, colInd, if (random.nextBoolean()) 1.0 else 0.0)
            }
        } while (S.det() == 0.0)

/*        val P = Matrix.constructWithCopy(
                arrayOf(
                        doubleArrayOf(0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0),
                        doubleArrayOf(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0)
                )
        )*/

        val P = Matrix.identity(n, n)
        for (i in 0 until n) {
            P.set(i, i, 0.0)
        }


        val columnsIsBusy = MutableList(n) {
            false
        }
        for (row in 0 until n) {
            var colInd: Int
            do {
                colInd = random.nextInt(n)
            } while (columnsIsBusy[colInd])
            P.set(row, colInd, 1.0)
            columnsIsBusy[colInd] = true
        }

        val openG = S.times(G).times(P)

        for (i in 0 until openG.rowDimension) {
            for (j in 0 until openG.columnDimension) {
                openG.set(i, j, openG[i, j] % 2)
            }
        }

        return Pair(
                OpenKey(
                        openG,
                        t
                ),
                SecretKey(
                        S,
                        G,
                        P
                )
        )

    }

}