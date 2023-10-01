package ml.nandor.confusegroups.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ManualConfusion(
    @PrimaryKey(autoGenerate = true)
    val id:Int? = null,
    val leftCard:String,
    val rightCard:String
)
