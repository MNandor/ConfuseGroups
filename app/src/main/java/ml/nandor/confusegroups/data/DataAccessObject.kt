package ml.nandor.confusegroups.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.ConfuseGroup
import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.DeckSize
import ml.nandor.confusegroups.domain.model.GroupMembership
import ml.nandor.confusegroups.domain.model.ManualConfusion
import ml.nandor.confusegroups.domain.model.NewReview
import ml.nandor.confusegroups.domain.model.Review
import ml.nandor.confusegroups.domain.model.SettingKV

@Dao
interface DataAccessObject {
    @Insert
    fun insertDeck(deck: Deck)

    @Query("SELECT * FROM Deck")
    fun listDecks():List<Deck>

    @Query("DELETE FROM deck WHERE name = :name")
    fun deleteDeckByName(name: String)

    @Insert
    fun insertCard(card: AtomicNote)

    @Query("SELECT * FROM AtomicNote WHERE deck = :deckName")
    fun getCardsByDeckName(deckName: String):List<AtomicNote>


    @Query("SELECT * from Deck WHERE name = :deckName")
    fun getDeckByName(deckName: String):Deck

    @Query("SELECT R.*" +
            "FROM Review R " +
            "JOIN (SELECT MAX(timeStamp) " +
            "AS max_timestamp, " +
            "question "+
            "FROM Review " +
            "GROUP BY question) latest " +
            "ON R.question = latest.question " +
            "AND R.timeStamp = latest.max_timestamp " +
            "LEFT JOIN AtomicNote " +
            "ON R.question = AtomicNote.question " +
            "WHERE AtomicNote.deck = :deckName")
    fun getMostRecentReviewsByDeckName(deckName:String):List<Review>

    @Insert
    fun insertReview(review: Review)

    @Query("SELECT * from Review")
    fun listReviews():List<Review>

    @Query("SELECT * FROM AtomicNote WHERE answer = :answer")
    fun getNotesMatchingAnswer(answer:String):List<AtomicNote>

    // todo this returns 1 when it should return 0
    @Query("SELECT name, count(*) FROM Deck INNER JOIN AtomicNote ON deck.name = AtomicNote.deck GROUP BY deck.name")
    fun getDeckSizes():List<DeckSize>

    @Query("UPDATE DECK SET displayName = :deckNewName WHERE name = :deckID")
    fun renameDeck(deckID: String, deckNewName: String)

    @Insert
    fun insertManualConfusion(manualConfusion: ManualConfusion)

    @Query("SELECT * FROM ManualConfusion")
    fun listManualConfusions(): List<ManualConfusion>

    @Query("UPDATE AtomicNote SET questionDisplay = :question, answer = :answer WHERE question = :id")
    fun updateCard(question: String?, answer: String, id: String)
    @Insert
    fun insertConfuseGroup(group: ConfuseGroup)
    @Insert
    fun insertGroupMembership(membership: GroupMembership)

    @Query("SELECT * FROM ConfuseGroup")
    fun listConfuseGroups():List<ConfuseGroup>

    @Query("SELECT * FROM GroupMembership")
    fun listGroupMemberships():List<GroupMembership>

    @Insert
    fun insertNewReview(review: NewReview)

    @Query("UPDATE ConfuseGroup SET displayName = :newName WHERE id = :groupID")
    fun renameConfuseGroup(groupID: String, newName: String)

    @Query("SELECT NewReview.* FROM NewReview LEFT JOIN AtomicNote ON NewReview.questionID = AtomicNote.question WHERE AtomicNote.deck = :deckID")
    fun getNewReviewsFromDeck(deckID: String): List<NewReview>

    // todo
//    @Query("SELECT NewReview.* FROM NewReview ")
//    fun getRecentNewReviewsFromDeck(deckID: String): List<NewReview>

    @Query("UPDATE Deck SET correlationPreference = :newVal WHERE name = :deckID")
    fun setDeckCorrelationPreferenceValue(deckID:String, newVal: Double)
    @Query("UPDATE Deck SET confgroupPreference = :newVal WHERE name = :deckID")
    fun setDeckGroupPreferenceValue(deckID:String, newVal: Double)
    @Query("UPDATE Deck SET randomPreference = :newVal WHERE name = :deckID")
    fun setDeckRandomPreferenceValue(deckID:String, newVal: Double)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setKV(kv: SettingKV)

    @Query("SELECT * FROM SettingKV WHERE keyName = :keyName")
    fun getKV(keyName: String):SettingKV

    @Query("DELETE FROM groupmembership WHERE cardID = :cardID AND groupID = :groupID")
    fun removeCardFromGroup(groupID: String, cardID: String)

    @Query("UPDATE Deck SET newCardsPerLevel = :newCardsPerLevel WHERE name = :deckName")
    fun setDeckNewCardsPerLevelValue(deckName: String, newCardsPerLevel: Int)
}