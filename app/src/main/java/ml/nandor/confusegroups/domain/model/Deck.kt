package ml.nandor.confusegroups.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity //todo domain layer shouldn't depend on room
data class Deck(

    // unique identifier of a deck
    @PrimaryKey
    val name: String,

    // limits number of unseen cards per level
    // if larger than deck size, all new cards will be seen before the first review
    val newCardsPerLevel: Int,

    // how much to multiply the period by on success
    // for example if 2, card will show 1, 2, 4, 8, etc. levels apart
    val successMultiplier:Double,

    // the higher, the more likely to show Backs that were previously wrongly selected
    val confuseExponent:Double,

    // how many successMultipliers to divide by on a failure
    // currently unused because we reset to 1 level (so this value is practically infinite)
    val failExponent:Int
)
