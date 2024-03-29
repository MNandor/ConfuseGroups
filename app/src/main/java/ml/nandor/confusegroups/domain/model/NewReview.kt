package ml.nandor.confusegroups.domain.model

import androidx.room.PrimaryKey

data class NewReview(
    @PrimaryKey
    val reviewID: Long? = 0,

    val timeStamp: Long,

    val questionID: String,

    val answerOptionID: String,

    val levelWhereThisHappened: Int,

    val streakValueAfterThis: Int,

    // if questionID == answerOptionID, then:
    // this bool being true means correct,
    // otherwise incorrect (false negative)
    // if questionID != answerOptionID, then:
    // this bool being false is correct,
    // otherwise incorrect (false positive)
    val wasThisOptionPicked: Boolean
)
