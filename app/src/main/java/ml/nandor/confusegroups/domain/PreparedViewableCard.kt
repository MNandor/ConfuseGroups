package ml.nandor.confusegroups.domain

data class PreparedViewableCard(
    val front: String,
    val correct: Int,
    val options: List<String>
)