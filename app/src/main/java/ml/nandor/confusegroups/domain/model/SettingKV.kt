package ml.nandor.confusegroups.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SettingKV(
    @PrimaryKey
    val keyName: String,
    val value: String
)
