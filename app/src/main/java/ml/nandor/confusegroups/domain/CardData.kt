package ml.nandor.confusegroups.domain

data class CardData(
    val front: String,
    val correct: Int,
    val options: List<String>
)