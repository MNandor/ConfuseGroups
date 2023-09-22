package ml.nandor.confusegroups

data class CardData(
    val front: String,
    val correct: Int,
    val options: List<String>
)
