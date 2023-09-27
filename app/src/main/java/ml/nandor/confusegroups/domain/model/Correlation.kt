package ml.nandor.confusegroups.domain.model

data class Correlation(
    val leftCard: AtomicNote,
    val rightCard: AtomicNote,
    val correlation: Double
)
