package ml.nandor.confusegroups.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.DeckSize
import ml.nandor.confusegroups.domain.model.ManualConfusion
import ml.nandor.confusegroups.domain.model.Review

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
    @Query("SELECT name, count(*) FROM Deck LEFT JOIN AtomicNote ON deck.name = AtomicNote.deck GROUP BY deck.name")
    fun getDeckSizes():List<DeckSize>

    @Query("UPDATE DECK SET displayName = :deckNewName WHERE name = :deckID")
    fun renameDeck(deckID: String, deckNewName: String)

    @Insert
    fun insertManualConfusion(manualConfusion: ManualConfusion)

    @Query("SELECT * FROM ManualConfusion")
    fun listManualConfusions(): List<ManualConfusion>
}