package ml.nandor.confusegroups.domain.usecase

import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.ConfuseGroupToAddTo
import ml.nandor.confusegroups.domain.repository.LocalStorageRepository
import timber.log.Timber
import javax.inject.Inject

class ListCardsGroupedFromDeckUseCase @Inject constructor(
    private val repository: LocalStorageRepository
): UseCase<String?, List<ConfuseGroupToAddTo>>() {
    override fun doStuff(input: String?): List<ConfuseGroupToAddTo> {

        // edge cases worth paying attention to:
        // 1 note part of multiple groups -> should show in all groups (duplicate is fine)
        // 1 note, not part of any groups -> should show as ungrouped
        // notes from other decks -> shouldn't show
        // groups with only notes from other decks -> shouldn't show in end result

        val cards =  if (input == null)
            listOf()
        else
            repository.getCardsByDeckName(input)

        val ungroupedCards = cards.toMutableList()

        val groups = repository.listConfuseGroups()

        val memberships = repository.listGroupMemberships()

        val map:MutableMap<String, ArrayList<AtomicNote>> = mutableMapOf()

        for (membership in memberships){
            if (!map.containsKey(membership.groupID)){
                map[membership.groupID] = arrayListOf()
            }
            val theCard = cards.find { it.id == membership.cardID }

            theCard?.let {
                if (! map[membership.groupID]!!.contains(it)) // don't show the same card in the same group twice
                    // todo should prevent these being added to the db to begin with
                    map[membership.groupID]!!.add(it)
            }

            ungroupedCards.remove(theCard)
        }

        val res =  map
            .filter { !it.value.isEmpty() } // groups that contain no card from current deck
            .map {
                ConfuseGroupToAddTo(
                    groups.find { itt ->
                        it.key == itt.id
                    },
                    it.value
                )
            } // convert to chosen data format

        val ungrouped = ConfuseGroupToAddTo(null, ungroupedCards)

        val final =  res + ungrouped

        Timber.d("${final}")

        return final
    }
}