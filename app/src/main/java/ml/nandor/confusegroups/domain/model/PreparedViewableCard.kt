package ml.nandor.confusegroups.domain.model

data class PreparedViewableCard(
    val correct: Int,
    val options: List<String>,
    val streakSoFar: Int,
    val note: AtomicNote,
)