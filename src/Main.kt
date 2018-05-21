fun main(args: Array<String>) {

    val message = "Hello, world!"

    val alice = McEliece()
    val bob = McEliece()

    val aliceOpenKey = alice.getOpenKey()

    val encryptedMessage = message.encrypt(bob, aliceOpenKey)
    val decryptedMessage = encryptedMessage.decrypt(alice)

    println("Message: $message")
    println("Decoded message: $decryptedMessage")

}