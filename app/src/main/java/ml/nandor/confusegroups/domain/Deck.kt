package ml.nandor.confusegroups.domain

data class Deck(
    val name: String,
    val newCardsPerDay: Int,
    val successMultiplier:Double
)
