package ml.nandor.confusegroups.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConfuseGroup(
    @PrimaryKey
    val id: String,
    val displayName: String?
)
