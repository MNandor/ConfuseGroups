package ml.nandor.confusegroups.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GroupMembership(
    @PrimaryKey
    val id: String,
    val cardID: String,
    val groupID: String
)
