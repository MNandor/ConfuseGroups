package ml.nandor.confusegroups

object Util {
    fun getDeckName(): String {
        // generate a random 8-length string for hidden deck name
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
        val name = (1..8).map { chars.random() }.joinToString(separator = "")

        return name
    }

    fun getCardName(): String {
        // literally the same but longer to avoid collision
        return getCardName() + getCardName() + getCardName()
    }
}