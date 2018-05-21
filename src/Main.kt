fun main(args: Array<String>) {
    val mcEliece = McEliece()
    val openKey = mcEliece.generateKey().first
    mcEliece.encrypt(openKey, listOf(1, 1, 0, 1))
}