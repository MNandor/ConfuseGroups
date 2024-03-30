package ml.nandor.confusegroups.domain.repository

import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.ConfuseGroup
import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.DeckSize
import ml.nandor.confusegroups.domain.model.GroupMembership
import ml.nandor.confusegroups.domain.model.ManualConfusion
import ml.nandor.confusegroups.domain.model.NewReview
import ml.nandor.confusegroups.domain.model.Review

interface LocalStorageRepository {
    fun insertDeck(deck: Deck)
    fun listDecks():List<Deck>
    fun deleteDeckByName(deckName: String)

    fun insertCard(card: AtomicNote)

    fun getCardsByDeckName(deckName: String?):List<AtomicNote>

    fun getMostRecentReviewsByDeckName(deckName: String):List<Review>

    fun getDeckByName(deckName: String):Deck

    fun insertReview(review: Review)

    fun listReviews():List<Review>

    fun getNotesMatchingAnswers(answer: String):List<AtomicNote>

    fun getDeckSizes(): List<DeckSize>

    fun renameDeck(deckID: String, deckNewName: String)
    fun insertManualConfusion(manualConfusion: ManualConfusion)
    fun listManualConfusions(): List<ManualConfusion>

    fun updateCard(card: AtomicNote)

    fun insertConfuseGroup(group: ConfuseGroup)

    fun makeCardPartOfGroup(membership: GroupMembership)

    fun listConfuseGroups(): List<ConfuseGroup>

    fun listGroupMemberships(): List<GroupMembership>

    fun insertNewReview(review: NewReview)

    fun renameConfuseGroup(groupID: String, newName: String)

    fun getNewReviewsFromDeck(deckID: String): List<NewReview>
}