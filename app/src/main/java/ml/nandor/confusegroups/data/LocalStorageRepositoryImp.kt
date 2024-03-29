package ml.nandor.confusegroups.data

import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.ConfuseGroup
import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.DeckSize
import ml.nandor.confusegroups.domain.model.GroupMembership
import ml.nandor.confusegroups.domain.model.ManualConfusion
import ml.nandor.confusegroups.domain.model.NewReview
import ml.nandor.confusegroups.domain.model.Review
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import timber.log.Timber
import javax.inject.Inject

class LocalStorageRepositoryImp @Inject constructor(
    private val dao: DataAccessObject
): LocalStorageRepository {
    override fun insertDeck(deck: Deck) {
        dao.insertDeck(deck)
    }

    override fun listDecks(): List<Deck> {
        try {
            Timber.d("Hello")
            return dao.listDecks()
        } catch (e: Exception){
            Timber.d(e.message)
            return dao.listDecks()
        }
    }

    override fun deleteDeckByName(deckName: String) {
        dao.deleteDeckByName(deckName)
    }

    override fun insertCard(card: AtomicNote) {

        Timber.d("Inserting note ${card.id} - ${card.answer}")
        dao.insertCard(card)
    }

    override fun getCardsByDeckName(deckName: String?):List<AtomicNote> {
        return if (deckName == null){
            listOf()
        } else {
            dao.getCardsByDeckName(deckName)
        }

    }

    override fun getMostRecentReviewsByDeckName(deckName: String): List<Review> {
        return dao.getMostRecentReviewsByDeckName(deckName)
    }

    override fun getDeckByName(deckName: String): Deck {
        return dao.getDeckByName(deckName)
    }

    override fun insertReview(review: Review){
        dao.insertReview(review)
        Timber.d("Storing review ${review.question} - ${review.answer} with streak ${review.streak}")
        Timber.d("Unpicked answers are: ${review.unpickedAnswers}")
    }

    override fun listReviews(): List<Review> {
        return dao.listReviews()
    }

    override fun getNotesMatchingAnswers(answer: String): List<AtomicNote> {
        return dao.getNotesMatchingAnswer(answer)
    }

    override fun getDeckSizes(): List<DeckSize> {
        return dao.getDeckSizes()
    }

    override fun renameDeck(deckID: String, deckNewName: String){
        dao.renameDeck(deckID, deckNewName)
    }

    override fun insertManualConfusion(manualConfusion: ManualConfusion) {
        dao.insertManualConfusion(manualConfusion)
    }

    override fun listManualConfusions(): List<ManualConfusion> {
        return dao.listManualConfusions()
    }

    override fun updateCard(card: AtomicNote){
        return dao.updateCard(card.question, card.answer, card.id)
    }

    override fun insertConfuseGroup(group: ConfuseGroup) {
        dao.insertConfuseGroup(group)
    }

    override fun makeCardPartOfGroup(membership: GroupMembership) {
        dao.insertGroupMembership(membership)
    }

    override fun listConfuseGroups(): List<ConfuseGroup>{
        return dao.listConfuseGroups()
    }

    override fun listGroupMemberships(): List<GroupMembership>{
        return dao.listGroupMemberships()
    }

    override fun insertNewReview(review: NewReview) {
        return dao.insertNewReview(review)
    }

}