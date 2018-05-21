fun main(args: Array<String>) {

    val message = listOf(1, 0, 1, 0)

    val alice = McEliece()
    val bob = McEliece()

    val aliceOpenKey = alice.getOpenKey()

    val bobEncrypted = bob.encrypt(aliceOpenKey, message.asMatrix)
    val aliceDecrypted = alice.decrypt(bobEncrypted).asList

    println("Message: $message")
    println("Decoded message: $aliceDecrypted")

}