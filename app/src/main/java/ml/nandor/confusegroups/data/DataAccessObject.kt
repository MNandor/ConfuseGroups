package ml.nandor.confusegroups.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.Deck

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
}