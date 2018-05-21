import Jama.Matrix
import java.util.*

data class OpenKey(
        val gMatrix: Matrix,
        val t: Int)

class McEliece {

    private lateinit var S: Matrix
    private lateinit var G: Matrix
    private lateinit var P: Matrix
    private lateinit var openG: Matrix

    private val n = 7
    private val k = 4
    private val t = 1

    init {
        generateKey()
    }

    fun getOpenKey(): OpenKey = OpenKey(openG, t)

    fun encrypt(openKey: OpenKey, messageBlock: Matrix): Matrix {

        if (openKey.gMatrix.rowDimension != messageBlock.columnDimension)
            throw IllegalArgumentException("Length of messageBlock must be equal to gMatrix's rows count")

        val random = Random()
        val n = openKey.gMatrix.columnDimension
        val errorValues = MutableList(n) {
            false
        }
        var errors = 0
        val errorMatrix = zerosMatrix(1, n)
        while (errors < openKey.t) {
            val ind = random.nextInt(n)
            if (!errorValues[ind]) {
                errorMatrix.set(0, ind, 1.0)
                errors++
            }
        }

        return messageBlock.times(openKey.gMatrix).plus(errorMatrix).asBinary

    }

    fun decrypt(encrypted: Matrix): Matrix {
        val cc = encrypted.times(P.inverse()).asBinary

        val H = Matrix.constructWithCopy(
                arrayOf(
                        doubleArrayOf(0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 1.0),
                        doubleArrayOf(1.0, 0.0, 1.0, 1.0, 0.0, 1.0, 0.0),
                        doubleArrayOf(1.0, 1.0, 0.0, 1.0, 1.0, 0.0, 0.0)
                )
        )

        do {
            val syndrom = H.times(cc.transpose()).asBinary
            if (!syndrom.isZeros) {
                val errorPosition = findErrorPosition(H, syndrom)
                cc.set(0, errorPosition, (cc.get(0, errorPosition) + 1) % 2)
            }
        } while (!syndrom.isZeros)

        val code = Matrix(
                arrayOf(
                        DoubleArray(k) {
                            cc[0, it]
                        }
                )
        )

        return code.times(S.inverse()).asBinary

    }

    private fun generateKey() {

        G = Matrix.constructWithCopy(
                arrayOf(
                        doubleArrayOf(1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0),
                        doubleArrayOf(0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0),
                        doubleArrayOf(0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0),
                        doubleArrayOf(0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0))).asBinary

        val random = Random()


        S = zerosMatrix(k, k)

        do {
            for (i in 0 until k * k) {
                val rowInd = i / k
                val colInd = i % k
                S.set(rowInd, colInd, if (random.nextBoolean()) 1.0 else 0.0)
            }
        } while (S.det() == 0.0)

        P = zerosMatrix(n, n)

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

        openG = S.times(G).asBinary.times(P).asBinary

    }

}