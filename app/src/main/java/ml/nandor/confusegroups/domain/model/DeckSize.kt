package ml.nandor.confusegroups.domain.model

import androidx.room.ColumnInfo

data class DeckSize(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "count(*)") val count: Int
)

