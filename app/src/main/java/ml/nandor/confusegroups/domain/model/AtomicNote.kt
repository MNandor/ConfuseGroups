package ml.nandor.confusegroups.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AtomicNote(
    @PrimaryKey @ColumnInfo(name = "question")
    val id: String,
    val answer: String,
    val deck: String,
    @ColumnInfo(name = "questionDisplay")
    val question: String?, // visually, what shows up as a question
    val mnemonic: String? = null
)
