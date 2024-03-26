package ml.nandor.confusegroups.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey


// kept in the database
// because I have some old data
// otherwise deprecated
@Entity
data class ManualConfusion(
    @PrimaryKey(autoGenerate = true)
    val id:Int? = null,
    val leftCard:String,
    val rightCard:String
)
