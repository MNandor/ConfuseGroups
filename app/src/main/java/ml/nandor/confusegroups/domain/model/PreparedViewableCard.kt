package ml.nandor.confusegroups.domain.model

data class PreparedViewableCard(
    val front: String,
    val correct: Int,
    val options: List<String>,
    val streakSoFar: Int
)