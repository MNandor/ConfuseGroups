package ml.nandor.confusegroups.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Review(
    // the front of the card. Unique identifier of AtomicNote question
    val question: String,

    // the [question of the [answer given by the user]]. correct if same as question
    val answer: String,

    // virtual days used by the app. Does not correspond to real days
    val level: Int,

    // number of times we got it right in the past
    // 0 means it's a re-learning new card
    val streak: Int,

    // unix timestamp
    @PrimaryKey
    val timeStamp: Int
)
