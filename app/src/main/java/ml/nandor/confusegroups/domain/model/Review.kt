package ml.nandor.confusegroups.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Review(
    // the front of the card. Unique identifier of AtomicNote question
    val question: String,

    // the answer the user gave to the question
    // might or might not be the correct answer
    // it's correct if the question-answer pair corresponds to an AtomicNote
    val answer: String,

    // virtual days used by the app. Does not correspond to real days
    val level: Int,

    // number of times we got it right in the past
    // 0 means it's a re-learning or new card
    val streak: Int,

    // unix timestamp
    @PrimaryKey
    val timeStamp: Long,

    // semicolon-separated list of answer presented to the user that weren't picked
    // if the user picked correctly, these are all wrong
    // if the user picked wrong, these include the right answer
    // see answer
    val unpickedAnswers: String
)
