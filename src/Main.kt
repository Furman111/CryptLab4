fun main(args: Array<String>) {

    val alice = McEliece()
    val bob = McEliece()

    val aliceOpenKey = alice.getOpenKey()

    val bobEncrypted = bob.encrypt(aliceOpenKey, listOf(1, 0, 1, 0).asMatrix)
    val aliceDecrypted = alice.decrypt(bobEncrypted).asList

}