package ml.nandor.confusegroups.presentation.decks

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
import ml.nandor.confusegroups.BuildConfig
import ml.nandor.confusegroups.Util
import ml.nandor.confusegroups.domain.Resource
import ml.nandor.confusegroups.domain.model.AtomicNote
import ml.nandor.confusegroups.domain.model.Deck
import ml.nandor.confusegroups.domain.model.SettingKV
import ml.nandor.confusegroups.domain.usecase.AddCardsFromTextUseCase
import ml.nandor.confusegroups.domain.usecase.CreateReverseDeckUseCase
import ml.nandor.confusegroups.domain.usecase.DeleteDeckUseCase
import ml.nandor.confusegroups.domain.usecase.GetDeckSizesUseCase
import ml.nandor.confusegroups.domain.usecase.GetKeyValueUseCase
import ml.nandor.confusegroups.domain.usecase.GetLevelOfMultipleDecksUseCase
import ml.nandor.confusegroups.domain.usecase.InsertCardUseCase
import ml.nandor.confusegroups.domain.usecase.InsertDeckUseCase
import ml.nandor.confusegroups.domain.usecase.ListCardsFromDeckUseCase
import ml.nandor.confusegroups.domain.usecase.ListDecksUseCase
import ml.nandor.confusegroups.domain.usecase.RenameDeckUseCase
import ml.nandor.confusegroups.domain.usecase.SetKeyValueUseCase
import ml.nandor.confusegroups.domain.usecase.UpdateDeckPreferencesUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DecksViewModel @Inject constructor(
    private val listDecksUseCase: ListDecksUseCase,
    private val getDeckSizesUseCase: GetDeckSizesUseCase,
    private val deleteDeckUseCase: DeleteDeckUseCase,
    private val listCardsFromDeckUseCase: ListCardsFromDeckUseCase,
    private val addCardsFromTextUseCase: AddCardsFromTextUseCase,
    private val renameDeckUseCase: RenameDeckUseCase,
    private val insertCardUseCase: InsertCardUseCase,
    private val insertDeckUseCase: InsertDeckUseCase,
    private val createReverseDeckUseCase: CreateReverseDeckUseCase,
    private val updateDeckPreferencesUseCase: UpdateDeckPreferencesUseCase,
    private val getLevelOfMultipleDecksUseCase: GetLevelOfMultipleDecksUseCase,
    private val getKeyValueUseCase: GetKeyValueUseCase,
    private val setKeyValueUseCase: SetKeyValueUseCase
): ViewModel() {

    // Define a coroutinescope so we don't run on main thread
    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    private val _decks: MutableState<List<Deck>> = mutableStateOf(listOf())
    val decks = _decks

    fun listDecksFromDatabase(){

        Timber.d("Loading decks!")

        listDecksUseCase(Unit).onEach {
            if (it is Resource.Success){
                // return to main thread to update state
                withContext(Dispatchers.Main) {
                    _decks.value = it.data!!
                }

                updateDeckSizesFromDatabase()
                updateDeckLevelsFromDatabase()
            }

        }.launchIn(viewModelScope)
    }

    private val _deckSizes = mutableStateOf<Map<String, Int>>(mapOf())
    private fun updateDeckSizesFromDatabase(){

        Timber.d("Loading deck sizes!")

        getDeckSizesUseCase(Unit).onEach {
            if (it is Resource.Success){
                withContext(Dispatchers.Main) {
                    _deckSizes.value = it.data!!.associate { Pair(it.name, it.count) }
                    Timber.d(it.data.toString())
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun updateDeckLevelsFromDatabase(){
        Timber.d("Loading deck levels!")

        getLevelOfMultipleDecksUseCase(_decks.value).onEach {
            if (it is Resource.Success){
                withContext(Dispatchers.Main) {
                    _deckLevels.value = it.data!!
                    Timber.d(it.data.toString())
                }
            }
        }.launchIn(viewModelScope)

    }

    private val _deckLevels = mutableStateOf<Map<String, Int>>(mapOf())

    fun getDeckSizeFromDeckName(deckName: String): Int {
        return _deckSizes.value[deckName] ?: 0
    }

    fun getDeckLevelFromDeckName(deckName: String?): Int = _deckLevels.value[deckName] ?: 0



    enum class DeckAction{
        DELETION,
        EDITING,
        INSPECTION,
        ADDING,
        RENAME,
        REVERSE
    }

    private val _editedDeckState:MutableState<Deck?> = mutableStateOf(null)
    val editedDeckState:State<Deck?> = _editedDeckState


    private val _deckActionBeingTaken:MutableState<DeckAction?> = mutableStateOf(null)
    val deckActionBeingTaken: State<DeckAction?> = _deckActionBeingTaken

    private val _deckBeingAccessed:MutableState<String?> = mutableStateOf(null)
    val deckBeingAccessed:State<String?> = _deckBeingAccessed


    private val _inspectedDeckCards:MutableState<List<AtomicNote>> = mutableStateOf(listOf())
    val inspectedDeckCards:State<List<AtomicNote>> = _inspectedDeckCards

    fun enterDeckActionMode(deckName:String? = null, mode:DeckAction? = null){

        Timber.d("DeckAction initiated: [[$deckName]], $mode")
        _deckBeingAccessed.value = deckName
        _deckActionBeingTaken.value = mode

        when(mode){
            DeckAction.EDITING -> {
                _editedDeckState.value = decks.value.find { it.name == deckName }
                // not yet implemented
            }
            DeckAction.INSPECTION -> {
                listCardsFromDeckUseCase(deckName).onEach {
                    if (it is Resource.Success){
                        withContext(Dispatchers.Main) {
                            _inspectedDeckCards.value = it.data!!
                        }
                    }
                }.launchIn(viewModelScope)

            }
            else -> {}
        }

    }

    fun deleteDeck(){
        if (_deckBeingAccessed.value != null){
            Timber.d("Deleting deck [[${_deckBeingAccessed.value}]]")

            deleteDeckUseCase(_deckBeingAccessed.value!!).onEach {
                if (it is Resource.Success){
                    listDecksFromDatabase()
                }
            }.launchIn(viewModelScope)
            enterDeckActionMode(null)
        }
    }

    fun addCardsAfterDeckInspection(text: String){
        Timber.d("After inspect feature, we're adding cards.")
        addCardsFromTextUseCase(Pair(_deckBeingAccessed.value!!, text)).onEach {
            updateDeckSizesFromDatabase()
        }
            .launchIn(viewModelScope)
    }

    fun renameDeck(deckName:String, deckDisplayName: String){
        Timber.d("Deleting deck [[$deckName]] to \"$deckDisplayName\"")
        renameDeckUseCase(Pair(deckName, deckDisplayName)).onEach {
            if (it is Resource.Success){
                listDecksFromDatabase()
            }
        }.launchIn(viewModelScope)
    }

    fun addCard(card: AtomicNote){
        Timber.d("Adding 1 card to deck: $card")
        insertCardUseCase(card).onEach {
            updateDeckSizesFromDatabase()
        }.launchIn(viewModelScope)
    }

    fun createDeck(){
        val name = Util.getDeckName()

        val deck = Deck(name = name, -1, 1.5, 1.0, 0)

        Timber.d("Creating deck. It will have the id [[$name]]")

        insertDeckUseCase(deck).onEach {
            if (it is Resource.Success){
                listDecksFromDatabase()
            }
        }.launchIn(viewModelScope)

    }

    fun reverseDeck(){
        if (_deckBeingAccessed.value == null) return

        Timber.d("Reversing ${_deckBeingAccessed.value}")
        createReverseDeckUseCase(_deckBeingAccessed.value!!).onEach {
            if (it is Resource.Success){
                listDecksFromDatabase()
                Timber.d("Created reverse deck")
            }
        }.launchIn(viewModelScope)
    }

    fun setPreferences(cor: String, grp: String, ran: String, newCardsPerLevel: String){
        if (_deckBeingAccessed.value == null) return

        val input = UpdateDeckPreferencesUseCase.InputDC(
            deckName = deckBeingAccessed.value!!,
            correlationPreference = cor,
            groupPreference = grp,
            randomPreference = ran,
            newCardsPerLevel = newCardsPerLevel
        )

        updateDeckPreferencesUseCase(input).onEach {
            if (it is Resource.Success){
                listDecksFromDatabase()
                Timber.d("Updated deck preference values")
            }
        }.launchIn(viewModelScope)
    }

    val thisDeck = derivedStateOf {
        _decks.value.find { it.name == _deckBeingAccessed.value }
    }

    private val _shouldShowInitialPopup = mutableStateOf(false)
    val shouldShowInitialPopup = _shouldShowInitialPopup
    fun checkForInitialPopup(){
        getKeyValueUseCase("seenInitialPopup").onEach {
            if (it is Resource.Success) {
                withContext(Dispatchers.Main) {
                    _shouldShowInitialPopup.value = if (it.data?.value == "true") false else true
                }

            }
        }.launchIn(viewModelScope)
    }

    fun hideInitialPopup(){
        _shouldShowInitialPopup.value = false
        if (BuildConfig.DEBUG)
            return
        setKeyValueUseCase(SettingKV("seenInitialPopup", "true")).launchIn(viewModelScope)

    }

    init {
        checkForInitialPopup()
    }


}