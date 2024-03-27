package ml.nandor.confusegroups.presentation.common

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ml.nandor.confusegroups.domain.Resource
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.ConfuseGroupToAddTo
import ml.nandor.confusegroups.domain.model.Correlation
import ml.nandor.confusegroups.domain.usecase.CreateConfuseGroupWithAnotherCardUseCase
import ml.nandor.confusegroups.domain.usecase.GetAllCorrelationsUseCase
import ml.nandor.confusegroups.domain.usecase.JoinConfuseGroupUseCase
import ml.nandor.confusegroups.domain.usecase.ListCardsFromDeckUseCase
import ml.nandor.confusegroups.domain.usecase.ListCardsGroupedFromDeckUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    private val listCardsFromDeckUseCase: ListCardsFromDeckUseCase,
    private val getAllCorrelationsUseCase: GetAllCorrelationsUseCase,
    private val listCardsGroupedFromDeckUseCase: ListCardsGroupedFromDeckUseCase,
    private val createConfuseGroupWithAnotherCardUseCase: CreateConfuseGroupWithAnotherCardUseCase,
    private val joinConfuseGroupUseCase: JoinConfuseGroupUseCase
): ViewModel() {

    // Define a coroutinescope so we don't run on main thread
    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    // viewmodel shared between all screens
    // mostly just used to keep track of what screen is open
    // and in what way


    // these generally correspond to the buttons on the Decks screen
    // see also DecksViewModel DeckActions
    // those open in popups
    // these are full-screens

    private val _selectedDeck: MutableState<String?> = mutableStateOf(null)
    val selectedDeck = _selectedDeck

    private val _selectedMode: MutableState<DeckOpenMode> = mutableStateOf(DeckOpenMode.NONE)
    val selectedMode = _selectedMode

    enum class DeckOpenMode {
        NONE,
        REVIEW,
        CORRELATIONS,
        CONFUSEGROUPS,
        VIEWCARDS
    }

    fun selectDeck(deckName: String?, deckMode: DeckOpenMode){
        Timber.d("Selected deck $deckName for review!")
        _selectedDeck.value = deckName
        _selectedMode.value = deckMode

        when(deckMode){
            DeckOpenMode.CORRELATIONS -> {
                Timber.d("Selected deck $deckName for correlation comparison!")
                getAllCorrelationsUseCase(deckName).onEach {
                    if (it is Resource.Success){
                        _correlations.value = it.data!!
                    }

                }.launchIn(viewModelScope)
            }

            DeckOpenMode.CONFUSEGROUPS -> {
                Timber.d("Selected deck $deckName for manual comparison2!")
                listCardsFromDeckUseCase(_selectedDeck.value).onEach {
                    if (it is Resource.Success){
                        withContext(Dispatchers.Main) {
                            // _allCardsForManual.value = it.data!!
                        }
                    }
                }.launchIn(viewModelScope)
            }

            DeckOpenMode.VIEWCARDS -> {
                Timber.d("Selected deck $deckName for card listing!")
                listCardsFromDeckUseCase(_selectedDeck.value).onEach {
                    if (it is Resource.Success){
                        withContext(Dispatchers.Main) {
                            // _allCardsForManual.value = it.data!!
                        }
                    }
                }.launchIn(viewModelScope)
            }

            DeckOpenMode.REVIEW -> {

            }

            DeckOpenMode.NONE -> {

            }
        }
    }

    fun deselectDeck(){
        _selectedDeck.value = null
        _selectedMode.value = DeckOpenMode.NONE
    }


    private val _correlations:MutableState<List<Correlation>> = mutableStateOf(listOf())
    val correlations: State<List<Correlation>> = _correlations

    private val _manualCardLeft:MutableState<String?> = mutableStateOf(null)
    val manualCardLeft:State<String?> = _manualCardLeft

    fun setManualLeft(cardName: String?){
        _manualCardLeft.value = cardName

        if (cardName == null)
            return

        setManualRightSearchTerm("")
        listCardsFromDeckUseCase(selectedDeck.value).onEach {
            if (it is Resource.Success){
                withContext(Dispatchers.Main) {
                    _allCardsForManual.value = it.data!!
                    Timber.d("There are ${it.data.size} options for right!")
                }
            }
        }.launchIn(viewModelScope)

        listCardsGroupedFromDeckUseCase(selectedDeck.value).onEach {
            if (it is Resource.Success){
                withContext(Dispatchers.Main) {
                    _groupsToAddTo.value = it.data!!
                }
            }
        }.launchIn(viewModelScope)
    }

    private val _groupsToAddTo:MutableState<List<ConfuseGroupToAddTo>> = mutableStateOf(listOf())


    val filteredGroupsToAddTo = derivedStateOf {
        _groupsToAddTo.value.filter {
            val str = manualRightSearchTerm.value.lowercase()
            if (it.confuseGroup?.displayName?.lowercase()?.contains(str) == true)
                return@filter true

            for (note in it.associatedNotes){
                if (note.question?.lowercase()?.contains(str) == true)
                    return@filter true
                if (note.answer.lowercase().contains(str))
                    return@filter true
            }

            return@filter false
        }
    }

    private val _allCardsForManual:MutableState<List<AtomicNote>> = mutableStateOf(listOf())

    private val _manualRightSearchTerm:MutableState<String> = mutableStateOf("")
    val manualRightSearchTerm:State<String> = _manualRightSearchTerm
    fun setManualRightSearchTerm(searchTerm: String){
        _manualRightSearchTerm.value = searchTerm
        Timber.d(">$searchTerm")
    }

    fun joinExistingGroup(groupID: String){
        val left = manualCardLeft.value

        if (left == null)
            return

        Timber.d("Merging $left into $groupID")

        joinConfuseGroupUseCase(Pair(left, groupID)).onEach {
            setManualLeft(left)
        }.launchIn(viewModelScope)
    }

    fun createGroupWithOtherCard(noteID: String){
        val left = manualCardLeft.value

        if (left == noteID)
            return

        if (left == null)
            return

        Timber.d("Creating group with $left and $noteID")

        createConfuseGroupWithAnotherCardUseCase(Pair(left, noteID)).onEach {
            setManualLeft(left)
        }.launchIn(viewModelScope)
    }

    fun getGroupMembershipCount(noteID: String):Int{
        return _groupsToAddTo.value.filter {
            if (it.confuseGroup == null)
                return@filter  false
            for (note in it.associatedNotes){
                if (note.id == noteID)
                    return@filter true
            }
            return@filter false
        }.size
    }

}