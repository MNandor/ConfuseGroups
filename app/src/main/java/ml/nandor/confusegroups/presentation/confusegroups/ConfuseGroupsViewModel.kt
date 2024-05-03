package ml.nandor.confusegroups.presentation.confusegroups

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ml.nandor.confusegroups.domain.Resource
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.ConfuseGroupToAddTo
import ml.nandor.confusegroups.domain.model.Correlation
import ml.nandor.confusegroups.domain.model.NewReview
import ml.nandor.confusegroups.domain.model.PreparedViewableCard
import ml.nandor.confusegroups.domain.model.Review
import ml.nandor.confusegroups.domain.usecase.CreateConfuseGroupWithAnotherCardUseCase
import ml.nandor.confusegroups.domain.usecase.GetAllCorrelationsUseCase
import ml.nandor.confusegroups.domain.usecase.GetLevelOfDeckUseCase
import ml.nandor.confusegroups.domain.usecase.GetQuestionFromAnswerUseCase
import ml.nandor.confusegroups.domain.usecase.GetViewablesFromDeckUseCase
import ml.nandor.confusegroups.domain.usecase.InsertNewReviewUseCase
import ml.nandor.confusegroups.domain.usecase.InsertReviewUseCase
import ml.nandor.confusegroups.domain.usecase.JoinConfuseGroupUseCase
import ml.nandor.confusegroups.domain.usecase.ListCardsFromDeckUseCase
import ml.nandor.confusegroups.domain.usecase.ListCardsGroupedFromDeckUseCase
import ml.nandor.confusegroups.domain.usecase.RemoveCardFromGroupUseCase
import ml.nandor.confusegroups.domain.usecase.RenameConfuseGroupUseCase
import ml.nandor.confusegroups.presentation.MainViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ConfuseGroupsViewModel @Inject constructor(
    private val listCardsGroupedFromDeckUseCase: ListCardsGroupedFromDeckUseCase,
    private val renameConfuseGroupUseCase: RenameConfuseGroupUseCase,
    private val removeCardFromGroupUseCase: RemoveCardFromGroupUseCase
): ViewModel() {

    // Define a coroutinescope so we don't run on main thread
    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    private val _deckGroups: MutableState<List<ConfuseGroupToAddTo>> = mutableStateOf(listOf())
    val deckGroups: State<List<ConfuseGroupToAddTo>> = _deckGroups

    private val _readyToCompose = mutableStateOf(false)
    val readyToCompose = _readyToCompose

    fun loadGroupsFromDatabse(deckName: String){
        _readyToCompose.value=false
        listCardsGroupedFromDeckUseCase(deckName).onEach {
            if (it is Resource.Success){
                withContext(Dispatchers.Main) {
                    _deckGroups.value = it.data!!
                    _readyToCompose.value=true
                }
            }

        }.launchIn(viewModelScope)
    }

    fun renameConfuseGroup(groupID: String, newName: String){

        Timber.d("renaming $groupID to $newName")
        renameConfuseGroupUseCase(Pair(groupID, newName)).launchIn(viewModelScope)

    }

    private val _groupToDeleteFrom:MutableState<String?> = mutableStateOf(null)
    private val _cardToDeleteFromGroup:MutableState<String?> = mutableStateOf(null)
    val isADeletionPending = derivedStateOf { _groupToDeleteFrom.value != null && _cardToDeleteFromGroup.value != null }
    fun selectCardForRemovalFromGroup(groupID: String, cardID: String){
        _groupToDeleteFrom.value = groupID
        _cardToDeleteFromGroup.value = cardID

    }

    fun decideOnRemovalFromGroup(doTheRemoval: Boolean){

        val groupID = _groupToDeleteFrom.value ?: return
        val cardID = _cardToDeleteFromGroup.value ?: return

        if (doTheRemoval){
            removeCardFromGroupUseCase(RemoveCardFromGroupUseCase.Input(groupID, cardID)).onEach {

                // We made a change to the database and we know exactly which on-screen element it impacts
                // We could recompose the entire universe by re-fetching everything from the database...
                // Or we could make an identical change here
                // Causing just once function to recompose
                // An added benefit: we don't scroll away
                if (it is Resource.Success){
                    val oldVal = deckGroups.value.toMutableList()
                    val indexOfGroup = oldVal.indexOfFirst { it.confuseGroup?.id ==  groupID && it.associatedNotes.any { it.id ==  cardID}}
                    val indexOfCard = oldVal[indexOfGroup].associatedNotes.indexOfFirst { it.id ==  cardID}
                    val newAssociatedNotes = oldVal[indexOfGroup].associatedNotes.toMutableList()
                    newAssociatedNotes.removeAt(indexOfCard)
                    oldVal[indexOfGroup] = oldVal[indexOfGroup].copy(associatedNotes = newAssociatedNotes)
                    withContext(Dispatchers.Main){
                        _deckGroups.value = oldVal
                    }
                }




            }.launchIn(viewModelScope)

        }

        _groupToDeleteFrom.value = null
        _cardToDeleteFromGroup.value = null

    }


}