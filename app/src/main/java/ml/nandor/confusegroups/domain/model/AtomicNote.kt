package ml.nandor.confusegroups.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AtomicNote(
    @PrimaryKey
    val question: String,
    val answer: String,
    val deck: String
)
