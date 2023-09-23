package ml.nandor.confusegroups.domain

data class Review(
    val question: String, // the front of the card. Unique identifier
    val answer: String, // the [question of the [answer given by the user]]. correct if same as question
    val day: Int, // virtual days used by the app. Does not correspond to real days
    val period: Double, // amount of days to wait before showing the card again
    val timeStamp: Int // unix timestamp, currently not used
)
