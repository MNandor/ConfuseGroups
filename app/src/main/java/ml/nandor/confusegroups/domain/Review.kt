package ml.nandor.confusegroups.domain

data class Review(
    // the front of the card. Unique identifier of AtomicNote question
    val question: String,

    // the [question of the [answer given by the user]]. correct if same as question
    val answer: String,

    // virtual days used by the app. Does not correspond to real days
    val level: Int,

    // amount of days to wait before showing the card again
    val period: Double,

    // unix timestamp, currently not used
    val timeStamp: Int
)
